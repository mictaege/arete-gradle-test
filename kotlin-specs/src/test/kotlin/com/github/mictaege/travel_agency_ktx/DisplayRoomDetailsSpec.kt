package com.github.mictaege.travel_agency_ktx

import com.github.mictaege.arete_ktx.Spec
import com.github.mictaege.travel_agency.*
import com.github.mictaege.travel_agency.Bed.QUEEN
import com.github.mictaege.travel_agency.Facilities.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.CoreMatchers.`is`

class DisplayRoomDetailsSpec : Spec({
    spec("Display Room Details Spec") {
        doc(
            narrative = """
                > As a *traveler*, I want to look at the *room details* so that I can *check if a room meets my needs and preferences*.
                
                The following room details should be displayed:
                - the popularity
                - maximum number of persons
                - the type of bed
                - it's facilities
                - its price per night
                - images of the room
            """.trimIndent(),
            tags = listOf(ActorTraveller::class, EntityRoom::class)
        )
        screenshots(UiDummyScreenshotTaker())
        describe("How Room Details And Facilities Are Displayed") {
            val room = Room(4, QUEEN, SHOWER, BATHROBE, HAIRDRYER, TELEVISION, WIFI).apply {
                popularity = 9
                pricePerNight = 123.45
            }
            itShould("display the rooms popularity") {
                assertThat(room.popularity, `is`(8))
            }
            itShould("display the rooms maximum number of persons") {
                assertThat(room.maxPersons, `is`(4))
            }
            itShould("display the rooms bed") {
                assertThat(room.bed, `is`(QUEEN))
            }
            itShould("display the rooms facilities") {
                assertThat(room.facilities,
                    `is`(listOf(SHOWER, BATHROBE, HAIRDRYER, TELEVISION, WIFI)))
            }
            itShould("display the rooms price per night") {
                assertThat(room.pricePerNight, `is`(123.45))
            }
        }
        describe("How Images Of The Room Are Displayed") {
            doc(
                narrative = """
                    The images of a room should give the *traveller* a good impression of the room and therefore be of good quality.

                    Example images for a room:
                """.trimIndent(),
                imageResourcePath = listOf(
                    "com/github/mictaege/travel_agency/room1.png",
                    "com/github/mictaege/travel_agency/room2.png"
                )
            )
            itShould("It should display all images in FullHD or an higher resolution") {
                assertThat(true, `is`(true))
            }
            itShould("display the images description if there is one") {

            }
        }
    }
})
