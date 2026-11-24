package com.github.mictaege.travel_agency_ktx

import com.github.mictaege.arete.ScreenshotExtension
import com.github.mictaege.arete_ktx.Spec
import com.github.mictaege.travel_agency.*
import com.github.mictaege.travel_agency.Bed.KING
import com.github.mictaege.travel_agency.Facilities.BALCONY
import com.github.mictaege.travel_agency.Facilities.HAIRDRYER
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import java.time.LocalDate

class ManageBookingsSpec : Spec({
    spec("Manage Bookings Spec") {
        doc(
            narrative = """
                > As a *traveler* I want to *list my bookings* so that I can *see my upcoming bookings*
                > - and I want to *list my booking history* so that I can *track my past bookings*
                > - and I want to *cancel an upcoming booking* if I *don't need the room anymore*.
            """.trimIndent(),
            plantUml = listOf(
                """
                @startuml
                left to right direction
                :Traveler:
                Traveler --> (List upcoming bookings)
                Traveler --> (List booking history)
                Traveler --> (Cancel upcoming booking)
                @enduml
                """.trimIndent()
            ),
            tags = listOf(ActorTraveller::class, ActorAccommodation::class, EntityReservation::class, Milestone1::class)
        )
        registerExtension(ScreenshotExtension(UiDummyScreenshotTaker()))

        val repository = BookingRepository()
        val traveler = newTraveler()
        repository.register(Accommodation(HOTEL_KRONE, Room(2, KING, HAIRDRYER, BALCONY)))
        fun book(from: LocalDate, to: LocalDate): Booking {
            val offers = repository.findRooms(2, "Munich", from, to)
            offers.forEach { repository.addToShoppingCart(traveler, it) }
            val b = repository.getOffersFromShoppingCart(traveler)[0]
            repository.checkOutFromShoppingCart(traveler, b)
            repository.initPayment(traveler, b, PaymentMethod.PAYPAL)
            return b
        }
        val historic0 = book(LocalDate.now().minusDays(14), LocalDate.now().minusDays(12))
        val historic1 = book(LocalDate.now().minusDays(8), LocalDate.now().minusDays(6))
        val upcoming0 = book(LocalDate.now().plusDays(8), LocalDate.now().plusDays(10))
        val upcoming1 = book(LocalDate.now().plusDays(12), LocalDate.now().plusDays(14))

        scenario("List Upcoming Bookings") {
            lateinit var bookings: List<Booking>
            `when`("traveler lists upcoming bookings") {
                bookings = repository.listUpcomingBookings(traveler)
            }
            then("all upcoming bookings are shown") {
                assertThat(bookings, hasSize(2))
                assertThat(bookings, containsInAnyOrder(upcoming0, upcoming1))
            }
        }
        scenario("List Booking History") {
            lateinit var bookings: List<Booking>
            `when`("traveler lists booking history") {
                bookings = repository.listBookingHistory(traveler)
            }
            then("all past bookings are shown") {
                assertThat(bookings, hasSize(2))
                assertThat(bookings, containsInAnyOrder(historic0, historic1))
            }
        }
        scenario("Cancel Upcoming Booking") {
            lateinit var otherTraveler: Traveler
            lateinit var booking: Booking
            given("is a booking for which cancellation is still possible") {
                otherTraveler = newTraveler()
                val offers = repository.findRooms(2, "Munich", LocalDate.now().plusDays(22), LocalDate.now().plusDays(24))
                offers.forEach { repository.addToShoppingCart(otherTraveler, it) }
                booking = repository.getOffersFromShoppingCart(otherTraveler)[0]
                repository.checkOutFromShoppingCart(otherTraveler, booking)
                repository.initPayment(otherTraveler, booking, PaymentMethod.PAYPAL)
            }
            `when`("traveler cancels the booking") {
                repository.cancelBooking(otherTraveler, booking)
            }
            then("the cancellation was successful") {
                assertThat(booking.state, `is`(BookingState.CANCELLED))
            }
            exampleCsv("Examples: Cancellation at different times before start",
                columns = arrayOf("Start of stay in x days", "Cancellation possible x days before start", "Cancellation possible?"),
                csvData = """
                    8|4|true
                    6|4|true
                    4|4|true
                    2|4|false
                    -2|4|false
                    2|0|false
                    0|0|false
                    -2|0|false
                    """.trimIndent(),
                delimiter = '|'
            ) { row ->
                val startInXDays = row[0].toString().toInt()
                val cancelPossibleXDaysBefore = row[1].toString().toInt()
                val cancelPossible = row[2].toString().toBoolean()
                val traveler = newTraveler()
                val room = Room(2, KING, HAIRDRYER, BALCONY)
                room.daysBeforeStartCancellationPossible = cancelPossibleXDaysBefore
                repository.register(Accommodation(
                    """
                    Ibis Hotel
                    Alexanderplatz 48
                    10178 Berlin
                    DE
                    """.trimIndent(), room))
                val start = LocalDate.now().plusDays(startInXDays.toLong())
                val offers = repository.findRooms(2, "Berlin", start, start.plusDays(2))
                offers.forEach { repository.addToShoppingCart(traveler, it) }
                val booking = repository.getOffersFromShoppingCart(traveler)[0]
                repository.checkOutFromShoppingCart(traveler, booking)
                repository.initPayment(traveler, booking, PaymentMethod.PAYPAL)
                if (cancelPossible) {
                    repository.cancelBooking(traveler, booking)
                    assertThat(booking.state, `is`(BookingState.CANCELLED))
                } else {
                    try { repository.cancelBooking(traveler, booking) } catch (e: Exception) {
                        assertThat(e.message, `is`("Cancellation is no longer possible"))
                    }
                    assertThat(booking.state, `is`(BookingState.CONFIRMED))
                }
            }
        }
    }
})
