package com.nehvin.loginscreen;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class YourAppHere extends AppCompatActivity {

    private Toolbar mTopToolbar;
    private FirebaseAuth mAuth;

    private static final String TAG = YourAppHere.class.getSimpleName();

    TextView welcome;
    TextView mEmailView;
    TextView mInfoView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_app_here);

        mAuth = FirebaseAuth.getInstance();
        welcome = findViewById(R.id.welcomeView);
        mEmailView = findViewById(R.id.emailView);
        mInfoView = findViewById(R.id.infoView);

        mTopToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(mTopToolbar);

        setTitle(R.string.your_app);

        Intent i = getIntent();
        String str = i.getStringExtra("username");


        if (Build.VERSION.SDK_INT > 26) {
            mEmailView.setText(str);
        } else {
            mEmailView.setTextSize(30);
            mEmailView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            mEmailView.setText(str);
        }

        mInfoView.setText("Min SDK 19 - KitKat 4.4\n" +
                "Constraint Layout\n" +
                "FragmentStatePagerAdapter\n" +
                "ViewPager with Animations\n" +
                "Screen sizes 4.7in and above\n" +
                "Requires no special permissions");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.logout && mAuth!= null) {

            mAuth.signOut();
            welcome.setText("Goodbye");
            Toast.makeText(YourAppHere.this, "Logout Clicked", Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}