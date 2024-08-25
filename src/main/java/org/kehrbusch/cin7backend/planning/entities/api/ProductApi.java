package org.kehrbusch.cin7backend.planning.entities.api;

import com.google.gson.annotations.SerializedName;

public class ProductApi {
    @SerializedName("ProductID")
    private String productId;
    @SerializedName("SKU")
    private String sku;
    @SerializedName("Name")
    private String name;
    @SerializedName("Quantity")
    private Long quantity;
    @SerializedName("Price")
    private Long price;
    @SerializedName("Discount")
    private Long discount;
    @SerializedName("Tax")
    private Long tax;
    @SerializedName("AverageCost")
    private Long averageCost;
    @SerializedName("TaxRule")
    private String taxRule;
    @SerializedName("Comment")
    private String comment;
    @SerializedName("DropShip")
    private Boolean dropShip;
    @SerializedName("BackorderQuantity")
    private Long backorderQuantity;
    @SerializedName("Total")
    private Long total;

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
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

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public Long getDiscount() {
        return discount;
    }

    public void setDiscount(Long discount) {
        this.discount = discount;
    }

    public Long getTax() {
        return tax;
    }

    public void setTax(Long tax) {
        this.tax = tax;
    }

    public Long getAverageCost() {
        return averageCost;
    }

    public void setAverageCost(Long averageCost) {
        this.averageCost = averageCost;
    }

    public String getTaxRule() {
        return taxRule;
    }

    public void setTaxRule(String taxRule) {
        this.taxRule = taxRule;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Boolean getDropShip() {
        return dropShip;
    }

    public void setDropShip(Boolean dropShip) {
        this.dropShip = dropShip;
    }

    public Long getBackorderQuantity() {
        return backorderQuantity;
    }

    public void setBackorderQuantity(Long backorderQuantity) {
        this.backorderQuantity = backorderQuantity;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}
