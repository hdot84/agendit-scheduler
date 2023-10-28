package com.agendit.scheduler.model;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ReminderConfig {

    @Value("${whatsapp.access.token}")
    public String whatsAppAccessToken;

    @Value("${whatsapp.message.id}")
    public String whatsAppMessageId;

}
