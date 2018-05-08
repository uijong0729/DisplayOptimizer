package com.euj.scit.newproject;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by SCIT on 2018-03-09.
 */

public class HelperActivity extends Activity {

    private int currentPage;
    private ImageView helper;
    private TextView tv1, tv2, tv3;

    public HelperActivity() {
        super();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.helperscreen);
        currentPage = 1;
        helper = (ImageView) findViewById(R.id.helper);
        tv1 = (TextView) findViewById(R.id.page1);
        tv2 = (TextView) findViewById(R.id.page2);
        tv3 = (TextView) findViewById(R.id.page3);
        TouchHandler th = new TouchHandler();
        helper.setOnTouchListener(th);
    }

    public void clickPage(View v)
    {
        if(v.getId() == R.id.page1)
        {
            if(currentPage != 1)
            {
                currentPage = 1;
                helper.invalidate();
                helper.setImageResource(R.drawable.menubar);
                tv1.setTextColor(Color.BLUE);
                tv2.setTextColor(Color.WHITE);
                tv3.setTextColor(Color.WHITE);

            }
        }
        else if(v.getId() == R.id.page2)
        {
            if(currentPage != 2)
            {
                currentPage = 2;
                helper.invalidate();
                helper.setImageResource(R.drawable.themselect);
                tv2.setTextColor(Color.BLUE);
                tv1.setTextColor(Color.WHITE);
                tv3.setTextColor(Color.WHITE);
            }

        }
        else if(v.getId() == R.id.page3)
        {
            if(currentPage != 3)
            {
                currentPage = 3;
                helper.invalidate();
                helper.setImageResource(R.drawable.memoconfirm);
                tv3.setTextColor(Color.BLUE);
                tv2.setTextColor(Color.WHITE);
                tv1.setTextColor(Color.WHITE);
            }
        }
    }

    public class TouchHandler implements View.OnTouchListener{

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

            float getOriginalX = 0f;
            float getChangeX = 0f;

            if(motionEvent.getAction() == MotionEvent.ACTION_DOWN)
            {
                getOriginalX = motionEvent.getX();
            }
            else if(motionEvent.getAction() == MotionEvent.ACTION_UP)
            {
                getChangeX = motionEvent.getX();

                float moveDistance = (getChangeX - getOriginalX);
                //Toast.makeText(HelperActivity.this, "Distance: "+ moveDistance, Toast.LENGTH_LONG).show();
                if(moveDistance > 50 && moveDistance < 600)
                {
                    switch (currentPage)
                    {
                        case 1:
                            currentPage++;
                            helper.invalidate();
                            helper.setImageResource(R.drawable.themselect);
                            tv2.setTextColor(Color.BLUE);
                            tv1.setTextColor(Color.WHITE);
                            tv3.setTextColor(Color.WHITE);
                            break;
                        case 2:
                            currentPage++;
                            helper.invalidate();
                            helper.setImageResource(R.drawable.memoconfirm);
                            tv3.setTextColor(Color.BLUE);
                            tv1.setTextColor(Color.WHITE);
                            tv2.setTextColor(Color.WHITE);
                            break;
                        case 3:
                            break;
                        default:
                    }
                }
                else if(moveDistance > 700)
                {
                    switch (currentPage)
                    {
                        case 1:
                            tv1.setTextColor(Color.BLUE);
                            tv2.setTextColor(Color.WHITE);
                            tv3.setTextColor(Color.WHITE);
                            break;
                        case 2:
                            currentPage--;
                            helper.invalidate();
                            helper.setImageResource(R.drawable.menubar);
                            tv1.setTextColor(Color.BLUE);
                            tv2.setTextColor(Color.WHITE);
                            tv3.setTextColor(Color.WHITE);
                            break;
                        case 3:
                            currentPage--;
                            helper.invalidate();
                            helper.setImageResource(R.drawable.themselect);
                            tv2.setTextColor(Color.BLUE);
                            tv1.setTextColor(Color.WHITE);
                            tv3.setTextColor(Color.WHITE);
                            break;
                        default:
                    }
                }
            }

            return true;
        }
    }
}
