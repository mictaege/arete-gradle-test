package com.github.mictaege.travel_agency_ktx

import com.github.mictaege.arete.ScreenshotExtension
import com.github.mictaege.arete_ktx.Spec
import com.github.mictaege.travel_agency.*
import com.github.mictaege.travel_agency.Bed.KING
import com.github.mictaege.travel_agency.Facilities.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.MediaType
import org.hamcrest.Matchers.contains
import org.hamcrest.Matchers.`is`
import java.time.LocalDate

object Phase {
    const val BOOKING: String = "Room booking"
    const val COMPLAINING: String = "Traveller complains"
    const val MEDIATION: String = "Mediation"
}

object Variant {
    const val JUSTIFIED: String = "Justified Complaint"
    const val UNJUSTIFIED: String = "Unjustified Complaint"
}

class ComplaintMediationSpec : Spec({
    spec("Complaint Mediation Spec") {
        doc(
            narrative = """
                > As a *traveler* I want to be able to *fill out complaints* in case of *issues with the accommodation*, so that I can *get a refund*.
                >
                > As an *accommodation*, I only want to *give refunds* if complaints are justified to *avoid scams*.
                >
                > As a *travel agency*, I want to *process complaints* efficiently, so that I can *resolve issues* and *maintain both, customer and accommodation trust*.
            """.trimIndent(),
            tags = listOf(EntityOffer::class, FlowMediation::class)
        )
        registerExtension(ScreenshotExtension(UiDummyScreenshotTaker()))
        beforeAll {
            attachToNarrative("spec-setup.txt", MediaType.TEXT_PLAIN, "spec setup".toByteArray())
        }
        afterAll {
            attachToNarrative("spec-teardown.txt", MediaType.TEXT_PLAIN, "spec teardown".toByteArray())
        }
        variableJourney("Complaint Mediation") {
            doc(
                narrative = "The agency mediates complaints between traveller and accommodation",
                plantUml = listOf("""
                    @startuml
                    start
                    :Accommodation offers Rooms;
                    :Traveller searches Rooms;
                    :Traveller books Room;
                    :Traveller arrives;
                    :Traveller complains;
                    :Agency proofs complaint;
                    if (Justified?) then (Yes)
                        :Accommodation told to refund;
                        :Accommodation pays refund;
                    else (No)
                        :Traveller is told about rejection;
                    endif
                    :Complaint is closed;
                    stop
                    @enduml
                """.trimIndent()),
                tags = listOf(ActorTraveller::class, ActorAccommodation::class, ActorAgency::class)
            )
            lateinit var repository: BookingRepository
            lateinit var traveler: Traveler
            lateinit var accommodation: Accommodation
            lateinit var booking: Booking
            lateinit var complaint: Complaint
            beforeVariant {
                repository = BookingRepository()
                traveler = newTraveler()
                accommodation = Accommodation(
                    """
                    Hotel Krone
                    Schlossallee 123
                    80539 Munich
                    DE
                    """.trimIndent(),
                    Room(2, KING, HAIRDRYER, BALCONY, SLIPPERS)
                )
            }
            afterVariant {
                attachToNarrative("variant-done.txt", MediaType.TEXT_PLAIN, "variant teardown".toByteArray())
            }
            step("an accommodation offers some rooms", phase = Phase.BOOKING) {
                repository.register(accommodation)
            }
            step("A traveller searches for a room for 2 people in Munich", phase = Phase.BOOKING) {
                val offers = repository.findRooms(2, "Munich", LocalDate.of(2026, 6, 5), LocalDate.of(2026, 6, 7))
                offers.forEach { repository.addToShoppingCart(traveler, it) }
                booking = repository.getOffersFromShoppingCart(traveler)[0]
            }
            step("the traveller books an offered room", phase = Phase.BOOKING) {
                repository.initPayment(traveler, booking, PaymentMethod.PAYPAL)
            }
            step("after arrival the traveller complains about a missing facility that has been offered", phase = Phase.COMPLAINING, variant = Variant.JUSTIFIED) {
                complaint = repository.complain(booking, HAIRDRYER)
            }
            step("after arrival the traveller complains about a missing facility that has not been offered", phase = Phase.COMPLAINING, variant = Variant.UNJUSTIFIED) {
                complaint = repository.complain(booking, COFFEE_MAKER)
            }
            step("then the accommodation is told to refund the traveller", phase = Phase.MEDIATION, variant = Variant.JUSTIFIED) {
                assertThat(accommodation.complaints, contains(complaint))
            }
            step("then the traveller is told that his complaint has been rejected", phase = Phase.MEDIATION, variant = Variant.UNJUSTIFIED) {
                assertThat(traveler.complaints, contains(complaint))
            }
            step("the accommodation is paying the refund to the traveller", phase = Phase.MEDIATION, variant = Variant.JUSTIFIED) {
                complaint = repository.refund(complaint)
                assertThat(complaint.isRefundPaid, `is`(true))
            }
            step("finally the case is closed", phase = Phase.MEDIATION) {
                assertThat(complaint.isClosed, `is`(true))
            }
        }
        journey("Refund Not Paid") {
            doc(
                narrative = "How to resolve a refund not paid complaint",
                tags = listOf(ActorTraveller::class, ActorAccommodation::class, ActorAgency::class)
            )
            beforeEach {
                attachToStep("before-step.txt", MediaType.TEXT_PLAIN, "before".toByteArray())
            }
            afterEach {
                attachToStep("after-step.txt", MediaType.TEXT_PLAIN, "after".toByteArray())
            }
            val repository: BookingRepository = BookingRepository()
            val traveler = newTraveler()
            val accommodation= Accommodation(
                """
                Hotel Krone
                Schlossallee 123
                80539 Munich
                DE
                """.trimIndent(),
                Room(2, KING, HAIRDRYER, BALCONY, SLIPPERS)
            )
            repository.register(accommodation)
            val offer = Offer(accommodation, accommodation.rooms[0], LocalDate.of(2026, 6, 5), LocalDate.of(2026, 6, 7))
            val booking = Booking(traveler, offer)
            val complaint = Complaint(booking, HAIRDRYER)

            step("an accommodation is told to pay a refund") {
                assertThat(complaint.isJustified, `is`(true))
            }
            step("The traveler has not received a refund after waiting for 2 weeks") {
                assertThat(complaint.isRefundPaid, `is`(false))
            }
            step("the traveler complains about the lack of a refund") {
                repository.requestMitigation(complaint)
            }
            step("the agency pays the refund on behalf of the accommodation") {
                assertThat(complaint.isRefundPaid, `is`(true))
            }
            step("the agency reprimands the accommodation") {
                assertThat(accommodation.isReprimanded, `is`(true))
            }
        }
    }
})
