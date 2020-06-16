package com.library.wdd;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.library.mylibrary.CustomButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final CustomButton relativeLayout = new CustomButton(MainActivity.this);
        CustomButton.KeyAccess("AKIA2FUYI7S2MOWWDL4W","mTaaNUa8t7InNVNNYq+gptb4QdLmHcFE4/HyDcrI","us-east-1:153fc86c-6b7c-4a1b-b253-9948f9409c4d","wddonboardingbucket");
        final ConstraintLayout constraintLayout =  findViewById(R.id.parent);
        final TextView textView = findViewById(R.id.tvClick);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                relativeLayout.init();


            }
        });

        constraintLayout.addView(relativeLayout);




    }
}
