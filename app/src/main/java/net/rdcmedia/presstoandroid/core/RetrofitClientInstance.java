package net.rdcmedia.presstoandroid.core;

import net.rdcmedia.presstoandroid.Configuration;

import net.rdcmedia.presstoandroid.Configuration;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClientInstance {

    private static Retrofit retrofit;

    public static Retrofit getRetrofitInstance(String baseUrl) {
        if (retrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(Configuration.TIME_OUT_SECONDS, TimeUnit.SECONDS)
                    .readTimeout(Configuration.TIME_OUT_SECONDS,TimeUnit.SECONDS).build();

            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(baseUrl).client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
    public static void reset(){
        retrofit = null;
    }
}