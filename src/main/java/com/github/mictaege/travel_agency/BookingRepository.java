package com.github.mictaege.travel_agency;

import static com.github.mictaege.travel_agency.RoomState.AVAILABLE;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BookingRepository {
    private final List<Accommodation> accommodations = new ArrayList<>();
    private final List<Booking> bookings = new ArrayList<>();

    public void register(final Accommodation accommodation) {
        accommodations.add(accommodation);
    }

    public List<Offer> findRooms(final int persons, final String city, final LocalDate from, final LocalDate to, Facilities... facilities) {
        return accommodations.stream()
                .filter(a -> a.getAddress().getCity().equals(city))
                .flatMap(a -> a.getRooms().stream()
                        .filter(r -> r.getState() == AVAILABLE)
                        .filter(r -> r.getMaxPersons() >= persons)
                        .filter(r -> bookings.stream().noneMatch(b -> b.getRoom().equals(r) && alreadyBooked(b, from, to)))
                        .map(r -> new Offer(a, r, from, to)))
                .toList();
    }

    private boolean alreadyBooked(final Booking booking, final LocalDate start, final LocalDate end) {
        return (start.isBefore(booking.getEnd()) || start.equals(booking.getEnd()))
                && (end.isAfter(booking.getStart()) || end.equals(booking.getStart()));
    }

    public void bookRoom(final Traveler traveler,
                         final Offer offer) {
        bookings.add(new Booking(traveler, offer));
    }

}
