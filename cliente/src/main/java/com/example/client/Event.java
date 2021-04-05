package com.example.client;

import java.util.Date;

public class Event {
    private  long currentTimeMillis;
    private  Date date;
    private double value;


    public long getCurrentTimeMillis() {
        return currentTimeMillis;
    }

    public Date getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "Event{" +
                "currentTimeMillis=" + currentTimeMillis +
                ", date=" + date +
                ", value=" + value +
                '}';
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
