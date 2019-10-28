package edu.uw.tcss450.phishapp;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;

import edu.uw.tcss450.phishapp.model.SetListPost;


/**
 * A simple {@link Fragment} subclass.
 */
public class SetListPostFragment extends Fragment {

    private String url = null;

    public SetListPostFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View v =  inflater.inflate(R.layout.fragment_set_list_post, container, false);
       return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (getArguments() != null) {
            SetListPost setListPost = (SetListPost) getArguments().getSerializable(getString(R.string.key_setList_view));
            updateContent(setListPost);
            url = setListPost.getUrl();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button buttonFullPost = getActivity().findViewById(R.id.button_setListPost_fullSetList);
        buttonFullPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openURL();
            }
        });
    }

    public void updateContent(SetListPost setListPost) {
        TextView longDateView = getActivity().findViewById(R.id.textView_setListPost_longDate);
        TextView locationView = getActivity().findViewById(R.id.textView_setListPost_location);
        TextView venueView = getActivity().findViewById(R.id.textView_setListPost_venue);
        TextView setListDataView = getActivity().findViewById(R.id.textView_setListPost_setListData);
        TextView setListNotesView  = getActivity().findViewById(R.id.textView_setListPost_setListNotes);

        longDateView.setText(setListPost.getLongDate());
        locationView.setText(setListPost.getLocation());
        venueView.setText(HtmlCompat.fromHtml(setListPost.getVenue(), HtmlCompat.FROM_HTML_MODE_LEGACY));
        setListDataView.setText(HtmlCompat.fromHtml(setListPost.getSetListData(), HtmlCompat.FROM_HTML_MODE_LEGACY));
        setListNotesView.setText(HtmlCompat.fromHtml(setListPost.getSetListNotes(), HtmlCompat.FROM_HTML_MODE_LEGACY));
    }

    public void openURL() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }
}
