package com.example.pediatriccareassistant.view;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.pediatriccareassistant.R;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().addOnBackStackChangedListener(this::updateActionBar);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        updateActionBar();
    }

    private void updateActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.spring_rain)));
        if (actionBar != null) {
            Fragment currentFragment = getCurrentFragment();
            if (currentFragment instanceof MainMenuBaseFragment) {
                actionBar.setDisplayHomeAsUpEnabled(false);
                actionBar.setDisplayShowHomeEnabled(false);
            } else {
                boolean enableBack = getSupportFragmentManager().getBackStackEntryCount() > 0;
                actionBar.setDisplayHomeAsUpEnabled(enableBack);
                actionBar.setDisplayShowHomeEnabled(enableBack);
            }
        }
    }

    private Fragment getCurrentFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.main_fragment_container);
    }
}
