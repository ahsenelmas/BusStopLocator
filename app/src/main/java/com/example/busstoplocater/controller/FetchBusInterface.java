    package com.example.busstoplocater.controller;

    import com.example.busstoplocater.model.BusStop;
    import com.example.busstoplocater.model.StopResponse;

    import java.util.List;

    public interface FetchBusInterface {
        public void fetchBusStops();
        public List<BusStop> parseStops(StopResponse response);
        public double distanceBetween(double lat1, double lon1, double lat2, double lon2);
        }
