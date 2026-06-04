package com.github.mictaege.travel_agency;

import java.util.UUID;

public class Complaint {

    private final String id = "CMPLN-" + UUID.randomUUID();
    private final Booking booking;
    private final Facilities missingFacility;
    private boolean closed;

    public Complaint(final Booking booking,
                     final Facilities missingFacility) {
        this.booking = booking;
        this.missingFacility = missingFacility;
        if (isJustified()) {
            booking.getAccommodation().addComplaint(this);
        } else {
            booking.getTraveler().addComplaint(this);
        }
        this.closed = true;
    }

    public String getId() {
        return id;
    }

    public Booking getBooking() {
        return booking;
    }

    public Facilities getMissingFacility() {
        return missingFacility;
    }

    public boolean isJustified() {
        return booking.getRoom().getFacilities().contains(missingFacility);
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }
}
