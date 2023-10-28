package com.agendit.scheduler.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PhoneVerificationRequest {
    @JsonProperty("phone_number")
    private String phoneNumber;
    @JsonProperty("verify_code")
    private String verifyCode;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }
}
