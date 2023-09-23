package com.agendit.scheduler.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Business {
    private Long businessId;

    private String businessName;

    private byte[] logo;

    private BusinessConfigResponse businessConfig;

    public Long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public byte[] getLogo() {
        return logo;
    }

    public void setLogo(byte[] logo) {
        this.logo = logo;
    }

    public BusinessConfigResponse getBusinessConfig() {
        return businessConfig;
    }

    public void setBusinessConfig(BusinessConfigResponse businessConfig) {
        this.businessConfig = businessConfig;
    }
}
