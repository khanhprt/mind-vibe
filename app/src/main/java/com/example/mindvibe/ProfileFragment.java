package com.example.mindvibe;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Locale;

public class ProfileFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        bindSessionProfile(view);
        setupLogout(view);
        return view;
    }

    private void bindSessionProfile(View view) {
        String email = SessionManager.getEmail(requireContext());
        if (email.isEmpty()) {
            return;
        }

        String displayName = email.contains("@") ? email.substring(0, email.indexOf('@')) : email;
        if (displayName.isEmpty()) {
            displayName = "MindVibe";
        }
        TextView textProfileAvatar = view.findViewById(R.id.textProfileAvatar);
        TextView textProfileName = view.findViewById(R.id.textProfileName);
        TextView textProfileEmail = view.findViewById(R.id.textProfileEmail);

        textProfileName.setText(displayName);
        textProfileEmail.setText(email);
        textProfileAvatar.setText(displayName.substring(0, 1).toUpperCase(Locale.getDefault()));
    }

    private void setupLogout(View view) {
        view.findViewById(R.id.rowLogout).setOnClickListener(v -> {
            SessionManager.clearSession(requireContext());
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}
