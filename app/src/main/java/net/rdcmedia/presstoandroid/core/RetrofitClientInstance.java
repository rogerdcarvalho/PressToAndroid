package net.rdcmedia.presstoandroid.core;

import net.rdcmedia.presstoandroid.Configuration;

import net.rdcmedia.presstoandroid.Configuration;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClientInstance {

    private static Retrofit retrofit;

    public static Retrofit getRetrofitInstance(String baseUrl) {
        if (retrofit == null) {
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
    public static void reset(){
        retrofit = null;
    }
}