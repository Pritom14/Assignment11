package com.example.shaloin.eleventhassignmentd;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.jar.Manifest;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ProgressDialog progressDialog;
    private ProgressBar progressBar;
    private int mProgressStatus = 0;
    EditText editText;
    private ImageView imageView;
    File fileForBitMapImage;
    File makeDirectory;
    File imagepath;
    private static final int PERMISSIONS_REQUEST_WRITE_IMAGE = 100;
    static final Integer WRITE_EXST = 0x3;
    static final Integer READ_EXST = 0x4;
    public static String strpath = android.os.Environment.getExternalStorageDirectory().toString();
    public static String dirName = "DIR_NAME";
    private String url = "http://www.cartoonize.net/sample/Effect2.jpg";
    private String et_url;
    private Bitmap bitmap = null;
    Button downButton, showSDButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.shownImageID);
        progressBar = (ProgressBar) findViewById(R.id.progressID);
        progressBar.setVisibility(View.INVISIBLE);
        editText = (EditText) findViewById(R.id.urlEditText);
        downButton = (Button) findViewById(R.id.startButton);
        showSDButton = (Button) findViewById(R.id.showButton);
        
        downButton.setOnClickListener(this);
        showSDButton.setOnClickListener(this);
    }

    private Handler messageHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            imageView.setImageBitmap(bitmap);
            progressBar.setVisibility(View.INVISIBLE);
        }
    };

    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permission)) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
            }
        } else {
            Toast.makeText(MainActivity.this, "Hello", Toast.LENGTH_LONG).show();
        }
    }

    public boolean saveImageInSDCard(Bitmap bitmap) {
        boolean success = false;
        makeDirectory = new File(strpath + "/" + dirName);
        makeDirectory.mkdir();
        String fileName = "your" + ".png";
        String dirPath = strpath + "/" + dirName + "/";

        FileOutputStream outputStream;

        File storagePath = new File(dirPath);
        fileForBitMapImage = new File(storagePath, fileName);
        try {
            outputStream = new FileOutputStream(fileForBitMapImage);
            success = bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            try {
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return success;
    }

    @Override
    public void onClick(View view) {

    }

    public void ask(View v) {
        switch (v.getId()) {
            case R.id.startButton:
                downloadImageFromInternet();
                break;
            case R.id.showButton:
                showImageFromSD();
                break;
            default:
                break;
        }
    }

    public void downloadImageFromInternet() {
        et_url = editText.getText().toString();
        if (checkURL(et_url)) {
            url = et_url;

        } else {
            Toast.makeText(getApplicationContext(), "Not valid", Toast.LENGTH_LONG).show();
        }
        progressBar.setVisibility(View.VISIBLE);
        new Thread() {
            public void run() {
                bitmap = downloadBitmap(url);
                messageHandler.sendEmptyMessage(0);
            }
        }.start();
    }

    private Bitmap downloadBitmap(String imageURL) {
        bitmap = null;
        try {
            // Download Image from URL
            InputStream input = new java.net.URL(imageURL).openStream();
            // Decode Bitmap
            bitmap = BitmapFactory.decodeStream(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        Boolean flag = false;
//        if(askPermission()){
        Boolean flag = saveImageInSDCard(bitmap);
        //}

        if (flag) {
            Log.e("DB ", "Image saved");
        } else {
            Log.e("DB ", "Image NOT saved");
        }
        return bitmap;
    }

    public static boolean checkURL(CharSequence input) {
        if (TextUtils.isEmpty(input)) {
            return false;
        }
        Pattern URL_PATTERN = Patterns.WEB_URL;
        boolean isURL = URL_PATTERN.matcher(input).matches();
        if (!isURL) {
            String urlString = input + "";
            if (URLUtil.isNetworkUrl(urlString)) {
                try {
                    new URL(urlString);
                    isURL = true;
                } catch (Exception e) {
                }
            }
        }
        return isURL;
    }

    private void showImageFromSD() {
        if (fileForBitMapImage.exists()) {
            imageView.setImageBitmap(BitmapFactory.decodeFile(fileForBitMapImage.getAbsolutePath()));
        } else {
            Log.e("showImg ", "FILE NOT EXIST");
        }
    }
}
