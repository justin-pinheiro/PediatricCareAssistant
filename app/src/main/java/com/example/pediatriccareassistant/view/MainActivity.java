package com.example.pediatriccareassistant.view;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.pediatriccareassistant.controller.AuthenticationController;
import com.example.pediatriccareassistant.controller.MyReceiver;
import com.example.pediatriccareassistant.controller.firebasehandler.EmbeddingsHandler;
import com.example.pediatriccareassistant.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.pediatriccareassistant.databinding.ActivityMainBinding;

import java.util.Calendar;

public class MainActivity extends BaseActivity {

    private BottomNavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if(!AuthenticationController.getInstance().isCurrentUser()){
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            Toast.makeText(this, "user: " + AuthenticationController.getInstance().getUserUid(), Toast.LENGTH_SHORT).show();

            navView = findViewById(R.id.nav_view);;
            setNavigationBarLinks();
            setDefaultFragment(savedInstanceState);
        }
    }

    private void setDefaultFragment(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            navView.setSelectedItemId(R.id.navigation_home);
        }
    }

    private void setNavigationBarLinks() {
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.navigation_home) {
                    setCurrentFragment(new HomeFragment());
                    return true;
                }
                if (item.getItemId() == R.id.navigation_tracker) {
                    setCurrentFragment(new TrackerFragment());
                    return true;
                }
                if (item.getItemId() == R.id.navigation_chatbot) {
                    setCurrentFragment(new ChatbotFragment());
                    return true;
                }
                return false;
            }
        });
    }

    private void setCurrentFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void setChatbotFragment(View view)
    {
        setCurrentFragment(new ChatbotFragment());
        navView.setSelectedItemId(R.id.navigation_chatbot);
    }

    public void setAddMeasurementFragment(View view) {
        setCurrentFragment(new AddMeasurementFragment());
    }

    public void setRemindersFragment(View view) {
        setCurrentFragment(new RemindersFragment());
    }

    public void setAddReminderFragment(View view) {
        setCurrentFragment(new AddReminderFragment());
    }

    public void setArticlesFragment(View view) {
        setCurrentFragment(new ArticlesFragment());
    }

    public void setHospitalsFragment(View view) {
        setCurrentFragment(new HospitalsFragment());
    }

    public void setChildrenFragment(View view) {
        setCurrentFragment(new ChildrenFragment());
    }

    public void setAddChildFragment(View view) {
        setCurrentFragment(new AddChildFragment());
    }

    public void setLastOpenedFragment(View view) {
        getSupportFragmentManager().popBackStackImmediate();
    }

    public void createEmbeddings(View view) {
        EmbeddingsHandler.getInstance().createEmbeddings(view.getContext());
    }

}