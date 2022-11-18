package com.example.secondchallenge.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.secondchallenge.R;
import com.example.secondchallenge.models.NotesViewModel;
import com.example.secondchallenge.util.MQTT;

public class MQTTDialogFragment extends DialogFragment {
    private final MQTT service;

    public MQTTDialogFragment(MQTT service) {
        this.service = service;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.mqtt_subscribe_dialog, null);
        EditText topic = view.findViewById(R.id.mqtt_topic);

        builder.setView(view)
               .setMessage("Topic")
               .setPositiveButton("Subscribe",
                   (dialog, id) -> this.service.subscribeToTopic(topic.getText().toString())
               )
               .setNegativeButton("Unsubscribe",
                   (dialog, id) -> this.service.unsubscribeTopic(topic.getText()
                                                                      .toString())
               );
        return builder.create();
    }
}
