package com.github.mictaege.travel_agency;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Traveler {
    private final String id = "TRVL-" + UUID.randomUUID();
    private String firstName;
    private String lastName;
    private String eMail;
    private Address address;
    private final List<Booking> confirmedBookings;
    private final List<Booking> invoicedBookings;
    private final List<Complaint> complaints;

    public Traveler(final String firstName,
                    final String lastName,
                    final String eMail,
                    final Address address) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.eMail = eMail;
        this.address = address;
        this.confirmedBookings = new ArrayList<>();
        this.invoicedBookings = new ArrayList<>();
        this.complaints = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    public String geteMail() {
        return eMail;
    }

    public void seteMail(final String eMail) {
        this.eMail = eMail;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(final Address address) {
        this.address = address;
    }

    public void receiveConfirmation(Booking booking) {
        this.confirmedBookings.add(booking);
    }

    public void receiveInvoice(Booking booking) {
        this.invoicedBookings.add(booking);
    }

    public List<Booking> getConfirmedBookings() {
        return confirmedBookings;
    }

    public List<Booking> getInvoicedBookings() {
        return invoicedBookings;
    }

    public void addComplaint(Complaint complaint) {
        complaints.add(complaint);
    }

    public List<Complaint> getComplaints() {
        return complaints;
    }
}
