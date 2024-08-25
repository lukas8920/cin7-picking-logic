package org.kehrbusch.cin7backend.planning.entities;

import org.kehrbusch.cin7backend.planning.entities.api.SaleOrderApi;
import org.kehrbusch.cin7backend.planning.entities.api.ProductApi;
import org.kehrbusch.cin7backend.util.NumberUtil;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class SaleOrderMapper {
    public List<SaleOrder> mapTimestamp(SaleOrderApi saleOrderApi, Date createTimestamp, Date modifyTimestamp){
        List<SaleOrder> saleOrder = flattenOrder(saleOrderApi);
        saleOrder.forEach(o -> {
            o.setModifyTimestamp(modifyTimestamp);
            o.setCreateTimestamp(createTimestamp);
        });
        return saleOrder;
    }

    public List<SaleOrder> mapTimestamp(SaleOrderApi saleOrderApi, Date timestamp) {
        return mapTimestamp(saleOrderApi, timestamp, timestamp);
    }

    private List<SaleOrder> flattenOrder(SaleOrderApi saleOrderApi){
        List<SaleOrder> saleOrders = new ArrayList<>();
        if (saleOrderApi.getLines() != null && saleOrderApi.getLines().size() > 0){
            int lineSize = saleOrderApi.getLines().size();
            for (int i = 0; i < lineSize; i++){
                SaleOrder saleOrder = mapProduct(saleOrderApi, i);
                saleOrders.add(saleOrder);
            }
        } else {
            SaleOrder saleOrder = mapBase(saleOrderApi);
            saleOrders.add(saleOrder);
        }
        return saleOrders;
    }

    private SaleOrder mapProduct(SaleOrderApi saleOrderApi, int i){
        String lineNumber = NumberUtil.formatNumber(i + 1);
        ProductApi productApi = saleOrderApi.getLines().get(i);
        SaleOrder saleOrder = mapBase(saleOrderApi);

        saleOrder.setLineNumber(lineNumber);
        saleOrder.setProductId(productApi.getProductId());
        saleOrder.setSku(productApi.getSku());
        saleOrder.setName(productApi.getName());
        saleOrder.setQuantity(productApi.getQuantity());

        return saleOrder;
    }

    private SaleOrder mapBase(SaleOrderApi saleOrderApi){
        SaleOrder saleOrder = new SaleOrder();
        saleOrder.setSaleOrderId(saleOrderApi.getSaleId());
        saleOrder.setSaleOrderNumber(saleOrderApi.getSaleOrderNumber());
        saleOrder.setStatus(saleOrderApi.getStatus());
        return saleOrder;
    }
}
