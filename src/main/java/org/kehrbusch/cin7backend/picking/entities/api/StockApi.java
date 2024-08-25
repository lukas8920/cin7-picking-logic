package org.kehrbusch.cin7backend.picking.entities.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class StockApi {
    @SerializedName("Total")
    private Long totalQty;
    @SerializedName("Page")
    private Long page;
    @SerializedName("ProductAvailabilityList")
    private List<StockProductApi> stockProducts;

    public List<StockProductApi> getStockProducts() {
        return stockProducts;
    }

    public void setStockProducts(List<StockProductApi> stockProducts) {
        this.stockProducts = stockProducts;
    }

    public Long getPage() {
        return page;
    }

    public void setPage(Long page) {
        this.page = page;
    }

    public Long getTotalQty() {
        return totalQty;
    }

    public void setTotalQty(Long totalQty) {
        this.totalQty = totalQty;
    }
}
