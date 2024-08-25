package org.kehrbusch.cin7backend.picking.application;

import org.kehrbusch.cin7backend.picking.entities.PickOrder;
import org.kehrbusch.cin7backend.picking.entities.PickOrderMapper;
import org.kehrbusch.cin7backend.picking.entities.PickToLocation;
import org.kehrbusch.cin7backend.picking.entities.api.StockApi;
import org.kehrbusch.cin7backend.picking.entities.api.StockProductApi;
import org.kehrbusch.cin7backend.picking.repository.database.PickOrderRepository;
import org.kehrbusch.cin7backend.planning.entities.SaleOrder;
import org.kehrbusch.cin7backend.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
public class PickingLogic {
    private final PickOrderRepository pickOrderRepository;
    private final PickOrderMapper pickOrderMapper;

    @Autowired
    public PickingLogic(PickOrderRepository pickOrderRepository, PickOrderMapper pickOrderMapper) {
        this.pickOrderRepository = pickOrderRepository;
        this.pickOrderMapper = pickOrderMapper;
    }

    public void triggerStockPicking(List<SaleOrder> saleOrders, PickCreateService.IStockProvider stockProvider) {
        //get all picks with sale id - if picks exist, then remove them & get create datetime
        Date createDate = DateUtil.getCurrentDateTime();
        Date modifyDate = DateUtil.getCurrentDateTime();
        if(!saleOrders.isEmpty()){
            PickOrder pickOrder = pickOrderRepository.removePickOrders(saleOrders.get(0).getSaleOrderId());
            createDate = pickOrder != null ? pickOrder.getCreateTimestamp() : createDate;
        }

        setTimestamps(saleOrders, createDate, modifyDate);
        String notFullToteId = pickOrderRepository.generateToteId();

        saleOrders.forEach(saleOrder ->
                stockProvider.provideStock(saleOrder, stock -> handleStockPicking(stock, saleOrder, notFullToteId)));
    }

    private void handleStockPicking(StockApi stock, SaleOrder saleOrder, String notFullToteId) {
        List<PickOrder> pickOrders = splitSaleOrder(saleOrder, stock);

        pickOrders.forEach(pickOrder -> {
            String toteId = pickOrder.getPickType() == PickOrder.PickType.FULL_BIN ? pickOrderRepository.generateToteId(notFullToteId) : notFullToteId;
            pickOrder.setToteNr(toteId);

            pickOrderRepository.saveOrUpdate(pickOrder);
        });
    }

    private List<PickOrder> splitSaleOrder(SaleOrder saleOrder, StockApi stock){
        List<PickOrder> pickOrders = new ArrayList<>();

        // sort stock locations
        stock.getStockProducts().sort((o1, o2) -> o2.getBin().compareTo(o1.getBin()));

        // identify locations for full totes
        List<StockProductApi> fullTotes = knapsackClosest(stock.getStockProducts(), saleOrder.getQuantity());
        fullTotes.forEach(fullTote -> {
            PickOrder pickOrder = pickOrderMapper.map(saleOrder, fullTote, PickOrder.PickType.FULL_BIN,
                    PickOrder.Status.OPEN, PickToLocation.FULL_LOCATION);
            pickOrders.add(pickOrder);
        });

        // identify locations for non-full totes
        long totalQty = fullTotes.stream().mapToLong(StockProductApi::getAvailable).sum();
        long remainder = saleOrder.getQuantity() - totalQty;
        if (remainder > 0){
            // remove chosen full totes
            List<StockProductApi> clearedTotes = new ArrayList<>(stock.getStockProducts());
            clearedTotes.removeAll(fullTotes);
            // identify tote with least qty which fulfills the order qty
            Optional<StockProductApi> optNonFullTote = clearedTotes.stream().min((o1, o2) -> o1.getAvailable() > o2.getAvailable() ? 1 : 0);
            if (optNonFullTote.isPresent()){
                PickOrder pickOrder = pickOrderMapper.map(saleOrder, optNonFullTote.get(), PickOrder.PickType.NON_FULL_BIN,
                        PickOrder.Status.OPEN, PickToLocation.NON_FULL_LOCATION, remainder);
                pickOrders.add(pickOrder);
            }
        }

        return pickOrders;
    }

    private List<StockProductApi> knapsackClosest(List<StockProductApi> stockLocations, long capacity) {
        int n = stockLocations.size();
        int iCapacity = (int) capacity;
        long[] dp = new long[iCapacity + 1];
        boolean[][] chosen = new boolean[n + 1][iCapacity + 1];

        // Build the DP table
        for (int i = 1; i <= n; i++) {
            long number = stockLocations.get(i - 1).getAvailable();
            for (int w = iCapacity; w >= number; w--) {
                if (dp[(int) (w - number)] + number > dp[w]) {
                    dp[w] = dp[(int) (w - number)] + number;
                    chosen[i][w] = true;
                }
            }
        }

        // Backtrack to find the selected items
        int w = iCapacity;
        List<StockProductApi> selectedItems = new ArrayList<>();
        for (int i = n; i > 0; i--) {
            if (chosen[i][w]) {
                selectedItems.add(stockLocations.get(i - 1));
                w -= stockLocations.get(i - 1).getAvailable();
            }
        }

        return selectedItems; // Return the closest fill to the capacity
    }

    private void setTimestamps(List<SaleOrder> saleOrders, Date createTimestamp, Date modifyTimestamp){
        for (SaleOrder saleOrder : saleOrders) {
            saleOrder.setCreateTimestamp(createTimestamp);
            saleOrder.setModifyTimestamp(modifyTimestamp);
        }
    }
}
