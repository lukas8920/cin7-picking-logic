package org.kehrbusch.cin7backend.picking.entities.api;

import com.google.gson.annotations.SerializedName;

public class StockProductApi {
    @SerializedName("ID")
    private String id;
    @SerializedName("SKU")
    private String sku;
    @SerializedName("Name")
    private String name;
    @SerializedName("Barcode")
    private String barcode;
    @SerializedName("Location")
    private String location;
    @SerializedName("Bin")
    private String bin;
    @SerializedName("Batch")
    private String batch;
    @SerializedName("ExpiryDate")
    private String expiryDate;
    @SerializedName("OnHand")
    private Long onHand;
    @SerializedName("Allocated")
    private Long allocated;
    @SerializedName("Available")
    private Long available;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getBin() {
        return bin;
    }

    public void setBin(String bin) {
        this.bin = bin;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Long getOnHand() {
        return onHand;
    }

    public void setOnHand(Long onHand) {
        this.onHand = onHand;
    }

    public Long getAllocated() {
        return allocated;
    }

    public void setAllocated(Long allocated) {
        this.allocated = allocated;
    }

    public Long getAvailable() {
        return available;
    }

    public void setAvailable(Long available) {
        this.available = available;
    }
}
