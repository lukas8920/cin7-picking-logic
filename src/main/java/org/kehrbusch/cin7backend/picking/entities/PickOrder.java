package org.kehrbusch.cin7backend.picking.entities;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "picking_pick_order")
public class PickOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private PickType pickType;
    private Date createTimestamp;
    private Date modifyTimestamp;
    private String toteNr;
    private String saleOrderId;
    private String product;
    private String fromLocation;
    private String toLocation;
    @Enumerated(EnumType.STRING)
    private Status status;
    private Long quantity;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PickType getPickType() {
        return pickType;
    }

    public void setPickType(PickType pickType) {
        this.pickType = pickType;
    }

    public String getToteNr() {
        return toteNr;
    }

    public void setToteNr(String binNr) {
        this.toteNr = binNr;
    }

    public String getSaleOrderId() {
        return saleOrderId;
    }

    public void setSaleOrderId(String saleOrderId) {
        this.saleOrderId = saleOrderId;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getFromLocation() {
        return fromLocation;
    }

    public void setFromLocation(String fromLocation) {
        this.fromLocation = fromLocation;
    }

    public String getToLocation() {
        return toLocation;
    }

    public void setToLocation(String toLocation) {
        this.toLocation = toLocation;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
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

    public enum PickType {
        FULL_BIN,
        NON_FULL_BIN
    }

    public enum Status {
        DONE,
        OPEN
    }
}
