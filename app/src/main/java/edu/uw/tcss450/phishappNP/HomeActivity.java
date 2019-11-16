package edu.uw.tcss450.phishappNP;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;

import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.uw.tcss450.phishappNP.model.BlogPost;
import edu.uw.tcss450.phishappNP.model.ChatMessageNotification;
import edu.uw.tcss450.phishappNP.model.Credentials;
import edu.uw.tcss450.phishappNP.model.SetListPost;
import edu.uw.tcss450.phishappNP.utils.GetAsyncTask;
import edu.uw.tcss450.phishappNP.utils.PushReceiver;
import me.pushy.sdk.Pushy;

public class HomeActivity extends AppCompatActivity {

    private String mJwToken;
    private Credentials mCredentials;
    private AppBarConfiguration mAppBarConfiguration;
    private ColorFilter mDefault;
    private HomePushMessageReceiver mPushMessageReciever;
    private ChatMessageNotification mChatMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        /*FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_successFragment, R.id.nav_blogFragment, R.id.nav_setListFragment, R.id.nav_chatFragment)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        navController.setGraph(R.navigation.mobile_navigation, getIntent().getExtras());

        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        HomeActivityArgs args = HomeActivityArgs.fromBundle(getIntent().getExtras());
        mJwToken = args.getJwt();
        mCredentials = args.getCredentials();
        mChatMessage = args.getChatMessage();

        /*navigationView.setNavigationItemSelectedListener(this::onNavigationSelected);*/
        if (args.getChatMessage() != null) {
            MobileNavigationDirections.ActionGlobalNavChatFragment directions =
                    ChatFragmentDirections.actionGlobalNavChatFragment().setEmail(mCredentials.getEmail()).setJwt(mJwToken).setMessage(args.getChatMessage());

            directions.setMessage(args.getChatMessage());
            navController.navigate(directions);
        } else {
            navigationView.setNavigationItemSelectedListener(this::onNavigationSelected);
        }

        mDefault = toolbar.getNavigationIcon().getColorFilter();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private boolean onNavigationSelected(final MenuItem menuItem) {
        NavController navController =
                Navigation.findNavController(this, R.id.nav_host_fragment);
        switch (menuItem.getItemId()) {
            case R.id.nav_successFragment:
                navController.navigate(R.id.nav_successFragment, getIntent().getExtras());
                break;
            case R.id.nav_blogFragment:
                Uri uri_blogPosts = new Uri.Builder()
                        .scheme("https")
                        .appendPath(getString(R.string.ep_base_url))
                        .appendPath(getString(R.string.ep_phish))
                        .appendPath(getString(R.string.ep_blog))
                        .appendPath(getString(R.string.ep_get))
                        .build();

                new GetAsyncTask.Builder(uri_blogPosts.toString())
                        .onPostExecute(this::handleBlogGetOnPostExecute)
                        .addHeaderField("authorization", mJwToken) //add the JWT as a header
                        .build().execute();
                break;
            case R.id.nav_setListFragment:
                Uri uri_setLists = new Uri.Builder()
                        .scheme("https")
                        .appendPath(getString(R.string.ep_base_url))
                        .appendPath(getString(R.string.ep_phish))
                        .appendPath(getString(R.string.ep_setlists))
                        .appendPath(getString(R.string.ep_recent))
                        .build();

                new GetAsyncTask.Builder(uri_setLists.toString())
                        .onPostExecute(this::handleSetListGetOnPostExecute)
                        .addHeaderField("authorization", mJwToken) //add the JWT as a header
                        .build().execute();
                break;
            case R.id.nav_chatFragment:
                // We've clicked on chat, reset the hamburger icon color
                ((Toolbar) findViewById(R.id.toolbar)).getNavigationIcon().setColorFilter(mDefault);

                MobileNavigationDirections.ActionGlobalNavChatFragment directions;
                if (mChatMessage != null) {
                    Log.d("tag", mChatMessage.getMessage());
                    directions =
                            ChatFragmentDirections.actionGlobalNavChatFragment().setEmail(mCredentials.getEmail()).setJwt(mJwToken).setMessage(mChatMessage);
                } else {
                    directions =
                            ChatFragmentDirections.actionGlobalNavChatFragment().setEmail(mCredentials.getEmail()).setJwt(mJwToken);
                }


              Navigation.findNavController(this, R.id.nav_host_fragment).navigate(directions);
                break;
        }
        //Close the drawer
        ((DrawerLayout) findViewById(R.id.drawer_layout)).closeDrawers();
        return true;
    }

