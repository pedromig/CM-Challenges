package com.example.secondchallenge.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.secondchallenge.R;
import com.example.secondchallenge.models.Note;
import com.example.secondchallenge.util.MQTT;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.StandardCharsets;

public class MQTTPublishDialogFragment extends DialogFragment {

    private final Note note;
    private final MQTT service;

    public MQTTPublishDialogFragment(MQTT service, Note note) {
        this.note = note;
        this.service = service;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.mqtt_publish_dialog, null);
        EditText topic = view.findViewById(R.id.mqtt_publish_topic);

        builder.setView(view)
               .setMessage("Publish Note")
               .setPositiveButton("Publish", (dialog, id) -> {
                   publishMessage(this.service,
                       this.note.getTitle() + "|" + this.note.getBody(), 2,
                       topic.getText().toString()
                   );
               });
        return builder.create();
    }

    public void publishMessage(MQTT client, String msg, int qos, String topic) {
        byte[] encodedPayload;
        encodedPayload = msg.getBytes(StandardCharsets.UTF_8);
        MqttMessage message = new MqttMessage(encodedPayload);
        message.setQos(qos);
        client.mqttAndroidClient.publish(topic, message);
    }
}
