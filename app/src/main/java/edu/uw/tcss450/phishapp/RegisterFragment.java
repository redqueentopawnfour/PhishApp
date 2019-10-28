package edu.uw.tcss450.phishapp;


import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import edu.uw.tcss450.phishapp.model.Credentials;
import edu.uw.tcss450.phishapp.utils.SendPostAsyncTask;

public class RegisterFragment extends Fragment {

    Credentials mCredentials;

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

        /* view.findViewById(R.id.button_register_register).setOnClickListener(this::handleRegisterRegisterButton);*/
    }

    private void attemptRegister(final View theButton) {
        EditText emailEdit = getActivity().findViewById(R.id.editText_register_email);
        EditText password1Edit = getActivity().findViewById(R.id.editText_register_password);
        EditText password2Edit = getActivity().findViewById(R.id.editText_register_retypePassword);
        EditText firstName = getActivity().findViewById(R.id.editText_register_firstName);
        EditText lastName = getActivity().findViewById(R.id.editText_register_lastName);
        EditText username = getActivity().findViewById(R.id.editText_register_username);

        if ((validateNames(firstName, lastName, username)) && (validateEmail(emailEdit)) && (validatePasswords(password1Edit, password2Edit))) {
            Credentials.Builder cBuilder = new Credentials.Builder(
                    emailEdit.getText().toString(),
                    password1Edit.getText().toString()
            );

            cBuilder.addFirstName(firstName.getText().toString());
            cBuilder.addLastName(lastName.getText().toString());
            cBuilder.addUsername(username.getText().toString());

            Credentials credentials = cBuilder.build();

            mCredentials = credentials;

            //build the web service URL
            Uri uri = new Uri.Builder()
                    .scheme("https")
                    .appendPath(getString(R.string.ep_base_url))
                    .appendPath(getString(R.string.ep_register))
                    .build();

            //build the JSONObject
            JSONObject jsonCredentials = mCredentials.asJSONObject();

            //instantiate and execute the AsyncTask.
            new SendPostAsyncTask.Builder(uri.toString(), jsonCredentials)
                    .onPreExecute(this::handleRegsiterOnPre)
                    .onPostExecute(this::handleRegisterOnPost)
                    .onCancelled(this::handleErrorsInTask)
                    .build().execute();

           /* Bundle args = new Bundle();
            args.putSerializable("Key",
                    new Credentials.Builder(
                            emailEdit.getText().toString(),
                            password1Edit.getText().toString())
                            .build());
            Navigation.findNavController(theButton)
                    .navigate(R.id.action_registerFragment_to_loginFragment, args);*/
        }
    }

    private boolean validateNames(EditText firstName, EditText lastName, EditText username) {
        boolean isValid = false;

        if (firstName.getText().length() == 0) {
            firstName.setError("First name must not be empty.");
        } else if (lastName.getText().length() == 0) {
            lastName.setError("Last name must not be empty.");
        } else if (username.getText().length() == 0) {
            username.setError("Username must not be empty.");
        } else {
            isValid = true;
        }

        return isValid;
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

    /**
     * Handle errors that may occur during the AsyncTask.
     * @param result the error message provide from the AsyncTask
     */
    private void handleErrorsInTask(String result) {
        Log.e("ASYNC_TASK_ERROR",  result);
    }

    /**
     * Handle the setup of the UI before the HTTP call to the webservice.
     */
    private void handleRegsiterOnPre() {
        getActivity().findViewById(R.id.layout_register_wait).setVisibility(View.VISIBLE);
    }

    /**
     * Handle onPostExecute of the AsynceTask. The result from our webservice is
     * a JSON formatted String. Parse it for success or failure.
     * @param result the JSON formatted String response from the web service
     */
    private void handleRegisterOnPost(String result) {
        try {
            JSONObject resultsJSON = new JSONObject(result);

            boolean success =
                    resultsJSON.getBoolean(
                            getString(R.string.keys_json_register_success));

            if (success) {
                RegisterFragmentDirections
                        .ActionFragmentRegisterToHomeActivity homeActivity =
                        RegisterFragmentDirections
                            .actionFragmentRegisterToHomeActivity(mCredentials);
                homeActivity.setJwt(
                        resultsJSON.getString(
                                getString(R.string.keys_json_register_jwt)));
                Navigation.findNavController(getView())
                        .navigate(homeActivity);
                return;
            } else {
                //Register was unsuccessful. Don’t switch fragments and
                // inform the user
                ((TextView) getView().findViewById(R.id.editText_register_email))
                        .setError("Register Unsuccessful");
            }
            getActivity().findViewById(R.id.layout_register_wait)
                    .setVisibility(View.GONE);
        } catch (JSONException e) {
            //It appears that the web service did not return a JSON formatted
            //String or it did not have what we expected in it.
            Log.e("JSON_PARSE_ERROR",  result
                    + System.lineSeparator()
                    + e.getMessage());
            getActivity().findViewById(R.id.layout_register_wait)
                    .setVisibility(View.GONE);
            ((TextView) getView().findViewById(R.id.editText_register_email))
                    .setError("Register Unsuccessful");
        }
    }
}
