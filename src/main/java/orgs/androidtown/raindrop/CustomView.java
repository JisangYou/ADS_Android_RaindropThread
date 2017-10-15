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

    public void addRainDrop(RainDrop rainDrop){

        this.rainDrops.add(rainDrop); // this의 의미?
    }

    public void runStage(){
        new Thread(){
            public void run(){
                while(MainActivity.runFlag){// 객체당 생명주기 함수가 있어서, 하나하나 다 지워주는 것인가?
                    // 반복문을 돌면서 전체 오브젝트의 좌표값을 갱신해준다
                    for(int i=0;i<rainDrops.size();i++) {
                        if(rainDrops.get(i).y > rainDrops.get(i).limit) { // i값들이 커지면 액티비티 상에서 떨어지는 효과가 있다?
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
