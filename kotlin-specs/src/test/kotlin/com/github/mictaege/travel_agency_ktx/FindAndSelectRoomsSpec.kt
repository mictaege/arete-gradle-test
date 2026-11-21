package com.github.mictaege.travel_agency_ktx

import com.github.mictaege.arete_ktx.Spec
import com.github.mictaege.travel_agency.*
import com.github.mictaege.travel_agency.Bed.*
import com.github.mictaege.travel_agency.Facilities.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import java.time.LocalDate

class FindAndSelectRoomsSpec : Spec({
    spec("Find And Select Rooms Spec") {
        doc(
            narrative = "> As a *traveler* I want to *find available rooms* for my trip *so that I can book a room for my stay*.\n",
            plantUml = listOf(
                """
                @startuml
                left to right direction
                :Traveler:
                Traveler --> (Find available rooms)
                Traveler --> (Select best offer from list)
                Traveler --> (Add to shopping cart)
                @enduml
                """.trimIndent(),
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
                """.trimIndent(),
                """
                @startsalt
                {
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
                """.trimIndent()
            ),
            tags = listOf(ActorTraveller::class, EntityOffer::class, EntityRoom::class)
        )
        screenshots(UiDummyScreenshotTaker())
        feature("Find Available Rooms") {
            doc(
                narrative = """
                    *Rooms* could be find by searching for accommodations
                    - in a *city*
                    - filtering by
                        - *availability*
                        - *size*
                        - and *facilities*
                """.trimIndent()
            )
            scenario("Find Available Rooms By City") {
                val repository = BookingRepository()
                lateinit var offers: List<Offer>
                given("some available rooms in Munich") {
                    repository.register(Accommodation(HOTEL_KRONE, Room(1, SINGLE), Room(2, DOUBLE)))
                }
                `when`("traveler looks for available rooms in Munich") {
                    offers = repository.findRooms(2, "Munich", LocalDate.of(2026, 1, 6), LocalDate.of(2026, 1, 7))
                }
                then("he should receive offers for the available rooms in Munich") {
                    assertThat(offers, hasSize(1))
                    assertThat(offers[0].accommodation.name, `is`("Hotel Krone"))
                    assertThat(offers[0].room.bed, `is`(DOUBLE))
                }
            }
            scenario("Do Not List Rooms In Other Cities") {
                val repository = BookingRepository()
                lateinit var offers: List<Offer>
                given("rooms only available in Berlin") {
                    repository.register(Accommodation(
                        """
                        Ibis Hotel
                        Alexanderplatz 48
                        10178 Berlin
                        DE
                        """.trimIndent(),
                        Room(2, QUEEN), Room(2, KING))
                    )
                }
                `when`("traveler looks for available rooms in Munich") {
                    offers = repository.findRooms(2, "Munich", LocalDate.of(2026, 1, 6), LocalDate.of(2026, 1, 7))
                }
                then("he should receive no offers for rooms in Berlin") {
                    assertThat(offers, `is`(empty()))
                }
            }
            scenario("Do Not List Rooms That Are To Small") {
                val repository = BookingRepository()
                lateinit var offers: List<Offer>
                given("some available rooms in Munich for one or two persons") {
                    repository.register(Accommodation(HOTEL_KRONE, Room(1, SINGLE), Room(2, DOUBLE)))
                }
                `when`("traveler looks for available rooms for five persons in Munich") {
                    offers = repository.findRooms(5, "Munich", LocalDate.of(2026, 1, 6), LocalDate.of(2026, 1, 7))
                }
                then("he should receive no offers for smaller rooms") {
                    assertThat(offers, `is`(empty()))
                }
            }
            scenario("Do Not List Rooms With Missing Facilities") {
                val repository = BookingRepository()
                lateinit var offers: List<Offer>
                given("some available rooms in Munich without balcony") {
                    repository.register(Accommodation(HOTEL_KRONE,
                        Room(2, KING, SHOWER, BATHROBE, HAIRDRYER, TELEVISION, WIFI),
                        Room(2, QUEEN, SHOWER, BATHROBE, HAIRDRYER, TELEVISION, BALCONY)
                    ))
                }
                `when`("traveler looks for available rooms with balcony in Munich") {
                    offers = repository.findRooms(2, "Munich", LocalDate.of(2026, 1, 6), LocalDate.of(2026, 1, 7), BALCONY)
                }
                then("he should receive no offers for rooms missing the balcony") {
                    assertThat(offers, `is`(empty()))
                }
            }
            scenario("Do Not List Rooms That Are Already Booked") {
                val repository = BookingRepository()
                lateinit var offers: List<Offer>
                given("some already booked rooms in Munich") {
                    val singleRoom = Room(1, SINGLE)
                    val doubleRoom = Room(2, DOUBLE)
                    val accommodation = Accommodation(HOTEL_KRONE, singleRoom, doubleRoom)
                    repository.register(accommodation)
                    val traveler = newTraveler()
                    repository.addToShoppingCart(traveler, Offer(accommodation, singleRoom, LocalDate.of(2026, 1, 3), LocalDate.of(2026, 1, 6)))
                    repository.addToShoppingCart(traveler, Offer(accommodation, doubleRoom, LocalDate.of(2026, 1, 7), LocalDate.of(2026, 1, 10)))
                }
                `when`("traveler looks for available rooms in Munich") {
                    offers = repository.findRooms(1, "Munich", LocalDate.of(2026, 1, 6), LocalDate.of(2026, 1, 7))
                }
                then("he should receive no offers for already booked rooms") {
                    assertThat(offers, `is`(empty()))
                }

                exampleGrid(
                    name = "Availability for different booking situations and dates:",
                    columns = arrayOf("Booked Room From", "Booked Room To", "Request From", "Request To", "Room Available?"),
                    source = {
                        val dFrmt: (LocalDate) -> String = { it.format(java.time.format.DateTimeFormatter.ofPattern("yyyy.MM.dd")) }
                        val bFrmt: (Boolean) -> String = { if (it) "✅" else "❌" }
                        row(
                            given(LocalDate.of(2026, 1, 5), dFrmt),
                            given(LocalDate.of(2026, 1, 10), dFrmt),
                            `when`(LocalDate.of(2026, 1, 12), dFrmt),
                            `when`(LocalDate.of(2026, 1, 14), dFrmt),
                            then(true, bFrmt)
                        )
                        row(
                            given(LocalDate.of(2026, 1, 5), dFrmt),
                            given(LocalDate.of(2026, 1, 10), dFrmt),
                            `when`(LocalDate.of(2026, 1, 10), dFrmt),
                            `when`(LocalDate.of(2026, 1, 12), dFrmt),
                            then(false, bFrmt)
                        )
                        row(
                            given(LocalDate.of(2026, 1, 5), dFrmt),
                            given(LocalDate.of(2026, 1, 10), dFrmt),
                            `when`(LocalDate.of(2026, 1, 6), dFrmt),
                            `when`(LocalDate.of(2026, 1, 8), dFrmt),
                            then(false, bFrmt)
                        )
                        row(
                            given(LocalDate.of(2026, 1, 5), dFrmt),
                            given(LocalDate.of(2026, 1, 10), dFrmt),
                            `when`(LocalDate.of(2026, 1, 2), dFrmt),
                            `when`(LocalDate.of(2026, 1, 12), dFrmt),
                            then(false, bFrmt)
                        )
                        row(
                            given(LocalDate.of(2026, 1, 5), dFrmt),
                            given(LocalDate.of(2026, 1, 10), dFrmt),
                            `when`(LocalDate.of(2026, 1, 2), dFrmt),
                            `when`(LocalDate.of(2026, 1, 5), dFrmt),
                            then(false, bFrmt)
                        )
                        row(
                            given(LocalDate.of(2026, 1, 5), dFrmt),
                            given(LocalDate.of(2026, 1, 10), dFrmt),
                            `when`(LocalDate.of(2026, 1, 2), dFrmt),
                            `when`(LocalDate.of(2026, 1, 4), dFrmt),
                            then(true, bFrmt)
                        )
                    }
                ) { row ->
                    val bookedFrom = row[0] as LocalDate
                    val bookedTo = row[1] as LocalDate
                    val requestFrom = row[2] as LocalDate
                    val requestTo = row[3] as LocalDate
                    val available = row[4] as Boolean
                    val repository = BookingRepository()
                    val room = Room(2, DOUBLE)
                    val accommodation = Accommodation(HOTEL_KRONE, room)
                    repository.register(accommodation)
                    val traveler = newTraveler()
                    repository.addToShoppingCart(traveler, Offer(accommodation, room, bookedFrom, bookedTo))
                    val offers = repository.findRooms(1, "Munich", requestFrom, requestTo)
                    assertThat(offers.isNotEmpty(), `is`(available))
                }
            }
            scenario("Show Advertisement") {
                disabled("Monetization feature disabled", hidden = true)
                val repository = BookingRepository()
                lateinit var offers: List<Offer>
                given("some available rooms in Munich") {
                    repository.register(Accommodation(HOTEL_KRONE, Room(1, SINGLE), Room(2, DOUBLE)))
                }
                `when`("traveler looks for available rooms in Munich") {
                    offers = repository.findRooms(2, "Munich", LocalDate.of(2026, 1, 6), LocalDate.of(2026, 1, 7))
                }
                then("he should receive offers for the available rooms in Munich and advertisements") {
                    assertThat(offers, hasSize(2))
                }
            }
        }
        feature("Select Best Offer From List") {
            doc(
                narrative = """
                    *Offers* for *Rooms* could be sorted by criteria like
                    - *number of persons*
                    - *popularity*
                    - *best price*

                    so that a *traveler* can find the best offer for his trip.
                """.trimIndent()
            )
            scenario("Sort Offers By Number Of Persons") {
                val repository = BookingRepository()
                lateinit var offers: List<Offer>
                given("some offers for available rooms") {
                    repository.register(Accommodation(HOTEL_KRONE, Room(2, KING), Room(6, QUEEN), Room(4, DOUBLE)))
                    offers = repository.findRooms(2, "Munich", LocalDate.of(2026, 1, 6), LocalDate.of(2026, 1, 7))
                }
                `when`("sort offers by number of persons") {
                    offers = repository.sortOffersByNumberOfPersons(offers)
                }
                then("the offers should be listed beginning with the smallest rooms first") {
                    assertThat(offers, hasSize(3))
                    assertThat(offers[0].room.maxPersons, `is`(2))
                    assertThat(offers[1].room.maxPersons, `is`(4))
                    assertThat(offers[2].room.maxPersons, `is`(6))
                }
            }
            scenario("Sort Offers By Popularity") {
                val repository = BookingRepository()
                val roomMngr = RoomManager()
                lateinit var offers: List<Offer>
                given("some offers for available rooms") {
                    repository.register(Accommodation(HOTEL_KRONE,
                        roomMngr.setPopularity(Room(2, KING), 2),
                        roomMngr.setPopularity(Room(2, QUEEN), 6),
                        roomMngr.setPopularity(Room(2, DOUBLE), 4)))
                    offers = repository.findRooms(2, "Munich", LocalDate.of(2026, 1, 6), LocalDate.of(2026, 1, 7))
                }
                `when`("sort offers by popularity") {
                    offers = repository.sortOffersByPopularity(offers)
                }
                then("the offers should be listed beginning with the most popular rooms first") {
                    assertThat(offers, hasSize(3))
                    assertThat(offers[0].room.popularity, `is`(6))
                    assertThat(offers[1].room.popularity, `is`(4))
                    assertThat(offers[2].room.popularity, `is`(2))
                }
            }
            scenario("Sort Offers By Best Price") {
                val repository = BookingRepository()
                val roomMngr = RoomManager()
                lateinit var offers: List<Offer>
                given("some offers for available rooms") {
                    repository.register(Accommodation(HOTEL_KRONE,
                        roomMngr.setPricePerNight(Room(2, KING), 89.99),
                        roomMngr.setPricePerNight(Room(2, QUEEN), 65.59),
                        roomMngr.setPricePerNight(Room(2, DOUBLE), 72.33)))
                    offers = repository.findRooms(2, "Munich", LocalDate.of(2026, 1, 6), LocalDate.of(2026, 1, 7))
                }
                `when`("sort offers by best price") {
                    offers = repository.sortOffersByBestPrice(offers)
                }
                then("the offers should be listed beginning with the cheapest rooms first") {
                    assertThat(offers, hasSize(3))
                    assertThat(offers[0].room.pricePerNight, `is`(65.59))
                    assertThat(offers[1].room.pricePerNight, `is`(72.33))
                    assertThat(offers[2].room.pricePerNight, `is`(89.99))
                }
            }
            scenario("Show Room Details") {
                doc(seeAlso = listOf(DisplayRoomDetailsSpec::class))
                val repository = BookingRepository()
                val roomMngr = RoomManager()
                lateinit var offers: List<Offer>
                lateinit var offer: Offer
                given("some offers for available rooms") {
                    repository.register(Accommodation(HOTEL_KRONE,
                        roomMngr.setPopularity(roomMngr.setPricePerNight(Room(2, KING, HAIRDRYER, BALCONY), 89.99), 8)))
                    offers = repository.findRooms(2, "Munich", LocalDate.of(2026, 1, 6), LocalDate.of(2026, 1, 7))
                }
                `when`("select an offer") {
                    offer = offers.first()
                }
                then("the offers room details should be shown") {
                    assertThat(offer, `is`(notNullValue()))
                    assertThat(offer.room.popularity, `is`(8))
                    assertThat(offer.room.maxPersons, `is`(2))
                    assertThat(offer.room.bed, `is`(KING))
                    assertThat(offer.room.facilities, `is`(listOf(HAIRDRYER, BALCONY)))
                    assertThat(offer.room.pricePerNight, `is`(89.99))
                }
            }
        }
        scenario("Add Selected Room To Shopping Cart") {
            doc(narrative = "If a *traveler* selects a *room* from the list of *offers* he can add the offer to the shopping cart.\n")
            val repository = BookingRepository()
            lateinit var traveler: Traveler
            lateinit var offers: List<Offer>
            lateinit var selectedOffer: Offer
            given("a registered traveler") {
                traveler = newTraveler()
            }
            given("some available rooms in Munich") {
                repository.register(Accommodation(HOTEL_KRONE, Room(1, SINGLE), Room(2, DOUBLE)))
            }
            `when`("traveler looks for available rooms in Munich") {
                offers = repository.findRooms(2, "Munich", LocalDate.of(2026, 1, 6), LocalDate.of(2026, 1, 7))
            }
            then("he should receive offers for the available rooms in Munich") {
                assertThat(offers, hasSize(1))
                assertThat(offers[0].accommodation.name, `is`("Hotel Krone"))
            }
            `when`("traveler selects the best offer") {
                selectedOffer = offers[0]
            }
            `when`("adds it to the shopping cart") {
                repository.addToShoppingCart(traveler, selectedOffer)
            }
            then("the room is added to the travelers shopping cart") {
                val bookings = repository.findBookings(traveler)
                assertThat(bookings, hasSize(1))
                assertThat(bookings[0].room, `is`(selectedOffer!!.room))
            }
        }
    }
})
