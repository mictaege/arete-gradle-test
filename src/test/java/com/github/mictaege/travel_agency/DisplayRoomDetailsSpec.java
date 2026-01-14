package com.github.mictaege.travel_agency;

import static com.github.mictaege.travel_agency.Bed.QUEEN;
import static com.github.mictaege.travel_agency.Facilities.BATHROBE;
import static com.github.mictaege.travel_agency.Facilities.HAIRDRYER;
import static com.github.mictaege.travel_agency.Facilities.SHOWER;
import static com.github.mictaege.travel_agency.Facilities.TELEVISION;
import static com.github.mictaege.travel_agency.Facilities.WIFI;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.github.mictaege.arete.Describe;
import com.github.mictaege.arete.ItShould;
import com.github.mictaege.arete.Narrative;
import com.github.mictaege.arete.ScreenshotExtension;
import com.github.mictaege.arete.Spec;

@Spec
@Narrative(
            """
            > As a *traveler*, I want to look at the *room details* so that I can *check if a room meets my needs and preferences*.
            
            The following room details should be displayed:
            - the popularity
            - maximum number of persons
            - the type of bed
            - it's facilities
            - its price per night
            - images of the room
            """
)
@ActorTraveller @EntityRoom
class DisplayRoomDetailsSpec {

    @RegisterExtension
    public ScreenshotExtension screenshots = new ScreenshotExtension(new UiDummyScreenshotTaker());

    private Room room;

    @BeforeEach
    void context() {
        room = new Room(4, QUEEN, SHOWER, BATHROBE, HAIRDRYER, TELEVISION, WIFI);
        room.setPopularity(9);
        room.setPricePerNight(123.45);
        room.setImages(List.of(new RoomImage("room1.png", "Bathroom"), new RoomImage("image2.png", "Living room")));
    }

    @Describe
    class HowRoomDetailsAndFacilitiesAreDisplayed {

        @ItShould(1)
        void displayTheRoomsPopularity() {
            assertThat(room.getPopularity(), is(8));
        }

        @ItShould(2)
        void displayTheRoomsMaximumNumberOfPersons() {
            assertThat(room.getMaxPersons(), is(4));
        }

        @ItShould(3)
        void displayTheRoomsBed() {
            assertThat(room.getBed(), is(QUEEN));
        }

        @ItShould(4)
        void displayTheRoomsFacilities() {
            assertThat(room.getFacilities(), is(List.of(SHOWER, BATHROBE, HAIRDRYER, TELEVISION, WIFI)));
        }

        @ItShould(4)
        void displayTheRoomsPricePerNight() {
            assertThat(room.getPricePerNight(), is(123.45));
        }


    }

    @Describe
    @Narrative(
            value = """
            The images of a room should give the *traveller* a good impression of the room and therefore be of good quality.
            
            Example images for a room:
            """,
            imageResourcePath = {
                    "com/github/mictaege/travel_agency/room1.png",
                    "com/github/mictaege/travel_agency/room2.png"
            }
    )
    class HowImagesOfTheRoomAreDisplayed {

        @ItShould(desc = "It should display all images in FullHD or an higher resolution")
        void displayAllImagesInFhdOrHigher() {
            assertThat(room.getImages().stream().allMatch(i -> i.getWidth() >= 1920), is(true));
            assertThat(room.getImages().stream().allMatch(i -> i.getWidth() >= 1080), is(true));
        }

        @ItShould
        void displayTheImagesDescriptionIfThereIsOne() {
            assertThat(room.getImages().get(0).getDescription(), is("Bathroom"));
            assertThat(room.getImages().get(1).getDescription(), is("Living room"));
        }

    }

}
