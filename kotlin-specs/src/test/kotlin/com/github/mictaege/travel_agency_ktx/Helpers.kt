package com.github.mictaege.travel_agency_ktx

import com.github.mictaege.travel_agency.Address
import com.github.mictaege.travel_agency.Traveler

fun newTraveler() = Traveler(
    "Max", "Mustermann", "max-mustermann@gnogle.com",
    Address(
        """
            Hauptstrasse 8"
            76131 Karlsruhe
            DE
        """.trimIndent()
    )
)

const val HOTEL_KRONE = "Hotel Krone\nSchlossallee 123\n80539 Munich\nDE\n"
