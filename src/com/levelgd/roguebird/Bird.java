package com.levelgd.roguebird;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by lvlgd on 22.09.2015.
 */
public class Bird {

    public boolean gameOver = false;
    public boolean canRestart = false;

    public float x;
    public float y;

    public float halfW;
    public float halfH;

    public float speedY = 0;

    float g;

    Paint paint;

    public Bird(float x, float y, int screenH, Paint paint){
        this.x = x;
        this.y = y;

        this.paint = paint;

        this.g = (float)screenH/43.2f;

        halfW = paint.measureText(">v@")/2;
        halfH = paint.getTextSize()/2;
    }

    public void updateRender(float deltaTime, Canvas canvas){

        speedY += deltaTime * g;

        if (y > 0 || speedY > 0) y += speedY;

        if (y > canvas.getHeight()) {
            gameOver = true;
            canRestart = true;
            y = canvas.getHeight();
        }

        if(speedY < 0){
            canvas.drawText(">v@",x,y,paint);
        }else{
            canvas.drawText(">^@",x,y,paint);
        }

    }
}
