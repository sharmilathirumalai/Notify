package com.example.notify.model;

public class EventModel {
    private long id;
    private String event_name;
    private String event_date;
    private String event_location;
    private String event_poster;

    public EventModel(String event_name, String event_date, String event_location) {
        this.event_name = event_name;
        this.event_date = event_date;
        this.event_location = event_location;
    }

    public EventModel(long id, String event_name, String event_date, String event_location) {
        this.id = id;
        this.event_name = event_name;
        this.event_date = event_date;
        this.event_location = event_location;
    }

    public EventModel(long id, String event_name, String event_date, String event_location, String event_poster) {
        this.id = id;
        this.event_name = event_name;
        this.event_date = event_date;
        this.event_location = event_location;
        this.event_poster = event_poster;
    }


    public long getId() {
        return id;
    }

    public String getDate() {
        return event_date;
    }

    public String getLocation() {
        return event_location;
    }

    public String getName() {
        return event_name;
    }
    public String getposter() {
        return event_poster;
    }

    public void setLocation(String event_location) {
        this.event_location = event_location;
    }

    public void setDate(String event_date) {
        this.event_date = event_date;
    }

    public void setName(String event_name) {
        this.event_name = event_name;
    }

    public void setposter(String event_poster) {
        this.event_poster = event_poster;
    }
}
