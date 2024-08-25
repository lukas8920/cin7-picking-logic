package org.kehrbusch.cin7backend.planning.application;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kehrbusch.cin7backend.picking.repository.database.PickOrderRepository;
import org.kehrbusch.cin7backend.planning.repository.database.SaleOrderRepository;
import org.kehrbusch.cin7backend.util.BadRequestException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

public class OrderVoidedServiceTest {
    private static OrderVoidedService orderVoidedService;

    @BeforeAll
    public static void setup(){
        SaleOrderRepository saleOrderRepository = mock(SaleOrderRepository.class);
        PickOrderRepository pickOrderRepository = mock(PickOrderRepository.class);
        orderVoidedService = new OrderVoidedService(saleOrderRepository, pickOrderRepository);
    }

    @Test
    public void testThatOnOrderVoidedThrowsErrorForUnknown(){
        Exception exception = assertThrows(BadRequestException.class, () -> orderVoidedService.onOrderVoided(null ));
        assertThat(exception.getMessage(), is("Invalid sale id - null"));
    }
}
