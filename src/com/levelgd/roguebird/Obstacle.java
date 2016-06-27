package com.levelgd.roguebird;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by lvlgd on 22.09.2015.
 */
public class Obstacle {

    public boolean remove = false;

    float x;
    float y;

    float speedX;
    float width;

    Paint paint;

    public Obstacle(float x, float y, int screenW, Paint paint){
        this.x = x;
        this.y = y;

        this.speedX = -(float)screenW/4.8f;
        this.width = paint.measureText("#");

        this.paint = paint;
    }

    public boolean updateRender(Bird bird, float deltaTime, Canvas canvas){

        if(!bird.gameOver) {

            x += speedX * deltaTime;

            if (x < -width) {
                remove = true;
                return false;
            }
        }

        float size = paint.getTextSize();
        float height = canvas.getHeight();

        float i = y + size;

        while ((i += size) < (height + size)){
            canvas.drawText("#", x, i, paint);
        }

        i = y - size;

        while ((i -= size) > 0){
            canvas.drawText("#", x, i, paint);
        }

        if(!bird.gameOver) {
            if (pointInRectangle(x, y + size*1.5f, size, height, bird.x + bird.halfW, bird.y + bird.halfH)) {
                return true;
                //canvas.drawRect(x, y + size, x + size, height, paint);
            }

            if (pointInRectangle(x, 0, size, y - size*1.5f, bird.x + 48, bird.y + 24)) {
                return true;
                //canvas.drawRect(x, 0, x + size, y - size*2, paint);
            }
        }

        return false;
    }

    boolean pointInRectangle(float rx, float ry, float rw, float rh, float x, float y) {
        return rx <= x && rx + rw >= x && ry <= y && ry + rh >= y;
    }

}
