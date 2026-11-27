package com.github.mictaege.travel_agency_ktx

import com.github.mictaege.arete.ScreenshotExtension
import com.github.mictaege.arete_ktx.Spec
import com.github.mictaege.arete_ktx.ref
import com.github.mictaege.travel_agency.*
import com.github.mictaege.travel_agency.Bed.KING
import com.github.mictaege.travel_agency.Facilities.BALCONY
import com.github.mictaege.travel_agency.Facilities.HAIRDRYER
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.MediaType
import java.time.LocalDate

class BookingProcessSpec : Spec({
    spec("Booking Process Spec") {
        doc(
            narrative = """
                > As a *traveler* I want to *select a payment method* so that I can *define which of my accounts will be charged*,
                > - and I want that *the transaction is save* so that I could *be sure that my money is not lost*,
                > - and I want to *receive a booking confirmation* so that I can *prove the booking has been made*,
                > - and I want to *receive an invoice* so that I can *claim the expenses for tax purposes*.
                
                > As an *accommodation* I want that *the transaction is save* so that I could *be sure that I will receive the money*. ,
            """.trimIndent(),
            plantUml = listOf(
                """
                @startuml
                left to right direction
                :Traveler:
                :Accommodation:
                :PaymentService:
                Traveler --> (Book Room)
                Traveler --> (Select Payment Method)
                Accommodation --> (Offer Payment Methods)
                (Book Room) .> (Select Payment Method) : <<include>>
                PaymentService --> (Ensure Secure Transaction)
                (Offer Payment Methods) .> (Ensure Secure Transaction) : <<include>>
                (Select Payment Method) .> (Ensure Secure Transaction) : <<include>>
                Traveler --> (Receive Booking Confirmation)
                Traveler --> (Receive Invoice)
                @enduml
                """.trimIndent(),
                """
                @startuml
                start
                :Traveler inits booking;
                :Accommodation offers payment method;
                :Traveler selects payment method;
                partition "Payment Service" {
                :Transaction will be initialized;
                :Transactions security is checked;
                if (Transaction secure?) then (Yes)
                :Confirm booking;
                else (No)
                :Show error message;
                stop
                endif
                }
                :Send booking confirmation;
                :Send invoice;
                stop
                @enduml
                """.trimIndent()
            ),
            attachmentResourcePath = listOf(
                "com/github/mictaege/travel_agency/example-confirmation.pdf",
                "com/github/mictaege/travel_agency/example-invoice.pdf"
            ),
            refs = listOf(
                ref(GithubRef::class, "14"),
                ref("16", url = "https://github.com/mictaege/arete-gradle/issues/", project = "arete-gradle"),
                ref("5", url = "https://github.com/mictaege/arete/issues/", project = "arete"),
                ref("6", url = "https://github.com/mictaege/arete/issues/", project = "arete")
            ),
            tags = listOf(ActorTraveller::class, ActorAccommodation::class, EntityRoom::class, FlowBooking::class,
                ExternalPaymentService::class)
        )
        attachToNarrative(
            "booking-summary.json",
            MediaType.APPLICATION_JSON,
            "{\"bookingProcess\":\"demo narrative attachment\"}".toByteArray(Charsets.UTF_8)
        )
        registerExtension(ScreenshotExtension(UiDummyScreenshotTaker()))
        scenario("Successful Booking") {
            doc(
                narrative = "Successful booking produces confirmation and invoice",
                tags = listOf(ActorTraveller::class)
            )
            attachToNarrative(
                "booking-confirmation.json",
                MediaType.APPLICATION_JSON,
                "{\"bookingConfirmation\":\"demo narrative attachment\"}".toByteArray(Charsets.UTF_8)
            )
            val repository = BookingRepository()
            lateinit var traveler: Traveler
            lateinit var offer: Booking
            lateinit var paymentMethods: List<PaymentMethod>
            given("a registered traveler") {
                traveler = newTraveler()
            }
            `when`("traveler checks out an offer") {
                repository.register(Accommodation(
                    """
                    Hotel Krone
                    Schlossallee 123
                    80539 Munich
                    DE
                    """.trimIndent(),
                    Room(2, KING, HAIRDRYER, BALCONY)
                ))
                val offers = repository.findRooms(2, "Munich", LocalDate.of(2026, 1, 6), LocalDate.of(2026, 1, 7))
                offers.forEach { repository.addToShoppingCart(traveler, it) }
                offer = repository.getOffersFromShoppingCart(traveler)[0]
                paymentMethods = repository.checkOutFromShoppingCart(traveler, offer)
            }
            then("the accommodation offers some payment methods") {
                assertThat(paymentMethods, `is`(not(empty())))
            }
            `when`("traveler selects a secure payment method") {
                repository.initPayment(traveler, offer, PaymentMethod.PAYPAL)
            }
            then("the booking is confirmed") {
                assertThat(offer.state, `is`(BookingState.CONFIRMED))
            }
            then("the traveler receives a confirmation") {
                assertThat(traveler.confirmedBookings, contains(offer))
            }
            then("the traveler receives an invoice") {
                assertThat(traveler.invoicedBookings, contains(offer))
                attachToStep(
                    "invoice.txt",
                    MediaType.TEXT_PLAIN,
                    "Invoice for booking ${offer.id}".toByteArray(Charsets.UTF_8)
                )
            }
        }
        scenario("Booking Process Interrupted") {
            disabled("Needs refinement")
            given("a registered traveler") { }
            `when`("traveler checks out an offer") { }
            then("the accommodation offers some payment methods") { }
            `when`("traveler selects a secure payment method") { }
            `when`("the booking is interrupted") { }
            then("the traveler receives an error message") { }
        }
        scenario("Unsecure Payment Method") {
            doc(
                tags = listOf(ActorAccommodation::class, ActorTraveller::class)
            )
            val repository = BookingRepository()
            lateinit var traveler: Traveler
            lateinit var offer: Booking
            lateinit var paymentMethods: List<PaymentMethod>
            lateinit var invalidPayment: Exception
            given("a registered traveler") { traveler = newTraveler() }
            `when`("traveler checks out an offer") {
                repository.register(Accommodation(
                    """
                    Hotel Krone
                    Schlossallee 123
                    80539 Munich
                    DE
                    """.trimIndent(),
                    Room(2, KING, HAIRDRYER, BALCONY)
                ))
                val offers = repository.findRooms(2, "Munich", LocalDate.of(2026, 1, 6), LocalDate.of(2026, 1, 7))
                offers.forEach { repository.addToShoppingCart(traveler, it) }
                offer = repository.getOffersFromShoppingCart(traveler)[0]
                paymentMethods = repository.checkOutFromShoppingCart(traveler, offer)
            }
            then("the accommodation offers some payment methods") {
                assertThat(paymentMethods, `is`(not(empty())))
            }
            `when`("traveler selects an unsecure payment method") {
                try {
                    repository.initPayment(traveler, offer, PaymentMethod.INVOICE)
                } catch (e: Exception) {
                    invalidPayment = e
                }
            }
            then("the traveler receives an error message") {
                assertThat(invalidPayment, `is`(notNullValue()))
                assertThat(invalidPayment.message, `is`("Payment method not secure"))
            }
        }
    }
})