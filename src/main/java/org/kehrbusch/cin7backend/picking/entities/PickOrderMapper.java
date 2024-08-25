package org.kehrbusch.cin7backend.picking.entities;

import org.kehrbusch.cin7backend.picking.entities.api.StockProductApi;
import org.kehrbusch.cin7backend.planning.entities.SaleOrder;
import org.springframework.stereotype.Component;

@Component
public class PickOrderMapper {
    public PickOrder map(SaleOrder saleOrder, StockProductApi stockProductApi,
                  PickOrder.PickType pickType, PickOrder.Status status, String toLocation){
        PickOrder pickOrder = new PickOrder();
        pickOrder.setPickType(pickType);
        pickOrder.setSaleOrderId(saleOrder.getSaleOrderId());
        pickOrder.setCreateTimestamp(saleOrder.getCreateTimestamp());
        pickOrder.setModifyTimestamp(saleOrder.getModifyTimestamp());
        pickOrder.setProduct(stockProductApi.getSku());
        pickOrder.setQuantity(stockProductApi.getAvailable());
        pickOrder.setStatus(status);
        pickOrder.setFromLocation(stockProductApi.getBin());
        pickOrder.setToLocation(toLocation);
        return pickOrder;
    }

    public PickOrder map(SaleOrder saleOrder, StockProductApi stockProductApi,
                         PickOrder.PickType pickType, PickOrder.Status status, String toLocation, long remainder){
        PickOrder pickOrder = map(saleOrder, stockProductApi, pickType, status, toLocation);
        pickOrder.setQuantity(remainder);
        return pickOrder;
    }
}
