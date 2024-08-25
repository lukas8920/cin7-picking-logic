package org.kehrbusch.cin7backend.planning.entities;

import org.kehrbusch.cin7backend.planning.entities.api.SaleOrderApi;
import org.kehrbusch.cin7backend.planning.entities.api.ProductApi;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class SaleSaleOrderMapperTest {
    private static Date now;
    private static SaleOrderMapper saleOrderMapper;
    private static SaleOrderApi saleOrderApi;

    @BeforeAll
    static void setup(){
        saleOrderMapper = new SaleOrderMapper();
        now = new Date();

        saleOrderApi = new SaleOrderApi();
        saleOrderApi.setSaleId("sale_id");
        saleOrderApi.setSaleOrderNumber("sale_order_number");
    }

    @Test
    public void testThatMapperWorksForMultipleProducts(){
        ProductApi productApi1 = new ProductApi();
        productApi1.setProductId("product_id1");
        productApi1.setSku("product_sku1");
        productApi1.setQuantity(1L);
        ProductApi productApi2 = new ProductApi();
        productApi2.setProductId("product_id2");
        productApi2.setSku("product_sku2");
        productApi2.setQuantity(2L);
        List<ProductApi> productApis = Arrays.asList(productApi1, productApi2);
        saleOrderApi.setLines(productApis);

        List<SaleOrder> saleOrders = saleOrderMapper.mapTimestamp(saleOrderApi, now);

        assertThat(saleOrders.size(), is(2));
        assertThat(saleOrders.get(0).getSaleOrderId(), is("sale_id"));
        assertThat(saleOrders.get(0).getSaleOrderNumber(), is("sale_order_number"));
        assertThat(saleOrders.get(0).getProductId(), is("product_id1"));
        assertThat(saleOrders.get(0).getSku(), is("product_sku1"));
        assertThat(saleOrders.get(0).getQuantity(), is(1L));
        assertThat(saleOrders.get(0).getLineNumber(), is("001"));
        assertThat(saleOrders.get(0).getModifyTimestamp(), is(now));
        assertThat(saleOrders.get(0).getCreateTimestamp(), is(now));

        assertThat(saleOrders.get(1).getSaleOrderId(), is("sale_id"));
        assertThat(saleOrders.get(1).getSaleOrderNumber(), is("sale_order_number"));
        assertThat(saleOrders.get(1).getProductId(), is("product_id2"));
        assertThat(saleOrders.get(1).getSku(), is("product_sku2"));
        assertThat(saleOrders.get(1).getQuantity(), is(2L));
        assertThat(saleOrders.get(1).getLineNumber(), is("002"));
        assertThat(saleOrders.get(1).getModifyTimestamp(), is(now));
        assertThat(saleOrders.get(1).getCreateTimestamp(), is(now));
    }

    @Test
    public void testThatMapperWorksForNoProduct(){
        saleOrderApi.setLines(new ArrayList<>());

        List<SaleOrder> saleOrders = saleOrderMapper.mapTimestamp(saleOrderApi, now);

        assertThat(saleOrders.size(), is(1));
        assertThat(saleOrders.get(0).getSaleOrderId(), is("sale_id"));
        assertThat(saleOrders.get(0).getSaleOrderNumber(), is("sale_order_number"));
        assertThat(saleOrders.get(0).getProductId(), nullValue());
        assertThat(saleOrders.get(0).getSku(), nullValue());
        assertThat(saleOrders.get(0).getQuantity(), nullValue());
        assertThat(saleOrders.get(0).getLineNumber(), nullValue());
        assertThat(saleOrders.get(0).getModifyTimestamp(), is(now));
        assertThat(saleOrders.get(0).getCreateTimestamp(), is(now));
    }
}
