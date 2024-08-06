package com.anjacarchistra.kvm.stratafest.util;

public class FoodDetails {
    private int lotid;
    private String name;

    public FoodDetails(int lotid, String name) {
        this.lotid = lotid;
        this.name = name;
    }

    public int getLotid() {
        return lotid;
    }

    public String getName() {
        return name;
    }
}
