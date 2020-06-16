package com.library.mylibrary;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import com.library.mylibrary.camera.FunCamera;
import com.library.mylibrary.imagecropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.library.mylibrary.imagecropper.CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE;
import static com.library.mylibrary.imagecropper.CropImage.getActivityResult;

public class WDDStarter extends AppCompatActivity {
    private static final String TAG = "WDDStarter";
    private MenuItem menu_setting;
    private Button wddStarterButton;
    private ArrayList<String> permissions = new ArrayList<>();
    private ArrayList<String> permissionsToRequest;
    private static final int REQUEST_IMAGE_CAPTURE = 1176, ALL_PERMISSIONS_RESULT_CAMERA = 1107, THUMBNAIL_SIZE = 200;
    private String pathOrigin;
    private File filelocation;
    private String accessKey, secretKey, cognitoPoolId, wddOnboardingBucket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_w_d_d_starter);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        accessKey = getIntent().getStringExtra(Constants.ACCESS_KEY);
        secretKey = getIntent().getStringExtra(Constants.SECRET_KEY);
        cognitoPoolId = getIntent().getStringExtra(Constants.COGNITO_POOL_ID);
        wddOnboardingBucket = getIntent().getStringExtra(Constants.BUCKET_NAME);
        Log.d(TAG, "onCreate: "+accessKey+" "+secretKey+" "+cognitoPoolId+" "+wddOnboardingBucket);
        wddStarterButton = (Button) findViewById(R.id.wdd_started_btn);
        wddStarterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });

    }

    public void openCamera() {
        permissions.add(WRITE_EXTERNAL_STORAGE);
        permissions.add(READ_EXTERNAL_STORAGE);
        permissions.add(CAMERA);
        permissionsToRequest = findUnAskedPermissions(permissions);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (permissionsToRequest.size() > 0) {
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT_CAMERA);
            } else {
                FunCamera.capturePhoto(WDDStarter.this, REQUEST_IMAGE_CAPTURE);
            }
        }

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                try {
                    Bitmap bitmap = getThumbnail(resultUri);
                    Log.d(TAG, "onCreate: "+accessKey+" "+secretKey+" "+cognitoPoolId+" "+wddOnboardingBucket);
                    startActivity(new Intent(WDDStarter.this, UploadActivity.class)
                            .putExtra(Constants.THUMBNAIL, bitmap)
                            .putExtra(Constants.PATH, pathOrigin)
                            .putExtra(Constants.FILETYPE, filelocation)
                            .putExtra(Constants.ACCESS_KEY, accessKey)
                            .putExtra(Constants.SECRET_KEY, secretKey)
                            .putExtra(Constants.COGNITO_POOL_ID, cognitoPoolId)
                            .putExtra(Constants.BUCKET_NAME, wddOnboardingBucket));

                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (data != null) {
                pathOrigin = data.getStringExtra(FunCamera.DATA_ORIGIN);
                Log.d(TAG, "onActivityResult: Path:" + pathOrigin);
                try {
                    filelocation = new File(pathOrigin);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Uri apkURI = FileProvider.getUriForFile(getApplicationContext(), getPackageName() + ".provider", filelocation);
                        performCrop(apkURI);
                    } else {

                        performCrop(Uri.fromFile(filelocation));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Bitmap getThumbnail(Uri uri) throws FileNotFoundException, IOException {
        InputStream input = getContentResolver().openInputStream(uri);

        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither = true;//optional
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();

        if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1)) {
            return null;
        }

        int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight : onlyBoundsOptions.outWidth;

        double ratio = (originalSize > THUMBNAIL_SIZE) ? (originalSize / THUMBNAIL_SIZE) : 1.0;

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
        bitmapOptions.inDither = true; //optional
        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//
        input = this.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();
        return bitmap;
    }

    private static int getPowerOfTwoForSampleRatio(double ratio) {
        int k = Integer.highestOneBit((int) Math.floor(ratio));
        if (k == 0) return 1;
        else return k;
    }

    private void performCrop(Uri picUri) {
        CropImage.activity(picUri)
                .start(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_item, menu);
        menu_setting = (MenuItem) menu.findItem(R.id.wdd_setting_menu);

        menu_setting.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                startActivity(new Intent(WDDStarter.this, SettingActivity.class));
                finish();
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}
