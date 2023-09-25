package com.agendit.scheduler.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BusinessConfigResponse {

    private Long businessConfigId;

    private Boolean sendWhatsAppReminder;

    private Integer whatsAppReminderBeforeTime;

    private String whatsAppMessageVersion;

    private String whatsAppMessageId;

    private String whatsAppAccessToken;

    public Long getBusinessConfigId() {
        return businessConfigId;
    }

    public void setBusinessConfigId(Long businessConfigId) {
        this.businessConfigId = businessConfigId;
    }

    public Boolean getSendWhatsAppReminder() {
        return sendWhatsAppReminder;
    }

    public void setSendWhatsAppReminder(Boolean sendWhatsAppReminder) {
        this.sendWhatsAppReminder = sendWhatsAppReminder;
    }

    public Integer getWhatsAppReminderBeforeTime() {
        return whatsAppReminderBeforeTime;
    }

    public void setWhatsAppReminderBeforeTime(Integer whatsAppReminderBeforeTime) {
        this.whatsAppReminderBeforeTime = whatsAppReminderBeforeTime;
    }

    public String getWhatsAppMessageVersion() {
        return whatsAppMessageVersion;
    }

    public void setWhatsAppMessageVersion(String whatsAppMessageVersion) {
        this.whatsAppMessageVersion = whatsAppMessageVersion;
    }

    public String getWhatsAppMessageId() {
        return whatsAppMessageId;
    }

    public void setWhatsAppMessageId(String whatsAppMessageId) {
        this.whatsAppMessageId = whatsAppMessageId;
    }

    public String getWhatsAppAccessToken() {
        return whatsAppAccessToken;
    }

    public void setWhatsAppAccessToken(String whatsAppAccessToken) {
        this.whatsAppAccessToken = whatsAppAccessToken;
    }
}
