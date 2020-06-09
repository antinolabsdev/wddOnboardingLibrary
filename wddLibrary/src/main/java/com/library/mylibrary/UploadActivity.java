/*In this Activity we get the Camera image, file type and name from WDDStarter Activity.
  After that we get the same thing with image picker.
  then Upload the image in S3 bucket.
  For Uploading part  we use AWS Cognitio services and S3 bucket service

  Created By :- Prakhar Pandey
  Date:- 6/3/2020
 */
package com.library.mylibrary;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.library.mylibrary.Constants.BUCKET_NAME;


public class UploadActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "Upload Activity";
    private Button buttonUpload, buttonPicker;
    private ImageView imageViewPickImage, imageViewCameraImage;
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissions = new ArrayList<>();
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private final static int IMAGE_RESULT = 200;
    private final static int ALL_PERMISSIONS_RESULT = 107;
    private File fileCamera, fileImagePicker;
    private Bitmap bitmap;
    private Uri selectedImageUri;
    private Boolean stateCheckImage1 = false, stateCheckImage2 = false;
    private TransferUtility transferUtility;
    private String cameraPath, imagePickerPath;
    private String accessKey, secretKey;
    private ProgressDialog progressDialogUpload;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        //Getting the image of Camera from WDDStarterActivity
        bitmap = (Bitmap) getIntent().getParcelableExtra(Constants.THUMBNAIL);
        //Getting the filetype pf Camera frm WddStarterActivity
        fileCamera = (File) getIntent().getSerializableExtra(Constants.FILETYPE);
        accessKey = getIntent().getStringExtra(Constants.ACCESS_KEY);
        secretKey = getIntent().getStringExtra(Constants.SECRET_KEY);
        //GetName of Camera.
        cameraPath = fileCamera.getName();
        init();
        imageViewCameraImage.setImageBitmap(bitmap);
        createTransferUtility();
        onclick();
    }

    public void createTransferUtility() {
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                Constants.COGNITO_POOL_ID, // Identity pool ID
                Regions.US_EAST_1 // Region
        );
        AmazonS3Client s3Client = new AmazonS3Client(credentialsProvider);
        transferUtility = TransferUtility.builder().s3Client(s3Client).context(getApplicationContext()).build();
    }

    private void onclick() {
        buttonPicker.setOnClickListener(this);
        buttonUpload.setOnClickListener(this);
    }

    private void permissionList() {
        permissions.add(WRITE_EXTERNAL_STORAGE);
        permissions.add(READ_EXTERNAL_STORAGE);
        permissionsToRequest = findUnAskedPermissions(permissions);
    }

    private ArrayList<String> findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList<String>();
        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }
        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }


    private void pickImage() {

        permissionList();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0) {
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
            } else {


                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), IMAGE_RESULT);


            }
        }
    }

    private void init() {
        buttonPicker = findViewById(R.id.wdd_image_picker_btn);
        buttonUpload = findViewById(R.id.wdd_upload_btn);
        imageViewPickImage = findViewById(R.id.wdd_image_chooser_pick_image);
        imageViewCameraImage = findViewById(R.id.wdd_camera_pick_image);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (data != null) {
            selectedImageUri = data.getData(); // return list of selected images paths.
            Log.d("UploadActivity", "onActivityResult: " + selectedImageUri);
            imageViewPickImage.setImageURI(selectedImageUri);
            imagePickerPath = getFileNameFromUri(selectedImageUri);

            try {
                fileImagePicker = createFileFromUri(selectedImageUri, imagePickerPath);
                ;
            } catch (IOException e) {

            }

        }

    }

    String getFileNameFromUri(Uri uri) {
        Cursor returnCursor = getContentResolver().query(uri, null, null, null, null);
        int nameIndex = 0;
        if (returnCursor != null) {
            nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            returnCursor.moveToFirst();
            String name = returnCursor.getString(nameIndex);
            returnCursor.close();
            return name;
        } else {
            return "";
        }
    }

    File createFileFromUri(Uri uri, String objectKey) throws IOException {
        InputStream is = getContentResolver().openInputStream(uri);
        File file = new File(getCacheDir(), objectKey);
        file.createNewFile();
        FileOutputStream fos = new FileOutputStream(file);
        byte[] buf = new byte[2046];
        int read = -1;
        while ((read = is.read(buf)) != -1) {
            fos.write(buf, 0, read);
        }
        fos.flush();
        fos.close();
        return file;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.wdd_image_picker_btn) {
            buttonPicker.setVisibility(View.INVISIBLE);
            buttonUpload.setVisibility(View.VISIBLE);
            pickImage();
        } else if (v.getId() == R.id.wdd_upload_btn) {
            Log.d(TAG, "progress bar is shown: " + stateCheckImage1 + stateCheckImage2);
            upload(fileCamera, fileImagePicker, cameraPath, imagePickerPath);
            Log.d(TAG, "progress bar is shown: " + stateCheckImage1 + stateCheckImage2);
            progressDialogUpload = new ProgressDialog(UploadActivity.this);
            progressDialogUpload.setMessage("Uploading file... ");
            progressDialogUpload.setIndeterminate(true);
            progressDialogUpload.setCancelable(false);
            progressDialogUpload.show();


        }
    }


    void upload(File fileCamera, File fileImagePicker, final String objectKey1, String objectKey2) {
        Log.d(TAG, "onStateChanged: " + "upload method called");
        TransferObserver transferObserver = transferUtility.upload(
                "wddonboardingbucket",
                objectKey1,
                fileCamera, CannedAccessControlList.PublicRead
        );
        transferObserver.setTransferListener(new TransferListener() {

            @Override
            public void onStateChanged(int id, TransferState state) {
                Log.d(TAG, "Transfer observer onStateChanged: " + state);
                if (TransferState.COMPLETED.equals(state)) {
                    Log.d(TAG, "Transfer observer onStateChanged: " + state + stateCheckImage1);
                    progressDialogUpload.dismiss();
                    Toast.makeText(UploadActivity.this, "Image uploaded", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

            }

            @Override
            public void onError(int id, Exception ex) {
                Toast.makeText(UploadActivity.this, "Error: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        TransferObserver transferObserverTargetImage = transferUtility.upload(
                BUCKET_NAME,
                objectKey2,
                fileImagePicker, CannedAccessControlList.PublicRead
        );
        transferObserverTargetImage.setTransferListener(new TransferListener() {

            @Override
            public void onStateChanged(int id, TransferState state) {
                Log.d(TAG, " Transfer image Target Image onStateChanged2: " + state);
                if (TransferState.COMPLETED.equals(state)) {
                    Log.d(TAG, "Transfer observer onStateChanged: " + state + stateCheckImage2);
                    progressDialogUpload.dismiss();
                    startActivity(new Intent(UploadActivity.this, ProfileMatchingActivity.class)
                            .putExtra(Constants.CAMERA_FILE, cameraPath)
                            .putExtra(Constants.IMAGE_FILENAME, imagePickerPath)
                            .putExtra(Constants.CAMERA_IMAGE, bitmap)
                            .putExtra(Constants.IMAGE_PICKER_IMAGE, selectedImageUri.toString())
                            .putExtra(Constants.ACCESS_KEY, accessKey)
                            .putExtra(Constants.SECRET_KEY, secretKey));
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

            }


            @Override
            public void onError(int id, Exception ex) {
                Toast.makeText(UploadActivity.this, "Error: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}


