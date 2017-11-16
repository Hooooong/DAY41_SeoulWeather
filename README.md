# RxJava와 Retrofit2를 사용하여 서울시 기상 관측 정보 가져오기

### 설명
____________________________________________________

![서울시 기상정보 받아오기](https://github.com/Hooooong/DAY41_SeoulWeather/blob/master/image/RxJava%2C%20Retrofit2%2C%20RxBinding.gif)

- RxJava 와 Retrofit2 를 통한 데이터 통신

- RxBinding 을 통해 EditText 변화함에 따라 통신

### KeyPoint
____________________________________________________

- RxJava 란?

  - 참조 : [RxJava](https://github.com/Hooooong/DAY40_RxJava2)

- RxBinding 이란?

  - 참조 : [RxBinding](https://github.com/Hooooong/DAY41_RxJava4)

- Retrofit2 이란?

  > Retrofit2 는 HTTP Api 를 Java interface 로 형태로 사용하는 통신 Library 이다.

  - 기존 HTTP 통신은 `HttpURLConnection` 이란 내장 API 와 Sub Thread 인 `AsyncTask` 를 사용하여 JSON 을 Parsing 해야 하기 때문에, 약 100줄의 코드로 작성해야 한다.

  - Retrofit2 은 통신과 Sub Thread 를 구현한 Library 로 간단하게 외부 데이터 통신을 구현할 수 있다.

  ```java
  // 1. 통신 인터페이스 작성
  public interface iService{
    // 기본 작성법
    @GET("/users")
    void getAllUsers();

    // @GET 의 Query 정보에 넣을 데이터는
    // @Path("") 로 정의하고
    @GET("/users/{id}")
    void getUser(@Path("id") String id);

    // @POST, @PUT, @DELETE 에 넣을 Body는
    // @Body 로 정의한다.
    @POST("/users")
    void setUser(@Body User group);
  }

  public class MainActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 1. Retrofit2 생성
        Retrofit retrofit = new Retrofit.Builder()
                  .baseUrl(SERVER_URL)
                  .addConverterFactory(GsonConverterFactory.create())
                  .build();
        // 2. 서비스 만들기 <- 인터페이스로부터
        iService service = retrofit.create(iService.class);
        // 3. Service 로 연결 준비
        Call<ResponseBody> remote = service.setUser(body);
        remote.enqueue(new Callback<ResponseBody>() {
                           @Override
                           public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                               if(response.isSuccessful()){
                                   // 데이터 응답 성공시
                                   // ResponseBody 에 넘겨온 Body 정보가 있다.
                                   ResponseBody data = response.body();
                                   try {
                                       Toast.makeText(StorageActivity.this, data.string(), Toast.LENGTH_SHORT).show();
                                   } catch (IOException e) {
                                       e.printStackTrace();
                                   }
                               }
                           }

                           @Override
                           public void onFailure(Call<ResponseBody> call, Throwable t) {
                                // 데이터 응답 실패시
                                Log.e("Retro",t.getMessage());
                           }
                       });
    }
  }
  ```

### Code Review
____________________________________________________

- MainActivity.java

  - RxJava, Retrofit2, RxBinding 을 통해 데이터를 통신하는 구역이다.

  ```java
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

          // EditText 가 반응할 때마다 데이터를 불러온다.
          // 마치 Ajax 처럼
          RxTextView.textChangeEvents(editArea)
                  .subscribe(ch -> {
                      if(ch.text().length() > 0){
                          setWeatherData(ch.text());
                      }else{
                          textData.setText("입력하신 구역에 대한 정보가 없습니다.");
                      }
                  });
      }

      // EditText 의 값을 서버에 전송해 데이터를 받아온다.
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

      // EditText 가 변경될 때 마다 변경된 Text 를 서버에 전송해 데이터를 받아온다.
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
  ```

- iWeather.java

  - Retrofit2 인터페이스 작성

  ```java
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
  ```

- ServiceGenerator.java

  - Class 별로 Retrofit 객체를 생성하는 Util성 Class 이다.

  ```java
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
  ```
