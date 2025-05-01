package com.example.tp_localisation.apis;

import com.example.tp_localisation.helpers.SSLHelper;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitClient {

    private static Retrofit retrofit;
    private static final String BASE_URL = "http://192.168.212.147/TP_LOCALISATION/ws/";

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            // Add detailed logging interceptor
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            // Configure OkHttpClient with timeouts
            OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .addInterceptor(loggingInterceptor);

            // Handling SSL trust if needed
            try {
                clientBuilder.sslSocketFactory(SSLHelper.getUnSafeSSLSocketFactory(), SSLHelper.getTrustManager());
                clientBuilder.hostnameVerifier((hostname, session) -> true);
            } catch (Exception e) {
                e.printStackTrace();
            }

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(clientBuilder.build())
                    .addConverterFactory(ScalarsConverterFactory.create())  // For string responses
                    .addConverterFactory(GsonConverterFactory.create())     // For JSON responses
                    .build();
        }
        return retrofit;
    }

    // Method to reset Retrofit instance (useful for testing different URLs)
    public static void resetRetrofitInstance() {
        retrofit = null;
    }
}