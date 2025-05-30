package com.example.busstoplocater;

import com.example.busstoplocater.controller.FetchBusController;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DistanceTest {

    @Test
    public void testDistanceBetween_sameLocation_returnsZero() {
        FetchBusController dummy = new FetchBusController(null, null, 0, 0, "", "", "", null);
        double distance = dummy.distanceBetween(52.23, 21.01, 52.23, 21.01);
        assertEquals(0.0, distance, 0.0001);
    }

    @Test
    public void testDistanceBetween_knownPoints() {
        FetchBusController dummy = new FetchBusController(null, null, 0, 0, "", "", "", null);
        double distance = dummy.distanceBetween(52.23, 21.01, 52.25, 21.03);
        assertEquals(2.5, distance, 0.5);
    }
}
