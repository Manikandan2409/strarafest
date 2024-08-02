package com.anjacarchistra.kvm.stratafest.dto;

public class Event {
    @Override
    public String toString() {
        return "Event{" +
                "eventId=" + eventId +
                ", eventName='" + eventName + '\'' +
                ", maxParticipant=" + maxParticipant +
                ", minParticipant=" + minParticipant +
                '}';
    }



    private int eventId;
    private String eventName;
    private int maxParticipant;
    private int minParticipant;

    public Event(int eventId, String eventName, int maxParticipant, int minParticipant) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.maxParticipant = maxParticipant;
        this.minParticipant = minParticipant;
    }

    public  Event(){}
    // Getters and setters
    public int getEventId() { return eventId; }
    public String getEventName() { return eventName; }
    public int getMaxParticipant() { return maxParticipant; }
    public int getMinParticipant() { return minParticipant; }
}
