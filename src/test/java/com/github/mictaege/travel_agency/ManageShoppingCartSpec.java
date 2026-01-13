package com.github.mictaege.travel_agency;

import static com.github.mictaege.travel_agency.Bed.KING;
import static com.github.mictaege.travel_agency.Facilities.BALCONY;
import static com.github.mictaege.travel_agency.Facilities.HAIRDRYER;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.extension.RegisterExtension;

import com.github.mictaege.arete.ExampleSource;
import com.github.mictaege.arete.Examples;
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
            > As a *traveler* I want to *check and manage my shopping cart* to *start (or cancel) a booking*.
            """,
        plantUml = {
                """
                @startuml
                :Traveler:
                Traveler --> (Check offer)
                Traveler --> (Remove offer)
                Traveler --> (Change offer)
                Traveler --> (Book offer)
                @enduml
                """
        }

)
@ActorTraveller @EntityOffer @EntityRoom
class ManageShoppingCartSpec {

    @RegisterExtension
    public ScreenshotExtension screenshots = new ScreenshotExtension(new UiDummyScreenshotTaker());

    @Feature
    class CheckOffersInShoppingCart {

        @Scenario
        @SeeAlso(DisplayRoomDetailsSpec.class)
        class ShowRoomDetails {

            private final BookingRepository repository = new BookingRepository();
            private final RoomManager roomMngr = new RoomManager();

            private Traveler traveler;
            private Booking offer;

            @Given(1)
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

            @Given(2)
            void someOffersInTheShoppingCart() {
                repository.register(new Accommodation(
                        """
                        Hotel Krone
                        Schlossallee 123
                        80539 Munich
                        DE
                        """,
                        roomMngr.setPopularity(roomMngr.setPricePerNight(new Room(2, KING, HAIRDRYER, BALCONY), 89.99), 8)
                ));
                final var offers = repository.findRooms(2, "Munich", LocalDate.of(2026, 1, 6), LocalDate.of(2026, 1, 7));
                offers.forEach(o -> repository.addToShoppingCart(traveler, o));
            }

            @When
            void selectingAnOffer() {
                offer = repository.getOffersFromShoppingCart(traveler).get(0);
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

        @Scenario
        class ShowTheTotalCostsOfAnOffer {

            private final BookingRepository repository = new BookingRepository();
            private final RoomManager roomMngr = new RoomManager();

            private Traveler traveler;
            private Booking offer;

            @Given(1)
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

            @Given(2)
            void someOffersInTheShoppingCart() {
                repository.register(new Accommodation(
                        """
                        Hotel Krone
                        Schlossallee 123
                        80539 Munich
                        DE
                        """,
                        roomMngr.setPopularity(roomMngr.setPricePerNight(new Room(2, KING, HAIRDRYER, BALCONY), 89.99), 8)
                ));
                final var offers = repository.findRooms(2, "Munich", LocalDate.of(2026, 1, 6), LocalDate.of(2026, 1, 12));
                offers.forEach(o -> repository.addToShoppingCart(traveler, o));
            }

            @When
            void selectingAnOffer() {
                offer = repository.getOffersFromShoppingCart(traveler).get(0);
            }

            @Then
            void theTotalCostsShouldBeShown() {
                assertThat(offer, is(notNullValue()));
                assertThat(offer.getTotalCosts(), is(539.94));
            }

            @Examples(
                    desc = "Examples: Calculate total costs for different number of days and prices per night",
                    pattern = "{0} days, {1} € per night => Total: {2} €",
                    srcClass = TotalCostExamplesSource.class
            )
            void totalCostExamples(final int numberOfDays,
                                   double pricePerNight,
                                   double expectedTotalCosts) {
                final var traveler = new Traveler(
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
                        roomMngr.setPopularity(roomMngr.setPricePerNight(new Room(2, KING, HAIRDRYER, BALCONY), pricePerNight), 8)
                ));
                final var offers = repository.findRooms(2, "Munich", LocalDate.of(2026, 1, 6), LocalDate.of(2026, 1, 6).plusDays(numberOfDays));
                offers.forEach(o -> repository.addToShoppingCart(traveler, o));

                final var offer = repository.getOffersFromShoppingCart(traveler).get(0);

                assertThat(offer, is(notNullValue()));
                assertThat(offer.getTotalCosts(), is(expectedTotalCosts));
            }

        }

        static class TotalCostExamplesSource extends ExampleSource {

            @Override
            protected void init() {
                example("{0}. Single day) ", given(1), given(45.33), then(45.33));
                example("{0}. Short stay) ", given(3), given(120.25), then(360.75));
                example("{0}. Whole week) ", given(7), given(74.66), then(522.62));
                example("{0}. Two weeks) ", given(14), given(69.99), then(979.86));
            }
        }

    }

    @Scenario
    class RemoveOfferFromShoppingCart {

        private final BookingRepository repository = new BookingRepository();
        private final RoomManager roomMngr = new RoomManager();

        private Traveler traveler;
        private Booking offer;

        @Given(1)
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

        @Given(2)
        void someOffersInTheShoppingCart() {
            repository.register(new Accommodation(
                    """
                    Hotel Krone
                    Schlossallee 123
                    80539 Munich
                    DE
                    """,
                    roomMngr.setPopularity(roomMngr.setPricePerNight(new Room(2, KING, HAIRDRYER, BALCONY), 89.99), 8)
            ));
            final var offers = repository.findRooms(2, "Munich", LocalDate.of(2026, 1, 6), LocalDate.of(2026, 1, 7));
            offers.forEach(o -> repository.addToShoppingCart(traveler, o));
        }

        @When(1)
        void selectingAnOffer() {
            offer = repository.getOffersFromShoppingCart(traveler).get(0);
        }

        @When(2)
        void removeFromShoppingCart() {
            repository.removeOfferFromShoppingCart(traveler, offer);
        }

        @Then
        void theShoppingCartShouldNotContainTheOfferAnymore() {
            var cart = repository.getOffersFromShoppingCart(traveler);
            assertThat(cart.contains(offer), is(false));
        }

    }

    @Feature
    class ChangeOfferInShoppingCart {

        @Scenario
        class ChangeOfferInShoppingCartSuccessfully {

            private final BookingRepository repository = new BookingRepository();
            private final RoomManager roomMngr = new RoomManager();

            private Traveler traveler;
            private Booking offer;

            @Given(1)
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

            @Given(2)
            void someOffersInTheShoppingCart() {
                repository.register(new Accommodation(
                        """
                        Hotel Krone
                        Schlossallee 123
                        80539 Munich
                        DE
                        """,
                        roomMngr.setPopularity(roomMngr.setPricePerNight(new Room(2, KING, HAIRDRYER, BALCONY), 89.99), 8)
                ));
                final var offers = repository.findRooms(2, "Munich", LocalDate.of(2026, 1, 6), LocalDate.of(2026, 1, 7));
                offers.forEach(o -> repository.addToShoppingCart(traveler, o));
            }

            @When(1)
            void selectingAnOffer() {
                offer = repository.getOffersFromShoppingCart(traveler).get(0);
            }

            @When(2)
            void theTimeOfStayIsChanged() {
                repository.changeTimeOfStay(traveler, offer, LocalDate.of(2026, 1, 8), LocalDate.of(2026, 1, 9));
            }

            @Then
            void theTimeOfStayShouldBeChangedInTheShoppingCart() {
                assertThat(offer.getStart(), is(LocalDate.of(2026, 1, 8)));
                assertThat(offer.getEnd(), is(LocalDate.of(2026, 1, 9)));
            }

        }

        @Scenario
        class ChangeOfferInShoppingCartNotPossibleIfRoomNotAvailable {

            @Scenario
            class ChangeOfferInShoppingCartSuccessfully {

                private final BookingRepository repository = new BookingRepository();
                private final RoomManager roomMngr = new RoomManager();

                private Traveler traveler;
                private Booking offer;
                private Exception exc;

                @Given(1)
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

                @Given(2)
                void someOffersInTheShoppingCart() {
                    repository.register(new Accommodation(
                            """
                            Hotel Krone
                            Schlossallee 123
                            80539 Munich
                            DE
                            """,
                            roomMngr.setPopularity(roomMngr.setPricePerNight(new Room(2, KING, HAIRDRYER, BALCONY), 89.99), 8)
                    ));
                    final var offers = repository.findRooms(2, "Munich", LocalDate.of(2026, 1, 6), LocalDate.of(2026, 1, 7));
                    offers.forEach(o -> repository.addToShoppingCart(traveler, o));
                }

                @Given(3)
                void theRoomIsBookedAtOtherTimes() {
                    final var offers = repository.findRooms(2, "Munich", LocalDate.of(2026, 1, 8), LocalDate.of(2026, 1, 9));
                    offers.forEach(o -> repository.addToShoppingCart(traveler, o));
                }

                @When(1)
                void selectingAnOffer() {
                    offer = repository.getOffersFromShoppingCart(traveler).get(0);
                }

                @When(2)
                void theTimeOfStayIsChangedToATimeWhenTheRoomIsAlreadyBooked() {
                    try {
                        repository.changeTimeOfStay(traveler, offer, LocalDate.of(2026, 1, 8), LocalDate.of(2026, 1, 9));
                    } catch (final Exception e) {
                        exc = e;
                    }
                }

                @Then(1)
                void theChangeIsRejected() {
                    assertThat(exc.getMessage(), is("Cannot change time of stay"));
                }

                @Then(2)
                void theOfferRemainsUnchanged() {
                    assertThat(offer.getStart(), is(LocalDate.of(2026, 1, 6)));
                    assertThat(offer.getEnd(), is(LocalDate.of(2026, 1, 7)));
                }

            }

        }

    }



}
