package com.agendit.scheduler.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProfessionalResponse {

    private Long professionalId;

    private String professionalName;

    public Long getProfessionalId() {
        return professionalId;
    }

    public void setProfessionalId(Long professionalId) {
        this.professionalId = professionalId;
    }

    public String getProfessionalName() {
        return professionalName;
    }

    public void setProfessionalName(String professionalName) {
        this.professionalName = professionalName;
    }
}
