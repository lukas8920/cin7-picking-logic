package org.kehrbusch.cin7backend.picking.application;

import org.kehrbusch.cin7backend.picking.entities.api.StockApi;
import org.kehrbusch.cin7backend.picking.repository.network.ProductAvailabilityApi;
import org.kehrbusch.cin7backend.planning.entities.SaleOrder;
import org.kehrbusch.cin7backend.planning.repository.database.SaleOrderRepository;
import org.kehrbusch.cin7backend.util.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;
import java.util.function.Consumer;

@Service
public class PickCreateService {
    private static final Logger logger = LoggerFactory.getLogger(PickCreateService.class);

    private static final Object pickLock = new Object();

    private final SaleOrderRepository saleOrderRepository;
    private final ProductAvailabilityApi availabilityApi;
    private final PickingLogic pickingLogic;

    @Autowired
    public PickCreateService(SaleOrderRepository saleOrderRepository,
                             ProductAvailabilityApi availabilityApi, PickingLogic pickingLogic){
        this.availabilityApi = availabilityApi;
        this.saleOrderRepository = saleOrderRepository;
        this.pickingLogic = pickingLogic;
    }

    public void onOrderAuthorized(String saleId) throws BadRequestException {
        if (saleId == null){
            throw new BadRequestException("Invalid sale id - " + saleId);
        }

        List<SaleOrder> saleOrder = saleOrderRepository.findSaleOrderById(saleId);
        if (saleOrder.isEmpty()){
            throw new BadRequestException("Not existing sale id - " + saleId);
        }

        new Thread(() -> runPickGeneration(saleOrder)).start();
    }

    private void runPickGeneration(List<SaleOrder> saleOrders){
        //lock to ensure no interfering pick creations
        synchronized (pickLock){
            pickingLogic.triggerStockPicking(saleOrders, (saleOrder, stockConsumer) -> {
                Call<StockApi> stockApi = availabilityApi.getStockApi(saleOrder.getSku());
                stockApi.enqueue(new Callback<>() {
                    @Override
                    public void onResponse(Call<StockApi> call, Response<StockApi> response) {
                        StockApi tmpStockApi = response.body();
                        logger.info("Received stock information for product - " + saleOrder.getSaleOrderId() + " - available - " + tmpStockApi.getTotalQty());
                        stockConsumer.accept(tmpStockApi);
                    }

                    @Override
                    public void onFailure(Call<StockApi> call, Throwable throwable) {
                        logger.error("Failed to get order at sale/order for sale id - " + saleOrder.getSaleOrderId() + " - product id - " + saleOrder.getProductId(), throwable);
                    }
                });
            });
        }
    }

    public interface IStockProvider {
        void provideStock(SaleOrder saleOrder, Consumer<StockApi> stockConsumer);
    }
}
