package org.kehrbusch.cin7backend.picking.application;

import org.kehrbusch.cin7backend.picking.entities.PickOrder;
import org.kehrbusch.cin7backend.picking.entities.PickOrderMapper;
import org.kehrbusch.cin7backend.picking.entities.PickToLocation;
import org.kehrbusch.cin7backend.picking.entities.api.StockApi;
import org.kehrbusch.cin7backend.picking.entities.api.StockProductApi;
import org.kehrbusch.cin7backend.picking.repository.database.PickOrderRepository;
import org.kehrbusch.cin7backend.planning.entities.SaleOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

public class PickLogicTest {
    private PickingLogic pickingLogic;
    private PickOrderRepository mockPickOrderRepository;
    private ArgumentCaptor<PickOrder> argumentCaptor;

    private int counter = 0;

    @BeforeEach
    public void setup(){
        counter = 0;

        PickOrderMapper pickOrderMapper = new PickOrderMapper();

        mockPickOrderRepository = mock(PickOrderRepository.class);
        argumentCaptor = ArgumentCaptor.forClass(PickOrder.class);

        pickingLogic = new PickingLogic(mockPickOrderRepository, pickOrderMapper);
        Answer<String> answer = invocation -> {
            counter += 1;
            return String.format("%06d", counter);
        };
        when(mockPickOrderRepository.generateToteId()).thenAnswer(answer);
        when(mockPickOrderRepository.generateToteId(any())).thenAnswer(answer);
    }

    private StockApi setUpSku1(){
        StockApi stock = new StockApi();
        StockProductApi productLocation1 = new StockProductApi();
        productLocation1.setSku("sku1");
        productLocation1.setBin("loc1");
        productLocation1.setAvailable(200L);
        StockProductApi productLocation2 = new StockProductApi();
        productLocation2.setSku("sku1");
        productLocation2.setBin("loc2");
        productLocation2.setAvailable(200L);
        stock.setStockProducts(Arrays.asList(productLocation1, productLocation2));
        return stock;
    }

    private StockApi setUpSku1AndSku2(){
        StockApi stock = setUpSku1();
        StockProductApi productLocation3 = new StockProductApi();
        productLocation3.setSku("sku2");
        productLocation3.setBin("loc3");
        productLocation3.setAvailable(100L);
        StockProductApi productLocation4 = new StockProductApi();
        productLocation4.setSku("sku2");
        productLocation4.setBin("loc4");
        productLocation4.setAvailable(300L);
        List<StockProductApi> existingProducts = new ArrayList<>(stock.getStockProducts());
        existingProducts.addAll(Arrays.asList(productLocation3, productLocation4));
        stock.setStockProducts(existingProducts);
        return stock;
    }

    //in cin7 terms a bin is a specific rack location
    @Test
    public void testFullToteAndNonFullToteSingleSku(){
        StockApi stock = setUpSku1();
        SaleOrder saleOrder = new SaleOrder();
        saleOrder.setSku("sku1");
        saleOrder.setQuantity(300L);
        List<SaleOrder> saleOrders = Collections.singletonList(saleOrder);
        PickCreateService.IStockProvider stockProvider = (tmpSaleOrder, stockConsumer) -> {
            stockConsumer.accept(stock);
        };

        pickingLogic.triggerStockPicking(saleOrders, stockProvider);

        verify(mockPickOrderRepository, times(2)).saveOrUpdate(argumentCaptor.capture());
        List<PickOrder> pickOrders = new ArrayList<>(argumentCaptor.getAllValues());

        assertThat(pickOrders.size(), is(2));
        validatePickOrder(pickOrders.get(0), "sku1", 200L, PickOrder.PickType.FULL_BIN, "loc2", "000002", PickOrder.Status.OPEN, PickToLocation.FULL_LOCATION);
        validatePickOrder(pickOrders.get(1), "sku1", 100L, PickOrder.PickType.NON_FULL_BIN, "loc1", "000001", PickOrder.Status.OPEN, PickToLocation.NON_FULL_LOCATION);
    }

