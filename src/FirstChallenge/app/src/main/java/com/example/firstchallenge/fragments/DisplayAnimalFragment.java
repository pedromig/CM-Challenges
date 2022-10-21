package com.example.firstchallenge.fragments;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.ListFragment;
import androidx.lifecycle.ViewModelProvider;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.firstchallenge.R;
import com.example.firstchallenge.activities.MainActivity;
import com.example.firstchallenge.models.AnimalViewModel;

import java.time.Duration;
import java.util.Objects;

public class DisplayAnimalFragment extends Fragment {

    private AnimalViewModel animalViewModel;
    private FragmentChangeListener fragmentChangeListener;
    private int selectedAnimal;

    private ImageView avatar;

    private Spinner spinner;

    private TextView owner;
    private TextView name;
    private TextView age;

    private Button musicButton;
    private Button editButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Fetch Animal View Model
        this.animalViewModel = new ViewModelProvider(requireActivity()).get(AnimalViewModel.class);

        // Instantiate View for this fragment
        View view = inflater.inflate(R.layout.fragment_display_animal, container, false);

        // Fetch Context for the Fragment Change listener
        this.fragmentChangeListener = (MainActivity) inflater.getContext();

        // Fetch select Position from Bundled Arguments
        if (getArguments() != null) {
            this.selectedAnimal = getArguments().getInt("selectedAnimal");
        }
        Log.w("IMPORTANT", String.valueOf(this.selectedAnimal));

        // Fetch Animal Spinner
        this.spinner = (Spinner) view.findViewById(R.id.spinner);

        // Fetch Image view
        this.avatar = (ImageView) view.findViewById(R.id.avatar);

        // Fetch Text Views
        this.owner = (TextView) view.findViewById(R.id.display_owner_text_view);
        this.name = (TextView) view.findViewById(R.id.display_name_text_view);
        this.age = (TextView) view.findViewById(R.id.display_age_text_view);

        // Fetch Buttons
        this.musicButton = (Button) view.findViewById(R.id.music_button);
        this.editButton = (Button) view.findViewById(R.id.edit_button);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set Spinner Listener
        this.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                assert animalViewModel.getAnimals() != null;
                assert position < animalViewModel.getAnimals().size();
                Log.e("IMPORTANT", "FDS" );

                // Fetch & Update Image View
                avatar.setImageResource(animalViewModel.getAnimals().get(position).getAvatar());

                // Fetch & Update Text Views
                String currentOwner = animalViewModel.getAnimals().get(position).getOwner();
                String currentName = animalViewModel.getAnimals().get(position).getName();
                String currentAge = String.valueOf(animalViewModel.getAnimals()
                                                                  .get(position)
                                                                  .getAge());

                name.setText(currentName);
                owner.setText(currentOwner);
                age.setText(currentAge);

                // Save Selected Position
                selectedAnimal = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Set Play Music Button Listener
        this.musicButton.setOnClickListener(v -> {

        });


        // Set Edit Button Listener
        this.editButton.setOnClickListener(v -> {
            // Build Information Bundle for sharing state with EditAnimal Fragment
            Bundle bundle = new Bundle();
            bundle.putInt("selectedAnimal", this.selectedAnimal);

            // Load new fragment with bundled information and switch to the other fragment
            EditAnimalFragment fragment = new EditAnimalFragment();
            fragment.setArguments(bundle);
            fragmentChangeListener.replaceFragment(fragment);
        });
    }
}


