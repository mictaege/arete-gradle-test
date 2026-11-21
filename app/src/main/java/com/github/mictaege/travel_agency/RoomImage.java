package com.github.mictaege.travel_agency;

import java.util.UUID;

public class RoomImage {
    private final String id = "RIMG-" + UUID.randomUUID();
    private final String fileName;
    private final String description;
    private final int width;
    private final int height;

    public RoomImage(final String fileName) {
        this(fileName, "");
    }

    public RoomImage(final String fileName, final String description) {
        this.fileName = fileName;
        this.description = description;
        this.width = 1920;
        this.height = 1080;
    }

    public String getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public String getDescription() {
        return description;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
