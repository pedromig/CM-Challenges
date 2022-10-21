package com.example.firstchallenge.models;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.firstchallenge.util.Animal;

import java.util.ArrayList;
import java.util.List;

public class AnimalViewModel extends ViewModel {
    private final MutableLiveData<ArrayList<Animal>> animals = new MutableLiveData<>();

    public void addAnimals(Animal... animals) {
        this.animals.setValue(new ArrayList<>(List.of(animals)));
    }

    public ArrayList<Animal> getAnimals() {
        return this.animals.getValue();
    }
}