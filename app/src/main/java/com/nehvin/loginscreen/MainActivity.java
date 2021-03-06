package com.nehvin.loginscreen;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.squareup.leakcanary.LeakCanary;

import java.util.List;

import butterknife.BindViews;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindViews(value = {R.id.logo,R.id.first,R.id.second,R.id.last})
    protected List<ImageView> sharedElements;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(getApplication());

        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
//        final AnimatedViewPager pager= findViewById(R.id.pager);
//        final ImageView background=findViewById(R.id.scrolling_background);
        final AnimatedViewPager pager= ButterKnife.findById(this, R.id.pager);
        final ImageView background=ButterKnife.findById(this, R.id.scrolling_background);
        int[] screenSize = screenSize();

        for(ImageView element:sharedElements){
            @ColorRes int color=element.getId() != R.id.logo ? R.color.white_transparent:R.color.color_logo_log_in;
            DrawableCompat.setTint(element.getDrawable(), ContextCompat.getColor(this,color));
        }

        //load a very big image and resize it, so it fits our needs
        Glide.with(this)
                .asBitmap()
                .load(R.drawable.busy)
                .apply(new RequestOptions()
                        .override(screenSize[0]*2,screenSize[1])
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC))
//                .override(screenSize[0]*2,screenSize[1])
//                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .into(new ImageViewTarget<Bitmap>(background) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        background.setImageBitmap(resource);
//                        background.scrollTo(-pager.getWidth(),0);
                        background.post(()->{
                            //we need to scroll to the very left edge of the image
                            //fire the scale animation
                            background.scrollTo(background.getWidth()/2,0);
                            ObjectAnimator xAnimator=ObjectAnimator.ofFloat(background, View.SCALE_X,4f,background.getScaleX());
                            ObjectAnimator yAnimator=ObjectAnimator.ofFloat(background,View.SCALE_Y,4f,background.getScaleY());
                            AnimatorSet set=new AnimatorSet();
                            set.playTogether(xAnimator,yAnimator);
                            set.setDuration(getResources().getInteger(R.integer.duration));
                            set.start();
                        });
                        pager.post(()->{
                            AuthAdapter adapter = new AuthAdapter(getSupportFragmentManager(), pager, background, sharedElements);
                            pager.setAdapter(adapter);
                        });
                    }
                });
    }

    private int[] screenSize(){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return new int[]{size.x,size.y};
    }

    public void twitterLogin(View view) {

        Toast.makeText(this, "Twitter Login yet to be implemented", Toast.LENGTH_SHORT).show();
    }

    public void linkedinLogin(View view) {

        Toast.makeText(this, "Linkedin Login yet to be implemented", Toast.LENGTH_SHORT).show();
    }

    public void facebookLogin(View view) {

        Toast.makeText(this, "Facebook Login yet to be implemented", Toast.LENGTH_SHORT).show();

    }
}
