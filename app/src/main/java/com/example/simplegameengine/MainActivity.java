package com.example.simplegameengine;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.SurfaceHolder;

public class MainActivity extends AppCompatActivity {

    GameView gameView;

    int width, height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gameView = new GameView(this);
        setContentView(gameView);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        width = size.x;
        height = size.y;
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }


    class GameView extends SurfaceView implements Runnable {
        Thread gameThread = null;
        SurfaceHolder ourHolder;
        volatile boolean playing;

        Canvas canvas;
        Paint paint;

        long fps;
        private long timeThisFrame;
        Bitmap bitmapPinguin;
        boolean isMoving = false;
        boolean forward = true;
        float walkSpeedPerSecond = 150;
        float pinguinXPosition = 20;

        public GameView(Context context) {
            super(context);

            ourHolder = getHolder();
            paint = new Paint();

            bitmapPinguin = BitmapFactory.decodeResource(this.getResources(), R.drawable.pinguin);
            playing = true;
        }

        @Override
        public void run() {
            while (playing) {
                long startFrameTime = System.currentTimeMillis();

                update();
                draw(startFrameTime);

                timeThisFrame = System.currentTimeMillis() - startFrameTime;
                if (timeThisFrame > 0) {
                    fps = 1000 / timeThisFrame;
                }
            }
        }

        public void update() {
            if (isMoving) {
                if(forward){
                    if(pinguinXPosition >= (getScreenWidth() - 100)){
                        forward = false;
                    }
                    else{
                        pinguinXPosition = pinguinXPosition + (walkSpeedPerSecond / fps);
                    }
                }
                else{
                    if(pinguinXPosition <= 20){
                        forward = true;
                    }
                    else{
                        pinguinXPosition = pinguinXPosition- (walkSpeedPerSecond/fps);
                    }
                }

            }
        }

        public void draw(long startFrameTime) {
            if (ourHolder.getSurface().isValid()){
                canvas = ourHolder.lockCanvas();

                canvas.drawColor(Color.argb(250,140,194,240));

                paint.setColor(Color.argb(255,73,73,73));

                paint.setTextSize(45);

                canvas.drawText("FPS : " + fps, 20,40,paint) ;
                canvas.drawText("Height : " + height + "  Width : " + width, 20,80,paint) ;
                canvas.drawText("Pinguin Height : " + bitmapPinguin.getHeight() + "  Pinguin Width : " + bitmapPinguin.getHeight(), 20,120,paint) ;
                canvas.drawText("Pinguin X Position : " + startFrameTime, 20,160,paint) ;

                canvas.drawBitmap (bitmapPinguin,pinguinXPosition,300,paint);

                ourHolder.unlockCanvasAndPost(canvas);

            }
        }

        public void pause() {
            playing = false;
            try {
                gameThread.join();
            } catch (InterruptedException e) {
                Log.e("Error", "Joining Thread");
            }
        }

        public void resume() {
            playing = true;
            gameThread = new Thread(this);
            gameThread.start();
        }

        @Override
        public boolean onTouchEvent (MotionEvent motionEvent){
            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    isMoving = true;
                    break;

                case MotionEvent.ACTION_UP:
                    isMoving = false;
                    break;
            }
            return true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();
    }
}