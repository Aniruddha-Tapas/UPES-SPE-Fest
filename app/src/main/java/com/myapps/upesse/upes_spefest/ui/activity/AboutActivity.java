package com.myapps.upesse.upes_spefest.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import com.myapps.upesse.upes_spefest.R;

public class AboutActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.about);


        findViewById(R.id.video_img_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=Njw0IUR6j6s"));
                startActivity(browser);
            }
        });

        findViewById(R.id.website).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent web = new Intent(Intent.ACTION_VIEW, Uri.parse("http://upesspe.org/"));
                startActivity(web);
            }
        });

    }

}


