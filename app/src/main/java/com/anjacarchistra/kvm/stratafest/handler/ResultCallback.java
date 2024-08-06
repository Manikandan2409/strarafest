package com.anjacarchistra.kvm.stratafest.handler;

import com.anjacarchistra.kvm.stratafest.dto.Event;
import com.anjacarchistra.kvm.stratafest.dto.Result;

import java.util.List;
import java.util.Set;

public interface ResultCallback {
        void onResultsSuccess(List<Result> results);
        void onError(String error);


}
