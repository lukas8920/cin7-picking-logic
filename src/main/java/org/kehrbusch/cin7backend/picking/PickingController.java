package org.kehrbusch.cin7backend.picking;

import org.kehrbusch.cin7backend.picking.application.PickCreateService;
import org.kehrbusch.cin7backend.picking.entities.api.OrderAuthorizedApi;
import org.kehrbusch.cin7backend.util.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/picking")
public class PickingController {
    private static final Logger logger = LoggerFactory.getLogger(PickingController.class);

    private final PickCreateService pickCreateService;

    @Autowired
    public PickingController(PickCreateService pickCreateService){
        this.pickCreateService = pickCreateService;
    }

    @PostMapping("/order/authorize")
    public ResponseEntity<String> handleAuthorizeOrder(@RequestBody OrderAuthorizedApi orderAuthorizedApi) throws BadRequestException {
        logger.info("Received order authorize webhook for sale id " + orderAuthorizedApi.getSaleId());
        this.pickCreateService.onOrderAuthorized(orderAuthorizedApi.getSaleId());
        return ResponseEntity.ok("Received order authorize webhook.");
    }
}
