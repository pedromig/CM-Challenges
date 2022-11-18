package com.example.secondchallenge.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.secondchallenge.R;
import com.example.secondchallenge.adapters.NotesAdapter;
import com.example.secondchallenge.models.Note;
import com.example.secondchallenge.models.NotesViewModel;

import java.util.Objects;

public class NoteDialogFragment extends DialogFragment {

    private final int clickedPosition;

    public NoteDialogFragment(int clickedPosition) {
        this.clickedPosition = clickedPosition;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        ListNotesFragment parent = (ListNotesFragment) getParentFragment();
        NotesAdapter adapter = (NotesAdapter) Objects.requireNonNull(parent)
                                                     .getNotesRecyclerView()
                                                     .getAdapter();

        View view = inflater.inflate(R.layout.note_dialog, null);
        EditText title = view.findViewById(R.id.dialog_note_title);

        NotesViewModel notesViewModel = new ViewModelProvider(requireActivity()).get(NotesViewModel.class);
        Note note = notesViewModel.getNotes().get(this.clickedPosition);

        title.setText(note.getTitle());

        assert adapter != null;
        builder.setView(view).setMessage("Note Title")
               .setPositiveButton(R.string.save, (dialog, id) -> {})
               .setNegativeButton(R.string.delete, (dialog, id) -> {
                   notesViewModel.removeNotes(note);
                   adapter.notifyDataSetChanged();
               });

        AlertDialog dialog = builder.create();
        dialog.show();

        // Customize Alert Dialog Button Behaviour
        Button btn = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        btn.setOnClickListener(v -> {
            String txt = title.getText().toString();
            Note other = notesViewModel.findNoteByTitle(txt);
            if (txt.isEmpty()) {
                Toast toast = Toast.makeText(this.getContext(), "Title cannot be empty!",
                    Toast.LENGTH_SHORT
                );
                toast.show();
            } else if (other != null && other != note) {
                Toast toast = Toast.makeText(this.getContext(), "Note with this name already " +
                        "exists!",
                    Toast.LENGTH_SHORT
                );
                toast.show();
            } else {
                notesViewModel.updateNote(
                    note,
                    title.getText().toString(),
                    note.getBody(),
                    note.getOrigin()
                );
                adapter.notifyItemChanged(this.clickedPosition);
                dialog.dismiss();
            }
        });

        return dialog;
    }
}