package com.example.notify.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EventModel {
    private long id;
    private String event_name;
    private Date event_date;
    private String event_location;
    private String event_poster = "";
    private Boolean isPrior = false;

    public EventModel(String event_name, String event_date, String event_location) {
        this.event_name = event_name;
        try {
            this.event_date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(event_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.event_location = event_location;
        this.isPrior = true;
    }

    public EventModel(long id, String event_name, String event_date, String event_location, String event_poster, Boolean isPrior) {
        this.id = id;
        this.event_name = event_name;
        try {
            Date date = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzzz yyyy").parse(event_date);
            String datestr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);

            this.event_date =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(datestr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.event_location = event_location;
        this.event_poster = event_poster;
        this.isPrior = isPrior;
    }

    public EventModel(String event_name, String event_date, String event_location, String event_poster, Boolean isPrior) {
        this.event_name = event_name;
        try {
            this.event_date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(event_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.event_location = event_location;
        this.event_poster = event_poster;
        this.isPrior = isPrior;
    }


    public long getId() {
        return id;
    }

    public Date getDate() {
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

    public Boolean getIsPrior() { return isPrior; }

    public void setLocation(String event_location) {
        this.event_location = event_location;
    }

    public void setDate(String event_date) {
        try {
            this.event_date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(event_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void setName(String event_name) {
        this.event_name = event_name;
    }

    public void setposter(String event_poster) {
        this.event_poster = event_poster;
    }

    public  void setPriority(Boolean isPrior) {
        this.isPrior = isPrior;
    }
}
