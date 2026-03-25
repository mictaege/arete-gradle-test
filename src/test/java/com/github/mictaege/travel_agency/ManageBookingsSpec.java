package com.github.mictaege.travel_agency;

import static com.github.mictaege.travel_agency.Bed.KING;
import static com.github.mictaege.travel_agency.Facilities.BALCONY;
import static com.github.mictaege.travel_agency.Facilities.HAIRDRYER;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.github.mictaege.arete.ExampleCsv;
import com.github.mictaege.arete.Given;
import com.github.mictaege.arete.Narrative;
import com.github.mictaege.arete.Scenario;
import com.github.mictaege.arete.ScreenshotExtension;
import com.github.mictaege.arete.Spec;
import com.github.mictaege.arete.Then;
import com.github.mictaege.arete.When;


@Spec
@Narrative(
        value =
                """
                > As a *traveler* I want to *list my bookings* so that I can *see my upcoming bookings*
                > - and I want to *list my booking history* so that I can *track my past bookings*
                > - and I want to *cancel an upcoming booking* if I *don't need the room anymore*.
                """,
        plantUml =
                """
                @startuml
                
                left to right direction
                
                :Traveler:
                Traveler --> (List upcoming bookings)
                Traveler --> (List booking history)
                Traveler --> (Cancel upcoming booking)
                @enduml
                """
)
@ActorTraveller @ActorAccommodation @EntityBooking
class ManageBookingsSpec {

    @RegisterExtension
    public ScreenshotExtension screenshots = new ScreenshotExtension(new UiDummyScreenshotTaker());

    private static final BookingRepository repository = new BookingRepository();

    private static Traveler traveler;
    private static Booking historicBooking_0;
    private static Booking historicBooking_1;
    private static Booking upcomingBooking_0;
    private static Booking upcomingBooking_1;

    @BeforeAll
    static void context() {
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
        repository.register(new Accommodation(
                """
                Hotel Krone
                Schlossallee 123
                80539 Munich
                DE
                """,
                new Room(2, KING, HAIRDRYER, BALCONY)
        ));
        var offers = repository.findRooms(2, "Munich", LocalDate.now().minusDays(14), LocalDate.now().minusDays(12));
        offers.forEach(o -> repository.addToShoppingCart(traveler, o));
        historicBooking_0 = repository.getOffersFromShoppingCart(traveler).get(0);
        repository.checkOutFromShoppingCart(traveler, historicBooking_0);
        repository.initPayment(traveler, historicBooking_0, PaymentMethod.PAYPAL);

        offers = repository.findRooms(2, "Munich", LocalDate.now().minusDays(8), LocalDate.now().minusDays(6));
        offers.forEach(o -> repository.addToShoppingCart(traveler, o));
        historicBooking_1 = repository.getOffersFromShoppingCart(traveler).get(0);
        repository.checkOutFromShoppingCart(traveler, historicBooking_1);
        repository.initPayment(traveler, historicBooking_1, PaymentMethod.PAYPAL);

        offers = repository.findRooms(2, "Munich", LocalDate.now().plusDays(8), LocalDate.now().plusDays(10));
        offers.forEach(o -> repository.addToShoppingCart(traveler, o));
        upcomingBooking_0 = repository.getOffersFromShoppingCart(traveler).get(0);
        repository.checkOutFromShoppingCart(traveler, upcomingBooking_0);
        repository.initPayment(traveler, upcomingBooking_0, PaymentMethod.PAYPAL);

        offers = repository.findRooms(2, "Munich", LocalDate.now().plusDays(12), LocalDate.now().plusDays(14));
        offers.forEach(o -> repository.addToShoppingCart(traveler, o));
        upcomingBooking_1 = repository.getOffersFromShoppingCart(traveler).get(0);
        repository.checkOutFromShoppingCart(traveler, upcomingBooking_1);
        repository.initPayment(traveler, upcomingBooking_1, PaymentMethod.PAYPAL);
    }

    @Scenario(1)
    class ListUpcomingBookings {

        private List<Booking> bookings;

        @When
        void travelerListsUpcomingBookings() {
            bookings = repository.listUpcomingBookings(traveler);
        }

        @Then
        void allUpcomingBookingsAreShown() {
            assertThat(bookings, hasSize(2));
            assertThat(bookings, containsInAnyOrder(upcomingBooking_0, upcomingBooking_1));
        }

    }

    @Scenario(2)
    class ListBookingHistory {

        private List<Booking> bookings;

        @When
        void travelerListsBookingHistory() {
            bookings = repository.listBookingHistory(traveler);
        }

        @Then
        void allPastBookingsAreShown() {
            assertThat(bookings, hasSize(2));
            assertThat(bookings, containsInAnyOrder(historicBooking_0, historicBooking_1));
        }

    }

    @Scenario(3)
    class CancelUpcomingBooking {

        private Traveler otherTraveler;
        private Booking booking;

        @Given
        void isABookingForWhichCancellationIsStillPossible() {
            otherTraveler = new Traveler(
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
            var offers = repository.findRooms(2, "Munich", LocalDate.now().plusDays(22), LocalDate.now().plusDays(24));
            offers.forEach(o -> repository.addToShoppingCart(otherTraveler, o));
            booking = repository.getOffersFromShoppingCart(otherTraveler).get(0);
            repository.checkOutFromShoppingCart(otherTraveler, booking);
            repository.initPayment(otherTraveler, booking, PaymentMethod.PAYPAL);
        }

        @When
        void travelerCancelsTheBooking() {
            repository.cancelBooking(otherTraveler, booking);
        }

        @Then
        void theCancellationWasSuccessful() {
            assertThat(booking.getState(), is(BookingState.CANCELLED));
        }

        @ExampleCsv(
                desc = "Examples: Cancellation at different times before start",
                columns = {"Start of stay in x days", "Cancellation possible x days before start", "Cancellation possible?"},
                delimiter = '|',
                csvResourcePath = "com/github/mictaege/travel_agency/cancellationAtDifferentTimes.csv"
        )
        void cancellationAtDifferentTimes(final String startOfStay,
                                          final String cancellationPossibleDaysBeforeStart,
                                          final String cancellationPossible) {

            var otherTraveler = new Traveler(
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

            var room = new Room(2, KING, HAIRDRYER, BALCONY);
            room.setDaysBeforeStartCancellationPossible(Integer.parseInt(cancellationPossibleDaysBeforeStart));
            repository.register(new Accommodation(
                    """
                    Ibis Hotel
                    Alexanderplatz 48
                    10178 Berlin
                    DE
                    """,
                    room
            ));
            final LocalDate start = LocalDate.now().plusDays(Integer.parseInt(startOfStay));
            var offers = repository.findRooms(2, "Berlin", start, start.plusDays(2));
            offers.forEach(o -> repository.addToShoppingCart(otherTraveler, o));
            var booking = repository.getOffersFromShoppingCart(otherTraveler).get(0);
            repository.checkOutFromShoppingCart(otherTraveler, booking);
            repository.initPayment(otherTraveler, booking, PaymentMethod.PAYPAL);

            if (Boolean.parseBoolean(cancellationPossible)) {
                repository.cancelBooking(otherTraveler, booking);
                assertThat(booking.getState(), is(BookingState.CANCELLED));
            } else {
                try {
                    repository.cancelBooking(otherTraveler, booking);
                } catch (Exception e) {
                    assertThat(e.getMessage(), is("Cancellation is no longer possible"));
                }
                assertThat(booking.getState(), is(BookingState.CONFIRMED));
            }
        }

    }


}
