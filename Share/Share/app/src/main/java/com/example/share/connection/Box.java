package com.example.share.connection;

public class Box {
    private int lock_id;
    private String  name;
    private int     availability;
    private int     height;
    private int     length;
    private int     width;
    private String  status;

    public Box(int lock_id, String name, int availability, int height, int length, int width, String status) {
        this.lock_id = lock_id;
        this.name = name;
        this.availability = availability;
        this.height = height;
        this.length = length;
        this.width = width;
        this.status = status;
    }

    public int getLock_id() {
        return lock_id;
    }

    public String getName() {
        return name;
    }

    public int getAvailability() {
        return availability;
    }

    public int getHeight() {
        return height;
    }

    public int getLength() {
        return length;
    }

    public int getWidth() {
        return width;
    }

    public String getStatus() {
        return status;
    }

    public void setAvailability(int availability) {
        this.availability = availability;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
