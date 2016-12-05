package com.dafasoft.covernavigation;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {

    private TextView mStepOneBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mStepOneBtn = (TextView) findViewById(R.id.tv_step_one);
        ShowTipsView tipsView = new ShowTipsView(this);
        tipsView.setTarget(mStepOneBtn);
        tipsView.show(this);
    }
}
