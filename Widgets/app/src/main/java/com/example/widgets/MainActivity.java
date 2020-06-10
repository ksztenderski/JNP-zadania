package com.example.widgets;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ArrayList<Fragment> fragments;
    private int currentFragmentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentFragmentId = 0;
        fragments = new ArrayList<>();
        fragments.add(ButtonsFragment.newInstance());
        fragments.add(WidgetsFragment.newInstance());
        closeFragment();
        displayFragment();

        findViewById(R.id.previous_button).setOnClickListener(view -> {
            if (currentFragmentId > 0) {
                closeFragment();
                --currentFragmentId;
                displayFragment();
            }
        });

        findViewById(R.id.next_button).setOnClickListener(view -> {
            if (currentFragmentId < fragments.size() - 1) {
                closeFragment();
                ++currentFragmentId;
                displayFragment();
            }
        });
    }

    public void displayFragment() {
        // Get the FragmentManager and start a transaction.
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Add the ButtonsFragment.
        fragmentTransaction.add(R.id.fragment_container, fragments.get(currentFragmentId)).addToBackStack(null).commit();
    }

    public void closeFragment() {
        // Get the FragmentManager.
        FragmentManager fragmentManager = getSupportFragmentManager();
        // Check to see if the fragment is already showing.
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);
        if (fragment != null) {
            // Create and commit the transaction to remove the fragment.
            FragmentTransaction fragmentTransaction =
                    fragmentManager.beginTransaction();
            fragmentTransaction.remove(fragment).commit();
        }
    }
}
