package com.github.mictaege.travel_agency;

import static com.github.mictaege.travel_agency.Bed.KING;
import static com.github.mictaege.travel_agency.Facilities.BALCONY;
import static com.github.mictaege.travel_agency.Facilities.COFFEE_MAKER;
import static com.github.mictaege.travel_agency.Facilities.HAIRDRYER;
import static com.github.mictaege.travel_agency.Facilities.SLIPPERS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

import java.time.LocalDate;

import com.github.mictaege.arete.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.RegisterExtension;

@Spec
@Narrative(
    value =
        """
        > As a *traveler* I want to be able to *fill out complaints* in case of *issues with the accommodation*, so that I can *get a refund*.
        >
        > As an *accommodation*, I only want to *give refunds* if complaints are justified to *avoid scams*.
        >
        > As a *travel agency*, I want to *process complaints* efficiently, so that I can *resolve issues* and *maintain both, customer and accommodation trust*.
        """
)
@EntityOffer @FlowMediation
class ComplaintMediationSpec {

    @RegisterExtension
    public ScreenshotExtension screenshots = new ScreenshotExtension(new UiDummyScreenshotTaker());

    @VariableJourney
    @Narrative(
            value = "The agency mediates complaints between traveller and accommodation",
            plantUml =
                """
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
                """
    )
    @ActorTraveller @ActorAccommodation @ActorAgency
    class ComplaintMediation {

        class Phase {
            static final String BOOKING = "Room booking";
            static final String COMPLAINING = "Traveller complains";
            static final String MEDIATION = "Mediation";
        }

        class Variant {
            static final String JUSTIFIED = "Justified Complaint";
            static final String UNJUSTIFIED = "Unjustified Complaint";
        }

        private final BookingRepository repository = new BookingRepository();

        private Traveler traveler;
        private Accommodation accommodation;
        private Booking booking;
        private Complaint complaint;

        @BeforeVariant
        void context() {
            traveler = new Traveler(
                    "Max",
                    "Mustermann",
                    "max-mustermann@gnogle.com",
                    new Address(
                            """
                                Hauptstrasse 8"
                                76131 Karlsruhe
                                DE
                                """
                    )
            );
        }

        @AfterVariant
        void tearDown() {
            traveler = null;
        }

        @Step(value = 1, phase = Phase.BOOKING)
        void anAccommodationOffersSomeRooms() {
            accommodation = new Accommodation(
                    """
                    Hotel Krone
                    Schlossallee 123
                    80539 Munich
                    DE
                    """,
                    new Room(2, KING, HAIRDRYER, BALCONY, SLIPPERS)
            );
            repository.register(accommodation);
        }

        @Step(value = 2, phase = Phase.BOOKING, desc = "A traveller searches for a room for 2 people in Munich")
        void aTravellerSearchesForARoom() {
            final var offers = repository.findRooms(2, "Munich", LocalDate.of(2026, 6, 5), LocalDate.of(2026, 6, 7));
            offers.forEach(o -> repository.addToShoppingCart(traveler, o));
            booking = repository.getOffersFromShoppingCart(traveler).get(0);
        }

        @Step(value = 3, phase = Phase.BOOKING)
        void theTravellerBooksAnOfferedRoom() {
            repository.initPayment(traveler, booking, PaymentMethod.PAYPAL);
        }

        @Step(value = 4, phase = Phase.COMPLAINING, variant = Variant.JUSTIFIED)
        void afterArrivalTheTravellerComplainsAboutAMissingFacilityThatHasBeenOffered() {
            complaint = repository.complain(booking, HAIRDRYER);
        }

        @Step(value = 4, phase = Phase.COMPLAINING, variant = Variant.UNJUSTIFIED)
        void afterArrivalTheTravellerComplainsAboutAMissingFacilityThatHasNotBeenOffered() {
            complaint = repository.complain(booking, COFFEE_MAKER);
        }

        @Step(value = 5, phase = Phase.MEDIATION, variant = Variant.JUSTIFIED)
        void thenTheAccommodationIsToldToRefundTheTraveller() {
            assertThat(accommodation.getComplaints(), contains(complaint));
        }

        @Step(value = 5, phase = Phase.MEDIATION, variant = Variant.UNJUSTIFIED)
        void thenTheTravellerIsToldThatHisComplaintHasBeenRejected() {
            assertThat(traveler.getComplaints(), contains(complaint));
        }

        @Step(value = 6, phase = Phase.MEDIATION, variant = Variant.JUSTIFIED)
        void theAccommodationIsPayingTheRefundToTheTraveller() {
            complaint = repository.refund(complaint);
            assertThat(complaint.isRefundPaid(), is(true));
        }

        @Step(value = 7, phase = Phase.MEDIATION, variant = {Variant.JUSTIFIED, Variant.UNJUSTIFIED})
        void finallyTheCaseIsClosed() {
            assertThat(complaint.isClosed(), is(true));
        }

    }

    @Journey
    @ActorTraveller @ActorAccommodation @ActorAgency
    class RefundNotPaid {

        private final BookingRepository repository = new BookingRepository();

        private Traveler traveler;
        private Accommodation accommodation;
        private Booking booking;
        private Complaint complaint;

        @BeforeAll
        void context() {
            traveler = new Traveler(
                    "Max",
                    "Mustermann",
                    "max-mustermann@gnogle.com",
                    new Address(
                            """
                                Hauptstrasse 8"
                                76131 Karlsruhe
                                DE
                                """
                    )
            );
            final var room = new Room(2, KING, HAIRDRYER, BALCONY, SLIPPERS);
            accommodation = new Accommodation(
                    """
                    Hotel Krone
                    Schlossallee 123
                    80539 Munich
                    DE
                    """,
                    room
            );
            repository.register(accommodation);
            final var offer = new Offer(accommodation, room, LocalDate.of(2026, 6, 5), LocalDate.of(2026, 6, 7));
            booking = new Booking(traveler, offer);
            complaint = new Complaint(booking, HAIRDRYER);
        }

        @Step(1)
        void anAccommodationIsToldToPayARefund() {
            assertThat(complaint.isJustified(), is(true));
        }

        @Step(value = 2, desc = "The traveler has not received a refund after waiting for 2 weeks")
        void afterEnoughTimeTheTravelerHasNotReceivedAnyRefund() {
            assertThat(complaint.isRefundPaid(), is(false));
        }

        @Step(3)
        void theTravelerComplainsAboutTheLackOfARefund() {
            repository.requestMitigation(complaint);
        }

        @Step(4)
        void theAgencyPaysTheRefundOnBehalfOfTheAccommodation() {
            System.out.println("Agency </code></pre><script>pays the refund</script>");
            assertThat(complaint.isRefundPaid(), is(true));
        }

        @Step(5)
        void theAgencyReprimandsTheAccommodation() {
            assertThat(accommodation.isReprimanded(), is(true));
        }

    }

}
