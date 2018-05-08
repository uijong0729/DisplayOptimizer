package com.euj.scit.newproject;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

/*
    Text and Image Service = tni

 */
public class tniservice extends Service {

    public tniservice() {

    }


    private TextView textView;                           //항상 보이게 할 뷰
    private WindowManager.LayoutParams mParams;  //layout params 객체. 뷰의 위치 및 크기
    private WindowManager mWindowManager;          //윈도우 매니저
    private long btnPressTime = 0;

    @Override
    public int onStartCommand(Intent intent, int flag, int startId)
    {
        if(intent == null)
        {
            return Service.START_STICKY;
        }
        else
        {
            if(intent.getStringExtra("type").equals("text"))
            {
                //Log.e("들어오는지 체크", "type체크");
                textView.setText(intent.getStringExtra("getText").toString());
                textView.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                textView.setGravity(View.TEXT_ALIGNMENT_CENTER);
                //textView.setAlpha(0.5f);
                textView.setPadding(15,15,15,15);
                textView.setBackgroundColor(Color.argb(150, 0, 0, 0));
                textView.setTextColor(Color.WHITE);
                textView.setTextSize(20);

                //Log.e("TextView완료 체크", " : 완료");

                mParams = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.TYPE_PHONE,  //TYPE_PHONE이 사라지고 26버전부터 추가
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,  //포커스를 가지지 않음
                        PixelFormat.TRANSLUCENT );

                mWindowManager = ( WindowManager ) getSystemService( WINDOW_SERVICE );
                //Log.e("mWindowManager 생성 체크", " : 완료");

                //Log.e("뷰 넣기전 null textview : ", textView.toString());
                //Log.e("뷰 넣기전 null mParams : ", mParams.toString());
                //Log.e("뷰 넣기전 null Manager : ", mWindowManager.toString());

                mWindowManager.addView( textView, mParams );
                Log.e("mWindowManager 뷰add 체크", " : 완료");

                TouchHandler th = new TouchHandler();
                textView.setOnTouchListener(th);
                //Log.e("핸들러 생성과 리스너 체크", " : 완료");

            }
            else if(intent.getStringExtra("type").equals("size"))
            {
                int textSize = intent.getIntExtra("size", 10);
                if(textView != null)
                {
                    textView.setTextSize(textSize);
                    mWindowManager.updateViewLayout(textView,mParams);
                }
            }

        }
        return super.onStartCommand(intent, flag, startId);
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public void onCreate() {
        TouchHandler th = new TouchHandler();
        //textView = new TextView(this);
        //Log.e("생성 체크", "onCreate");
        //Log.e("생성 체크", textView.toString());
        textView = new TextView(this);//뷰 생성
        textView.setOnTouchListener(th);

        super.onCreate();
    }



    @Override
    public void onDestroy() {
        if(textView != null) {
            ((WindowManager) getSystemService(WINDOW_SERVICE)).removeView(textView);
            //mWindowManager.removeView(textView);
            textView = null;
            mParams = null;
        }
        ((MainActivity)MainActivity.mContext).memoInvisibility();
        super.onDestroy();
    }





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

                    Toast.makeText(tniservice.this, "더블클릭하여 텍스트를 편집합니다. 메모를 삭제하시려면 취소를 눌러주세요.", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(tniservice.this, TextActivity.class);
                    intent.putExtra("oldText", textView.getText().toString());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    Log.e("onTouch: ", textView.getText().toString());
                    startActivity(intent);



                    ((MainActivity)MainActivity.mContext).memoVisibility();
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


  /*  if (System.currentTimeMillis() > btnPressTime + 1000) {
        btnPressTime = System.currentTimeMillis();
        Toast.makeText(getApplicationContext(), "한번 더 터치하면 실행됩니다.",
                Toast.LENGTH_SHORT).show();
        return;
        }
		if (System.currentTimeMillis() <= btnPressTime + 1000) {
        Intent it = new Intent(MainActivity.this,TwoActivity.class);
        startActivity(it);
        }*/
}
