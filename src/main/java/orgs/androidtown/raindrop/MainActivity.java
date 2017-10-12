package orgs.androidtown.raindrop;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.FrameLayout;

import java.util.Random;

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

                    RainDrop rainDrop = new RainDrop(x, y, speed, size, Color.RED, height);
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

























