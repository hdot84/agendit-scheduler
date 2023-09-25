package com.agendit.scheduler.model.response;

import com.agendit.scheduler.model.request.AppointmentState;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AppointmentResponse {

    private Long appointmentId;

    private Long businessId;

    private BranchResponse branch;

    private ProfessionalResponse professional;

    private ServiceResponse service;

    private Date appointmentDateStart;

    private Date appointmentDateEnd;

    private Integer duration;

    private String customerName;

    private String customerPhone;

    private String customerEmail;

    private AppointmentState state;

    private String extraServices;

    public Long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
    }

    public BranchResponse getBranch() {
        return branch;
    }

    public void setBranch(BranchResponse branch) {
        this.branch = branch;
    }

    public ProfessionalResponse getProfessional() {
        return professional;
    }

    public void setProfessional(ProfessionalResponse professional) {
        this.professional = professional;
    }

    public ServiceResponse getService() {
        return service;
    }

    public void setService(ServiceResponse service) {
        this.service = service;
    }

    public AppointmentState getState() {
        return state;
    }

    public void setState(AppointmentState state) {
        this.state = state;
    }

    public String getExtraServices() {
        return extraServices;
    }

    public void setExtraServices(String extraServices) {
        this.extraServices = extraServices;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public Date getAppointmentDateStart() {
        return appointmentDateStart;
    }

    public void setAppointmentDateStart(Date appointmentDateStart) {
        this.appointmentDateStart = appointmentDateStart;
    }

    public Date getAppointmentDateEnd() {
        return appointmentDateEnd;
    }

    public void setAppointmentDateEnd(Date appointmentDateEnd) {
        this.appointmentDateEnd = appointmentDateEnd;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }
}
