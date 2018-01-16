package orgs.androidtown.raindrop;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.FrameLayout;

import java.util.Random;

//TODO 화면을 그려주는 것과, 어떻게 떨어지는 구현했는지!

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

























