package com.euj.scit.newproject;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.os.IBinder;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Switch;
import android.widget.Toast;

import static java.security.AccessController.getContext;

/**
 * Created by sunphiz on 2014. 10. 3..
 */

/*
   자신의 앱이 종료된 후에도 항상 해당 뷰가 떠 있어야 한다. 그래서 Activity에서 뷰를 추가하는 것이 아니라 Service에서 뷰를 추가 해야 한다.

 */
public class ScreenFilterService extends Service
{

    private View display;

    private WindowManager.LayoutParams mParams = null;
    private WindowManager mWindowManager;
    private Paint mPaint;
    private int whichCommend;


    //private Window getWindow;

   // private MainActivity ma = new MainActivity();

/*    public void setGetWindow(Window w) {
        this.getWindow = w;
    }*/

    public void setWhichCommend(int whichCommend) {
        this.whichCommend = whichCommend;
    }


       @Override
    public int onStartCommand(Intent intent, int flag, int startId)
    {
        if(intent == null)
        {
            return Service.START_STICKY;
        }
        else
        {
            //Toast.makeText(this, "인텐트가 연속적으로 오나", Toast.LENGTH_SHORT).show();
            switch (intent.getStringExtra("type"))
            {
                case "bright":
                    int bright = intent.getIntExtra("bright", 0);
                    Settings.System.putInt(getContentResolver(), "screen_brightness", bright);
                    break;

                case "screen":

                    if(display == null) {
                        display = new MyLoadView(this);
                    }
                    else
                    {
                        //display.setVisibility(View.GONE);
                        mWindowManager.removeView(display);
                        display = new MyLoadView(this);
                    }


                    //mWindowManager = ( WindowManager ) getSystemService( WINDOW_SERVICE );
                    mWindowManager.addView( display, mParams );
                    whichCommend = intent.getIntExtra("getCommand", 0);
                    break;

                default:
            }


        }
        return super.onStartCommand(intent, flag, startId);
    }



    @Override
    public void onCreate()
    {
        //display = new MyLoadView( this );

        if(mParams == null)
        {
            mParams = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                            | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                            | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                    PixelFormat.TRANSLUCENT );

            mWindowManager = ( WindowManager ) getSystemService( WINDOW_SERVICE );
           // mWindowManager.addView( display, mParams );

        }
        super.onCreate();
    }






    @Override
    public IBinder onBind( Intent intent )
    {
        return null;
    }

    //서비스 종료시 뷰를 제거해야함
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if (display != null) {
            ((WindowManager) getSystemService(WINDOW_SERVICE)).removeView(display);
            display.invalidate();
            mPaint = null;
            display = null;
        }
    }
    /*
        com.euj.scit.newproject.ScreenFilterService


     */




    public class MyLoadView extends View
    {


        public MyLoadView( Context context )
        {
            super( context );
            mPaint = new Paint();
        }



        @Override
        protected void onDraw( Canvas canvas )
        {   //canvas.drawARGB( 100, 255, 212, 0 );
            super.onDraw( canvas );

            switch (whichCommend)
            {
                case MainActivity.MOVIE:
                    invalidate();
                    canvas.drawARGB( 30, 75, 75, 0 );
                    break;

                case MainActivity.OUTSIDEN:
                    invalidate();
                    canvas.drawARGB( 40, 75, 75, 0 );
                    //Settings.System.putInt(getContentResolver(), "screen_brightness", 70);
                    break;
                case MainActivity.OFFICE:
                    invalidate();
                    canvas.drawARGB( 50, 75, 75, 0);
                    /*android.provider.Settings.System.putInt(getContentResolver(),
                            android.provider.Settings.System.SCREEN_BRIGHTNESS, 120);*/
                    break;
                case MainActivity.OUTSIDEA:
                    invalidate();
                    canvas.drawARGB( 60, 75, 75, 0 );
                    //Settings.System.putInt(getContentResolver(), "screen_brightness", 150);
                    break;

            }

        }

        //화면에 뷰가 추가 될 때 호출 된다.
        @Override
        protected void onAttachedToWindow()
        {
            invalidate();
            super.onAttachedToWindow();
        }


        //화면에 뷰가 제거될 때 호출된다.
        @Override
        protected void onDetachedFromWindow()
        {
            super.onDetachedFromWindow();
        }

        //뷰의 크기나 위치를 계산할때 이용
        @Override
        protected void onMeasure( int widthMeasureSpec, int heightMeasureSpec )
        {
            super.onMeasure( widthMeasureSpec, heightMeasureSpec );
        }


    }


}