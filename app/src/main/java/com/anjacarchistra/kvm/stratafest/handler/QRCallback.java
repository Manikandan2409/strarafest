package com.anjacarchistra.kvm.stratafest.handler;

public interface QRCallback {
    void  onSuccess(String message);
    void  onError(String errorMessage);
}
