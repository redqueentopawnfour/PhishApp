package edu.uw.tcss450.phishappNP;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.uw.tcss450.phishappNP.SetListFragment.OnListFragmentInteractionListener;
import edu.uw.tcss450.phishappNP.model.SetListPost;

/**
 * {@link RecyclerView.Adapter} that can display a {@link SetListPost} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MySetListRecyclerViewAdapter extends RecyclerView.Adapter<MySetListRecyclerViewAdapter.ViewHolder> {

    private final List<SetListPost> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MySetListRecyclerViewAdapter(List<SetListPost> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_set, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mLongDate.setText(mValues.get(position).getLongDate());
        holder.mLocation.setText(mValues.get(position).getLocation());
        holder.mVenue.setText(HtmlCompat.fromHtml(mValues.get(position).getVenue(), HtmlCompat.FROM_HTML_MODE_LEGACY));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mLongDate;
        public final TextView mLocation;
        public final TextView mVenue;
        public SetListPost mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mLongDate = (TextView) view.findViewById(R.id.longDate);
            mLocation = (TextView) view.findViewById(R.id.location);
            mVenue = (TextView) view.findViewById(R.id.venue);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mLocation.getText() + "'";
        }
    }
}
