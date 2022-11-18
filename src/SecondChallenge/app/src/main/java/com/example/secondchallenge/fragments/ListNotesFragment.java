package com.example.secondchallenge.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.example.secondchallenge.R;
import com.example.secondchallenge.activities.MainActivity;
import com.example.secondchallenge.adapters.NotesAdapter;
import com.example.secondchallenge.listeners.FragmentChangeListener;
import com.example.secondchallenge.listeners.NoteFilterListener;
import com.example.secondchallenge.models.Note;
import com.example.secondchallenge.models.NotesViewModel;

import java.util.Objects;

public class ListNotesFragment extends Fragment {
    private FragmentChangeListener fragmentChangeListener;
    private NotesViewModel notesViewModel;

    private RecyclerView notesRecyclerView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Instantiate View for this fragment
        View view = inflater.inflate(R.layout.fragment_list_notes, container, false);

        // Fetch Context for the Fragment Change listener
        this.fragmentChangeListener = (MainActivity) inflater.getContext();

        // Setup Action Bar
        setHasOptionsMenu(true);

        // Fetch Notes
        this.notesViewModel =
            new ViewModelProvider(requireActivity()).get(NotesViewModel.class);
        NotesAdapter notesAdapter = new NotesAdapter(
            getChildFragmentManager(),
            this.fragmentChangeListener,
            this.notesViewModel.getNotes()
        );

        // Recycler View Layout Manager
        LinearLayoutManager notesLayoutManager = new LinearLayoutManager(getContext(),
            LinearLayoutManager.VERTICAL,
            false
        );

        // Setup Recycler View
        this.notesRecyclerView = view.findViewById(R.id.note_list);
        this.notesRecyclerView.setLayoutManager(notesLayoutManager);
        this.notesRecyclerView.setAdapter(notesAdapter);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.list_notes_fragment_actions, menu);

        // Setup Filter Button Properties
        MenuItem search = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) search.getActionView();
        EditText searchText =
            (EditText) searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchText.setHint("Search Note");
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        // Setup Filter Button Listener
        searchView.setOnQueryTextListener(
            new NoteFilterListener((NotesAdapter) this.notesRecyclerView.getAdapter()));
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_new_note) {
            // Build Information Bundle for sharing state with Edit Notes Fragment
            Bundle bundle = new Bundle();
            bundle.putInt("selectedNote", Note.NEW_NOTE);

            // Load new fragment with bundled information and switch to the other fragment
            EditNoteFragment fragment = new EditNoteFragment();
            fragment.setArguments(bundle);
            this.fragmentChangeListener.replaceFragment(fragment);
        } else if (item.getItemId() == R.id.action_subscribe) {
            MQTTDialogFragment fragment = new MQTTDialogFragment();
            fragment.show(getChildFragmentManager(), "NoteDialog");
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public RecyclerView getNotesRecyclerView() {
        return notesRecyclerView;
    }
}