package com.github.mictaege.travel_agency;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.github.mictaege.travel_agency.PaymentMethod.*;

public class Accommodation {
    private final String id = "ACMD-" + UUID.randomUUID();
    private final String name;
    private final Address address;
    private final List<Room> rooms;
    private final List<PaymentMethod> paymentMethods;
    private final List<Complaint> complaints;
    private boolean reprimanded;

    public Accommodation(final String fullAddress,
                         final Room... rooms) {
        this.name = fullAddress.split("\n")[0].trim();
        this.address = new Address(fullAddress.replace(name, "").trim());
        this.rooms = List.of(rooms);
        this.paymentMethods = List.of(PREPAYMENT, PAYPAL, CREDITCARD);
        this.complaints = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Address getAddress() {
        return address;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public List<PaymentMethod> getPaymentMethods() {
        return paymentMethods;
    }

    public void addComplaint(Complaint complaint) {
        complaints.add(complaint);
    }

    public List<Complaint> getComplaints() {
        return complaints;
    }

    public void setReprimanded(boolean reprimanded) {
        this.reprimanded = reprimanded;
    }

    public boolean isReprimanded() {
        return reprimanded;
    }
}
