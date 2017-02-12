package com.myapps.upesse.upes_spefest.ui.Models;

public class Event {

    private String event_name;

    public Event() {
        // empty default constructor, necessary for Firebase to be able to deserialize comments
    }

    public Event(String event_name) {
        this.event_name = event_name;
    }

    public String getEvent_name() {
        return event_name;
    }
}
