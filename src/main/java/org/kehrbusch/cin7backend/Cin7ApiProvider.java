package org.kehrbusch.cin7backend;

import com.azure.security.keyvault.secrets.SecretClient;
import org.kehrbusch.cin7backend.picking.repository.network.ProductAvailabilityApi;
import org.kehrbusch.cin7backend.planning.repository.network.SaleApi;
import org.kehrbusch.cin7backend.util.Profile;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.Locale;

@Configuration
public class Cin7ApiProvider {
    private static final String PROD_POSTMAN_KEY= "azure.keyvault.qualifier.postman.key";
    private static final String PROD_ACCOUNT_ID= "azure.keyvault.qualifier.account.id";
    private static final String PROD_APPLICATION_ID= "azure.keyvault.qualifier.application.id";

    private static final String TEST_CIN7_ACCOUNT_ID = "cin7.account.id";
    private static final String TEST_CIN7_APPLICATION_ID = "cin7.application.id";
    private static final String TEST_CIN7_POSTMAN_KEY = "cin7.postman.key";

    private static final String CIN7_URL_QUALIFIER = "cin7.url";
    private static final String ACTIVE_PROFILE = "spring.profiles.active";

    private final String accountId;
    private final String applicationId;
    private final String postmanKey;
    private final boolean isMockProfile;
    private final Retrofit retrofit;

    @Autowired
    public Cin7ApiProvider(Environment env, ApplicationContext applicationContext, SecretClient secretClient){
        String profile = env.getProperty(ACTIVE_PROFILE);
        profile = profile != null ? profile.toUpperCase(Locale.ROOT) : "";

        String cin7Url = env.getProperty(secretClient.getSecret(CIN7_URL_QUALIFIER).getValue());
        accountId = profile.equals(Profile.PROD.toString()) ?
                applicationContext.getBean(SecretClient.class).getSecret(env.getProperty(PROD_ACCOUNT_ID)).getValue() : env.getProperty(TEST_CIN7_ACCOUNT_ID);
        applicationId = profile.equals(Profile.PROD.toString()) ?
                applicationContext.getBean(SecretClient.class).getSecret(env.getProperty(PROD_APPLICATION_ID)).getValue() : env.getProperty(TEST_CIN7_APPLICATION_ID);
        postmanKey = profile.equals(Profile.PROD.toString()) ?
                applicationContext.getBean(SecretClient.class).getSecret(env.getProperty(PROD_POSTMAN_KEY)).getValue() : env.getProperty(TEST_CIN7_POSTMAN_KEY);

        isMockProfile = (profile.equals(Profile.MOCK.toString()) || profile.equals(Profile.TEST.toString()) || profile.equals(Profile.PROD.toString()));

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new HeaderInterceptor())
                .build();
        this.retrofit = new Retrofit.Builder()
                .baseUrl(cin7Url)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }

    @Bean(name = "saleApi")
    public SaleApi provideSaleApi(){
        return retrofit.create(SaleApi.class);
    }

    @Bean(name = "productAvailabilityApi")
    public ProductAvailabilityApi productAvailabilityApi(){
        return retrofit.create(ProductAvailabilityApi.class);
    }

    class HeaderInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request originalRequest = chain.request();
            Request.Builder requestBuilder = originalRequest.newBuilder()
                    .header("account_id", accountId)
                    .header("application_key", applicationId);
            if (isMockProfile){
                requestBuilder = requestBuilder.header("x-api-key", postmanKey);
            }

            Request request = requestBuilder.build();
            return chain.proceed(request);
        }
    }
}
