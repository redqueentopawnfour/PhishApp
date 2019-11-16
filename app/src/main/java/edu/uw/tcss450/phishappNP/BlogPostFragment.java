package edu.uw.tcss450.phishappNP;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import edu.uw.tcss450.phishappNP.model.BlogPost;


/**
 * A simple {@link Fragment} subclass.
 */
public class BlogPostFragment extends Fragment {

    private String url = null;

    public BlogPostFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View v =  inflater.inflate(R.layout.fragment_blog_post, container, false);
       return v;
    }


    @Override
    public void onStart() {
        super.onStart();

        if (getArguments() != null) {
            BlogPost blogPost = (BlogPost) getArguments().getSerializable(getString(R.string.key_blogPost_view));
            updateContent(blogPost);
            url = blogPost.getUrl();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button buttonFullPost = getActivity().findViewById(R.id.button_blogPost_fullPost);
        buttonFullPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openURL();
            }
        });
    }

    public void updateContent(BlogPost blogPost) {
        TextView titleView = getActivity().findViewById(R.id.textView_blogPost_title);
        TextView publishDateView = getActivity().findViewById(R.id.textView_blogPost_publishDate);
        TextView fullTeaserView = getActivity().findViewById(R.id.textView_blogPost_fullTeaser);

        titleView.setText(blogPost.getTitle());
        publishDateView.setText(blogPost.getPubDate());
        fullTeaserView.setText(HtmlCompat.fromHtml(blogPost.getTeaser(), HtmlCompat.FROM_HTML_MODE_LEGACY));
    }

    public void openURL() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }
}
