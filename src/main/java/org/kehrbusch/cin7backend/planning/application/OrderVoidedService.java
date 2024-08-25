package org.kehrbusch.cin7backend.planning.application;

import org.kehrbusch.cin7backend.picking.repository.database.PickOrderRepository;
import org.kehrbusch.cin7backend.planning.repository.database.SaleOrderRepository;
import org.kehrbusch.cin7backend.util.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderVoidedService {
    private final SaleOrderRepository saleOrderRepository;
    private final PickOrderRepository pickOrderRepository;

    @Autowired
    public OrderVoidedService(SaleOrderRepository saleOrderRepository, PickOrderRepository pickOrderRepository){
        this.saleOrderRepository = saleOrderRepository;
        this.pickOrderRepository = pickOrderRepository;
    }

    public void onOrderVoided(String saleId) throws BadRequestException {
        if (saleId == null){
            throw new BadRequestException("Invalid sale id - " + saleId);
        }
        new Thread(() -> {
            saleOrderRepository.removeSaleOrders(saleId);
            pickOrderRepository.removePickOrders(saleId);
        }).start();
    }
}
