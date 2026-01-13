package com.github.mictaege.travel_agency;

import static com.github.mictaege.travel_agency.Bed.DOUBLE;
import static com.github.mictaege.travel_agency.Bed.KING;
import static com.github.mictaege.travel_agency.Bed.QUEEN;
import static com.github.mictaege.travel_agency.Bed.SINGLE;
import static com.github.mictaege.travel_agency.Facilities.BALCONY;
import static com.github.mictaege.travel_agency.Facilities.BATHROBE;
import static com.github.mictaege.travel_agency.Facilities.HAIRDRYER;
import static com.github.mictaege.travel_agency.Facilities.SHOWER;
import static com.github.mictaege.travel_agency.Facilities.TELEVISION;
import static com.github.mictaege.travel_agency.Facilities.WIFI;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.api.extension.RegisterExtension;

import com.github.mictaege.arete.ExampleGrid;
import com.github.mictaege.arete.ExampleGridSource;
import com.github.mictaege.arete.Feature;
import com.github.mictaege.arete.Given;
import com.github.mictaege.arete.Narrative;
import com.github.mictaege.arete.Scenario;
import com.github.mictaege.arete.ScreenshotExtension;
import com.github.mictaege.arete.SeeAlso;
import com.github.mictaege.arete.Spec;
import com.github.mictaege.arete.Then;
import com.github.mictaege.arete.When;

@Spec
@Narrative(
        value =
            """
            > As a *traveler* I want to *find available rooms* for my trip *so that I can book a room for my stay*.
            """,
        plantUml = {
                """
                @startuml
                :Traveler:
                Traveler --> (Find available rooms)
                Traveler --> (Select best offer from list)
                Traveler --> (Add to shopping cart)
                @enduml
                """,
                """
                @startuml
                start
                repeat :Insert search criteria;
                    :Review provided offers;
                backward:Refine search criteria;
                repeat while (one offered room acceptable) is (no)
                -> yes;
                :Select room;
                :Add to shopping cart;
                stop
                @enduml
                """,
                """
                @startsalt
                {
                    scale 16
                    {
                        ^2 Pers.^ | "City, Postalcode, ..." | ^2026-01-06^ | ^2026-01-18^ | [Search <&magnifying-glass>]
                    }
                    {
                        [] Shower | [] Bathrobe | [X] Hairdryer | [] Television | [X] WiFi
                        [] Minibar | [X] Safe | [X] Balcony | [] Iron | [X] Desk
                    }
                    {T#
                        Persons | City | Accommodation | Address | Bed | Details | Book
                        2 | Munich | Hotel Krone | Schlossallee 123 |Double Bed | <&external-link> | <&task>
                        4 | Munich | Parkhotel | Wittelbacherstr. 48 |2 King-Size | <&external-link> |
                    }
                    {
                        [Book <&basket>]
                    }
                }
                @endsalt
                """
        }

)
@ActorTraveller @EntityOffer @EntityRoom
class FindAndSelectRoomsSpec {

    @RegisterExtension
    public ScreenshotExtension screenshots = new ScreenshotExtension(new UiDummyScreenshotTaker());

    @Feature(1)
    @Narrative(
            """
            *Rooms* could be find by searching for accommodations
            - in a *city*
            - filtering by
                - *availability*
                - *size*
                - and *facilities*
            """
    )
    class FindAvailableRooms {

        @Scenario
        class FindAvailableRoomsByCity {

            private final BookingRepository repository = new BookingRepository();

            private List<Offer> offers;

            @Given
            void someAvailableRoomsInMunich() {
                repository.register(new Accommodation(
                        """
                        Hotel Krone
                        Schlossallee 123
                        80539 Munich
                        DE
                        """,
                        new Room(1, SINGLE),
                        new Room(2, DOUBLE)
                ));
            }

            @When
            void travelerLooksForAvailableRoomsInMunich() {
                offers = repository.findRooms(2, "Munich", LocalDate.of(2026, 1, 6), LocalDate.of(2026, 1, 7));
            }

