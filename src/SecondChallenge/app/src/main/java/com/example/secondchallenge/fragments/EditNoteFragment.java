package com.example.secondchallenge.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.secondchallenge.R;
import com.example.secondchallenge.activities.MainActivity;
import com.example.secondchallenge.listeners.FragmentChangeListener;
import com.example.secondchallenge.models.Note;
import com.example.secondchallenge.models.NotesViewModel;
import com.example.secondchallenge.util.MQTT;

public class EditNoteFragment extends Fragment {
    private int selectedNote;
    private NotesViewModel notesViewModel;
    private FragmentChangeListener fragmentChangeListener;

    private MQTT mqttService;

    private EditText title;
    private EditText body;

    private Note note;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Instantiate View for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_note, container, false);

        // Fetch Notes View Model
        this.notesViewModel = new ViewModelProvider(requireActivity()).get(NotesViewModel.class);

        // Fetch MqttService
        this.mqttService = ((MainActivity) requireActivity()).getMqttService();


        // Fetch Context for the Fragment Change listener
        this.fragmentChangeListener = (MainActivity) inflater.getContext();

        // Setup Action Bar
        setHasOptionsMenu(true);

        // Fetch arguments passed to the fragment
        Bundle bundle = this.getArguments();
        this.selectedNote = bundle != null ? bundle.getInt("selectedNote") :
            this.notesViewModel.getNotes().size();

        // Edit Text fields for the notes
        this.title = (EditText) view.findViewById(R.id.note_title);
        this.body = (EditText) view.findViewById(R.id.note_body);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Populate the fields with the (saved) information if available
        if (this.selectedNote != Note.NEW_NOTE) {
            // Fetch note
            this.note = this.notesViewModel.getNotes().get(this.selectedNote);
            System.out.println(this.selectedNote);

            // Display selected note information
            this.title.setText(this.note.getTitle());
            this.body.setText(this.note.getBody());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Note other = this.notesViewModel.findNoteByTitle(this.title.getText()
                                                                  .toString());
        if (item.getItemId() == R.id.action_save) {
            if (this.title.getText().toString().isEmpty()) {
                Toast toast = Toast.makeText(this.getContext(), "Title cannot be empty!",
                    Toast.LENGTH_SHORT
                );
                toast.show();
                return super.onOptionsItemSelected(item);
            } else if (other != null && other != this.note) {
                Toast toast = Toast.makeText(this.getContext(), "A note with this name " +
                        "already exists!",
                    Toast.LENGTH_SHORT
                );
                toast.show();
                return super.onOptionsItemSelected(item);
            } else if (this.selectedNote == Note.NEW_NOTE) {
                this.notesViewModel.addNotes(new Note(
                        this.title.getText().toString(),
                        this.body.getText().toString()
                    )
                );
            } else {
                notesViewModel.updateNote(note,
                    this.title.getText().toString(),
                    this.body.getText().toString(),
                    note.getOrigin()
                );
            }
        } else if (item.getItemId() == R.id.action_publish) {
            if (this.title.getText().toString().isEmpty()) {
                Toast toast = Toast.makeText(this.getContext(), "Title cannot be empty!",
                    Toast.LENGTH_SHORT
                );
                toast.show();
                return super.onOptionsItemSelected(item);
            }
            if (other == null) {
                Toast toast = Toast.makeText(this.getContext(), "You must save the note first!",
                    Toast.LENGTH_SHORT
                );
                toast.show();
                return super.onOptionsItemSelected(item);
            }
            MQTTPublishDialogFragment fragment = new MQTTPublishDialogFragment(
                this.mqttService,
                note
            );
            fragment.show(getChildFragmentManager(), "MQTTPublishDialog");
            return true;
        } else if (item.getItemId() == R.id.action_exit) {
            ListNotesFragment fragment = new ListNotesFragment();
            this.fragmentChangeListener.replaceFragment(fragment);
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.edit_note_fragment_actions, menu);
    }
}