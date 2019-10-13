package edu.uw.tcss450.phishapp;


import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import edu.uw.tcss450.phishapp.model.Credentials;


public class RegisterFragment extends Fragment {

    public RegisterFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_register, container, false);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.button_register_register)
                .setOnClickListener(this::attemptRegister);
    }

    private void attemptRegister(final View theButton) {
        EditText emailEdit = getActivity().findViewById(R.id.editText_register_email);
        EditText password1Edit = getActivity().findViewById(R.id.editText_register_password);
        EditText password2Edit = getActivity().findViewById(R.id.editText_register_retypePassword);

        if ((validateEmail(emailEdit)) && (validatePasswords(password1Edit, password2Edit))) {
            Bundle args = new Bundle();
            args.putSerializable("Key",
                    new Credentials.Builder(
                            emailEdit.getText().toString(),
                            password1Edit.getText().toString())
                            .build());

            Navigation.findNavController(theButton)
                    .navigate(R.id.action_registerFragment_to_loginFragment, args);
        }
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

    private boolean validatePasswords(EditText password1, EditText password2) {
        boolean isValid = false;

        if ((validatePassword(password1)) && (validatePassword(password2))) {
            if (!password1.getText().toString().equals(password2.getText().toString())) {
                password1.setError("Passwords must match.");
            } else {
                isValid = true;
            }
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