    @Test
    public void testMultiFullTotesSingleSku(){
        StockApi stock = setUpSku1();
        SaleOrder saleOrder = new SaleOrder();
        saleOrder.setSku("sku1");
        saleOrder.setQuantity(400L);
        List<SaleOrder> saleOrders = Collections.singletonList(saleOrder);
        PickCreateService.IStockProvider stockProvider = (tmpSaleOrder, stockConsumer) -> {
            stockConsumer.accept(stock);
        };

        pickingLogic.triggerStockPicking(saleOrders, stockProvider);

        verify(mockPickOrderRepository, times(2)).saveOrUpdate(argumentCaptor.capture());
        List<PickOrder> pickOrders = new ArrayList<>(argumentCaptor.getAllValues());

        assertThat(pickOrders.size(), is(2));
        validatePickOrder(pickOrders.get(0), "sku1", 200L, PickOrder.PickType.FULL_BIN, "loc1", "000002", PickOrder.Status.OPEN, PickToLocation.FULL_LOCATION);
        validatePickOrder(pickOrders.get(1), "sku1", 200L, PickOrder.PickType.FULL_BIN, "loc2", "000003", PickOrder.Status.OPEN, PickToLocation.FULL_LOCATION);
    }

    @Test
    public void testOnlyNonFullToteSingleSku(){
        StockApi stock = setUpSku1();
        SaleOrder saleOrder = new SaleOrder();
        saleOrder.setSku("sku1");
        saleOrder.setQuantity(100L);
        List<SaleOrder> saleOrders = Collections.singletonList(saleOrder);
        PickCreateService.IStockProvider stockProvider = (tmpSaleOrder, stockConsumer) -> {
            stockConsumer.accept(stock);
        };

        pickingLogic.triggerStockPicking(saleOrders, stockProvider);

        verify(mockPickOrderRepository).saveOrUpdate(argumentCaptor.capture());
        List<PickOrder> pickOrders = new ArrayList<>(argumentCaptor.getAllValues());

        assertThat(pickOrders.size(), is(1));
        validatePickOrder(pickOrders.get(0), "sku1", 100L, PickOrder.PickType.NON_FULL_BIN, "loc2", "000001", PickOrder.Status.OPEN, PickToLocation.NON_FULL_LOCATION);
    }

    @Test
    public void testMultiFullTotesMultiSkus(){
        StockApi stock = setUpSku1AndSku2();
        SaleOrder saleOrder1 = new SaleOrder();
        saleOrder1.setSku("sku1");
        saleOrder1.setQuantity(400L);
        SaleOrder saleOrder2 = new SaleOrder();
        saleOrder2.setSku("sku2");
        saleOrder2.setQuantity(400L);
        List<StockProductApi> productLocations = new ArrayList<>(stock.getStockProducts());
        List<SaleOrder> saleOrders = Arrays.asList(saleOrder1, saleOrder2);
        PickCreateService.IStockProvider stockProvider = (tmpSaleOrder, stockConsumer) -> {
            if (tmpSaleOrder.getSku().equals("sku1")){
                stock.setStockProducts(productLocations.stream().filter(p -> p.getSku().equals("sku1")).collect(Collectors.toList()));
                stockConsumer.accept(stock);
            } else {
                stock.setStockProducts(productLocations.stream().filter(p -> p.getSku().equals("sku2")).collect(Collectors.toList()));
                stockConsumer.accept(stock);
            }
        };

        pickingLogic.triggerStockPicking(saleOrders, stockProvider);

        verify(mockPickOrderRepository, times(4)).saveOrUpdate(argumentCaptor.capture());
        List<PickOrder> pickOrders = new ArrayList<>(argumentCaptor.getAllValues());

        validatePickOrder(pickOrders.get(0), "sku1", 200L, PickOrder.PickType.FULL_BIN, "loc1", "000002", PickOrder.Status.OPEN, PickToLocation.FULL_LOCATION);
        validatePickOrder(pickOrders.get(1), "sku1", 200L, PickOrder.PickType.FULL_BIN, "loc2", "000003", PickOrder.Status.OPEN, PickToLocation.FULL_LOCATION);
        validatePickOrder(pickOrders.get(2), "sku2", 100L, PickOrder.PickType.FULL_BIN, "loc3", "000004", PickOrder.Status.OPEN, PickToLocation.FULL_LOCATION);
        validatePickOrder(pickOrders.get(3), "sku2", 300L, PickOrder.PickType.FULL_BIN, "loc4", "000005", PickOrder.Status.OPEN, PickToLocation.FULL_LOCATION);
    }

