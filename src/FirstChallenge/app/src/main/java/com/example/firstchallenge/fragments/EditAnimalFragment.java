package com.example.firstchallenge.fragments;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.firstchallenge.R;
import com.example.firstchallenge.activities.MainActivity;
import com.example.firstchallenge.models.AnimalViewModel;
import com.example.firstchallenge.util.Animal;

public class EditAnimalFragment extends Fragment {

    private AnimalViewModel animalViewModel;
    private FragmentChangeListener fragmentChangeListener;
    private int selectedAnimal;

    private ImageView avatar;

    private EditText owner;
    private EditText name;
    private EditText age;

    private Button saveButton;
    private Button backButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        // Fetch Animal View Model
        this.animalViewModel = new ViewModelProvider(requireActivity()).get(AnimalViewModel.class);

        // Instantiate View for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_animal, container, false);

        // Fetch Context for the Fragment Change listener
        this.fragmentChangeListener = (MainActivity) inflater.getContext();

        // Fetch select Position from Bundled Arguments
        if (getArguments() != null) {
            this.selectedAnimal = getArguments().getInt("selectedAnimal");
        }
        Log.w("IMPORTANT", String.valueOf(this.selectedAnimal));

        // Fetch Image view
        this.avatar = (ImageView) view.findViewById(R.id.image_view);

        // Fetch Edit Text Views
        this.owner = (EditText) view.findViewById(R.id.edit_text_owner);
        this.name = (EditText) view.findViewById(R.id.edit_text_name);
        this.age = (EditText) view.findViewById(R.id.edit_text_age);

        // Fetch Buttons
        this.saveButton = (Button) view.findViewById(R.id.save_button);
        this.backButton = (Button) view.findViewById(R.id.back_button);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        assert animalViewModel.getAnimals() != null;

        // Fetch current animal
        Animal animal = animalViewModel.getAnimals().get(this.selectedAnimal);

        // Display selected animal information
        this.avatar.setImageResource(animal.getAvatar());
        this.owner.setText(animal.getOwner(), TextView.BufferType.EDITABLE);
        this.name.setText(animal.getName(), TextView.BufferType.EDITABLE);
        this.age.setText(String.valueOf(animal.getAge()), TextView.BufferType.EDITABLE);

        this.saveButton.setOnClickListener(v -> {
            // Fetch Updated Information from Edit Text fields
            String newOwner = this.owner.getText().toString();
            String newName = this.name.getText().toString();
            int newAge = Integer.parseInt(this.age.getText().toString());

            // Update the animal information
            animal.setOwner(newOwner);
            animal.setName(newName);
            animal.setAge(newAge);
        });

        this.backButton.setOnClickListener(v -> {
            // Build Information Bundle for sharing state with EditAnimal Fragment
            Bundle bundle = new Bundle();
            bundle.putInt("selectedAnimal", this.selectedAnimal);

            // Load new fragment with bundled information and switch to the other fragment
            DisplayAnimalFragment fragment = new DisplayAnimalFragment();
            fragment.setArguments(bundle);
            fragmentChangeListener.replaceFragment(fragment);
        });
    }
}