package com.agendit.scheduler.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BusinessConfigResponse {

    private Long businessConfigId;

    private Boolean emailEnabled;

    private Boolean smsEnabled;

    private Boolean whatsAppEnabled;

    private Boolean sendInitialNotification;

    private Boolean sendReminder;

    private Integer reminderBeforeTime;

    public Long getBusinessConfigId() {
        return businessConfigId;
    }

    public void setBusinessConfigId(Long businessConfigId) {
        this.businessConfigId = businessConfigId;
    }

    public Boolean getEmailEnabled() {
        return emailEnabled;
    }

    public void setEmailEnabled(Boolean emailEnabled) {
        this.emailEnabled = emailEnabled;
    }

    public Boolean getSmsEnabled() {
        return smsEnabled;
    }

    public void setSmsEnabled(Boolean smsEnabled) {
        this.smsEnabled = smsEnabled;
    }

    public Boolean getWhatsAppEnabled() {
        return whatsAppEnabled;
    }

    public void setWhatsAppEnabled(Boolean whatsAppEnabled) {
        this.whatsAppEnabled = whatsAppEnabled;
    }

    public Boolean getSendInitialNotification() {
        return sendInitialNotification;
    }

    public void setSendInitialNotification(Boolean sendInitialNotification) {
        this.sendInitialNotification = sendInitialNotification;
    }

    public Boolean getSendReminder() {
        return sendReminder;
    }

    public void setSendReminder(Boolean sendReminder) {
        this.sendReminder = sendReminder;
    }

    public Integer getReminderBeforeTime() {
        return reminderBeforeTime;
    }

    public void setReminderBeforeTime(Integer reminderBeforeTime) {
        this.reminderBeforeTime = reminderBeforeTime;
    }
}
