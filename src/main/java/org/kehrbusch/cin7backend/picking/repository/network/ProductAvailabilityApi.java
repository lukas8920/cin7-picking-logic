package org.kehrbusch.cin7backend.picking.repository.network;

import org.kehrbusch.cin7backend.picking.entities.api.StockApi;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ProductAvailabilityApi {
    @GET("ref/productavailability")
    Call<StockApi> getStockApi(@Query("Sku") String sku);
}
