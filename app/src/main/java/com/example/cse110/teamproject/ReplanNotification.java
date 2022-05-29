package com.example.cse110.teamproject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class ReplanNotification extends DialogFragment {

    boolean userReaction;

    ReplanNotification() { userReaction = false; }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setMessage("Off Track. Replan?")
                .setPositiveButton("Yes", (dialogInterface, i) -> {
                    userReaction = true;
                })
                .setNegativeButton("No", (dialogInterface, i) -> dismiss());
        return builder.create();
    }

    public boolean getUserReaction() { return userReaction; }
}
