package com.anjacarchistra.kvm.stratafest.api;

public class Profile {
    public int getParticipantid() {
        return participantid;
    }

    public void setParticipantid(int participantid) {
        this.participantid = participantid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCollegename() {
        return collegename;
    }

    public void setCollegename(String collegename) {
        this.collegename = collegename;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Profile(int participantid, String name, String collegename, String departmentName, String eventName, String email, String password) {
        this.participantid = participantid;
        this.name = name;
        this.collegename = collegename;
        this.departmentName = departmentName;
        this.eventName = eventName;
        this.email = email;
        this.password = password;
    }

    private  int participantid;
    private  String name;
    private  String collegename;
    private  String departmentName;
    private  String eventName;
    private  String email;
    private  String password;


}
