package com.euj.scit.newproject;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.MainThread;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import static android.content.pm.PackageManager.PERMISSION_DENIED;

public class MainActivity extends AppCompatActivity {

    public static Context mContext;
    public View display;
    public TextView movie;
    public SeekBar sb, themb, textSize, imgSize;
    public Button themOut, memoOut;
    public LinearLayout themSeek, textSeek, imgSeek;

    //public boolean isRunningTextbox = false;

    //응답코드
    private static final int REQ_CODE_OVERLAY_PERMISSION = 1;
    private static final int REQ_CODE_WRITE_PERMISSION = 2;

    //명령코드
    public static final int MOVIE = 0;
    public static final int OUTSIDEN = 1;
    public static final int OFFICE = 2;
    public static final int OUTSIDEA = 3;
    public static final int SELECT = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mContext = this;
        /////////////////////////////////////////////////////////////////
        movie = (TextView) findViewById(R.id.movie);
        sb = (SeekBar) findViewById(R.id.bright);
        themb = (SeekBar) findViewById(R.id.them);
        textSize = (SeekBar) findViewById(R.id.textSize);
        imgSize = (SeekBar) findViewById(R.id.imgSize);
        themOut = (Button) findViewById(R.id.themCancle);
        memoOut = (Button) findViewById(R.id.memoCancle);
        themSeek = (LinearLayout) findViewById(R.id.themSeek);
        textSeek = (LinearLayout) findViewById(R.id.textSeek);
        imgSeek = (LinearLayout) findViewById(R.id.imgSeek);

        /////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////
        thembarHandler th = new thembarHandler();
        SeekbarHandler sh = new SeekbarHandler();
        ImgSeekbarHandler ibh = new ImgSeekbarHandler();
        memobarHandler mh = new memobarHandler();


        themb.setOnSeekBarChangeListener(th);
        textSize.setOnSeekBarChangeListener(mh);
        imgSize.setOnSeekBarChangeListener(ibh);
        sb.setOnSeekBarChangeListener(sh);


        themOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopService(new Intent(MainActivity.this, PictureService.class));
                themOut.setVisibility(View.INVISIBLE);
                themSeek.setVisibility(View.INVISIBLE);
            }
        });

        memoOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopService(new Intent(MainActivity.this, tniservice.class));
                memoOut.setVisibility(View.INVISIBLE);
                textSeek.setVisibility(View.INVISIBLE);
            }
        });

        ////////////////////////////////////////////////////////////////
            //시스템설정 권한검사
            if (Settings.System.canWrite(this)) {
                Log.e("에러메시지", "권한이 있습니다.");

            } else
                {
                Log.e("에러메시지", "권한이 없습니다.");

                Toast.makeText(this,"시스템 밝기모드를 설정하려면 시스템의 권한이 필요합니다. 설정페이지로 이동합니다.", Toast.LENGTH_SHORT).show();

                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_SETTINGS}, REQ_CODE_WRITE_PERMISSION);
                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + this.getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }

            if(isServiceRunningCheck("com.euj.scit.newproject.PictureService"))
            {
                if(themOut.getVisibility() == View.INVISIBLE || themSeek.getVisibility() == View.INVISIBLE) {
                    themOut.setVisibility(View.VISIBLE);
                    themSeek.setVisibility(View.VISIBLE);
                }
            }

        if(isServiceRunningCheck("com.euj.scit.newpro0ject.tniservice"))
        {
            if(memoOut.getVisibility() == View.INVISIBLE || textSeek.getVisibility() == View.INVISIBLE) {
                memoOut.setVisibility(View.VISIBLE);
                textSeek.setVisibility(View.VISIBLE);
            }
        }


        }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //테마설정
        if (id == R.id.action_settings) {

            if(Settings.canDrawOverlays(this) == false)
            {
                checkOverlayPermission();
            }
            else {

                Intent intent = new Intent(this, PostActivity.class);
                //돌아 올 때 아무것도 받지 않는 호출
                startActivity(intent);
                //돌아 올 때 무언가를 받는 호출
                //startActivityForResult(intent2, 1);
            }
            return true;
        }
        //메모 추가
        else if (id == R.id.action_textSetting)
        {
            if(Settings.canDrawOverlays(this) == false)
            {
                checkOverlayPermission();
            }
            else {
                    Intent intent = new Intent(this, TextActivity.class);
                    startActivity(intent);
                    //isRunningTextbox = true;

            }
            return true;
        }
        //도움말
        else if(id == R.id.action_imageSetting)
        {
            Intent intent = new Intent(this, HelperActivity.class);
            startActivity(intent);
            return true;
        }
        //개발자문의
        else if(id == R.id.action_sendEmail)
        {
            Uri uri = Uri.parse("mailto:uijong0729@naver.com");
            Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void clickMode(View v){
        //클릭시 권한
        //오버레이 권한검사
        if (Settings.canDrawOverlays(this)) {
            Log.e("에러메시지", "권한이 있습니다.");
            Intent intent = new Intent(MainActivity.this, ScreenFilterService.class);
            intent.putExtra("type", "screen");
            switch (v.getId())
            {
                case R.id.movie:
                    intent.putExtra("getCommand", MOVIE);
                    startService(intent);
                    Toast.makeText(this,"약 모드입니다", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.outsideN:
                    intent.putExtra("getCommand", OUTSIDEN);
                    startService(intent);
                    Toast.makeText(this,"중 모드입니다", Toast.LENGTH_SHORT).show();
                    break;

                case R.id.office:
                    intent.putExtra("getCommand", OFFICE);
                    startService(intent);
                    Toast.makeText(this,"강 모드입니다", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.outsideA:
                    intent.putExtra("getCommand", OUTSIDEA);
                    startService(intent);
                    Toast.makeText(this,"극강 모드입니다", Toast.LENGTH_SHORT).show();
                    break;

                default:
                    closeView();
                    break;
            }
        }
        else
        {
            Log.e("에러메시지", "권한이 없습니다.");
            Toast.makeText(this,"화면 오버레이 를 설정하려면 시스템의 권한이 필요합니다. 설정페이지로 이동합니다.", Toast.LENGTH_SHORT).show();

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW}, REQ_CODE_OVERLAY_PERMISSION);
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, REQ_CODE_OVERLAY_PERMISSION);
        }

    }

    public void closeView() {
        stopService(new Intent(this, ScreenFilterService.class));
    }


    class ImgSeekbarHandler implements SeekBar.OnSeekBarChangeListener{

        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            Intent intent = new Intent(MainActivity.this, PictureService.class);
            intent.putExtra("size", i);
            intent.putExtra("type", "sizeControl");
            startService(intent);
        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }





    //리스너
    class SeekbarHandler implements SeekBar.OnSeekBarChangeListener{

        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            if (Settings.System.canWrite(MainActivity.this)) {
                Intent intent = new Intent(MainActivity.this, ScreenFilterService.class);
                intent.putExtra("bright", i);
                intent.putExtra("type", "bright");
                startService(intent);
            } else
            {
                Log.e("에러메시지", "권한이 없습니다.");

                Toast.makeText(MainActivity.this,"시스템 밝기모드를 설정하려면 시스템의 권한이 필요합니다. 설정페이지로 이동합니다.", Toast.LENGTH_SHORT).show();

                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_SETTINGS}, REQ_CODE_WRITE_PERMISSION);
                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + MainActivity.this.getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }

        }



        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    class thembarHandler implements SeekBar.OnSeekBarChangeListener{

        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            Intent intent = new Intent(MainActivity.this, PictureService.class);
            intent.putExtra("alpha", i);
            intent.putExtra("intentType", "alpha");
            startService(intent);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    class memobarHandler implements SeekBar.OnSeekBarChangeListener{

        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            Intent intent = new Intent(MainActivity.this, tniservice.class);
            intent.putExtra("size", i);
            intent.putExtra("type", "size");
            startService(intent);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }


    public void themVisibility(){

        if(themOut.getVisibility() != View.VISIBLE) {
            themOut.setVisibility(View.VISIBLE);
        }

        if(themSeek.getVisibility() != View.VISIBLE)
        {
            themSeek.setVisibility(View.VISIBLE);
        }

    }

    public void themInvisibility(){

        if(themOut.getVisibility() == View.VISIBLE) {
            themOut.setVisibility(View.INVISIBLE);
            themSeek.setVisibility(View.INVISIBLE);
        }

    }


    public void memoVisibility(){

        if(memoOut.getVisibility() != View.VISIBLE) {
            memoOut.setVisibility(View.VISIBLE);
            textSeek.setVisibility(View.VISIBLE);
        }
        else {
            memoOut.setVisibility(View.INVISIBLE);
            textSeek.setVisibility(View.INVISIBLE);
        }

    }

    public void memoInvisibility(){

            memoOut.setVisibility(View.INVISIBLE);
            textSeek.setVisibility(View.INVISIBLE);

    }

    public void checkOverlayPermission(){
        Log.e("에러메시지", "권한이 없습니다.");
        Toast.makeText(this,"화면 오버레이 를 설정하려면 시스템의 권한이 필요합니다. 설정페이지로 이동합니다.", Toast.LENGTH_SHORT).show();

        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW}, REQ_CODE_OVERLAY_PERMISSION);
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, REQ_CODE_OVERLAY_PERMISSION);
    }

    public boolean isServiceRunningCheck(String serviceName) {
        try {
            ActivityManager manager = (ActivityManager) this.getSystemService(Activity.ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceName.equals(service.service.getClassName())) {
                    return true;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "백그라운드 앱에서 실행됩니다.", Toast.LENGTH_SHORT).show();


        super.onBackPressed();
    }
}
