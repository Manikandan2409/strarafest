package com.anjacarchistra.kvm.stratafest;

public class Student {
    private String name;
    private String collegeName;
    private String prize;
    private String eventName;
    public Student(String name, String collegeName, String prize, String eventName) {
        this.name = name;
        this.collegeName = collegeName;
        this.prize = prize;
        this.eventName = eventName;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCollege() { return collegeName; }
    public void setCollege(String collegeName) { this.collegeName = collegeName; }

    public String getPrize()
    {
        return prize;
    }

    public void setPrize(String prize) { this.prize = prize; }

    public String getEventName() { return eventName; }

    public void setEventName(String eventName) { this.eventName = eventName; }

    @Override
    public String toString() {
        return "Student{" +
                "name='" + name + '\'' +
                ", collegeName='" + collegeName + '\'' +
                ", prize=" + prize +
                ", eventName='" + eventName + '\'' +
                '}';
    }
}
