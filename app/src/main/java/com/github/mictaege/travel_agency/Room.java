package com.github.mictaege.travel_agency;

import java.util.List;
import java.util.UUID;

public class Room {
    private final String id = "ROOM-" + UUID.randomUUID();
    private final int maxPersons;
    private final Bed bed;
    private final List<Facilities> facilities;
    private RoomState state;
    private int popularity;
    private double pricePerNight;
    private List<RoomImage> images;
    private int daysBeforeStartCancellationPossible;

    public Room(final int maxPersons,
                final Bed bed,
                final Facilities... facilities) {
        this.maxPersons = maxPersons;
        this.bed = bed;
        this.facilities = List.of(facilities);
        this.state = RoomState.AVAILABLE;
        this.popularity = 0;
        this.images = List.of();
        this.daysBeforeStartCancellationPossible = 3;
    }

    public Room(final int maxPersons,
                final Bed bed,
                final RoomState state,
                final Facilities... facilities) {
        this.maxPersons = maxPersons;
        this.bed = bed;
        this.facilities = List.of(facilities);
        this.state = state;
        this.popularity = 0;
        this.images = List.of();
        this.daysBeforeStartCancellationPossible = 3;
    }

    public String getId() {
        return id;
    }

    public int getMaxPersons() {
        return maxPersons;
    }

    public Bed getBed() {
        return bed;
    }

    public List<Facilities> getFacilities() {
        return facilities;
    }

    public RoomState getState() {
        return state;
    }

    public void setState(final RoomState state) {
        this.state = state;
    }

    public int getPopularity() {
        return popularity;
    }

    public void setPopularity(final int popularity) {
        this.popularity = popularity;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(final double pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public List<RoomImage> getImages() {
        return images;
    }

    public void setImages(final List<RoomImage> images) {
        this.images = images;
    }

    public int getDaysBeforeStartCancellationPossible() {
        return daysBeforeStartCancellationPossible;
    }

    public void setDaysBeforeStartCancellationPossible(final int daysBeforeStartCancellationPossible) {
        this.daysBeforeStartCancellationPossible = daysBeforeStartCancellationPossible;
    }
}
