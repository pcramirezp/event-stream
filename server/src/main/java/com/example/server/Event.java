package com.example.server;

import java.util.Date;

public class Event {
    private final long currentTimeMillis;
    private final Date date;
    private final double value;

    public double getValue() {
        return value;
    }

    public Event(long currentTimeMillis, Date date, Double value) {
        this.currentTimeMillis = currentTimeMillis;
        this.date = date;
        this.value = value;
    }

    public long getCurrentTimeMillis() {
        return currentTimeMillis;
    }

    public Date getDate() {
        return date;
    }
}
