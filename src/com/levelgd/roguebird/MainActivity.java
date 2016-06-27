package com.levelgd.roguebird;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;

import java.util.Random;
import java.util.Vector;

public class MainActivity extends Activity implements Runnable, View.OnTouchListener {

    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;

    Thread thread = null;
    volatile boolean running = false;

    protected int WIDTH;
    protected int HEIGHT;

    float deltaTime;
    long deltaTimeL;
    long mPrevTimeNanos;
    long lastTimeNanos;
    int frames;
    int fps;

    Paint paint;
    Paint oPaint;
    Random random;

    Bird bird;
    Vector<Obstacle> obstacles;
    int score = 0;
    float timer = 0f;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        WIDTH = metrics.widthPixels;
        HEIGHT = metrics.heightPixels;

        //int textSize = (int)(16f * metrics.scaledDensity + 0.5f);
        int textSize = (int)(HEIGHT/22.5f);

        //Log.i("TEXT SIZE", textSize + " " + System.nanoTime());
        //Typeface typeface = Typeface.createFromAsset(MainActivity.this.getAssets(),"consola.ttf");

        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTypeface(Typeface.MONOSPACE);
        paint.setTextSize(textSize);
        paint.setAntiAlias(false);

        oPaint = new Paint();
        oPaint.setColor(Color.GREEN);
        oPaint.setTypeface(Typeface.MONOSPACE);
        oPaint.setTextSize(textSize * 2);
        oPaint.setAntiAlias(false);

        random = new Random();

        surfaceView = new SurfaceView(this);
        surfaceView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        surfaceView.setOnTouchListener(this);
        surfaceHolder = surfaceView.getHolder();

        setContentView(surfaceView);

        fps(System.nanoTime());

        startRound();
    }

    public void startRound(){
        bird = new Bird(WIDTH/10,HEIGHT/2, HEIGHT, paint);
        obstacles = new Vector<>();
        timer = 0;
        score = 0;
    }

    public void run() {
        while (running){
            synchronized (surfaceHolder){
                if(!surfaceHolder.getSurface().isValid()) continue;

                Canvas canvas = surfaceHolder.lockCanvas();
                canvas.drawColor(Color.BLACK);

                bird.updateRender(deltaTime, canvas);
                for(Obstacle o : obstacles) {
                    if(o.updateRender(bird, deltaTime, canvas)){
                        bird.gameOver = true;
                        break;
                    }
                }

                for(int i = 0; i < obstacles.size(); i++){
                    if(obstacles.get(i).remove){
                        obstacles.remove(i);
                        i--;

                        score++;
                    }
                }

                if(!bird.gameOver && (timer += deltaTime) > 1.4f){
                    timer -= 1.4f;
                    obstacles.add(new Obstacle(WIDTH,
                            random.nextInt(HEIGHT - (int) oPaint.getTextSize()) + oPaint.getTextSize(), WIDTH, oPaint));
                }

                //canvas.drawText("fps: " + fps, 16, 48, paint);
                canvas.drawText("score: " + score, 16, 48, paint);

                if(bird.gameOver) {

                    String restart;

                    if(bird.canRestart) {
                        restart = "TAP TO RESTART";
                    }else{
                        restart = "GAME OVER";
                    }

                    canvas.drawText(restart, WIDTH / 2 - paint.measureText(restart)/2, HEIGHT/2 + paint.getTextSize()/2, paint);
                }

                surfaceHolder.unlockCanvasAndPost(canvas);
            }

            fps(System.nanoTime());
        }
    }

    public void pause(){
        running = false;
        while (true){
            try{
                thread.join();
                return;
            }catch (InterruptedException ie){
                Log.e("thread.join()", ie.getMessage());
            }
        }
    }

    public void resume(){
        running = true;
        thread = new Thread(this);
        thread.start();
    }

    @Override
    protected void onResume(){
        super.onResume();
        this.resume();
    }

    @Override
    protected void onPause(){
        super.onPause();
        this.pause();
    }

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if(!bird.gameOver) bird.speedY = -(HEIGHT/90f);

            if(bird.canRestart) startRound();
        }

        return true;
    }

    void fps(long timeStampNanos) {
        long intervalNanos;
        if (mPrevTimeNanos == 0) {
            intervalNanos = 0;
        } else {
            intervalNanos = timeStampNanos - mPrevTimeNanos;
            if (intervalNanos > 1000000000L) intervalNanos = 0;
        }
        mPrevTimeNanos = timeStampNanos;
        deltaTimeL = intervalNanos;
        deltaTime = intervalNanos / 1000000000.0f;
        lastTimeNanos += deltaTimeL;
        frames++;
        if (lastTimeNanos >= 1000000000L) {
            lastTimeNanos -= 1000000000L;
            fps = frames;
            frames = 0;
        }
    }
}
