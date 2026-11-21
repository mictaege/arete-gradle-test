package com.github.mictaege.travel_agency;

import java.time.LocalDate;
import java.util.UUID;

public class Offer {
    private final String id = "OFFR-" + UUID.randomUUID();
    private final Accommodation accommodation;
    private final Room room;
    private final LocalDate start;
    private final LocalDate end;

    public Offer(final Accommodation accommodation,
                 final Room room,
                 final LocalDate start,
                 final LocalDate end) {
        this.accommodation = accommodation;
        this.room = room;
        this.start = start;
        this.end = end;
    }

    public String getId() {
        return id;
    }

    public Accommodation getAccommodation() {
        return accommodation;
    }

    public Room getRoom() {
        return room;
    }

    public LocalDate getStart() {
        return start;
    }

    public LocalDate getEnd() {
        return end;
    }

}
