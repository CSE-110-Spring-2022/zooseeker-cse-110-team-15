package com.example.cse110.teamproject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.List;

public class ReplanNotification extends DialogFragment {

    protected boolean userReaction;

    List<ExhibitsDirectionsActivity> observers;

    ReplanNotification() {
        userReaction = false;
        observers = new ArrayList<>();
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setMessage("Off Track. Replan?")
                .setPositiveButton("Yes", (dialogInterface, i) -> {
                    userReaction = true;
                    notifyObservers();
                })
                .setNegativeButton("No", (dialogInterface, i) -> dismiss());
        return builder.create();
    }

    public boolean getUserReaction() { return userReaction; }

    private void notifyObservers() {
        for (ExhibitsDirectionsActivity o : observers) {
            o.updateReplan();
        }
    }

    public void addObserver(ExhibitsDirectionsActivity o) {
        observers.add(o);
    }

}
