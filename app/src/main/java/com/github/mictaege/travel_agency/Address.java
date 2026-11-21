package com.github.mictaege.travel_agency;

import java.util.UUID;

public class Address {
    private final String id = "ADRS-" + UUID.randomUUID();
    private String street;
    private String streetNumber;
    private String postalCode;
    private String city;
    private Country country;

    public Address(final String addressStr) {
        final String[] parts = addressStr.split("\n");
        this.street = parts[0].split(" ")[0];
        this.streetNumber = parts[0].split(" ")[1];
        this.postalCode = parts[1].split(" ")[0];
        this.city = parts[1].split(" ")[1];
        this.country = Country.valueOf(parts[2]);
    }

    public String getId() {
        return id;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(final String street) {
        this.street = street;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(final String streetNumber) {
        this.streetNumber = streetNumber;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(final String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(final String city) {
        this.city = city;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(final Country country) {
        this.country = country;
    }
}
