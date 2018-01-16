package orgs.androidtown.raindrop;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jisang on 2017-10-10.
 */

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
