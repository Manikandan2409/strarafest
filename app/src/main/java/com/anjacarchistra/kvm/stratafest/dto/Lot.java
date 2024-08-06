package com.anjacarchistra.kvm.stratafest.dto;

public class Lot {
    private String lotId;
    private String lotName;

    public Lot(String lotId, String lotName) {
        this.lotId = lotId;
        this.lotName = lotName;
    }

    public String getLotId() {
        return lotId;
    }

    public String getLotName() {
        return lotName;
    }
}
