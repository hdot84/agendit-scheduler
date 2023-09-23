package com.agendit.scheduler.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BusinessConfigResponse {

    private Long businessConfigId;

    private Boolean sendWhatsAppReminder;

    private Integer whatsappReminderBeforeTime;

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

    public Integer getWhatsappReminderBeforeTime() {
        return whatsappReminderBeforeTime;
    }

    public void setWhatsappReminderBeforeTime(Integer whatsappReminderBeforeTime) {
        this.whatsappReminderBeforeTime = whatsappReminderBeforeTime;
    }
}
