package com.example.thirdchallenge.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.example.thirdchallenge.R;
import com.example.thirdchallenge.fragments.DashboardFragment;
import com.example.thirdchallenge.fragments.FragmentChanger;
import com.example.thirdchallenge.models.DashboardViewModel;
import com.example.thirdchallenge.models.Measure;
import com.example.thirdchallenge.util.MQTT;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import kotlinx.coroutines.channels.Channel;

public class MainActivity extends AppCompatActivity implements FragmentChanger {

    public MainActivity() {
        super(R.layout.activity_main);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Init Shared View Model With Data
        new ViewModelProvider(this).get(DashboardViewModel.class);

        // Instantiate Display Dashboard Fragment
        replaceFragment(new DashboardFragment());
    }


    @Override
    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment, fragment.toString());
        fragmentTransaction.addToBackStack(fragment.toString());
        fragmentTransaction.commit();
    }
}