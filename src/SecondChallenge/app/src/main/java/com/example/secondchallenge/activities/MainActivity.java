package com.example.secondchallenge.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.Menu;

import com.example.secondchallenge.R;
import com.example.secondchallenge.fragments.MQTTPopupDialogFragment;
import com.example.secondchallenge.listeners.FragmentChangeListener;
import com.example.secondchallenge.fragments.ListNotesFragment;
import com.example.secondchallenge.models.Note;
import com.example.secondchallenge.models.NotesViewModel;
import com.example.secondchallenge.util.AsyncNoteLoader;
import com.example.secondchallenge.util.MQTT;
import com.example.secondchallenge.util.NotesDatabase;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements FragmentChangeListener,
    AsyncNoteLoader.Callback {

    private MQTT mqttService;
    private NotesViewModel notesViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Open Database Connection
        NotesDatabase database = new NotesDatabase(this);

        // Init Shared View Model With Data
        this.notesViewModel = new ViewModelProvider(this).get(NotesViewModel.class);
        notesViewModel.setDatabase(database);

        // Cenas
        AsyncNoteLoader loader = new AsyncNoteLoader(database);
        loader.executeAsync(this);

        // Start Mqtt Service
        this.mqttService = setupMqttService();
    }

    @Override
    public void onComplete(ArrayList<Note> notes) {
        // Fill ViewModel with information
        this.notesViewModel.setNotes(notes);

        setContentView(R.layout.activity_main);

        // Set up Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Instantiate ListNotes Fragment
        replaceFragment(new ListNotesFragment());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        ActionBar ab = getSupportActionBar();
        Objects.requireNonNull(ab).setTitle("Notes");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment, fragment.toString());
        fragmentTransaction.addToBackStack(fragment.toString());
        fragmentTransaction.commit();
    }

    private MQTT setupMqttService() {
        MQTT mqttService = new MQTT(getApplicationContext(), "notes-example-app", "lol");
        mqttService.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                System.out.println(reconnect);
                System.out.println(serverURI);
                System.out.println("CONNECTION COMPLETE");
            }

            @Override
            public void connectionLost(Throwable cause) {
                System.out.println("CONNECTION LOST");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                String msg = message.toString();
                MQTTPopupDialogFragment fragment = new MQTTPopupDialogFragment(topic, msg);
                fragment.show(getSupportFragmentManager(), "MqttPopupDialog");
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                System.out.println("DELIVERED");
            }
        });
        mqttService.connect();
        return mqttService;
    }


    public MQTT getMqttService() {
        return this.mqttService;
    }
}