    private void handleBlogGetOnPostExecute(final String result) {
        //parse JSON

        try {
            JSONObject root = new JSONObject(result);
            if (root.has(getString(R.string.keys_json_blogs_response))) {
                JSONObject response = root.getJSONObject(
                        getString(R.string.keys_json_blogs_response));
                if (response.has(getString(R.string.keys_json_blogs_data))) {
                    JSONArray data = response.getJSONArray(
                            getString(R.string.keys_json_blogs_data));

                    BlogPost[] blogs = new BlogPost[data.length()];

                    for (int i = 0; i < data.length(); i++) {
                        JSONObject jsonBlog = data.getJSONObject(i);

                        blogs[i] = new BlogPost.Builder(
                                jsonBlog.getString(
                                        getString(R.string.keys_json_blogs_pubdate)),
                                jsonBlog.getString(
                                        getString(R.string.keys_json_blogs_title)))
                                .addTeaser(jsonBlog.getString(
                                        getString(R.string.keys_json_blogs_teaser)))
                                .addUrl(jsonBlog.getString(
                                        getString(R.string.keys_json_blogs_url)))
                                .build();
                    }

                    MobileNavigationDirections.ActionGlobalNavBlogFragment directions
                            = BlogFragmentDirections.actionGlobalNavBlogFragment(blogs);

                    Navigation.findNavController(this, R.id.nav_host_fragment)
                            .navigate(directions);
                } else {
                    Log.e("ERROR!", "No data array");
                }
            } else {
                Log.e("ERROR!", "No response");
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("ERROR!", e.getMessage());
        }
    }

    private void logout() {
        new DeleteTokenAsyncTask().execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            logout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void handleSetListGetOnPostExecute(final String result) {
        //parse JSON

        try {
            JSONObject root = new JSONObject(result);
            if (root.has(getString(R.string.keys_json_setlists_response))) {
                JSONObject response = root.getJSONObject(
                        getString(R.string.keys_json_setlists_response));
                if (response.has(getString(R.string.keys_json_setlists_data))) {
                    JSONArray data = response.getJSONArray(
                            getString(R.string.keys_json_setlists_data));

                    SetListPost[] setlist = new SetListPost[data.length()];

                    for (int i = 0; i < data.length(); i++) {
                        JSONObject jsonSetList = data.getJSONObject(i);

                        setlist[i] = new SetListPost.Builder(
                                jsonSetList.getString(
                                        getString(R.string.keys_json_setlists_long_date)),
                                jsonSetList.getString(
                                        getString(R.string.keys_json_setlists_location)),
                                jsonSetList.getString(
                                        getString(R.string.keys_json_setlists_venue)))
                                .addSetListData(jsonSetList.getString(
                                        getString(R.string.keys_json_setlists_setlistdata)))
                                .addSetListNotes(jsonSetList.getString(
                                        getString(R.string.keys_json_setlists_setlistnotes)))
                                .addUrl(jsonSetList.getString(
                                        getString(R.string.keys_json_setlists_url)))
                                .build();
                    }

                    MobileNavigationDirections.ActionGlobalNavSetListFragment directions
                            = SetListFragmentDirections.actionGlobalNavSetListFragment(setlist);

                    Navigation.findNavController(this, R.id.nav_host_fragment)
                            .navigate(directions);
                } else {
                    Log.e("ERROR!", "No data array");
                }
            } else {
                Log.e("ERROR!", "No response");
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("ERROR!", e.getMessage());
        }
    }

    // Deleting the Pushy device token must be done asynchronously. Good thing
    // we have something that allows us to do that.
    class DeleteTokenAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            //since we are already doing stuff in the background, go ahead
            //and remove the credentials from shared prefs here.
            SharedPreferences prefs =
                    getSharedPreferences(
                            getString(R.string.keys_shared_prefs),
                            Context.MODE_PRIVATE);

            prefs.edit().remove(getString(R.string.keys_prefs_password)).apply();
            prefs.edit().remove(getString(R.string.keys_prefs_email)).apply();

            //unregister the device from the Pushy servers
            Pushy.unregister(HomeActivity.this);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //close the app
            finishAndRemoveTask();

            //or close this activity and bring back the Login
//            Intent i = new Intent(this, MainActivity.class);
//            startActivity(i);
//            //Ends this Activity and removes it from the Activity back stack.
//            finish();
        }
    }

    /**
     * A BroadcastReceiver that listens for messages sent from PushReceiver
     */
    private class HomePushMessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            NavController nc =
                    Navigation.findNavController(HomeActivity.this, R.id.nav_host_fragment);
            NavDestination nd = nc.getCurrentDestination();
            if (nd.getId() != R.id.nav_chatFragment) {

                if (intent.hasExtra("SENDER") && intent.hasExtra("MESSAGE")) {
                    String sender = intent.getStringExtra("SENDER");
                    String messageText = intent.getStringExtra("MESSAGE");

                    //change the hamburger icon to red alerting the user of the notification
                    ((Toolbar) findViewById(R.id.toolbar)).getNavigationIcon()
                            .setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);

                    Log.d("HOME", sender + ": " + messageText);
                    mChatMessage =
                            new ChatMessageNotification.Builder(sender, messageText).build();
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPushMessageReciever == null) {
            mPushMessageReciever = new HomePushMessageReceiver();
        }
        IntentFilter iFilter = new IntentFilter(PushReceiver.RECEIVED_NEW_MESSAGE);
        registerReceiver(mPushMessageReciever, iFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mPushMessageReciever != null){
            unregisterReceiver(mPushMessageReciever);
        }
    }
}
