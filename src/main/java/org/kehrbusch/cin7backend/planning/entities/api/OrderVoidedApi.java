package org.kehrbusch.cin7backend.planning.entities.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OrderVoidedApi {
    @JsonProperty("SaleID")
    private String saleId;
    @JsonProperty("SaleOrderNumber")
    private String saleOrderNumber;
    @JsonProperty("EventType")
    private String eventType;
    @JsonProperty("CustomerName")
    private String customerName;
    @JsonProperty("CustomerContactName")
    private String customerContactName;
    @JsonProperty("CustomerContactEmail")
    private String customerContactEmail;

    public String getSaleId() {
        return saleId;
    }

    public void setSaleId(String saleId) {
        this.saleId = saleId;
    }

    public String getSaleOrderNumber() {
        return saleOrderNumber;
    }

    public void setSaleOrderNumber(String saleOrderNumber) {
        this.saleOrderNumber = saleOrderNumber;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerContactName() {
        return customerContactName;
    }

    public void setCustomerContactName(String customerContactName) {
        this.customerContactName = customerContactName;
    }

    public String getCustomerContactEmail() {
        return customerContactEmail;
    }

    public void setCustomerContactEmail(String customerContactEmail) {
        this.customerContactEmail = customerContactEmail;
    }
}
