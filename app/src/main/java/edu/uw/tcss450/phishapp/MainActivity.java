package edu.uw.tcss450.phishapp;


import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;

import edu.uw.tcss450.phishapp.model.Credentials;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
