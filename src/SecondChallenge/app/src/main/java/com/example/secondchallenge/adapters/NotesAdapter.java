package com.example.secondchallenge.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.secondchallenge.R;
import com.example.secondchallenge.listeners.FragmentChangeListener;
import com.example.secondchallenge.listeners.NoteClickListener;
import com.example.secondchallenge.listeners.NoteLongClickListener;
import com.example.secondchallenge.models.Note;

import java.util.ArrayList;
import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> implements Filterable {

    private final FragmentManager fragmentManager;
    private final FragmentChangeListener fragmentChangeListener;

    private final ArrayList<Note> notes;
    private final ArrayList<Note> notesFull;

    private final Filter filter = new Filter() {
        @Override
        protected Filter.FilterResults performFiltering(CharSequence constraint) {
            Filter.FilterResults results = new Filter.FilterResults();
            ArrayList<Note> filtered = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filtered.addAll(notesFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Note item : notesFull) {
                    if (item.getTitle().toLowerCase().contains(filterPattern.toLowerCase())) {
                        filtered.add(item);
                    }
                }
            }
            results.values = filtered;
            return results;
        }

        @Override
        @SuppressLint("NotifyDataSetChanged")
        protected void publishResults(CharSequence constraint, Filter.FilterResults results) {
            notes.clear();
            notes.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public NotesAdapter(FragmentManager fragmentManager, FragmentChangeListener fragmentChangeListener,
                        ArrayList<Note> notes) {
        this.notes = notes;
        this.notesFull = new ArrayList<>(notes);
        this.fragmentManager = fragmentManager;
        this.fragmentChangeListener = fragmentChangeListener;
    }

    @NonNull
    @Override
    public NotesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                                  .inflate(R.layout.note_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesAdapter.ViewHolder holder, int position) {
        Note model = notes.get(position);
        String origin = model.getOrigin();
        holder.getTitle().setText(model.getTitle());
        holder.getOrigin().setText("Origin: " + (origin.equals("self") ? "Phone" : origin));
        holder.getView()
              .setOnClickListener(new NoteClickListener(position, fragmentChangeListener));
        holder.getView()
              .setOnLongClickListener(new NoteLongClickListener(position, fragmentManager));
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final TextView origin;

        public ViewHolder(@NonNull View view) {
            super(view);
            this.title = view.findViewById(R.id.note_title);
            this.origin = view.findViewById(R.id.note_origin);
        }

        public TextView getOrigin() {
            return origin;
        }

        public TextView getTitle() {
            return title;
        }

        public View getView() {
            return this.itemView;
        }
    }
}
