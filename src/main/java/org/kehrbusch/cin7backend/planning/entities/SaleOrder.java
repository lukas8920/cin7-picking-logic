package org.kehrbusch.cin7backend.planning.entities;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table (
        name = "planning_sale_order",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "sale_order_id")
        }
)
public class SaleOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sale_order_id")
    private String saleOrderId;
    private Date createTimestamp;
    private Date modifyTimestamp;
    private String saleOrderNumber;
    private String status;
    private String lineNumber;
    private String productId;
    private String sku;
    private String name;
    private Long quantity;

    public String getSaleOrderId() {
        return saleOrderId;
    }

    public void setSaleOrderId(String orderId) {
        this.saleOrderId = orderId;
    }

    public Date getCreateTimestamp() {
        return createTimestamp;
    }

    public void setCreateTimestamp(Date createTimestamp) {
        this.createTimestamp = createTimestamp;
    }

    public Date getModifyTimestamp() {
        return modifyTimestamp;
    }

    public void setModifyTimestamp(Date modifyTimestamp) {
        this.modifyTimestamp = modifyTimestamp;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSaleOrderNumber() {
        return saleOrderNumber;
    }

    public void setSaleOrderNumber(String saleOrderNumber) {
        this.saleOrderNumber = saleOrderNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(String lineNumber) {
        this.lineNumber = lineNumber;
    }

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
}
