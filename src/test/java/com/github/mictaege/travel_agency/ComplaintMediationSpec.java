package com.github.mictaege.travel_agency;

import com.github.mictaege.arete.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.time.LocalDate;

import static com.github.mictaege.travel_agency.Bed.KING;
import static com.github.mictaege.travel_agency.Facilities.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

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

        static final String JUSTIFIED_COMPLAINT = "Justified Complaint";
        static final String UNJUSTIFIED_COMPLAINT = "Unjustified Complaint";

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
        }

        @Step(1)
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

        @Step(2)
        void aTravellerSearchesForARoom() {
            final var offers = repository.findRooms(2, "Munich", LocalDate.of(2026, 6, 5), LocalDate.of(2026, 6, 7));
            offers.forEach(o -> repository.addToShoppingCart(traveler, o));
            booking = repository.getOffersFromShoppingCart(traveler).get(0);
        }

        @Step(3)
        void theTravellerBooksAnOfferedRoom() {
            repository.initPayment(traveler, booking, PaymentMethod.PAYPAL);
        }

        @Step(value = 4, variant = JUSTIFIED_COMPLAINT)
        void afterArrivalTheTravellerComplainsAboutAMissingFacilityThatHasBeenOffered() {
            complaint = repository.complain(booking, HAIRDRYER);
        }

        @Step(value = 4, variant = UNJUSTIFIED_COMPLAINT)
        void afterArrivalTheTravellerComplainsAboutAMissingFacilityThatHasNotBeenOffered() {
            complaint = repository.complain(booking, COFFEE_MAKER);
        }

        @Step(value = 5, variant = JUSTIFIED_COMPLAINT)
        void thenTheAccommodationIsToldToRefundTheTraveller() {
            assertThat(accommodation.getComplaints(), contains(complaint));
        }

        @Step(value = 5, variant = UNJUSTIFIED_COMPLAINT)
        void thenTheTravellerIsToldThatHisComplaintHasBeenRejected() {
            assertThat(traveler.getComplaints(), contains(complaint));
        }

        @Step(value = 6, variant = JUSTIFIED_COMPLAINT)
        void theAccommodationIsPayingTheRefundToTheTraveller() {
            complaint = repository.refund(complaint);
            assertThat(complaint.isRefundPaid(), is(true));
        }


        @Step(7)
        void finallyTheCaseIsClosed() {
            assertThat(complaint.isClosed(), is(true));
        }

    }

    @Journey
    @ActorTraveller @ActorAccommodation @ActorAgency
    class RefundNotPaid {

        static final String JUSTIFIED_COMPLAINT = "Justified Complaint";
        static final String UNJUSTIFIED_COMPLAINT = "Unjustified Complaint";

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

        @Step(2)
        void afterEnoughTimeTheTravelerHasNotReceivedAnyRefund() {
            assertThat(complaint.isRefundPaid(), is(false));
        }

        @Step(3)
        void theTravelerComplainsAboutTheLackOfARefund() {
            repository.requestMitigation(complaint);
        }

        @Step(4)
        void theAgencyPaysTheRefundOnBehalfOfTheAccommodation() {
            System.out.println("Agency pays the refund </code></pre><script>alert(\"boom\")</script>");
            assertThat(complaint.isRefundPaid(), is(true));
        }

        @Step(5)
        void theAgencyReprimandsTheAccommodation() {
            assertThat(accommodation.isReprimanded(), is(true));
        }

    }

}