            @Then
            void heShouldReceiveOffersForTheAvailableRoomsInMunich() {
                assertThat(offers.size(), is(1));
                assertThat(offers.get(0).getAccommodation().getName(), is("Hotel Krone"));
                assertThat(offers.get(0).getRoom().getBed(), is(DOUBLE));
            }

        }

        @Scenario
        class DoNotListRoomsInOtherCities {

            private final BookingRepository repository = new BookingRepository();

            private List<Offer> offers;

            @Given
            void roomsOnlyAvailableInBerlin() {
                repository.register(new Accommodation(
                        """
                        Ibis Hotel
                        Alexanderplatz 48
                        10178 Berlin
                        DE
                        """,
                        new Room(2, QUEEN),
                        new Room(2, KING)
                ));
            }

            @When
            void travelerLooksForAvailableRoomsInMunich() {
                offers = repository.findRooms(2, "Munich", LocalDate.of(2026, 1, 6), LocalDate.of(2026, 1, 7));
            }

            @Then
            void heShouldReceiveNoOffersForRoomsInBerlin() {
                assertThat(offers.isEmpty(), is(true));
            }

        }

        @Scenario
        class DoNotListRoomsThatAreToSmall {

            private final BookingRepository repository = new BookingRepository();

            private List<Offer> offers;

            @Given
            void someAvailableRoomsInMunichForOneOrTwoPersons() {
                repository.register(new Accommodation(
                        """
                        Hotel Krone
                        Schlossallee 123
                        80539 Munich
                        DE
                        """,
                        new Room(1, SINGLE),
                        new Room(2, DOUBLE)
                ));
            }

            @When
            void travelerLooksForAvailableRoomsForFivePersonsInMunich() {
                offers = repository.findRooms(5, "Munich", LocalDate.of(2026, 1, 6), LocalDate.of(2026, 1, 7));
            }

            @Then
            void heShouldReceiveNoOffersForSmallerRooms() {
                assertThat(offers.isEmpty(), is(true));
            }

        }



        @Scenario
        class DoNotListRoomsWithMissingFacilities {

            private final BookingRepository repository = new BookingRepository();

            private List<Offer> offers;

            @Given
            void someAvailableRoomsInMunichWithoutBalcony() {
                repository.register(new Accommodation(
                        """
                        Hotel Krone
                        Schlossallee 123
                        80539 Munich
                        DE
                        """,
                        new Room(2, KING, SHOWER, BATHROBE, HAIRDRYER, TELEVISION, WIFI),
                        new Room(2, QUEEN, SHOWER, BATHROBE,HAIRDRYER, TELEVISION, WIFI)
                ));
            }

            @When
            void travelerLooksForAvailableRoomsWithBalconyInMunich() {
                offers = repository.findRooms(2, "Munich", LocalDate.of(2026, 1, 6), LocalDate.of(2026, 1, 7), BALCONY);
            }

            @Then
            void heShouldReceiveNoOffersForRoomsMissingTheBalcony() {
                assertThat(offers.isEmpty(), is(true));
            }

        }

        @Scenario
        class DoNotListRoomsThatAreAlreadyBooked {

            private final BookingRepository repository = new BookingRepository();

            private List<Offer> offers;

            @Given
            void someAlreadyBookedRoomsInMunich() {
                final Room singleRoom = new Room(1, SINGLE);
                final Room doubleRoom = new Room(2, DOUBLE);
                final Accommodation accommodation = new Accommodation(
                        """
                                Hotel Krone
                                Schlossallee 123
                                80539 Munich
                                DE
                                """,
                        singleRoom,
                        doubleRoom
                );
                repository.register(accommodation);
                final var traveler = new Traveler("Max",
                        "Mustermann",
                        "max-mustermann@gnogle.com",
                        new Address("""
                    Hauptstrasse 8"
                    76131 Karlsruhe
                    DE
                    """));
                final var singleRoomOffer = new Offer(accommodation, singleRoom, LocalDate.of(2026, 1, 3), LocalDate.of(2026, 1, 6));
                repository.addToShoppingCart(traveler, singleRoomOffer);
                final var doubleRoomOffer = new Offer(accommodation, doubleRoom, LocalDate.of(2026, 1, 7), LocalDate.of(2026, 1, 10));
                repository.addToShoppingCart(traveler, doubleRoomOffer);
            }

