package com.hooooong.rxbasic05.service;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.hooooong.rxbasic05.iWeather.SERVER_URL;

/**
 * Created by Android Hong on 2017-11-16.
 */

public class ServiceGenerator {

    public static <I> I create(Class<I> classname) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SERVER_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(classname);
    }

}
