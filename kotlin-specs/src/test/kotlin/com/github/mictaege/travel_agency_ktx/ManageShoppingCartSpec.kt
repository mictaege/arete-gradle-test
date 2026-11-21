package com.github.mictaege.travel_agency_ktx

import com.github.mictaege.arete_ktx.Spec
import com.github.mictaege.travel_agency.*
import com.github.mictaege.travel_agency.Bed.KING
import com.github.mictaege.travel_agency.Facilities.BALCONY
import com.github.mictaege.travel_agency.Facilities.HAIRDRYER
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.notNullValue
import java.time.LocalDate

class ManageShoppingCartSpec : Spec({
    spec("Manage Shopping Cart Spec") {
        doc(
            narrative = "> As a *traveler* I want to *check and manage my shopping cart* to *start (or cancel) a booking*.",
            plantUml = listOf(
                """
                @startuml
                left to right direction
                :Traveler:
                Traveler --> (Check offer)
                Traveler --> (Remove offer)
                Traveler --> (Change offer)
                Traveler --> (Check out offer)
                @enduml
                """.trimIndent()
            ),
            tags = listOf(ActorTraveller::class, EntityOffer::class, EntityRoom::class)
        )
        screenshots(UiDummyScreenshotTaker())
        feature("Check Offers In Shopping Cart") {
            scenario("Show Room Details") {
                doc(seeAlso = listOf(DisplayRoomDetailsSpec::class))
                val repository = BookingRepository()
                val roomMngr = RoomManager()
                lateinit var traveler: Traveler
                lateinit var offer: Booking
                given("a registered traveler") { traveler = newTraveler() }
                given("some offers in the shopping cart") {
                    repository.register(Accommodation(HOTEL_KRONE,
                        roomMngr.setPopularity(roomMngr.setPricePerNight(Room(2, KING, HAIRDRYER, BALCONY), 89.99), 8)))
                    repository.findRooms(2, "Munich", LocalDate.of(2026, 1, 6), LocalDate.of(2026, 1, 7))
                        .forEach { repository.addToShoppingCart(traveler, it) }
                }
                `when`("selecting an offer") {
                    offer = repository.getOffersFromShoppingCart(traveler)[0]
                }
                then("the offers room details should be shown") {
                    assertThat(offer, `is`(notNullValue()))
                    assertThat(offer.room.popularity, `is`(8))
                    assertThat(offer.room.maxPersons, `is`(2))
                    assertThat(offer.room.bed, `is`(KING))
                    assertThat(offer.room.facilities, `is`(listOf(HAIRDRYER, BALCONY)))
                    assertThat(offer.room.pricePerNight, `is`(89.99))
                }
            }
            scenario("Show The Total Costs Of An Offer") {
                val repository = BookingRepository()
                val roomMngr = RoomManager()
                lateinit var traveler: Traveler
                lateinit var offer: Booking
                given("a registered traveler") {
                    traveler = newTraveler()
                }
                given("some offers in the shopping cart") {
                    repository.register(Accommodation(HOTEL_KRONE,
                        roomMngr.setPopularity(roomMngr.setPricePerNight(Room(2, KING, HAIRDRYER, BALCONY), 89.99), 8)))
                    repository.findRooms(2, "Munich", LocalDate.of(2026, 1, 6), LocalDate.of(2026, 1, 12))
                        .forEach { repository.addToShoppingCart(traveler, it) }
                }
                `when`("selecting an offer") {
                    offer = repository.getOffersFromShoppingCart(traveler)[0]
                }
                then("the total costs should be shown") {
                    assertThat(offer, `is`(notNullValue()))
                    assertThat(offer.totalCosts, `is`(539.94))
                }
                examples(
                    name = "Examples: Calculate total costs for different number of days and prices per night",
                    pattern = "{0} days, {1} € per night => Total: {2} €",
                    source = {
                        example("{0}. Single day) ", given(1), given(45.33), then(45.33))
                        example("{0}. Short stay) ", given(3), given(120.25), then(360.75))
                        example("{0}. Whole week) ", given(7), given(74.66), then(522.62))
                        example("{0}. Two weeks) ", given(14), given(69.99), then(979.86))
                    }
                ) { row ->
                    val traveler = newTraveler()
                    val days = row[1] as Int
                    val price = row[2] as Double
                    val expected = row[3] as Double
                    val repo = BookingRepository()
                    val rMngr = RoomManager()
                    repo.register(
                        Accommodation(
                            HOTEL_KRONE,
                            rMngr.setPricePerNight(Room(2, KING, HAIRDRYER, BALCONY), price)
                        )
                    )
                    repo.findRooms(
                        2,
                        "Munich",
                        LocalDate.of(2026, 1, 6),
                        LocalDate.of(2026, 1, 6).plusDays(days.toLong())
                    ).forEach { repo.addToShoppingCart(traveler, it) }
                    val booking = repo.getOffersFromShoppingCart(traveler)[0]
                    assertThat(booking.totalCosts, `is`(expected))
                }
            }
        }
        feature("Change Time Of Stay") {
            scenario("Change Time Of Stay Successfully") {
                val repository = BookingRepository()
                lateinit var traveler: Traveler
                lateinit var offer: Booking
                given("a registered traveler") {
                    traveler = newTraveler()
                }
                given("some offers in the shopping cart") {
                    repository.register(Accommodation(HOTEL_KRONE, Room(2, KING, HAIRDRYER, BALCONY)))
                    repository.findRooms(2, "Munich", LocalDate.of(2026, 1, 6), LocalDate.of(2026, 1, 7))
                        .forEach { repository.addToShoppingCart(traveler, it) }
                }
                `when`("selecting an offer") {
                    offer = repository.getOffersFromShoppingCart(traveler)[0]
                }
                `when`("the time of stay is changed") {
                    repository.changeTimeOfStay(traveler, offer, LocalDate.of(2026, 1, 8), LocalDate.of(2026, 1, 9))
                }
                then("the time of stay should be changed in the shopping cart") {
                    assertThat(offer.start, `is`(LocalDate.of(2026, 1, 8)))
                    assertThat(offer.end, `is`(LocalDate.of(2026, 1, 9)))
                }
            }
            scenario("Change Time Of Stay Not Possible If Room Not Available") {
                val repository = BookingRepository()
                lateinit var traveler: Traveler
                lateinit var offer: Booking
                lateinit var exc: Exception
                given("a registered traveler") {
                    traveler = newTraveler()
                }
                given("some offers in the shopping cart") {
                    repository.register(Accommodation(HOTEL_KRONE, Room(2, KING, HAIRDRYER, BALCONY)))
                    repository.findRooms(2, "Munich", LocalDate.of(2026, 1, 6), LocalDate.of(2026, 1, 7))
                        .forEach { repository.addToShoppingCart(traveler, it) }
                }
                given("the room is booked at other times") {
                    repository.findRooms(2, "Munich", LocalDate.of(2026, 1, 8), LocalDate.of(2026, 1, 9))
                        .forEach { repository.addToShoppingCart(traveler, it) }
                }
                `when`("selecting an offer") {
                    offer = repository.getOffersFromShoppingCart(traveler)[0]
                }
                `when`("the time of stay is changed to a time when the room is already booked") {
                    try {
                        repository.changeTimeOfStay(traveler, offer, LocalDate.of(2026, 1, 8), LocalDate.of(2026, 1, 9))
                    } catch (e: Exception) { exc = e }
                }
                then("the change is rejected") {
                    assertThat(exc.message, `is`("Cannot change time of stay"))
                }
                then("the offer remains unchanged") {
                    assertThat(offer.start, `is`(LocalDate.of(2026, 1, 6)))
                    assertThat(offer.end, `is`(LocalDate.of(2026, 1, 7)))
                }
            }
        }
        scenario("Check Out An Offer") {
            doc(seeAlso = listOf(BookingProcessSpec::class))
            val repository = BookingRepository()
            lateinit var traveler: Traveler
            lateinit var offer: Booking
            given("a registered traveler") {
                traveler = newTraveler()
            }
            given("some offers in the shopping cart") {
                repository.register(Accommodation(HOTEL_KRONE, Room(2, KING, HAIRDRYER, BALCONY)))
                repository.findRooms(2, "Munich", LocalDate.of(2026, 1, 6), LocalDate.of(2026, 1, 7))
                    .forEach { repository.addToShoppingCart(traveler, it) }
            }
            `when`("selecting an offer") {
                offer = repository.getOffersFromShoppingCart(traveler)[0]
            }
            `when`("check it out") {
                repository.checkOutFromShoppingCart(traveler, offer)
            }
            then("the offer is checked out") {
                assertThat(offer.state, `is`(BookingState.IN_CHECK_OUT))
            }
            then("the shopping cart should not contain the offer anymore") {
                assertThat(repository.getOffersFromShoppingCart(traveler).contains(offer), `is`(false))
            }
        }
        scenario("Remove Offer From Shopping Cart") {
            val repository = BookingRepository()
            lateinit var traveler: Traveler
            lateinit var offer: Booking
            given("a registered traveler") {
                traveler = newTraveler()
            }
            given("some offers in the shopping cart") {
                repository.register(Accommodation(HOTEL_KRONE, Room(2, KING, HAIRDRYER, BALCONY)))
                repository.findRooms(2, "Munich", LocalDate.of(2026, 1, 6), LocalDate.of(2026, 1, 7))
                    .forEach { repository.addToShoppingCart(traveler, it) }
            }
            `when`("selecting an offer") {
                offer = repository.getOffersFromShoppingCart(traveler)[0]
            }
            `when`("remove from shopping cart") {
                repository.removeOfferFromShoppingCart(traveler, offer)
            }
            then("the shopping cart should not contain the offer anymore") {
                assertThat(repository.getOffersFromShoppingCart(traveler).contains(offer), `is`(false))
            }
        }
    }
})