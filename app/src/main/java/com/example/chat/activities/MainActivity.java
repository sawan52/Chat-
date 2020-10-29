package com.example.chat.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.example.chat.R;
import com.example.chat.adapters.TabAccessAdapter;
import com.example.chat.fragments.ChatFragment;
import com.example.chat.fragments.GroupFragment;
import com.example.chat.fragments.RequestFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private Toolbar mainPageToolbar;
    private TabLayout mainPageTabLayout;
    private ViewPager mainPageViewPager;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainPageToolbar = findViewById(R.id.main_page_toolbar);
        mainPageTabLayout = findViewById(R.id.main_page_tab_layout);
        mainPageViewPager = findViewById(R.id.main_page_view_pager);

        mAuth = FirebaseAuth.getInstance();

        // Set the Toolbar for our App
        setSupportActionBar(mainPageToolbar);
        getSupportActionBar().setTitle(R.string.app_name);

        setUpTabAccessAdapter(mainPageViewPager);
        mainPageTabLayout.setupWithViewPager(mainPageViewPager);

    }

    private void setUpTabAccessAdapter(ViewPager viewPager) {
        // Set the View Pager for all the three Fragments
        TabAccessAdapter tabAccessAdapter = new TabAccessAdapter(getSupportFragmentManager());
        tabAccessAdapter.addFragment(new ChatFragment(), "CHATS");
        tabAccessAdapter.addFragment(new GroupFragment(), "GROUPS");
        tabAccessAdapter.addFragment(new RequestFragment(), "REQUESTS");
        viewPager.setAdapter(tabAccessAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            sendUserToSignInActivity();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.sign_out_button) {
            mAuth.signOut();
            sendUserToSignInActivity();
        }
        return true;
    }

    private void sendUserToSignInActivity() {
        Intent intent = new Intent(MainActivity.this, SignInActivity.class);
        startActivity(intent);
        finish();
    }

}
