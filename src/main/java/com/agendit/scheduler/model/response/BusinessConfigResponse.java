package com.agendit.scheduler.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BusinessConfigResponse {

    private Long businessConfigId;

    private Boolean sendReminder;

    private Integer reminderBeforeTime;

    public Long getBusinessConfigId() {
        return businessConfigId;
    }

    public void setBusinessConfigId(Long businessConfigId) {
        this.businessConfigId = businessConfigId;
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
