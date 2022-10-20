package com.example.firstchallenge.fragments;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.firstchallenge.R;
import com.example.firstchallenge.models.AnimalViewModel;

public class EditAnimalFragment extends Fragment {

    private AnimalViewModel mViewModel;

    public static EditAnimalFragment newInstance() {
        return new EditAnimalFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_animal, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(AnimalViewModel.class);
        // TODO: Use the ViewModel
    }

}