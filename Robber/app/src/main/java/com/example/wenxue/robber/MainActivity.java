package com.example.wenxue.robber;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView mText = null;
    private Button mButton = null;
    private Button mBtnEnd = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mText = (TextView)findViewById(R.id.text);
        mButton = (Button)findViewById(R.id.button);
        mBtnEnd = (Button)findViewById(R.id.btn);
        mButton.setOnClickListener(this);
        mBtnEnd.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
//        switch(v.getId()){
//            case R.id.button:
//                startActivity(intent);
//                break;
//            case R.id.btn:
//                startActivity(intent);
//                break;
//        }
        startActivity(intent);
        Log.d("robber", "onClick()");
    }
}
