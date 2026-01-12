package com.github.mictaege.travel_agency;

import static com.github.mictaege.travel_agency.Bed.DOUBLE;
import static com.github.mictaege.travel_agency.Bed.KING;
import static com.github.mictaege.travel_agency.Bed.QUEEN;
import static com.github.mictaege.travel_agency.Bed.SINGLE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;

import com.github.mictaege.arete.ExampleGrid;
import com.github.mictaege.arete.ExampleGridSource;
import com.github.mictaege.arete.Given;
import com.github.mictaege.arete.Narrative;
import com.github.mictaege.arete.Scenario;
import com.github.mictaege.arete.Spec;
import com.github.mictaege.arete.Then;
import com.github.mictaege.arete.When;

@Spec
@Narrative(
        value =
            """
            > As a *traveler* I want to *find available rooms* for my trip *so that I can book a room for my stay*.
            
            Rooms could be find by searching for accommodation
            - in a *city*
            - filtering by
                - *availability*
                - *size*
                - and *facilities*
            """,
        plantUml = {
                """
                @startuml
                :Traveler:
                Traveler --> (Find available rooms)
                Traveler --> (Select best offer from list)
                Traveler --> (Book selected room)
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
                :Book room;
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
class FindAndBookRoomsSpec {

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
            repository.bookRoom(traveler, singleRoomOffer);
            final var doubleRoomOffer = new Offer(accommodation, doubleRoom, LocalDate.of(2026, 1, 7), LocalDate.of(2026, 1, 10));
            repository.bookRoom(traveler, doubleRoomOffer);
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
            repository.bookRoom(traveler, doubleRoomOffer);

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
