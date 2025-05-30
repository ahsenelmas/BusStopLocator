package com.example.busstoplocater.model;

import java.util.List;

public class StopResponse {
    public List<Result> result;

    public static class Result {
        public Values[] values;
    }

    public static class Values {
        public String key;
        public String value;
    }
}