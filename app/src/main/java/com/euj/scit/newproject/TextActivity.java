package com.euj.scit.newproject;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class TextActivity extends Activity {

    Button textConfirm, textCancle;
    EditText textContent;
    FileOutputStream out = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);
        textConfirm = (Button) findViewById(R.id.textConfirm);
        textCancle = (Button) findViewById(R.id.textCancle);
        textContent = (EditText) findViewById(R.id.textContent);

        //인텐트
        Intent getIntent = getIntent();
        processIntent(getIntent);

        if (isServiceRunningCheck("com.euj.scit.newproject.tniservice")) {
            stopService(new Intent(this, tniservice.class));
        }

        if (Settings.canDrawOverlays(this) == false)
        {
            Toast.makeText(this,"화면 오버레이 를 설정하려면 시스템의 권한이 필요합니다. 설정페이지로 이동합니다.", Toast.LENGTH_SHORT).show();

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW}, 1);
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 1);
        }



        //파일이 있으면 불러오기
        FileInputStream fis = null;

        try {
            fis = openFileInput("fileName");
            //fis.read();       // 1바이트씩 읽음 (반복문으로 처리해야함)
            //fis.read(array);  // 바이트 배열을 통째로 읽음
            byte[] buf = new byte[fis.available()];
            fis.read(buf);
                        /*
                            .available() : 객체의 용량(바이트크기)을 리턴한다.
                         */

            //파일에 있는 문자열을 textview로 가져온다.
            String str = new String(buf);
            textContent.setText(str);

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                fis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    public void textButton(View view){

        if(view.getId() == R.id.textConfirm){

            //텍스트 입력완료
            String getText = insertEnter(textContent.getText().toString());

            if(getText.length() >= 1) {
                //힘들 땐, 열심히 하겠다던 초심 생각하즈아아아아아ㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏ
                Intent intent = new Intent(TextActivity.this, tniservice.class);
                intent.putExtra("type", "text");
                intent.putExtra("getText", getText);
                startService(intent);
                ((MainActivity) MainActivity.mContext).memoVisibility();

                //여기서부터 파일관리
                String fileName = "fileName";
                String fileContent = textContent.getText().toString();
                try {
                    out = openFileOutput(fileName, Context.MODE_PRIVATE);// MODE_PRIVATE : 기본 내장메모리
                    //파일에 쓰기
                    out.write(fileContent.getBytes());

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    //스트림 닫기
                    if (out != null) {
                        try {
                            out.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                //액티비티 반환
                finish();
            }
            else
            {
                Toast.makeText(this, "한 글자 초과로 입력하셔야 합니다", Toast.LENGTH_SHORT).show();
            }

        }
        else if(view.getId() == R.id.textCancle) {
            //텍스트 입력취소
            stopService(new Intent(TextActivity.this, tniservice.class));
            finish();
        }
        else if(view.getId() == R.id.textReset)
        {
            textContent.setText("");
            String fileName = "fileName";
            String fileContent = "";
            try
            {
                out = openFileOutput(fileName, Context.MODE_PRIVATE);// MODE_PRIVATE : 기본 내장메모리
                //파일에 쓰기
                out.write(fileContent.getBytes());

            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                //스트림 닫기
                if(out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }

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

    public String insertEnter(String msg)
    {
        StringBuffer sb = new StringBuffer();

        for(int i = 0 ; i < msg.length(); i++)
        {
            char ch = msg.charAt(i);
            if(i % 10 == 0 && i != 0)
            {
                sb.append(ch + "\n");
            }
            else
            {
                sb.append(ch);
            }
        }


        return sb.toString();
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        processIntent(intent);
        super.onNewIntent(intent);
    }

    private void processIntent(Intent intent)
    {
        if(intent != null)
        {
            String geText = intent.getStringExtra("oldText");
            Log.e("onintent: ", textContent.getText().toString());
            textContent.setText(geText);
        }
    }

/*

   String getText = data.getStringExtra("oldText");
          textContent.setText(getText);
*
* */
}
