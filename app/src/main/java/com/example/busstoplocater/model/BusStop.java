package com.example.busstoplocater.model;

public class BusStop {
    private String number;
    private String name;
    private double lat;
    private double lon;

    public BusStop(String name, String number, double lat, double lon) {
        this.number = number;
        this.name = name;
        this.lat = lat;
        this.lon = lon;
    }

    public String getNumber() { return number; }

    public String getName() { return name; }
    public double getLat() { return lat; }
    public double getLon() { return lon; }
}

// zoom into search find add the slupek
// bus stop cant find toast message it should be go to the home screen
