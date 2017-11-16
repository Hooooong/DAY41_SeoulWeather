package com.hooooong.rxbasic05;


import com.hooooong.rxbasic05.domain.WeatherApi;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Android Hong on 2017-11-16.
 */

public interface iWeather {

    String SERVER_URL = "http://openapi.seoul.go.kr:8088/";
    String SERVER_KEY = "50704e635368393232347853416a4d";

    // 0. @GET 에 uri 를 설정한다.
    // { } 를 넣으면 코드상에서 값을 넣을 수 있다.
    @GET("{key}/json/RealtimeWeatherStation/{startNum}/{countNum}/{gu}")
    // 1. 호출되는 값을 설정한다.
    // {} 안에 Mapping 시키기 위해 @Path 를 사용한다.
    Observable<WeatherApi> getData(@Path("key") String key, @Path("startNum") int startNum, @Path("countNum") int countNum, @Path("gu") String gu);
}
