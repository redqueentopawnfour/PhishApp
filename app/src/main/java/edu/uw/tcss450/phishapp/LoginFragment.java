package edu.uw.tcss450.phishapp;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
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


public class LoginFragment extends Fragment {

    Credentials mCredentials;

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

           /* Navigation.findNavController(theButton)
                    .navigate(R.id.action_fragment_login_to_homeActivity, args);

            LoginFragmentDirections.ActionFragmentLoginToHomeActivity homeActivity =
                    LoginFragmentDirections.actionFragmentLoginToHomeActivity(new Credentials.Builder(
                            emailEdit.getText().toString(),
                            passwordEdit.getText().toString())
                            .build());

            homeActivity.setJwt("Will get a token from the WS later");
            Navigation.findNavController(getView()).navigate(homeActivity);*/

            Credentials credentials = new Credentials.Builder(
                    emailEdit.getText().toString(),
                    passwordEdit.getText().toString())
                    .build();

            //build the web service URL
            Uri uri = new Uri.Builder()
                    .scheme("https")
                    .appendPath(getString(R.string.ep_base_url))
                    .appendPath(getString(R.string.ep_login))
                    .build();

            //build the JSONObject
            JSONObject msg = credentials.asJSONObject();

            mCredentials = credentials;

            //instantiate and execute the AsyncTask.
            new SendPostAsyncTask.Builder(uri.toString(), msg)
                    .onPreExecute(this::handleLoginOnPre)
                    .onPostExecute(this::handleLoginOnPost)
                    .onCancelled(this::handleErrorsInTask)
                    .build().execute();

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
    private void handleLoginOnPre() {
        getActivity().findViewById(R.id.layout_login_wait).setVisibility(View.VISIBLE);
    }

    /**
     * Handle onPostExecute of the AsynceTask. The result from our webservice is
     * a JSON formatted String. Parse it for success or failure.
     * @param result the JSON formatted String response from the web service
     */
    private void handleLoginOnPost(String result) {
        try {
            JSONObject resultsJSON = new JSONObject(result);
            boolean success =
                    resultsJSON.getBoolean(
                            getString(R.string.keys_json_login_success));

            if (success) {
                LoginFragmentDirections
                        .ActionFragmentLoginToHomeActivity homeActivity =
                        LoginFragmentDirections
                                .actionFragmentLoginToHomeActivity(mCredentials);
                homeActivity.setJwt(
                        resultsJSON.getString(
                                getString(R.string.keys_json_login_jwt)));
                Navigation.findNavController(getView())
                        .navigate(homeActivity);
                return;
            } else {
                //Login was unsuccessful. Don’t switch fragments and
                // inform the user
                ((TextView) getView().findViewById(R.id.editText_login_email))
                        .setError("Login Unsuccessful");
            }
            getActivity().findViewById(R.id.layout_login_wait)
                    .setVisibility(View.GONE);
        } catch (JSONException e) {
            //It appears that the web service did not return a JSON formatted
            //String or it did not have what we expected in it.
            Log.e("JSON_PARSE_ERROR",  result
                    + System.lineSeparator()
                    + e.getMessage());
            getActivity().findViewById(R.id.layout_login_wait)
                    .setVisibility(View.GONE);
            ((TextView) getView().findViewById(R.id.editText_login_email))
                    .setError("Login Unsuccessful");
        }
    }

}
