package com.anjacarchistra.kvm.stratafest.handler;

import com.anjacarchistra.kvm.stratafest.dto.Lot;

import java.util.List;

public interface LotCallback {
    void onSucceed(List<Lot> lots);

    void onProblem(String error);
}