    @Test
    public void testOnlyNonFullToteMultiSkus(){
        StockApi stock = setUpSku1AndSku2();
        SaleOrder saleOrder1 = new SaleOrder();
        saleOrder1.setSku("sku1");
        saleOrder1.setQuantity(100L);
        SaleOrder saleOrder2 = new SaleOrder();
        saleOrder2.setSku("sku2");
        saleOrder2.setQuantity(50L);
        List<StockProductApi> productLocations = new ArrayList<>(stock.getStockProducts());
        List<SaleOrder> saleOrders = Arrays.asList(saleOrder1, saleOrder2);
        PickCreateService.IStockProvider stockProvider = (tmpSaleOrder, stockConsumer) -> {
            if (tmpSaleOrder.getSku().equals("sku1")){
                stock.setStockProducts(productLocations.stream().filter(p -> p.getSku().equals("sku1")).collect(Collectors.toList()));
                stockConsumer.accept(stock);
            } else {
                stock.setStockProducts(productLocations.stream().filter(p -> p.getSku().equals("sku2")).collect(Collectors.toList()));
                stockConsumer.accept(stock);
            }
        };

        pickingLogic.triggerStockPicking(saleOrders, stockProvider);

        verify(mockPickOrderRepository, times(2)).saveOrUpdate(argumentCaptor.capture());
        List<PickOrder> pickOrders = new ArrayList<>(argumentCaptor.getAllValues());

        validatePickOrder(pickOrders.get(0), "sku1", 100L, PickOrder.PickType.NON_FULL_BIN, "loc2", "000001", PickOrder.Status.OPEN, PickToLocation.NON_FULL_LOCATION);
        validatePickOrder(pickOrders.get(1), "sku2", 50L, PickOrder.PickType.NON_FULL_BIN, "loc3", "000001", PickOrder.Status.OPEN, PickToLocation.NON_FULL_LOCATION);
        assertThat(pickOrders.size(), is(2));
    }

    @Test
    public void notEnoughStockSingleStock(){
        StockApi stock = setUpSku1();
        SaleOrder saleOrder = new SaleOrder();
        saleOrder.setSku("sku1");
        saleOrder.setQuantity(600L);
        List<SaleOrder> saleOrders = Collections.singletonList(saleOrder);
        PickCreateService.IStockProvider stockProvider = (tmpSaleOrder, stockConsumer) -> {
            stockConsumer.accept(stock);
        };

        pickingLogic.triggerStockPicking(saleOrders, stockProvider);

        verify(mockPickOrderRepository, times(2)).saveOrUpdate(argumentCaptor.capture());
        List<PickOrder> pickOrders = new ArrayList<>(argumentCaptor.getAllValues());

        assertThat(pickOrders.size(), is(2));
        validatePickOrder(pickOrders.get(0), "sku1", 200L, PickOrder.PickType.FULL_BIN, "loc1", "000002", PickOrder.Status.OPEN, PickToLocation.FULL_LOCATION);
        validatePickOrder(pickOrders.get(1), "sku1", 200L, PickOrder.PickType.FULL_BIN, "loc2", "000003", PickOrder.Status.OPEN, PickToLocation.FULL_LOCATION);
    }

    private void validatePickOrder(PickOrder pickOrder, String sku, Long quantity, PickOrder.PickType pickType, String location,
            String toteNr, PickOrder.Status status, String pickToLocation){
        assertThat(pickOrder.getProduct(), is(sku));
        assertThat(pickOrder.getQuantity(), is(quantity));
        assertThat(pickOrder.getPickType(), is(pickType));
        assertThat(pickOrder.getFromLocation(), is(location));
        assertThat(pickOrder.getToteNr(), is(toteNr));
        assertThat(pickOrder.getStatus(), is(status));
        assertThat(pickOrder.getToLocation(), is(pickToLocation));
    }
}
