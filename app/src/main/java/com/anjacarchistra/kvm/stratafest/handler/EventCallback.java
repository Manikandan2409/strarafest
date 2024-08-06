package com.anjacarchistra.kvm.stratafest.handler;

import com.anjacarchistra.kvm.stratafest.dto.Event;
import com.anjacarchistra.kvm.stratafest.dto.Lot;
import com.anjacarchistra.kvm.stratafest.dto.Result;

import java.util.List;

public interface EventCallback {
   void onSuccess(List<Event> events);


    void onError(String error);
}
