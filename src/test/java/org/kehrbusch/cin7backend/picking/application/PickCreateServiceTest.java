package org.kehrbusch.cin7backend.picking.application;

import org.kehrbusch.cin7backend.picking.repository.network.ProductAvailabilityApi;
import org.kehrbusch.cin7backend.planning.repository.database.SaleOrderRepository;
import org.kehrbusch.cin7backend.util.BadRequestException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

public class PickCreateServiceTest {
    private static PickCreateService pickCreateService;
    private static SaleOrderRepository mockSaleOrderRepo;

    @BeforeAll
    public static void setup(){
        mockSaleOrderRepo = mock(SaleOrderRepository.class);
        ProductAvailabilityApi mockProductAvailability = mock(ProductAvailabilityApi.class);
        PickingLogic pickingLogic = Mockito.mock(PickingLogic.class);

        pickCreateService = new PickCreateService(mockSaleOrderRepo, mockProductAvailability, pickingLogic);
    }

    @Test
    public void testThatOnOrderAuthorizedThrowsErrorForNull(){
        String saleId = null;
        Exception exception = assertThrows(BadRequestException.class, () -> pickCreateService.onOrderAuthorized(saleId));
        assertThat(exception.getMessage(), is("Invalid sale id - null"));
    }

    @Test
    public void testThatOnOrderAuthorizedThrowsErrorNonExistentSaleOrder() {
        String saleId = "1";
        when(mockSaleOrderRepo.findSaleOrderById(saleId)).thenReturn(new ArrayList<>());
        Exception exception = assertThrows(BadRequestException.class, () -> pickCreateService.onOrderAuthorized(saleId));
        assertThat(exception.getMessage(), is("Not existing sale id - 1"));
    }
}
