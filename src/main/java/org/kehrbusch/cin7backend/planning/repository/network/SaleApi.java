package org.kehrbusch.cin7backend.planning.repository.network;

import org.kehrbusch.cin7backend.planning.entities.api.SaleOrderApi;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SaleApi {
    @GET("sale/order")
    Call<SaleOrderApi> getSaleOrder(
            @Query("SaleID") String saleId,
            @Query("CombineAdditionalCharges") Boolean combineAdditionalCharges,
            @Query("IncludeProductInfo") Boolean includeProductInfo
    );
}
