package edu.uw.tcss450.phishapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import edu.uw.tcss450.phishapp.model.Credentials;


public class LoginFragment extends Fragment {

    public LoginFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_login, container, false);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.button_login_signIn)
                .setOnClickListener(this::attemptLogin);

        view.findViewById(R.id.button_login_register)
                .setOnClickListener(b -> {
                    NavController nc = Navigation.findNavController(getView());

                    if (nc.getCurrentDestination().getId() != R.id.nav_fragment_login) {
                        nc.navigateUp();
                    }
                    nc.navigate(R.id.action_loginFragment_to_registerFragment);
                });
    }

    private void attemptLogin(final View theButton) {
        EditText emailEdit = getActivity().findViewById(R.id.editText_login_email);
        EditText passwordEdit = getActivity().findViewById(R.id.editText_login_password);

        if (validateEmail(emailEdit) && validatePassword(passwordEdit)) {
            Bundle args = new Bundle();
            args.putSerializable("Key",
                    new Credentials.Builder(
                            emailEdit.getText().toString(),
                            passwordEdit.getText().toString())
                            .build());

            Navigation.findNavController(theButton)
                    .navigate(R.id.action_fragment_login_to_homeActivity, args);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if (getArguments() != null) {
            Credentials credentials = (Credentials) getArguments().getSerializable("Key");
            updateContent(credentials);
        }
    }

    public void updateContent(Credentials credentials) {
        EditText emailEdit = getActivity().findViewById(R.id.editText_login_email);
        EditText passwordEdit = getActivity().findViewById(R.id.editText_login_password);

        String email = credentials.getEmail();
        String password = credentials.getPassword();

        emailEdit.setText(email);
        passwordEdit.setText(password);
    }

    private boolean validateEmail(EditText email) {
       boolean isValid = false;
        if (email.getText().length() == 0) {
            email.setError("Field must not be empty.");
        } else if (email.getText().toString().chars().filter(ch -> ch == '@').count() != 1) {
            email.setError("Field must contain a valid email address");
        } else {
            isValid = true;
        }

        return isValid;
    }

    private boolean validatePassword(EditText password) {
        boolean isValid = false;

        if (password.getText().length() == 0) {
            password.setError("Field must not be empty.");
        } else if (password.getText().length() < 5) {
            password.setError("Password must be longer than 5 characters");
        } else {
            isValid = true;
        }

        return isValid;
    }
}
