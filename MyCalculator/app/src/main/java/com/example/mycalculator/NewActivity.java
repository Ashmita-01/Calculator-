package com.example.mycalculator;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class NewActivity extends AppCompatActivity {
    TextView tv;
    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);

        Toast.makeText(this,"Button Clicked",Toast.LENGTH_SHORT).show();

        Button pickImage = findViewById(R.id.pick_img);

        pickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ActivityCompat.checkSelfPermission(NewActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){

                    ActivityCompat.requestPermissions(NewActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            100);

                    return;
                }
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
                intent.setType("image/*");
                startActivityForResult(intent,1);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1 && resultCode == RESULT_OK){
            img = findViewById(R.id.img_view);
            final List<Bitmap> bitmaps = new ArrayList<>();

            ClipData clipData = data.getClipData();

            if(clipData!=null){
                for(int i=0;i<clipData.getItemCount();i++){
                    Uri imageUri = clipData.getItemAt(i).getUri();

                    try{
                        InputStream is = getContentResolver().openInputStream(imageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(is);
                        bitmaps.add(bitmap);

                    }
                    catch (FileNotFoundException e){
                        e.printStackTrace();
                    }
                }

            }else
            {
                Uri imageUri = data.getData();
                try {
                    InputStream is = getContentResolver().openInputStream(imageUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    bitmaps.add(bitmap);
                }
                catch (FileNotFoundException e){
                    e.printStackTrace();
                }
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for(final Bitmap b : bitmaps){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                img.setImageBitmap(b);
                            }
                        });
                        try {
                            Thread.sleep(3000);
                        }
                        catch (InterruptedException e){
                            e.printStackTrace();
                        }
                    }

                }
            }).start();
        }
    }
}
