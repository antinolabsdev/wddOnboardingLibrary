package com.library.mylibrary;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.IntentCompat;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.model.CompareFacesMatch;
import com.amazonaws.services.rekognition.model.Label;
import com.amazonaws.services.rekognition.model.TextDetection;
import com.library.mylibrary.adapter.TabsPagerAdapter;
import com.library.mylibrary.apicalls.CompareFaceLabelAsycTask;
import com.library.mylibrary.apicalls.DetectLabelAsycTask;
import com.library.mylibrary.apicalls.DetectTextAsycTask;
import com.library.mylibrary.interfaces.CallbackInterface;

import com.library.mylibrary.interfaces.CompareFaceInterface;
import com.library.mylibrary.interfaces.DetectLabelCallback;
import com.library.mylibrary.interfaces.TextDetectionInterface;


import java.util.ArrayList;
import java.util.List;


public class ProfileMatchingActivity extends AppCompatActivity implements CompareFaceInterface, DetectLabelCallback, TextDetectionInterface, CallbackInterface {
    private static final String TAG = "ProfileMatching";
    private String cameraImageName, imagePickerName;
    private Bitmap bitmapcameraImage;
    private Uri imagePickeruriImage;
    private ImageView cbFacialMatching,cbIdMatching,cbObjectMatching;
    private ViewPager pager;
    public List<Label> labels=new ArrayList<>();
    public List<CompareFacesMatch> compareFacesMatchList=new ArrayList<>();
    private List<TextDetection> textDetectionImage;
    private ProgressBar progressBar;
    private TabsPagerAdapter adapter;
    private Boolean isCbFacial=false,isCbId=false,isCbObject=false;
    private String accesskey, secretKey,  wddOnboardingBucket;
    private RelativeLayout button;
    private int selectedColor=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_matching);
        cameraImageName = getIntent().getStringExtra(Constants.CAMERA_FILE);
        imagePickerName = getIntent().getStringExtra(Constants.IMAGE_FILENAME);
        bitmapcameraImage = getIntent().getParcelableExtra(Constants.CAMERA_IMAGE);
        imagePickeruriImage = Uri.parse(getIntent().getStringExtra(Constants.IMAGE_PICKER_IMAGE));
        accesskey= getIntent().getStringExtra(Constants.ACCESS_KEY);
        secretKey= getIntent().getStringExtra(Constants.SECRET_KEY);
        wddOnboardingBucket = getIntent().getStringExtra(Constants.BUCKET_NAME);
        selectedColor = getIntent().getIntExtra(Constants.COLORTYPE,0);
        if(selectedColor==0)
        {
            getSupportActionBar().setBackgroundDrawable(new
                    ColorDrawable((getColor(android.R.color.holo_blue_bright))));
        }else {
            getSupportActionBar().setBackgroundDrawable(new
                    ColorDrawable((selectedColor)));
        }
        initView();
        //All API CALLS
        CompareFaceLabelAsycTask compareFaceLabelAsycTask = new CompareFaceLabelAsycTask(cameraImageName,imagePickerName,this,this,accesskey,secretKey,wddOnboardingBucket);
        compareFaceLabelAsycTask.execute();
        DetectLabelAsycTask myAsyncTaskdetectLabel = new DetectLabelAsycTask(imagePickerName,this,this,accesskey,secretKey,wddOnboardingBucket);
        myAsyncTaskdetectLabel.execute();
        DetectTextAsycTask detectTextAsycTask = new DetectTextAsycTask(imagePickerName,this,this,accesskey,secretKey,wddOnboardingBucket);
        detectTextAsycTask.execute();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileMatchingActivity.this,WDDStarter.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
            }
        });

    }

    @Override
    public void onTaskComplete() {
        if(isCbObject)
        {
            cbObjectMatching.setBackgroundResource(R.drawable.selecticon);
            cbObjectMatching.setClickable(false);

        }
        if(isCbId)
        {
            cbIdMatching.setBackgroundResource(R.drawable.selecticon);
            cbIdMatching.setClickable(false);
        }
        if(isCbFacial)
        {
            cbFacialMatching.setBackgroundResource(R.drawable.selecticon);
            cbFacialMatching.setClickable(false);
        }

        adapter = new TabsPagerAdapter(this, getSupportFragmentManager(),
                labels, compareFacesMatchList,
                bitmapcameraImage,imagePickeruriImage,textDetectionImage);
        pager.setAdapter(adapter);
        Log.d(TAG, "onCreate: "+labels.size()+compareFacesMatchList.size());
        progressBar.setVisibility(View.GONE);
    }

    private void initView() {
        cbFacialMatching = findViewById(R.id.wdd_cb_facial_image);
        cbIdMatching = findViewById(R.id.wdd_cb_id_match);
        cbObjectMatching = findViewById(R.id.wdd_cb_object_find);
        pager = (ViewPager) findViewById(R.id.pager);
        progressBar = findViewById(R.id.wdd_profile_matching_pb);
        button = findViewById(R.id.wdd_back_to_home);
    }

    @Override
    public void detectLabelCallBack(List<Label> labels,Boolean value) {
        this.labels=labels;
        this.isCbObject=value;
    }

    @Override
    public void compareFaceCallback(List<CompareFacesMatch> compareFaceList,Boolean value) {
        this.compareFacesMatchList=compareFaceList;
        this.isCbFacial=value;
    }

    @Override
    public void textDetectionCallback(List<TextDetection> textDetections,Boolean value) {
        this.textDetectionImage=textDetections;
        this.isCbId=value;
    }


}