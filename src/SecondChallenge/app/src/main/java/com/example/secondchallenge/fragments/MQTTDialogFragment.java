package com.example.secondchallenge.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.secondchallenge.R;

public class MQTTDialogFragment extends DialogFragment {

    public MQTTDialogFragment() {}

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.mqtt_dialog, null);
        EditText topic = view.findViewById(R.id.mqtt_topic);

        builder.setView(view)
               .setPositiveButton("Subscribe", (dialog, id) -> {
               })
               .setNegativeButton("Unsubscribe", (dialog, id) -> {
               });
        return builder.create();
    }
}
