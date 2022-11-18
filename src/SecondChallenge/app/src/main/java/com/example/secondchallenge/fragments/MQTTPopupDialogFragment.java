package com.example.secondchallenge.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.secondchallenge.R;
import com.example.secondchallenge.models.Note;
import com.example.secondchallenge.models.NotesViewModel;

public class MQTTPopupDialogFragment extends DialogFragment {

    private String origin;
    private String title;
    private String message;

    public MQTTPopupDialogFragment(String origin, String payload) {
        this.origin = origin;
        String[] split = payload.split("\\|");
        this.title = split[0];
        this.message = split[1];
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.mqtt_note_dialog, null);

        NotesViewModel notesViewModel = new ViewModelProvider(requireActivity()).get(NotesViewModel.class);

        TextView topic = (TextView) view.findViewById(R.id.mqtt_new_note_origin);
        EditText title = (EditText) view.findViewById(R.id.mqtt_new_note_title);

        topic.setText("Origin: " + this.origin);
        title.setText(this.title);

        builder.setView(view)
               .setMessage("New Note")
               .setPositiveButton("Accept", (dialog, id) -> {})
               .setNegativeButton("Reject", (dialog, id) -> {
                   // It is what it is...
               });
        AlertDialog dialog = builder.create();
        dialog.show();

        // Customize Alert Dialog Button Behaviour
        Button btn = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        btn.setOnClickListener(v -> {
            String txt = title.getText().toString();
            if (txt.isEmpty()) {
                Toast toast = Toast.makeText(this.getContext(), "Title cannot be empty!",
                    Toast.LENGTH_SHORT
                );
                toast.show();
            } else if (notesViewModel.findNoteByTitle(txt) != null) {
                Toast toast = Toast.makeText(this.getContext(), "Note with this name already " +
                        "exists!",
                    Toast.LENGTH_SHORT
                );
                toast.show();
            } else {
                Note note = new Note(title.getText().toString(), this.message, this.origin);
                notesViewModel.addNotes(note);
                dialog.dismiss();
            }
        });
        return dialog;
    }
}