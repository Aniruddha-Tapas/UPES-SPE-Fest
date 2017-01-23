package com.myapps.upesse.upes_spefest.splash;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;

import com.myapps.upesse.upes_spefest.R;
import com.myapps.upesse.upes_spefest.auth.MainActivity;


public class AboutSplash extends AppCompatActivity {

    private static final String DEFAULT_CHECK="show-on-startup";
    private  static final String SHOW_OPTION="show";
    private ImageButton next;
    private CheckBox showByDefault;
    private SharedPreferences preferences;
     private  boolean result;
    private static final String ABOUT_NIMBUS = "Rules";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash2);



        preferences=getPreferences(MODE_PRIVATE);
        showByDefault=(CheckBox)findViewById(R.id.show_by_default);
        next=(ImageButton)findViewById(R.id.splash_next_imagebutton);
         Intent intent=getIntent();
        if(intent!=null){
         if(intent.hasExtra(SHOW_OPTION))  {
                result=intent.getBooleanExtra(SHOW_OPTION,false);
             if(result){
                 showByDefault.setVisibility(View.GONE);
                 next.setVisibility(View.GONE);
             }
            }
            if(intent.hasExtra(ABOUT_NIMBUS)){
                findViewById(R.id.video_img).setVisibility(View.GONE);
                showByDefault.setVisibility(View.GONE);
                next.setVisibility(View.GONE);
                findViewById(R.id.nimbus_is).setVisibility(View.GONE);
            }
        }
        Boolean isDefaultChecked=preferences.getBoolean(DEFAULT_CHECK,true);

        showByDefault.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(DEFAULT_CHECK, isChecked);
                editor.commit();

            }
        });

        showByDefault.setSelected(isDefaultChecked);

        if(isDefaultChecked==false){
            Intent i=null;
            if(!intent.hasExtra(ABOUT_NIMBUS)){
                if(!result){
                    i = new Intent(AboutSplash.this, MainActivity.class);
                    startActivity(i);
                    finish();
                    overridePendingTransition(R.anim.slide_in_done, R.anim.slide_out_done);

                }
            }
            else {
               // i=new Intent(AboutSplash.this,homeActivity.class);
            }


        }

        findViewById(R.id.video_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=Njw0IUR6j6s"));
                startActivity(browser);
            }
        });

        next.setRotation(-90);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AboutSplash.this, MainActivity.class);
                startActivity(i);
                finish();

                overridePendingTransition(R.anim.slide_in_done, R.anim.slide_out_done);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(result)
        overridePendingTransition(R.anim.close_main, R.anim.close_next);
    }


}