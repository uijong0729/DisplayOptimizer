package com.euj.scit.newproject;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.net.URI;

import static android.content.ContentValues.TAG;


public class PictureService extends Service {
    //public static boolean serviceCheck = true;

    public PictureService() {

    }


    private ImageView mImgView;                           //항상 보이게 할 뷰
    private WindowManager.LayoutParams mParams;  //layout params 객체. 뷰의 위치 및 크기
    private WindowManager mWindowManager;          //윈도우 매니저


    @Override
    public int onStartCommand(Intent intent, int flag, int startId)
    {
        if(intent == null)
        {
            return Service.START_STICKY;
        }
        else
        {
            if(intent.getStringExtra("intentType") != null && intent.getStringExtra("intentType").equals("alpha"))
            {
                int alpha = intent.getIntExtra("alpha", 77);
                mImgView.setImageAlpha(alpha);
                //mWindowManager.updateViewLayout(mImgView, mParams);
                //View(mImgView, mParams);
            }
            else if(intent.getStringExtra("type") != null && intent.getStringExtra("type").equals("sizeControl"))
            {
                Display display = ((MainActivity)MainActivity.mContext).getWindowManager().getDefaultDisplay();
                Point sizer = new Point();
                display.getSize(sizer);
                int width = sizer.x;
                int height = sizer.y;

                int size = intent.getIntExtra("size", 1);
                mParams.width = (width/3) + (width/12*size);
                mParams.height = (height/3) + (height/12*size);
                mWindowManager.updateViewLayout(mImgView, mParams);
            }
            else
             {
                Uri uri = intent.getParcelableExtra("uri");
                Log.e("onStartCommand: ", uri + "");
                mImgView.setImageURI(uri);
                mImgView.setImageAlpha(77);

                if (intent.getIntExtra("screenType", 0) == PostActivity.NOMAL_SCREEN)
                {
                    Display display = ((MainActivity)MainActivity.mContext).getWindowManager().getDefaultDisplay();
                    Point size = new Point();
                    display.getSize(size);
                    int width = size.x;
                    int height = size.y;


                    mImgView.setLayoutParams(new ViewGroup.LayoutParams(
                            width/3,
                            height/3));
                    mImgView.setScaleType(ImageView.ScaleType.FIT_XY);

                    mParams = new WindowManager.LayoutParams(
                            width/3,
                            height/3,
                            WindowManager.LayoutParams.TYPE_PHONE,  //TYPE_PHONE이 사라지고 26버전부터 추가
                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,  //포커스를 가지지 않음
                            PixelFormat.TRANSLUCENT );

                    TouchHandler th = new TouchHandler();
                    mImgView.setOnTouchListener(th);
                }
                else
                 {
                     mImgView.setLayoutParams(new ViewGroup.LayoutParams(
                             ViewGroup.LayoutParams.MATCH_PARENT,
                             ViewGroup.LayoutParams.MATCH_PARENT));
                     mImgView.setScaleType(ImageView.ScaleType.FIT_XY);


                     //최상위 윈도우에 넣기 위한 설정
                     mParams = new WindowManager.LayoutParams(
                             WindowManager.LayoutParams.MATCH_PARENT,
                             WindowManager.LayoutParams.MATCH_PARENT,
                             WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                             WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                                     | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                                     | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                             PixelFormat.TRANSLUCENT);
                 }

                                             //투명

                mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);  //윈도우 매니저
                mWindowManager.addView(mImgView, mParams);      //윈도우에 뷰 넣기. permission 필요.
            }

        }
        return super.onStartCommand(intent, flag, startId);
    }



    @Override
    public void onCreate() {
        super.onCreate();
        mImgView = new ImageView(this);



    }

    @Override
    public IBinder onBind(Intent arg0) { return null; }


    @Override
    public void onDestroy() {

        if(mImgView != null) {
            ((WindowManager) getSystemService(WINDOW_SERVICE)).removeView(mImgView);
            mWindowManager.removeView(mImgView);
            mImgView = null;
            mWindowManager = null;
        }
        ((MainActivity)MainActivity.mContext).imgSeek.setVisibility(View.INVISIBLE);
        super.onDestroy();
    }

    private long btnPressTime = 0;
    float xpos = 0;
    float ypos = 0;

    class TouchHandler implements View.OnTouchListener{

        @Override
        public boolean onTouch(View view, MotionEvent ME) {
            if(ME.getAction() == MotionEvent.ACTION_DOWN)
            {
                xpos = ME.getRawX();
                ypos = ME.getRawY();
                if (System.currentTimeMillis() > btnPressTime + 210) {
                    btnPressTime = System.currentTimeMillis();
                }else if (System.currentTimeMillis() <= btnPressTime + 210) {

                    Toast.makeText(PictureService.this, "더블클릭하여 이미지를 편집합니다. 삭제하시려면 취소를 눌러주세요.", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(PictureService.this, PostActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                    ((MainActivity)MainActivity.mContext).themInvisibility();
                    onDestroy();
                }
            }
            if(ME.getAction() == MotionEvent.ACTION_MOVE && mWindowManager != null)
            {
                WindowManager.LayoutParams lp = (WindowManager.LayoutParams) view.getLayoutParams();
                float dx = xpos - ME.getRawX();
                float dy = ypos - ME.getRawY();
                xpos = ME.getRawX();
                ypos = ME.getRawY();

                lp.x = (int) (lp.x - dx);
                lp.y = (int) (lp.y - dy);

                mWindowManager.updateViewLayout(view,lp);
            }

            return true;//터치모션 계속인식
        }
    }

}
