package com.github.mictaege.travel_agency;

import java.time.LocalDate;
import java.util.UUID;

public class Booking {
    private final String id = "BKNG-" + UUID.randomUUID();
    private final Traveler traveler;
    private final Accommodation accommodation;
    private Room room;
    private LocalDate start;
    private LocalDate end;

    public String getId() {
        return id;
    }

    public Booking(final Traveler traveler,
                   final Offer offer) {
        this.traveler = traveler;
        this.accommodation = offer.getAccommodation();
        this.room = offer.getRoom();
        this.start = offer.getStart();
        this.end = offer.getEnd();
    }

    public Traveler getTraveler() {
        return traveler;
    }

    public Accommodation getAccommodation() {
        return accommodation;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(final Room room) {
        this.room = room;
    }

    public LocalDate getStart() {
        return start;
    }

    public void setStart(final LocalDate start) {
        this.start = start;
    }

    public LocalDate getEnd() {
        return end;
    }

    public void setEnd(final LocalDate end) {
        this.end = end;
    }
}
