package com.library.mylibrary.apicalls;

import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClient;
import com.amazonaws.services.rekognition.model.DetectTextRequest;
import com.amazonaws.services.rekognition.model.DetectTextResult;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.S3Object;
import com.amazonaws.services.rekognition.model.TextDetection;
import com.library.mylibrary.interfaces.CallbackInterface;
import com.library.mylibrary.ProfileMatchingActivity;
import com.library.mylibrary.interfaces.TextDetectionInterface;
import com.library.mylibrary.Constants;

import java.util.ArrayList;
import java.util.List;

public class DetectTextAsycTask extends AsyncTask<Void,Void, List<TextDetection>> {

    private String cameraImage;
    private List<TextDetection> textDetectionsResult = new ArrayList<>();
    private ProfileMatchingActivity profileMatchingActivity;
    SharedPreferences sharedPreferences;
    private TextDetectionInterface textDetctionInterface;
    private CallbackInterface callBackInterface;
    private Boolean checkBooleanResulDetectText=false;
    private String accessKey;
    private String secretKey;
    private String bucketName;

    public DetectTextAsycTask(String cameraImageName, ProfileMatchingActivity profileMatchingActivity, CallbackInterface callBackInterface, String bucketName, String accesskey, String secretKey) {
        this.cameraImage=cameraImageName;
        this.textDetctionInterface= (TextDetectionInterface) profileMatchingActivity;
        this.callBackInterface=callBackInterface;
        this.accessKey=accesskey;
        this.secretKey=secretKey;
        this.bucketName=bucketName;
    }

    @Override
    protected List<TextDetection> doInBackground(Void... voids) {
        AmazonRekognition rekognitionClient = new AmazonRekognitionClient(new BasicAWSCredentials(accessKey, secretKey));

        DetectTextRequest request = new DetectTextRequest()
                .withImage(new Image()
                        .withS3Object(new S3Object()
                                .withName(cameraImage)
                                .withBucket(bucketName)));


        try {
            DetectTextResult result = rekognitionClient.detectText(request);
            System.out.println("Detected lines and words for " + cameraImage);
            List<TextDetection> textDetections = result.getTextDetections();
            textDetectionsResult=textDetections;
            if(textDetectionsResult!=null)
            {
                checkBooleanResulDetectText=true;
                }


            for (TextDetection text: textDetections) {

                System.out.println("Detected: " + text.getDetectedText());
                System.out.println("Confidence: " + text.getConfidence().toString());
                System.out.println("Id : " + text.getId());
                System.out.println("Parent Id: " + text.getParentId());
                System.out.println("Type: " + text.getType());
                System.out.println();
            }


        } catch(Exception e) {
            e.printStackTrace();
        }
        return textDetectionsResult;
    }

    @Override
    protected void onPostExecute(List<TextDetection> textDetections) {
        super.onPostExecute(textDetections);
        textDetctionInterface.textDetectionCallback(textDetections,checkBooleanResulDetectText);
        callBackInterface.onTaskComplete();
    }
}
