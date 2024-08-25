package org.kehrbusch.cin7backend.planning;

import org.kehrbusch.cin7backend.planning.application.OrderCreateService;
import org.kehrbusch.cin7backend.planning.application.OrderVoidedService;
import org.kehrbusch.cin7backend.planning.entities.api.OrderVoidedApi;
import org.kehrbusch.cin7backend.planning.entities.api.QuoteAuthorizedApi;
import org.kehrbusch.cin7backend.util.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/planning/order")
public class OrderController {
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    private final OrderCreateService orderCreateService;
    private final OrderVoidedService orderVoidedService;

    @Autowired
    public OrderController(OrderCreateService orderCreateService, OrderVoidedService orderVoidedService){
        this.orderCreateService = orderCreateService;
        this.orderVoidedService = orderVoidedService;
    }

    @PostMapping("/create")
    public ResponseEntity<String> handleCreateOrder(@RequestBody QuoteAuthorizedApi quoteAuthorizedApi)
            throws BadRequestException {
        logger.info("Received order notice with sale id - " + quoteAuthorizedApi.getSaleId());
        this.orderCreateService.onOrderCreated(quoteAuthorizedApi.getSaleId());
        return ResponseEntity.ok("Received order creation notice.");
    }

    @PostMapping("/voided")
    public ResponseEntity<String> handleOrderVoided(@RequestBody OrderVoidedApi orderVoidedApi) throws BadRequestException {
        logger.info("Receive order void notice for sale id - " + orderVoidedApi.getSaleId());
        this.orderVoidedService.onOrderVoided(orderVoidedApi.getSaleId());
        return ResponseEntity.ok("Received order voided notice.");
    }
}