            @When
            void travelerLooksForAvailableRoomsInMunich() {
                offers = repository.findRooms(1, "Munich", LocalDate.of(2026, 1, 6), LocalDate.of(2026, 1, 7));
            }

            @Then
            void heShouldReceiveNoOffersForAlreadyBookedRooms() {
                assertThat(offers.isEmpty(), is(true));
            }

        @ExampleGrid(
                desc = "Availability for different booking situations and dates:",
                columns = { "Booked Room From", "Booked Room To", "Request From", "Request To", "Room Available?" },
                srcMethod = "availabilityForDifferentDatesSource"
        )
        void availabilityForDifferentDates(final LocalDate bookedFrom,
                                           final LocalDate bookedTo,
                                           final LocalDate requestFrom,
                                           final LocalDate requestTo,
                                           final boolean available) {
            final BookingRepository repository = new BookingRepository();
            final Room doubleRoom = new Room(2, DOUBLE);
            final Accommodation accommodation = new Accommodation(
                    """
                            Hotel Krone
                            Schlossallee 123
                            80539 Munich
                            DE
                            """,
                    doubleRoom
            );
            repository.register(accommodation);
            final var traveler = new Traveler("Max",
                    "Mustermann",
                    "max-mustermann@gnogle.com",
                    new Address("""
                    Hauptstrasse 8"
                    76131 Karlsruhe
                    DE
                    """));
            final var doubleRoomOffer = new Offer(accommodation, doubleRoom, bookedFrom, bookedTo);
            repository.addToShoppingCart(traveler, doubleRoomOffer);

                final var offers = repository.findRooms(1, "Munich", requestFrom, requestTo);

                assertThat(!offers.isEmpty(), is(available));
            }

            private void availabilityForDifferentDatesSource(final ExampleGridSource s) {
                final Function<LocalDate, String> dFrmt = (d) -> d.format(java.time.format.DateTimeFormatter.ofPattern("yyyy.MM.dd"));
                final Function<Boolean, String> bFrmt = (b) -> b ? "\u2705" : "\u274C";

                s.row(
                        s.given(LocalDate.of(2026, 1, 5), dFrmt),
                        s.given(LocalDate.of(2026, 1, 10), dFrmt),
                        s.when(LocalDate.of(2026, 1, 12), dFrmt),
                        s.when(LocalDate.of(2026, 1, 14), dFrmt),
                        s.then(true, bFrmt)
                );
                s.row(
                        s.given(LocalDate.of(2026, 1, 5), dFrmt),
                        s.given(LocalDate.of(2026, 1, 10), dFrmt),
                        s.when(LocalDate.of(2026, 1, 10), dFrmt),
                        s.when(LocalDate.of(2026, 1, 12), dFrmt),
                        s.then(false, bFrmt)
                );
                s.row(
                        s.given(LocalDate.of(2026, 1, 5), dFrmt),
                        s.given(LocalDate.of(2026, 1, 10), dFrmt),
                        s.when(LocalDate.of(2026, 1, 6), dFrmt),
                        s.when(LocalDate.of(2026, 1, 8), dFrmt),
                        s.then(false, bFrmt)
                );
                s.row(
                        s.given(LocalDate.of(2026, 1, 5), dFrmt),
                        s.given(LocalDate.of(2026, 1, 10), dFrmt),
                        s.when(LocalDate.of(2026, 1, 2), dFrmt),
                        s.when(LocalDate.of(2026, 1, 12), dFrmt),
                        s.then(false, bFrmt)
                );
                s.row(
                        s.given(LocalDate.of(2026, 1, 5), dFrmt),
                        s.given(LocalDate.of(2026, 1, 10), dFrmt),
                        s.when(LocalDate.of(2026, 1, 2), dFrmt),
                        s.when(LocalDate.of(2026, 1, 5), dFrmt),
                        s.then(false, bFrmt)
                );
                s.row(
                        s.given(LocalDate.of(2026, 1, 5), dFrmt),
                        s.given(LocalDate.of(2026, 1, 10), dFrmt),
                        s.when(LocalDate.of(2026, 1, 2), dFrmt),
                        s.when(LocalDate.of(2026, 1, 4), dFrmt),
                        s.then(true, bFrmt)
                );
            }
        }

    }

