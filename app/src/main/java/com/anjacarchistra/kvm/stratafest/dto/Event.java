package com.anjacarchistra.kvm.stratafest.dto;

import java.util.Set;

public class Event {
    public Event(int eventId, String eventName, int maxParticipant, int minParticipant, String time, String venue, Boolean prelims, Set<String> prelimswinner, Boolean winner, Set<String> prize) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.maxParticipant = maxParticipant;
        this.minParticipant = minParticipant;
        this.time = time;
        this.venue = venue;
        this.prelims = prelims;
        this.prelimswinner = prelimswinner;
        Winner = winner;
        this.prize = prize;
    }

    @Override
    public String toString() {
        return "Event{" +
                "eventId=" + eventId +
                ", eventName='" + eventName + '\'' +
                ", maxParticipant=" + maxParticipant +
                ", minParticipant=" + minParticipant +
                ", time='" + time + '\'' +
                ", venue='" + venue + '\'' +
                '}';
    }

    private int eventId;
    private String eventName;
    private int maxParticipant;
    private int minParticipant;
    private  String time;
    private  String venue;
    private Boolean prelims;
    private Set<String> prelimswinner;
    private  Boolean Winner;
    private Set<String> prize;

    public Boolean getPrelims() {
        return prelims;
    }

    public void setPrelims(Boolean prelims) {
        this.prelims = prelims;
    }

    public Set<String> getPrelimswinner() {
        return prelimswinner;
    }

    public void setPrelimswinner(Set<String> prelimswinner) {
        this.prelimswinner = prelimswinner;
    }

    public Boolean getWinner() {
        return Winner;
    }

    public void setWinner(Boolean winner) {
        Winner = winner;
    }

    public Set<String> getPrize() {
        return prize;
    }

    public void setPrize(Set<String> prize) {
        this.prize = prize;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public void setMaxParticipant(int maxParticipant) {
        this.maxParticipant = maxParticipant;
    }

    public void setMinParticipant(int minParticipant) {
        this.minParticipant = minParticipant;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }


    public Event(int eventId, String eventName, int maxParticipant, int minParticipant) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.maxParticipant = maxParticipant;
        this.minParticipant = minParticipant;
    }

    public Event(int eventId, String eventName, int maxParticipant, int minParticipant, String time, String venue) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.maxParticipant = maxParticipant;
        this.minParticipant = minParticipant;
        this.time = time;
        this.venue = venue;
    }
    public  Event(){}
    // Getters and setters
    public int getEventId() { return eventId; }
    public String getEventName() { return eventName; }
    public int getMaxParticipant() { return maxParticipant; }
    public int getMinParticipant() { return minParticipant; }

    public String getTime() {
        return time;
    }

    public String getVenue() {
        return venue;
    }
}
