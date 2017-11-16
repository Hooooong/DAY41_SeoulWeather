package com.hooooong.rxbasic05;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hooooong.rxbasic05.service.ServiceGenerator;
import com.hooooong.rxbasic05.domain.Row;
import com.hooooong.rxbasic05.domain.WeatherApi;
import com.jakewharton.rxbinding2.widget.RxTextView;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private TextView textData;
    private EditText editArea;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textData =  findViewById(R.id.textData);
        editArea = findViewById(R.id.editArea);


        RxTextView.textChangeEvents(editArea)
                .subscribe(ch -> {
                    if(ch.text().length() > 0){
                        setWeatherData(ch.text());
                    }else{
                        textData.setText("입력하신 구역에 대한 정보가 없습니다.");
                    }
                });
    }

    public void doSend(View view){
        if(TextUtils.isEmpty(editArea.getText())){
            Toast.makeText(this, " 구역을 입력해주시기 바랍니다. ", Toast.LENGTH_SHORT).show();
            return;
        }
        // 2. 서비스 만들기 <- 인터페이스로부터
        iWeather service = ServiceGenerator.create(iWeather.class);

        // 3. 옵저버 생성( Emitter 생성)
        Observable<WeatherApi> observable = service.getData(iWeather.SERVER_KEY, 1, 5, editArea.getText().toString());

        // 4. 발행 시작
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                // 5. 구독
                .subscribe(weatherApi -> {
                    String result = "";
                    if(weatherApi.getRealtimeWeatherStation() != null){
                        for (Row row : weatherApi.getRealtimeWeatherStation().getRow()) {
                            result += "지역명 : " + row.getSTN_NM() +"\n";
                            result += "온도 : " + row.getSAWS_TA_AVG() + "\n";
                            result += "습도 : " + row.getSAWS_HD() + "\n";
                        }
                        textData.setText(result);
                    }else{
                        result += "입력하신 구역에 대한 정보가 없습니다.";
                        textData.setText(result);
                    }

                });

    }

    public void setWeatherData(CharSequence data) {
        // 2. 서비스 만들기 <- 인터페이스로부터
        iWeather service = ServiceGenerator.create(iWeather.class);

        // 3. 옵저버 생성( Emitter 생성)
        Observable<WeatherApi> observable = service.getData(iWeather.SERVER_KEY, 1, 5, data.toString());
        // 4. 발행 시작
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                // 5. 구독
                .subscribe(weatherApi -> {
                    String result = "";
                    if(weatherApi.getRealtimeWeatherStation() != null){
                        for (Row row : weatherApi.getRealtimeWeatherStation().getRow()) {
                            result += "지역명 : " + row.getSTN_NM() +"\n";
                            result += "온도 : " + row.getSAWS_TA_AVG() + "\n";
                            result += "습도 : " + row.getSAWS_HD() + "\n";
                        }
                        textData.setText(result);
                    }else{
                        result += "입력하신 구역에 대한 정보가 없습니다.";
                        textData.setText(result);
                    }

                });
    }
}

// 0. Retrofit2 인터페이스 생성