    @Feature(2)
    @Narrative(
            """
            *Offers* for *Room* could could be sorted by criteria like
            - *number of persons*
            - *popularity*
            - *best price*
            
            so that a *traveler* can find the best offer for his trip.
            """
    )
    class SelectBestOfferFromList {

        @Scenario
        class SortOffersByNumberOfPersons {

            private final BookingRepository repository = new BookingRepository();

            private List<Offer> offers;

            @Given
            void someOffersForAvailableRooms() {
                repository.register(new Accommodation(
                        """
                        Hotel Krone
                        Schlossallee 123
                        80539 Munich
                        DE
                        """,
                        new Room(2, KING),
                        new Room(6, QUEEN),
                        new Room(4, DOUBLE)
                ));
                offers = repository.findRooms(2, "Munich", LocalDate.of(2026, 1, 6), LocalDate.of(2026, 1, 7));
            }

            @When
            void sortOffersByNumberOfPersons() {
                offers = repository.sortOffersByNumberOfPersons(offers);
            }

            @Then
            void theOffersShouldBeListedBeginningWithTheSmallestRoomsFirst() {
                assertThat(offers.size(), is(3));
                assertThat(offers.get(0).getRoom().getMaxPersons(), is(2));
                assertThat(offers.get(1).getRoom().getMaxPersons(), is(4));
                assertThat(offers.get(2).getRoom().getMaxPersons(), is(6));
            }

        }

        @Scenario
        class SortOffersByPopularity {

            private final BookingRepository repository = new BookingRepository();
            private final RoomManager roomMngr = new RoomManager();

            private List<Offer> offers;

            @Given
            void someOffersForAvailableRooms() {
                repository.register(new Accommodation(
                        """
                        Hotel Krone
                        Schlossallee 123
                        80539 Munich
                        DE
                        """,
                        roomMngr.setPopularity(new Room(2, KING), 2),
                        roomMngr.setPopularity(new Room(2, QUEEN), 6),
                        roomMngr.setPopularity(new Room(2, DOUBLE), 4)
                ));
                offers = repository.findRooms(2, "Munich", LocalDate.of(2026, 1, 6), LocalDate.of(2026, 1, 7));
            }

            @When
            void sortOffersByPopularity() {
                offers = repository.sortOffersByPopularity(offers);
            }

            @Then
            void theOffersShouldBeListedBeginningWithTheMostPopularRoomsFirst() {
                assertThat(offers.size(), is(3));
                assertThat(offers.get(0).getRoom().getPopularity(), is(6));
                assertThat(offers.get(1).getRoom().getPopularity(), is(4));
                assertThat(offers.get(2).getRoom().getPopularity(), is(2));
            }

        }

        @Scenario
        class SortOffersByBestPrice {

            private final BookingRepository repository = new BookingRepository();
            private final RoomManager roomMngr = new RoomManager();

            private List<Offer> offers;

            @Given
            void someOffersForAvailableRooms() {
                repository.register(new Accommodation(
                        """
                        Hotel Krone
                        Schlossallee 123
                        80539 Munich
                        DE
                        """,
                        roomMngr.setPricePerNight(new Room(2, KING), 89.99),
                        roomMngr.setPricePerNight(new Room(2, QUEEN), 65.59),
                        roomMngr.setPricePerNight(new Room(2, DOUBLE), 72.33)
                ));
                offers = repository.findRooms(2, "Munich", LocalDate.of(2026, 1, 6), LocalDate.of(2026, 1, 7));
            }

