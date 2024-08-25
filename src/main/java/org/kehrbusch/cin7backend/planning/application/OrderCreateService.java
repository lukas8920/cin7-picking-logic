package org.kehrbusch.cin7backend.planning.application;

import org.kehrbusch.cin7backend.planning.entities.SaleOrder;
import org.kehrbusch.cin7backend.planning.entities.api.SaleOrderApi;
import org.kehrbusch.cin7backend.planning.entities.SaleOrderMapper;
import org.kehrbusch.cin7backend.planning.repository.database.SaleOrderRepository;
import org.kehrbusch.cin7backend.planning.repository.network.SaleApi;
import org.kehrbusch.cin7backend.util.BadRequestException;
import org.kehrbusch.cin7backend.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.Date;
import java.util.List;

@Service
public class OrderCreateService {
    private static final Logger logger = LoggerFactory.getLogger(OrderCreateService.class);

    private final SaleApi saleApi;
    private final SaleOrderRepository saleOrderRepository;
    private final SaleOrderMapper saleOrderMapper;

    @Autowired
    public OrderCreateService(@Qualifier("saleApi") SaleApi saleApi, SaleOrderRepository saleOrderRepository,
                              SaleOrderMapper saleOrderMapper){
        this.saleApi = saleApi;
        this.saleOrderRepository = saleOrderRepository;
        this.saleOrderMapper = saleOrderMapper;
    }

    public void onOrderCreated(String saleId) throws BadRequestException {
        if (saleId == null){
            throw new BadRequestException("Invalid sale id - " + saleId);
        }

        Date currentTimestamp = DateUtil.getCurrentDateTime();
        new Thread(() -> requestOrder(saleId, currentTimestamp)).start();
    }

    private void requestOrder(String saleId, Date currentTimestamp){
        Call<SaleOrderApi> orderApiCall = this.saleApi.getSaleOrder(saleId, false, true);
        orderApiCall.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<SaleOrderApi> call, Response<SaleOrderApi> response) {
                SaleOrderApi saleOrderApi = response.body();
                OrderCreateService.this.handleOrderLog(saleOrderApi, currentTimestamp);
            }

            @Override
            public void onFailure(Call<SaleOrderApi> call, Throwable throwable) {
                logger.error("Failed to get order at sale/order for sale id - " + saleId, throwable);
            }
        });
    }

    private void handleOrderLog(SaleOrderApi saleOrderApi, Date currentTimestamp){
        SaleOrder saleOrder = saleOrderRepository.removeSaleOrders(saleOrderApi.getSaleId());
        List<SaleOrder> saleOrders;
        if (saleOrder != null){
            saleOrders = saleOrderMapper.mapTimestamp(saleOrderApi, saleOrder.getCreateTimestamp(), currentTimestamp);
        } else {
            saleOrders = saleOrderMapper.mapTimestamp(saleOrderApi, currentTimestamp);
        }

        saleOrders.forEach(o -> {
            this.saleOrderRepository.saveOrUpdate(o);
            logger.info("Saved order to database with sale id - " + o.getSaleOrderId());
        });
    }
}
