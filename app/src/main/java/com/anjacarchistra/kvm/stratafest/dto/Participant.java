package com.anjacarchistra.kvm.stratafest.dto;


public class Participant {
    @Override
    public String toString() {
        return "Participant{" +
                "name='" + name + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;

    public Participant(String name) {
        this.name = name;
    }
    public  Participant(){}




}
