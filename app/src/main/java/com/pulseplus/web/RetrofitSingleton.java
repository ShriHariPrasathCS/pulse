package com.pulseplus.web;


import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitSingleton {

    //public  static String BASE_URL = "http://52.34.89.223";
    public static String BASE_URL = "http://52.11.250.70";

    public static HttpLoggingInterceptor logging;

    private static OkHttpClient.Builder httpClient;

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());

    public static <T> T createService(Class<T> serviceClass)
    {
        //Set Logging
        if (logging == null) {
            logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        }
        if (httpClient == null) {
            httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(logging);
            httpClient.retryOnConnectionFailure(true);
            httpClient.connectTimeout(2, TimeUnit.MINUTES);
            httpClient.readTimeout(2, TimeUnit.MINUTES);
        }

        Retrofit retrofit = builder.client(httpClient.build()).build();
        return retrofit.create(serviceClass);
    }

}
