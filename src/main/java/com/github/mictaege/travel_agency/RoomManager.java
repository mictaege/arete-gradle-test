package com.github.mictaege.travel_agency;

public class RoomManager {

    public Room setPopularity(final Room room, final int popularity) {
        room.setPopularity(popularity);
        return room;
    }

    public Room setPricePerNight(final Room room, final double pricePerNight) {
        room.setPricePerNight(pricePerNight);
        return room;
    }

}
