package com.alladinsane.officelocator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class Splash extends Activity {


    private static ArrayList<OfficeLocation> officeLocations = new ArrayList<OfficeLocation>();
    Animation fade1, fade2;
    ImageView logo;
    TextView title;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        findViews();

        loadAnimations();

        runAnimations();

        DataManager dataManager = new DataManager(this);
        String url = getString(R.string.url);
        dataManager.setUrl(url);
        dataManager.execute();

        boolean flag = dataManager.getFinished();
        while (!flag)

        {
            try {
                Thread.sleep(100);
                flag = dataManager.getFinished();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        officeLocations = dataManager.getLocations();

        startNextActivityWhenAnimationCompletes();
    }
    public void loadAnimations()
    {
        fade1 = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fade2 = AnimationUtils.loadAnimation(this, R.anim.fade_in2);
    }
    public void runAnimations()
    {
        try{
            logo.startAnimation(fade1);
            title.startAnimation(fade2);
        }
        catch(NullPointerException e)
        {
            System.out.println("No animations were loaded");
        }
    }
    public void startNextActivityWhenAnimationCompletes()
    {
        fade2.setAnimationListener(new Animation.AnimationListener(){
            //Once animations have completed, our splash page launches
            //the main menu activity
            public void onAnimationEnd(Animation animation){
                Intent intent = buildIntent();
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                Splash.this.finish();
            }
            public Intent buildIntent()
            {
                Intent intent = new Intent(Splash.this, MapsActivity.class);
                intent.putParcelableArrayListExtra("officeLocations", officeLocations);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                return intent;
            }
            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub

            }
        });
    }

    public void onPause() {
        super.onPause();
        //stop the animation
        ImageView logo = (ImageView) findViewById(R.id.logo);
        TextView title = (TextView) findViewById(R.id.title);

        logo.clearAnimation();
        title.clearAnimation();
    }
    public void findViews()
    {
        logo = (ImageView) findViewById(R.id.logo);
        title = (TextView) findViewById(R.id.title);
    }
}