# RainDrop
## 쓰레드를 활용한 예제
- 화면에 크기가 다른 도형(원)이 순서없이 떨어지는 예제

### 클래스 구성

- mainActivity class&layout
- RainDrop class
- CustomView class

#### mainActivity

```Java
public class MainActivity extends AppCompatActivity {
    FrameLayout stage;
    CustomView view;
    public static boolean runFlag = true;
    int width = 0;
    int height = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stage = (FrameLayout)findViewById(R.id.stage);
        view = new CustomView(this);
        stage.addView(view);

        // 화면을 지속적으로 다시 그려준다
        view.runStage();

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        width = metrics.widthPixels;
        height = metrics.heightPixels;
    }

    Random random = new Random();
    public void addRainDrop(View v){
        new Thread(){
            public void run(){
                while(runFlag) {
                    int x = random.nextInt(width);
                    int speed = random.nextInt(2) + 1;
                    int size = random.nextInt(40) + 10;
                    int y = size * -1;

                    RainDrop rainDrop = new RainDrop(x, y, speed, size, Color.BLUE, height);
                    view.addRainDrop(rainDrop);
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
        runFlag=false;
        super.onDestroy();
    }
}

}
```

- 메인 레이아웃에 구성 FrameLayout을 stage로 구성
- DisplayMetrics를 구성해, 가로와 세로의 값들을 변수들에 할당
- CustomView에 있는 runStage 쓰레드를 호출함
- 각기 다른 도형(원)을 만들기 위해 random함수를 사용하고, 이를 쓰레드에 적용시킴
- 쓰레드 제어는 onDestroy에서 flag값을 주어 제어함.

#### RainDrop

```Java
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
```

- 데이터 클래스
- limit변수는 화면 상에서 사라질 때를 위해 만들었음

#### CustomView

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

    public void addRainDrop(RainDrop rainDrop){
        this.rainDrops.add(rainDrop); // 메인쪽 메서드와 네이밍이 같아서?
    }

    public void runStage(){
        new Thread(){
            public void run(){
                while(MainActivity.runFlag){
                    // 반복문을 돌면서 전체 오브젝트의 좌표값을 갱신해준다
                    for(int i=0;i<rainDrops.size();i++) {
                        if(rainDrops.get(i).y > rainDrops.get(i).limit) {
                            rainDrops.remove(i);
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

- 물방울과 스테이지를 그려주는 클래스

- 메인에서 쓰레드가 돌면서, 랜덤 값을 RainDrop클래스에 넣어주고,
-  RainDrop클래스에서 나온 객체들이 ArrayList rainDrops에 더해짐.
- runStage 메서드에서는 떨어지는 것을 쓰레드로 구현
- 반복문 구현하는 것이 떨어지는 효과가 있음. 그리고 서로 다른 스피드로 떨어지는 것을 구현할 수 있음.
- runStage 메서드에서 rainDrops의 위치값이 limit값에 도달하기전에 지워줌
- 서브 쓰레드이다 보니 postInvalidate를 해줌
