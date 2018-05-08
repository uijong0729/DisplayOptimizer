package com.euj.scit.newproject;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Picture;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;


/**
 * Created by SCIT on 2018-03-06.
 */

public class PostActivity extends Activity{

    //갤러리코드
    private final int GALLERY_CODE = 7;
    private final int REQ_GALLERY_CODE = 8;
    public ImageView iv;
    private ToggleButton imgToggle;
    private int imgCode = 0;
    public final static int FULL_SCREEN = 1;
    public final static int NOMAL_SCREEN = 2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_layout);
        //////
        iv =  (ImageView) findViewById(R.id.imgBack);
        imgToggle = (ToggleButton) this.findViewById(R.id.imgToggle);

        if(imgToggle == null) {
            Log.e("onCreate: ", "toggle = null");
        }


        imgToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imgToggle.isChecked())
                {
                    //true (ON)
                    imgCode = NOMAL_SCREEN;
                    ((MainActivity)MainActivity.mContext).imgSeek.setVisibility(View.VISIBLE);
                }
                else
                {
                    //false (OFF)
                    imgCode = FULL_SCREEN;
                    ((MainActivity)MainActivity.mContext).imgSeek.setVisibility(View.INVISIBLE);
                }
            }
        });

        //////

        if (isServiceRunningCheck("com.euj.scit.newproject.PictureService")) {
            stopService(new Intent(this, PictureService.class));
        }
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
        {
            Log.e("imgClick: ", "권한 없을 때");
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQ_GALLERY_CODE);


        }
    }




    public void imgClick(View view){

        if (Settings.canDrawOverlays(this) == false)
        {
            Toast.makeText(this,"화면 오버레이 를 설정하려면 시스템의 권한이 필요합니다. 설정페이지로 이동합니다.", Toast.LENGTH_SHORT).show();

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW}, 1);
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 1);
        }
        else
        {

        }


        //Log.e("imgClick: ", "권한체크 하는지 ");
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
        {
            Log.e("imgClick: ", "권한 없음");
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQ_GALLERY_CODE);
        }
        else {
            Log.e("imgClick: ", "권한 있을 때");
            //Toast.makeText(this, "이미지", Toast.LENGTH_SHORT).show();
            if (isServiceRunningCheck("com.euj.scit.newproject.PictureService") == false)
            {
                //PictureService.serviceCheck = false;
                startService(new Intent(this, PictureService.class));
            } else {
                //PictureService.serviceCheck = true;
                stopService(new Intent(this, PictureService.class));
            }
        }
    }

    public void imgMenu(View view)
    {
        if(view.getId() == R.id.selectImg)
        {
            Intent intent = new Intent();
            // Show only images, no videos or anything else
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_CODE);
        }
        else if(view.getId() == R.id.cancleImg)
        {
            ((MainActivity)MainActivity.mContext).themSeek.setVisibility(View.INVISIBLE);
            ((MainActivity)MainActivity.mContext).themOut.setVisibility(View.INVISIBLE);
            ((MainActivity)MainActivity.mContext).imgSeek.setVisibility(View.INVISIBLE);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();
            //Toast.makeText(this, uri.toString(), Toast.LENGTH_LONG).show();
            Log.e("onActivityResult: ", uri.toString());

            try {
                //Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));
                ImageView imageView = (ImageView) findViewById(R.id.imgBack);
                imageView.setImageURI(uri);
                //imageView.setImageBitmap(bitmap);

                Intent sendUri = new Intent(this, PictureService.class);
                sendUri.putExtra("uri", uri);
                sendUri.putExtra("screenType", imgCode);
                startService(sendUri);
                ((MainActivity)MainActivity.mContext).themVisibility();

            } catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                finish();
            }
        }
    }










    public boolean isServiceRunningCheck(String serviceName) {
        ActivityManager manager = (ActivityManager) this.getSystemService(Activity.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceName.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }





    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {


            if (requestCode == GALLERY_CODE) {
                iv.setImageBitmap(null);

                Uri mediaUri = data.getData();
                String mediaPath = mediaUri.getPath();

                //display the image
                try {
                    InputStream inputStream = getBaseContext().getContentResolver().openInputStream(mediaUri);
                    Bitmap bm = BitmapFactory.decodeStream(inputStream);

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    byte[] byteArray = stream.toByteArray();

                    iv.setImageBitmap(bm);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }*/
}
