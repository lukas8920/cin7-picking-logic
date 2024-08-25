package org.kehrbusch.cin7backend.picking.entities.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OrderAuthorizedApi {
    @JsonProperty("SaleID")
    private String saleId;
    @JsonProperty("SaleOrderNumber")
    private String saleOrderNumber;
    @JsonProperty("EventType")
    private String eventType;

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
}
