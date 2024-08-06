package com.anjacarchistra.kvm.stratafest.handler;

import com.anjacarchistra.kvm.stratafest.dto.Profile;

import java.util.List;

public interface AuthCallback {
    void onSuccess(Profile profiles);

    void onError(String errorMessage);
}
