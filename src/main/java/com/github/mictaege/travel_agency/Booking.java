package com.github.mictaege.travel_agency;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class Booking {
    private final String id = "BKNG-" + UUID.randomUUID();
    private final Traveler traveler;
    private final Accommodation accommodation;
    private Room room;
    private LocalDate start;
    private LocalDate end;
    private BookingState state;

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
        this.state = BookingState.OFFERED;
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

    public BookingState getState() {
        return state;
    }

    public void setState(final BookingState state) {
        this.state = state;
    }

    public double getTotalCosts() {
        if (start == null || end == null || room == null) {
            return 0.0;
        }
        final long nights = ChronoUnit.DAYS.between(start, end);
        return Math.round(Math.max(0, nights) * room.getPricePerNight() * 100.0) / 100.0;
    }
}
