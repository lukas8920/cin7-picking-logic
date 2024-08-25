package org.kehrbusch.cin7backend.planning.entities.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SaleOrderApi {
    @SerializedName("SaleID")
    private String saleId;
    @SerializedName("SaleOrderNumber")
    private String saleOrderNumber;
    @SerializedName("Memo")
    private String memo;
    @SerializedName("Status")
    private String status;
    @SerializedName("Lines")
    private List<ProductApi> lines;
    @SerializedName("TotalBeforeTax")
    private Long totalBeforeTax;
    @SerializedName("Tax")
    private Long tax;
    @SerializedName("Total")
    private Long total;

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

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getTotalBeforeTax() {
        return totalBeforeTax;
    }

    public void setTotalBeforeTax(Long totalBeforeTax) {
        this.totalBeforeTax = totalBeforeTax;
    }

    public Long getTax() {
        return tax;
    }

    public void setTax(Long tax) {
        this.tax = tax;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<ProductApi> getLines() {
        return lines;
    }

    public void setLines(List<ProductApi> lines) {
        this.lines = lines;
    }
}
