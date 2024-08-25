package org.kehrbusch.cin7backend.planning.application;

import org.kehrbusch.cin7backend.planning.entities.SaleOrder;
import org.kehrbusch.cin7backend.planning.entities.SaleOrderMapper;
import org.kehrbusch.cin7backend.planning.entities.api.SaleOrderApi;
import org.kehrbusch.cin7backend.planning.repository.database.SaleOrderRepository;
import org.kehrbusch.cin7backend.planning.repository.network.SaleApi;
import org.kehrbusch.cin7backend.util.BadRequestException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class SaleOrderCreateServiceTest {
    private static OrderCreateService orderCreateService;
    private static SaleOrderRepository saleOrderRepository;
    private static SaleApi saleApi;
    private static SaleOrderMapper saleOrderMapper;
    private static Call<SaleOrderApi> mockCall;

    private CountDownLatch latch;

    @BeforeAll
    public static void setup(){
        saleOrderRepository = mock(SaleOrderRepository.class);
        saleApi = mock(SaleApi.class);
        saleOrderMapper = mock(SaleOrderMapper.class);
        mockCall = mock(Call.class);
        orderCreateService = new OrderCreateService(saleApi, saleOrderRepository, saleOrderMapper);
    }

    @BeforeEach
    public void reset(){
        latch = new CountDownLatch(1);
    }

    @Test
    public void testThatOnOrderCreateThrowsErrorForUnknown(){
        Exception exception = assertThrows(BadRequestException.class, () -> orderCreateService.onOrderCreated(null ));
        assertThat(exception.getMessage(), is("Invalid sale id - null"));
    }

    @Test
    public void testThatOnCreateOrderLogsOrderToRepository() throws InterruptedException, BadRequestException {
        SaleOrder saleOrder = new SaleOrder();
        saleOrder.setSaleOrderId("1");
        SaleOrderApi saleOrderApi = new SaleOrderApi();
        saleOrderApi.setSaleId("1");

        AtomicBoolean checkFlag = new AtomicBoolean(false);

        doAnswer(invocation -> {
            Callback<SaleOrderApi> callback = invocation.getArgument(0);
            Response<SaleOrderApi> response = Response.success(saleOrderApi);
            callback.onResponse(mockCall, response);
            return null;
        }).when(mockCall).enqueue(any(Callback.class));
        when(saleOrderRepository.saveOrUpdate(saleOrder)).thenAnswer(invocationOnMock -> {
            checkFlag.set(true);
            latch.countDown();
            return null;
        });
        when(saleApi.getSaleOrder("1", false, true)).thenReturn(mockCall);
        when(saleOrderMapper.mapTimestamp(eq(saleOrderApi), any(Date.class))).thenReturn(List.of(saleOrder));

        orderCreateService.onOrderCreated("1");

        latch.await();

        assertThat(checkFlag.get(), is(true));
    }
}