package com.github.mictaege.travel_agency;

import com.github.mictaege.arete.*;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.time.LocalDate;
import java.util.List;

import static com.github.mictaege.travel_agency.Bed.KING;
import static com.github.mictaege.travel_agency.Facilities.BALCONY;
import static com.github.mictaege.travel_agency.Facilities.HAIRDRYER;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


@Spec
@Narrative(
        value =
                """
                > As a *traveler* I want to *select a payment method* so that I can *define which of my accounts will be charged*
                > - and I want that *the transaction is save* so that I could *be sure that my money is not lost*
                > - and I want to *receive a booking confirmation* so that I can *prove the booking has been made*
                > - and I want to *receive an invoice* so that I can *claim the expenses for tax purposes*.
                
                > As an *accommodation* I want that *the transaction is save* so that I could *be sure that I will receive the money*.
                """,
        plantUml = {
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
                """,
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
                """
        },
        attachmentResourcePath = {
                "com/github/mictaege/travel_agency/example-confirmation.pdf",
                "com/github/mictaege/travel_agency/example-invoice.pdf"
        }

)
@ActorTraveller @ActorAccommodation @ExternalPaymentService
@EntityRoom @FlowBooking
class BookingProcessSpec {

    @RegisterExtension
    public ScreenshotExtension screenshots = new ScreenshotExtension(new UiDummyScreenshotTaker());

    @Scenario
    @ActorTraveller
    class SuccessfulBooking {

        private final BookingRepository repository = new BookingRepository();

        private Traveler traveler;
        private Booking offer;
        private List<PaymentMethod> paymentMethods;

        @Given(seq = 1, step = 1)
        void aRegisteredTraveler() {
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

        @When(seq = 1, step = 1)
        void travelerChecksOutAnOffer() {
            repository.register(new Accommodation(
                    """
                    Hotel Krone
                    Schlossallee 123
                    80539 Munich
                    DE
                    """,
                    new Room(2, KING, HAIRDRYER, BALCONY)
            ));
            final var offers = repository.findRooms(2, "Munich", LocalDate.of(2026, 1, 6), LocalDate.of(2026, 1, 7));
            offers.forEach(o -> repository.addToShoppingCart(traveler, o));
            offer = repository.getOffersFromShoppingCart(traveler).get(0);
            paymentMethods = repository.checkOutFromShoppingCart(traveler, offer);
        }

        @Then(seq = 1, step = 1)
        void theAccommodationOffersSomePaymentMethods() {
            assertThat(paymentMethods, is(not(empty())));
        }

        @When(seq = 2, step = 1)
        void travelerSelectsASecurePaymentMethod() {
            repository.initPayment(traveler, offer, PaymentMethod.PAYPAL);
        }

        @Then(seq = 3, step = 1)
        void theBookingIsConfirmed() {
            assertThat(offer.getState(), is(BookingState.CONFIRMED));
        }

        @Then(seq = 3, step = 2)
        void theTravelerReceivesAConfirmation() {
            assertThat(traveler.getConfirmedBookings(), contains(offer));
        }

        @Then(seq = 3, step = 3)
        void theTravelerReceivesAnInvoice() {
            assertThat(traveler.getInvoicedBookings(), contains(offer));
        }

    }

    @Scenario
    @ActorTraveller
    @ActorAccommodation
    class UnsecurePaymentMethod {

        private final BookingRepository repository = new BookingRepository();

        private Traveler traveler;
        private Booking offer;
        private List<PaymentMethod> paymentMethods;
        private Exception invalidPayment;

        @Given(seq = 1, step = 1)
        void aRegisteredTraveler() {
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

        @When(seq = 1, step = 1)
        void travelerChecksOutAnOffer() {
            repository.register(new Accommodation(
                    """
                    Hotel Krone
                    Schlossallee 123
                    80539 Munich
                    DE
                    """,
                    new Room(2, KING, HAIRDRYER, BALCONY)
            ));
            final var offers = repository.findRooms(2, "Munich", LocalDate.of(2026, 1, 6), LocalDate.of(2026, 1, 7));
            offers.forEach(o -> repository.addToShoppingCart(traveler, o));
            offer = repository.getOffersFromShoppingCart(traveler).get(0);
            paymentMethods = repository.checkOutFromShoppingCart(traveler, offer);
        }

        @Then(seq = 1, step = 1)
        void theAccommodationOffersSomePaymentMethods() {
            assertThat(paymentMethods, is(not(empty())));
        }

        @When(seq = 2, step = 1)
        void travelerSelectsAnUnsecurePaymentMethod() {
            try {
                repository.initPayment(traveler, offer, PaymentMethod.INVOICE);
            } catch (final Exception e) {
                invalidPayment = e;
            }

        }

        @Then(seq = 3, step = 1)
        void theTravelerReceivesAnErrorMessage() {
            assertThat(invalidPayment, is(notNullValue()));
            assertThat(invalidPayment.getMessage(), is("Payment method not secure"));
        }

    }

    @Scenario
    @Disabled("Needs refinement")
    class BookingProcessInterrupted {

        @Given(seq = 1, step = 1)
        void aRegisteredTraveler() {
        }

        @When(seq = 1, step = 1)
        void travelerChecksOutAnOffer() {
        }

        @Then(seq = 1, step = 1)
        void theAccommodationOffersSomePaymentMethods() {
        }

        @When(seq = 2, step = 1)
        void travelerSelectsASecurePaymentMethod() {
        }

        @When(seq = 2, step = 2)
        void theBookingIsInterrupted() {
        }

        @Then(seq = 3, step = 1)
        void theTravelerReceivesAnErrorMessage() {
        }

    }
}
