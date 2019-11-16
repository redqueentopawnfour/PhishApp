package edu.uw.tcss450.phishappNP;


import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import edu.uw.tcss450.phishappNP.model.Credentials;

public class SuccessFragment extends Fragment {

    public SuccessFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
     /*   if (getArguments() != null) {
            Credentials credentials = (Credentials) getArguments().getSerializable("Key");
            updateContent(credentials);
        }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_success, container, false);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        HomeActivityArgs args = HomeActivityArgs.fromBundle(getArguments());
        Credentials credentials = args.getCredentials();
        ((TextView) getActivity().findViewById(R.id.text_success_email)).
                setText(credentials.getEmail());
        String jwt = args.getJwt();
        Log.d("JWT", jwt);
    }

    public void updateContent(Credentials credentials) {
        TextView emailDisplay = getActivity().findViewById(R.id.text_success_email);

        String email = credentials.getEmail();

        emailDisplay.setText(email);
    }
}
