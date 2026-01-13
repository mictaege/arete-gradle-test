package com.github.mictaege.travel_agency;

import static com.github.mictaege.travel_agency.RoomState.AVAILABLE;
import static java.util.Comparator.comparing;
import static java.util.Comparator.reverseOrder;
import static java.util.stream.Collectors.toList;

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
                        .filter(r -> r.getFacilities().containsAll(List.of(facilities)))
                        .filter(r -> bookings.stream().noneMatch(b -> b.getRoom().equals(r) && alreadyBooked(b, from, to)))
                        .map(r -> new Offer(a, r, from, to)))
                .toList();
    }

    private boolean alreadyBooked(final Booking booking, final LocalDate start, final LocalDate end) {
        return (start.isBefore(booking.getEnd()) || start.equals(booking.getEnd()))
                && (end.isAfter(booking.getStart()) || end.equals(booking.getStart()));
    }

    public void addToShoppingCart(final Traveler traveler,
                                  final Offer offer) {
        bookings.add(new Booking(traveler, offer));
    }

    public List<Booking> findBookings(final Traveler traveler) {
        return bookings.stream().filter(b -> b.getTraveler().equals(traveler)).collect(toList());
    }

    public List<Offer> sortOffersByNumberOfPersons(final List<Offer> offers) {
        return offers.stream().sorted(comparing(offer -> offer.getRoom().getMaxPersons())).collect(toList());
    }

    public List<Offer> sortOffersByPopularity(final List<Offer> offers) {
        return offers.stream().sorted(comparing(offer -> offer.getRoom().getPopularity(), reverseOrder())).collect(toList());
    }

    public List<Offer> sortOffersByBestPrice(final List<Offer> offers) {
        return offers.stream().sorted(comparing(offer -> offer.getRoom().getPricePerNight())).collect(toList());
    }

    public List<Booking> getOffersFromShoppingCart(final Traveler traveler) {
        return bookings.stream()
                .filter(b -> b.getTraveler().equals(traveler))
                .filter(b -> b.getState() == BookingState.OFFERED)
                .collect(toList());
    }

    public void removeOfferFromShoppingCart(final Traveler traveler, final Booking offer) {
        bookings.remove(offer);
    }

    public void changeTimeOfStay(final Traveler traveler, final Booking offer, final LocalDate start, final LocalDate end) {
        if (bookings.stream().noneMatch(b -> !offer.equals(b) && b.getRoom().equals(offer.getRoom()) && alreadyBooked(b, start, end))) {
            offer.setStart(start);
            offer.setEnd(end);
        } else {
            throw new IllegalStateException("Cannot change time of stay");
        }
    }

    public List<PaymentMethods> checkOutFromShoppingCart(final Traveler traveler, final Booking offer) {
        offer.setState(BookingState.IN_CHECK_OUT);
        return offer.getAccommodation().getPaymentMethods();
    }

    public void initPayment(Traveler traveler, Booking offer, PaymentMethods paymentMethods) {
        if (offer.getAccommodation().getPaymentMethods().contains(paymentMethods)) {
            offer.setState(BookingState.CONFIRMED);
            traveler.receiveConfirmation(offer);
            traveler.receiveInvoice(offer);
        } else {
            throw new IllegalStateException("Payment method not secure");
        }
    }
}
