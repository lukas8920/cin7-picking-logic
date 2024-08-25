package org.kehrbusch.cin7backend.planning.repository.database;

import org.kehrbusch.cin7backend.planning.entities.SaleOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class SaleOrderRepository {
    private final SaleOrderJpaRepository saleOrderJpaRepository;

    @Autowired
    public SaleOrderRepository(SaleOrderJpaRepository saleOrderJpaRepository){
        this.saleOrderJpaRepository = saleOrderJpaRepository;
    }

    public SaleOrder saveOrUpdate(SaleOrder saleOrder) {
        return this.saleOrderJpaRepository.save(saleOrder);
    }

    @Transactional
    public SaleOrder removeSaleOrders(String saleId){
        SaleOrder saleOrder = saleOrderJpaRepository.findFirstBySaleOrderId(saleId);
        if (saleOrder != null){
            this.saleOrderJpaRepository.deleteAllBySaleOrderId(saleId);
        }
        return saleOrder;
    }

    public List<SaleOrder> findSaleOrderById(String saleId){
        return this.saleOrderJpaRepository.findBySaleOrderIdIs(saleId);
    }
}
