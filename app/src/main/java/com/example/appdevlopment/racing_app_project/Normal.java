// the max x postiion is about 1800 the max y position is about 855
package com.example.appdevlopment.racing_app_project;

import android.app.Activity;
import android.content.Context;
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
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class Normal extends Activity {
    GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gameView = new GameView(this);
        setContentView(gameView);
    }

    class GameView extends SurfaceView implements Runnable {

        Thread gameThread = null;

        SurfaceHolder ourHolder;

        volatile boolean playing;

        Canvas canvas;
        Paint paint;

        long fps;

        private long timeThisFrame;

        Bitmap bitmapup_button;
        Bitmap bitmapdown_button;
        Bitmap bitmapright_button;
        Bitmap bitmapleft_button;

        Bitmap bitmappolice_car;

        boolean isMoving = false;
        boolean isMovingBackwords = false;
        boolean isMovingUp = false;
        boolean isMovingDown = false;

        float walkSpeedPerSecond = 10;

        float police_carXPosition = 10;
        float police_carYPosition = 10;

        int screenX;
        int screenY;

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        screenX = size.X;
        screenY = size.y;

        public GameView(Context context) {
            super(context);
            ourHolder = getHolder();
            paint = new Paint();
            bitmapup_button = BitmapFactory.decodeResource(this.getResources(), R.drawable.up_button);
            bitmapdown_button = BitmapFactory.decodeResource(this.getResources(), R.drawable.down_button);
            bitmapright_button = BitmapFactory.decodeResource(this.getResources(), R.drawable.right_button);
            bitmapleft_button = BitmapFactory.decodeResource(this.getResources(), R.drawable.left_button);
            //left button about 200 along y axis
            //left button is 62 by 62 pixels in paint
            bitmappolice_car = BitmapFactory.decodeResource(this.getResources(), R.drawable.police_car);
            playing = true;
        }

        @Override
        public void run() {
            while (playing) {
                long startFrameTime = System.currentTimeMillis();

                update();

                draw();

                timeThisFrame = System.currentTimeMillis() - startFrameTime;
                if (timeThisFrame > 0) {
                    fps = 1000 / timeThisFrame;
                }
            }
        }

        public void update() {
            if (isMoving) {
                police_carXPosition = police_carXPosition + (walkSpeedPerSecond + fps);
            }
            if (isMovingBackwords){
                police_carXPosition = police_carXPosition - (walkSpeedPerSecond + fps);
            }
            if (isMovingUp) {
                police_carYPosition = police_carYPosition - (walkSpeedPerSecond +fps);
            }
            if (isMovingDown) {
                police_carYPosition = police_carYPosition + (walkSpeedPerSecond +fps);
            }
        }

        public void draw() {

            if (ourHolder.getSurface().isValid()) {
                canvas = ourHolder.lockCanvas();

                canvas.drawColor(Color.argb(255,255,129,89));

                paint.setTextSize(45);

                canvas.drawText("FPS:" + fps, 20, 40, paint);

                canvas.drawBitmap(bitmapup_button, 1395, 675, paint);

                canvas.drawBitmap(bitmapdown_button, 205, 675, paint);

                canvas.drawBitmap(bitmapright_button, 1600, 675, paint);

                canvas.drawBitmap(bitmapleft_button, 5, 675, paint);

                canvas.drawBitmap(bitmappolice_car, police_carXPosition, police_carYPosition, paint);

                ourHolder.unlockCanvasAndPost(canvas);
            }
        }

        public void pause() {
            playing = false;
            try {
                gameThread.join();
            } catch (InterruptedException e) {
                Log.e("Error:", "joining thread");
            }
        }

        public void resume() {
            playing = true;
            gameThread = new Thread(this);
            gameThread.start();
        }

        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {

            System.out.println("The X-position of the click is: " + motionEvent.getX());

            System.out.println("The Y-position of the click is: " + motionEvent.getY());

            if ((motionEvent.getX() > 1600) && (motionEvent.getY() > 660)) {

                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {

                    case MotionEvent.ACTION_DOWN:

                        isMoving = true;

                        break;

                    case MotionEvent.ACTION_UP:

                        isMoving = false;

                        break;
                }
            } else if ((motionEvent.getX() < 185) && (motionEvent.getY() > 660)) {
                //185 long on the left button on the x axis
                //650 on top of the left button
                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {

                    case MotionEvent.ACTION_DOWN:

                        isMovingBackwords = true;

                        break;

                    case MotionEvent.ACTION_UP:

                        isMovingBackwords = false;

                        break;
                }
            } else if ((motionEvent.getX() < 405 && motionEvent.getX() > 205 && motionEvent.getY() > 660)) {

                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {

                    case  MotionEvent.ACTION_DOWN:

                        isMovingDown = true;

                        break;

                    case MotionEvent.ACTION_UP:

                        isMovingDown = false;

                        break;
                }
            } else if ((motionEvent.getX() < 1595 && motionEvent.getX() > 1395 && motionEvent.getY() > 660)) {

                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {

                    case  MotionEvent.ACTION_DOWN:

                        isMovingUp = true;

                        break;

                    case MotionEvent.ACTION_UP:

                        isMovingUp = false;

                        break;
                }
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