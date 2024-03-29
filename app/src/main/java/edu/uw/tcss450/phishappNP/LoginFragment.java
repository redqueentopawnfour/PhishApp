package edu.uw.tcss450.phishappNP;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import edu.uw.tcss450.phishappNP.model.ChatMessageNotification;
import edu.uw.tcss450.phishappNP.model.Credentials;
import me.pushy.sdk.Pushy;


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
       /*     args.putSerializable("Key",
                    new Credentials.Builder(
                            emailEdit.getText().toString(),
                            passwordEdit.getText().toString())
                            .build());*/

            doLogin(new Credentials.Builder(
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

   /*         Credentials credentials = new Credentials.Builder(
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
*/
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        //retrieve the stored credentials from SharedPrefs
        if (prefs.contains(getString(R.string.keys_prefs_email)) &&
                prefs.contains(getString(R.string.keys_prefs_password))) {

            final String email = prefs.getString(getString(R.string.keys_prefs_email), "");
            final String password = prefs.getString(getString(R.string.keys_prefs_password), "");
            //Load the two login EditTexts with the credentials found in SharedPrefs
            EditText emailEdit = getActivity().findViewById(R.id.editText_login_email);
            emailEdit.setText(email);
            EditText passwordEdit = getActivity().findViewById(R.id.editText_login_password);
            passwordEdit.setText(password);

            doLogin(new Credentials.Builder(
                    emailEdit.getText().toString(),
                    passwordEdit.getText().toString())
                    .build());
        }
/*
        if (getArguments() != null) {
            Credentials credentials = (Credentials) getArguments().getSerializable("Key");
            updateContent(credentials);
        }*/
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

   /* *//**
     * Handle errors that may occur during the AsyncTask.
     * @param result the error message provide from the AsyncTask
     *//*
    private void handleErrorsInTask(String result) {
        Log.e("ASYNC_TASK_ERROR",  result);
    }

    *//**
     * Handle the setup of the UI before the HTTP call to the webservice.
     *//*
    private void handleLoginOnPre() {
        getActivity().findViewById(R.id.layout_login_wait).setVisibility(View.VISIBLE);
    }

    *//**
     * Handle onPostExecute of the AsynceTask. The result from our webservice is
     * a JSON formatted String. Parse it for success or failure.
     * @param result the JSON formatted String response from the web service
     *//*
    private void handleLoginOnPost(String result) {
        try {
            JSONObject resultsJSON = new JSONObject(result);
            boolean success =
                    resultsJSON.getBoolean(
                            getString(R.string.keys_json_login_success));

            if (success) {
                saveCredentials(mCredentials);
                LoginFragmentDirections
                        .ActionFragmentLoginToHomeActivity homeActivity =
                        LoginFragmentDirections
                                .actionFragmentLoginToHomeActivity(mCredentials);
                homeActivity.setJwt(
                        resultsJSON.getString(
                                getString(R.string.keys_json_login_jwt)));
                Navigation.findNavController(getView())
                        .navigate(homeActivity);
                getActivity().finish();
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
*/
    private void saveCredentials(final Credentials credentials) {
        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        //Store the credentials in SharedPrefs
        prefs.edit().putString(getString(R.string.keys_prefs_email), credentials.getEmail()).apply();
        prefs.edit().putString(getString(R.string.keys_prefs_password), credentials.getPassword()).apply();
    }

    private void doLogin(Credentials credentials) {
        //build the web service URL
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_login))
                .appendPath(getString(R.string.ep_pushy))
                .build();

      /*  //build the JSONObject
        JSONObject msg = credentials.asJSONObject();*/

        new AttemptLoginTask().execute(uri.toString());

        mCredentials = credentials;

      /*  //instantiate and execute the AsyncTask.
        //Feel free to add a handler for onPreExecution so that a progress bar
        //is displayed or maybe disable buttons.
        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPreExecute(this::handleLoginOnPre)
                .onPostExecute(this::handleLoginOnPost)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();*/
    }

    class AttemptLoginTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            getActivity().findViewById(R.id.layout_login_wait).setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... urls) {
            //get pushy token
            String deviceToken = "";

            try {
                // Assign a unique token to this device
                deviceToken = Pushy.register(getActivity().getApplicationContext());

                //subscribe to a topic (this is a Blocking call)
                Pushy.subscribe("all", getActivity().getApplicationContext());
            }
            catch (Exception exc) {

                cancel(true);
                // Return exc to onCancelled
                return exc.getMessage();
            }

            //feel free to remove later.
            Log.d("LOGIN", "Pushy Token: " + deviceToken);


            //attempt to log in: Send credentials AND pushy token to the web service
            StringBuilder response = new StringBuilder();
            HttpURLConnection urlConnection = null;

            try {
                URL urlObject = new URL(urls[0]);
                urlConnection = (HttpURLConnection) urlObject.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");


                urlConnection.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(urlConnection.getOutputStream());

                JSONObject message = mCredentials.asJSONObject();
                message.put("token", deviceToken);

                wr.write(message.toString());
                wr.flush();
                wr.close();

                InputStream content = urlConnection.getInputStream();
                BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                String s = "";
                while((s = buffer.readLine()) != null) {
                    response.append(s);
                }
                publishProgress();
            } catch (Exception e) {
                response = new StringBuilder("Unable to connect, Reason: "
                        + e.getMessage());
                cancel(true);
            } finally {
                if(urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

            return response.toString();
        }

        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);
            getActivity().findViewById(R.id.layout_login_wait).setVisibility(View.GONE);
            Log.e("LOGIN_ERROR", "Error in Login Async Task: " + s);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {

                Log.d("JSON result",result);
                JSONObject resultsJSON = new JSONObject(result);
                boolean success = resultsJSON.getBoolean("success");


                if (success) {
                    saveCredentials(mCredentials);

                    //Login was successful. Switch to the SuccessFragment.
                    LoginFragmentDirections.ActionFragmentLoginToHomeActivity homeActivity =
                            LoginFragmentDirections
                                    .actionFragmentLoginToHomeActivity(mCredentials);
                    homeActivity.setJwt(resultsJSON.getString(
                            getString(R.string.keys_json_login_jwt)));

                    if (getArguments() != null) {

                        if (getArguments().containsKey("type")) {
                            if (getArguments().getString("type").equals("msg")) {
                                String msg = getArguments().getString("message");
                                String sender = getArguments().getString("sender");

                                ChatMessageNotification chat =
                                        new ChatMessageNotification.Builder(sender, msg).build();
                                homeActivity.setChatMessage(chat);
                            }
                        }
                    }

                    Navigation.findNavController(getView()).navigate(homeActivity);
                    getActivity().finish();
                    return;
                } else {
                    //Saving the token wrong. Don’t switch fragments and inform the user
                    ((TextView) getView().findViewById(R.id.editText_login_email))
                            .setError("Login Unsuccessful");
                }
            } catch (JSONException e) {
                //It appears that the web service didn’t return a JSON formatted String
                //or it didn’t have what we expected in it.
                Log.e("JSON_PARSE_ERROR",  result
                        + System.lineSeparator()
                        + e.getMessage());

                ((TextView) getView().findViewById(R.id.editText_login_email))
                        .setError("Login Unsuccessful");
            }
        }
    }
}
