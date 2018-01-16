# ADS04 Android

## 수업 내용
- Thread를 활용해 비오는 화면을 만드는 학습

## Code Review

### MainActivity

```Java
public class MainActivity extends AppCompatActivity {
    FrameLayout stage;
    CustomView customView;
    public static boolean runFlag = true;
    int width = 0;
    int height = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stage = (FrameLayout)findViewById(R.id.stage); // FrameLayout을 stage로 선언
        customView = new CustomView(this); //View를 커스텀한 클래스의 객체를 세팅 
        stage.addView(customView); // stage에 customeView를 동적으로 추가

        // 화면을 지속적으로 다시 그려준다
        customView.runStage();// 해당 클래스에 만든 메소드 호출(runStage라는 쓰레드가 호출된 것임.)

        DisplayMetrics metrics = getResources().getDisplayMetrics(); // DisplayMetrics라는 클래스의 객체를 만드는데 이는 디스플레이의 가로와 세로길이를 구하기 위함
        width = metrics.widthPixels;
        height = metrics.heightPixels; // 메인액티비티에서 메트릭스를 그릴 수 있고, 그 크기를 구할 수 있다.
    }

    Random random = new Random(); // randome클래스의 객체를 사용(무작위로 만들어지는 뷰들을 만들기 위한 준비)
    public void addRainDrop(View v){ // 버튼 이벤트
        new Thread(){
            public void run(){
                while(runFlag) {
                    int x = random.nextInt(width);
                    int speed = random.nextInt(2) + 1;
                    int size = random.nextInt(40) + 10;
                    int y = size * -1;

                    RainDrop rainDrop = new RainDrop(x, y, speed, size, Color.BLUE, height); // limit값에 height를 넣어
                    customView.addRainDrop(rainDrop);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

    }


    @Override
    protected void onDestroy() { 
        runFlag=false; // flag값으로 thread를 관리한다. 왠만하면, thread는 interrupt(), stop()로 관리하지 않는다. 
        super.onDestroy();
    }
}

class RainDrop {
    // 속성
    float x;
    float y;
    float speed;
    float size;
    int color;
    // 생명주기 - 바닥에 닿을때 까지
    float limit;

    public RainDrop(float x, float y, float speed, float size, int color, float limit){
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.size = size;
        this.color = color;
        this.limit = limit;
    }

}
```

- 메인 레이아웃에 FrameLayout을 stage로 선언
- DisplayMetrics를 구성해, 가로와 세로의 값들을 포함하고 있는 변수들을 만듦
- CustomView에 있는 runStage 쓰레드를 호출함
- 각기 다른 도형(원)을 만들기 위해 random함수를 사용하고, 이를 runFlag가 false가 될때까지 계속해서 빗 방울을 나타내는 RainDrop를 Customeview에 넣어줌.
- 쓰레드 제어는 onDestroy에서 flag값을 주어 제어함.

### CustomView

```Java
public class CustomView extends View {
    Paint paint;
    List<RainDrop> rainDrops = new ArrayList<>();

    public CustomView(Context context) {
        super(context);
        // 색지정
        paint = new Paint();
        paint.setColor(Color.BLUE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(rainDrops.size() > 0) {

            for(int i=0; i< rainDrops.size(); i++) {
                RainDrop rainDrop = rainDrops.get(i);
                paint.setColor(rainDrop.color);
                canvas.drawCircle(rainDrop.x
                        , rainDrop.y
                        , rainDrop.size
                        , paint);
            }
        }
    }

    public void addRainDrop(RainDrop rainDrop){ // MainActivity에 쓰레드가 실행되는 한 계속 이쪽으로 RainDrop 객체가 들어와
                                                // ArrayList에 추가된다. 

        this.rainDrops.add(rainDrop);
    }

    public void runStage(){
        new Thread(){
            public void run(){
                while(MainActivity.runFlag){//MainThread의 runFlag값과 동일하게 갱신이 되야 하기에...
                    // 반복문을 돌면서 전체 오브젝트의 좌표값을 갱신해준다
                    for(int i=0;i<rainDrops.size();i++) {
                        if(rainDrops.get(i).y > rainDrops.get(i).limit) { // 모델 객체의 높이값이 limit보다 크면 지운다. 이는 곧 빗방울이
                            rainDrops.remove(i);                          // 화면에서 사라지는 것을 구현하는 것.
                            i--;
                        } else {
                            rainDrops.get(i).y += rainDrops.get(i).speed;
                        }
                    }
                    postInvalidate();
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }
}
```

- invalidate() 는 UI thread에서 호출
- postInvalidate() 는 non-UI thread에서 호출


## 보충설명

### invalidate vs postInvalidate

[androidDeveloperSite](https://developer.android.com/reference/android/view/View.html#invalidate())

>> " Invalidate the whole view. If the view is visible, onDraw(android.graphics.Canvas) will be called at some point in the future. 
This must be called from a UI thread. To call from a non-UI thread, call postInvalidate(). "
함수를 호출한 view가 visible 하면 미래의(멀지 않은) 한 시점에 해당 view 를 다시 그리는 onDraw() 를 호출하도록 한다. 이 때, 

- __invalidate() 는 UI thread 에서 호출__이 되어야만 하고
- __postInvalidate() 는 non-UI thread에서 호출__되어야 한다.


### 출처

- [출처] : [Android]View.invalidate() vs View.postInvalidate()|작성자 mavie

## TODO

- rainDrop 같이 thread를 활용해 간단한 프로그램 만들어보기

## Retrospect

- 빗방울이 내리는 프로그램을 만드는데 위와 같은 방법만 있는 것은 아니겠지만, 그래도 어떻게 구현이 되는지 맥락을 알게되었다.

## Output

![rain_1](https://user-images.githubusercontent.com/31605792/35003610-ab619ef8-fb30-11e7-93cd-52e910a67580.gif)
