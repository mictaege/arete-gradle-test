package com.github.mictaege.travel_agency;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class Accommodation {
    private final String id = "ACMD-" + UUID.randomUUID();
    private final String name;
    private final Address address;
    private final List<Room> rooms;

    public Accommodation(final String fullAddress,
                         final Room... rooms) {
        this.name = fullAddress.split("\n")[0].trim();
        this.address = new Address(fullAddress.replace(name, "").trim());
        this.rooms = List.of(rooms);
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

}
