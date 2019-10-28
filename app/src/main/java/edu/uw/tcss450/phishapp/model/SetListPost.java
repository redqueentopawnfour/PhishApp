package edu.uw.tcss450.phishapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Class to encapsulate a Phish.net Set List. Building an Object requires a Long Date, Location, and Venue
 *
 * @author Nadia Polk
 * @version 26 October 2019
 */
public class SetListPost implements Serializable, Parcelable {

    private final String mLongDate;
    private final String mLocation;
    private final String mVenue;
    private final String mUrl;
    private final String mSetListData;
    private final String mSetListNotes;

    protected SetListPost(Parcel in) {
        mLongDate = in.readString();
        mLocation = in.readString();
        mVenue = in.readString();
        mUrl = in.readString();
        mSetListData = in.readString();
        mSetListNotes = in.readString();
    }

    public static final Creator<SetListPost> CREATOR = new Creator<SetListPost>() {
        @Override
        public SetListPost createFromParcel(Parcel in) {
            return new SetListPost(in);
        }

        @Override
        public SetListPost[] newArray(int size) {
            return new SetListPost[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mLongDate);
        dest.writeString(mLocation);
        dest.writeString(mVenue);
        dest.writeString(mUrl);
        dest.writeString(mSetListData);
        dest.writeString(mSetListNotes);
    }

    /**
     * Helper class for building Credentials.
     *
     * @author Charles Bryan
     */
    public static class Builder {
        private final String mLongDate;
        private final String mLocation;
        private final String mVenue;
        private  String mUrl = "";
        private  String mSetListData = "";
        private  String mSetListNotes = "";

        /**
         * Constructs a new Builder.
         *
         * @param longDate the date of the set
         * @param location the location of the set
         * @param venue the venue of the set
         */
        public Builder(String longDate, String location, String venue) {
            this.mLongDate = longDate;
            this.mLocation = location;
            this.mVenue = venue;
        }

        /**
         * Add an optional url for the full set post.
         * @param val an optional url for the full set post
         * @return the Builder of this SetListPost
         */
        public Builder addUrl(final String val) {
            mUrl = val;
            return this;
        }

        /**
         * Add an optional full set list data for the full set post.
         * @param val an optional full set data for the full set post.
         * @return the Builder of this SetListPost
         */
        public Builder addSetListData(final String val) {
            mSetListData = val;
            return this;
        }

        /**
         * Add an optional full set list notes for the full set post.
         * @param val an optional full set notes for the full set post.
         * @return the Builder of this SetListPost
         */
        public Builder addSetListNotes(final String val) {
            mSetListNotes = val;
            return this;
        }

        public SetListPost build() {
            return new SetListPost(this);
        }
    }

    private SetListPost(final Builder builder) {
        this.mLongDate = builder.mLongDate;
        this.mLocation = builder.mLocation;
        this.mVenue = builder.mVenue;
        this.mUrl = builder.mUrl;
        this.mSetListData = builder.mSetListData;
        this.mSetListNotes = builder.mSetListNotes;
    }

    public String getLongDate() {
        return mLongDate;
    }

    public String getLocation() {
        return mLocation;
    }

    public String getVenue() {
        return mVenue;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getSetListData() {
        return mSetListData;
    }

    public String getSetListNotes() {
        return mSetListNotes;
    }
}
