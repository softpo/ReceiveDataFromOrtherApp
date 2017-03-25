package com.softpo.receivedatafromortherapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //运行时权限，检查用户是否已经授权读取内存卡权限
        if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
        } else {
            //没有该权限，申请
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
        }

        // 获取意图，动作，以及MIME类型
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleSendText(intent); // 处理文本数据
            } else if (type.startsWith("image/")) {
                Log.d("flag", "----------------->onCreate: " + "image/");
                handleSendImage(intent); // 处理单张图片数据
            }
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
            if (type.startsWith("*/")) {
                handleSendMultipleImages(intent); // 处理多类型数据
            }
        }
    }

    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            //TextView展示数据
            TextView textView = (TextView) findViewById(R.id.text);
            textView.setText(sharedText);
        }
    }

    void handleSendImage(Intent intent) {
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            // ImageView 展示数据
            ImageView imageView = (ImageView) findViewById(R.id.img);
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageURI(imageUri);
        }
    }

    void handleSendMultipleImages(Intent intent) {
        ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (imageUris != null) {
            //显示图片，并且播放视频
            //ImageView展示数据
            ImageView imageView = (ImageView) findViewById(R.id.img);
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageURI(imageUris.get(0));
            //VideoView播放视频
            Uri video_uri = imageUris.get(1);
            VideoView videoView = (VideoView) findViewById(R.id.video);
            videoView.setVisibility(View.VISIBLE);
            videoView.setVideoURI(video_uri);
            MediaController controller = new MediaController(this);
            videoView.setMediaController(controller);
            controller.setMediaPlayer(videoView);
            videoView.start();
        }
    }


    //申请权限的回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "获取读取内存卡权限", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }
    }
}
