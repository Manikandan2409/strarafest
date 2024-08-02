package com.anjacarchistra.kvm.stratafest.handler;

import com.anjacarchistra.kvm.stratafest.dto.Event;

import java.util.List;

public interface ApiCallback {
    void onSuccess(List<Event> events);
    void onError(String error);
}