            @When
            void sortOffersByBestPrice() {
                offers = repository.sortOffersByBestPrice(offers);
            }

            @Then
            void theOffersShouldBeListedBeginningWithTheCheapestRoomsFirst() {
                assertThat(offers.size(), is(3));
                assertThat(offers.get(0).getRoom().getPricePerNight(), is(65.59));
                assertThat(offers.get(1).getRoom().getPricePerNight(), is(72.33));
                assertThat(offers.get(2).getRoom().getPricePerNight(), is(89.99));
            }

        }

        @Scenario
        @SeeAlso(DisplayRoomDetailsSpec.class)
        class ShowRoomDetails {

            private final BookingRepository repository = new BookingRepository();
            private final RoomManager roomMngr = new RoomManager();

            private List<Offer> offers;
            private Offer offer;

            @Given
            void someOffersForAvailableRooms() {
                repository.register(new Accommodation(
                        """
                        Hotel Krone
                        Schlossallee 123
                        80539 Munich
                        DE
                        """,
                        roomMngr.setPopularity(roomMngr.setPricePerNight(new Room(2, KING, HAIRDRYER, BALCONY), 89.99), 8)
                ));
                offers = repository.findRooms(2, "Munich", LocalDate.of(2026, 1, 6), LocalDate.of(2026, 1, 7));
            }

            @When
            void selectAnOffer() {
                offer = offers.getFirst();
            }

            @Then
            void theOffersRoomDetailsShouldBeShown() {
                assertThat(offer, is(notNullValue()));
                assertThat(offer.getRoom().getPopularity(), is(8));
                assertThat(offer.getRoom().getMaxPersons(), is(2));
                assertThat(offer.getRoom().getBed(), is(KING));
                assertThat(offer.getRoom().getFacilities(), is(List.of(HAIRDRYER, BALCONY)));
                assertThat(offer.getRoom().getPricePerNight(), is(89.99));
            }

        }

    }

    @Scenario(3)
    @Narrative(
            """
            If a *traveler* selects a *room* from the list of *offers* he can add the offer to the shopping cart.
            """
    )
    class AddSelectedRoomToShoppingCart {

        private final BookingRepository repository = new BookingRepository();

        private Traveler traveler;
        private List<Offer> offers;
        private Offer selectedOffer;

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

        @Given(seq = 1, step = 2)
        void someAvailableRoomsInMunich() {
            repository.register(new Accommodation(
                    """
                    Hotel Krone
                    Schlossallee 123
                    80539 Munich
                    DE
                    """,
                    new Room(1, SINGLE),
                    new Room(2, DOUBLE)
            ));
        }

        @When(seq = 2, step = 1)
        void travelerLooksForAvailableRoomsInMunich() {
            offers = repository.findRooms(2, "Munich", LocalDate.of(2026, 1, 6), LocalDate.of(2026, 1, 7));
        }

        @Then(seq = 3, step = 1)
        void heShouldReceiveOffersForTheAvailableRoomsInMunich() {
            assertThat(offers.size(), is(1));
            assertThat(offers.get(0).getAccommodation().getName(), is("Hotel Krone"));
            assertThat(offers.get(0).getRoom().getBed(), is(DOUBLE));
        }

        @When(seq = 4, step = 1)
        void travelerSelectsTheBestOffer() {
            selectedOffer = offers.get(0);
        }

        @When(seq = 4, step = 2)
        void addsItToTheShoppingCart() {
            repository.addToShoppingCart(traveler, selectedOffer);
        }

        @Then(seq = 4, step = 1)
        void theRoomIsAddedToTheTravelersShoppingCart() {
            final List<Booking> bookings = repository.findBookings(traveler);
            assertThat(bookings.size(), is(1));
            assertThat(bookings.get(0).getRoom(), is(selectedOffer.getRoom()));
        }

    }

}
