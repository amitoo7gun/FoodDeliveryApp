package com.example.amit.faasos;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.amit.fasoos.R;
import com.example.amit.faasos.data.FaasosDBContract;
import com.squareup.picasso.Picasso;

import java.io.InputStream;

/**
 * Created by amit on 7/27/2015.
 */
public class DetailFragment extends android.support.v4.app.Fragment implements LoaderManager.LoaderCallbacks<Cursor>,View.OnClickListener {
    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    static final String DETAIL_URI = "URI";

    private static final String FASOOS_SHARE_HASHTAG = "#fasoosApp";

    private ShareActionProvider mShareActionProvider;
    private String mFasoosShare;
    private Uri mUri;



    private static final int DETAIL_LOADER = 0;

    private static final String[] DETAIL_COLUMNS = {
            FaasosDBContract.FaasosEntry._ID,
            FaasosDBContract.FaasosEntry.COLUMN_FAASOS_NAME,
            FaasosDBContract.FaasosEntry.COLUMN_FAASOS_IMAGE,
            FaasosDBContract.FaasosEntry.COLUMN_FAASOS_CATEGORY,
            FaasosDBContract.FaasosEntry.COLUMN_FAASOS_SPICEMETER,
            FaasosDBContract.FaasosEntry.COLUMN_FAASOS_DESCRIPTION,
            FaasosDBContract.FaasosEntry.COLUMN_FAASOS_RATING,
            FaasosDBContract.FaasosEntry.COLUMN_FAASOS_PRICE,
            FaasosDBContract.FaasosEntry.COLUMN_FAASOS_ISVEG,
            FaasosDBContract.FaasosEntry.COLUMN_FAASOS_LIKED
    };

    // These indices are tied to DETAIL_COLUMNS.  If DETAIL_COLUMNS changes, these
    // must change.
    public static final int COL_PRODUCTS_ID = 0;
    public static final int COLUMN_FAASOS_NAME = 1;
    public static final int COLUMN_FAASOS_IMAGE_URL = 2;
    public static final int COLUMN_FAASOS_CATEGORY = 3;
    public static final int COLUMN_FAASOS_SPICEMETER = 4;
    public static final int COLUMN_FAASOS_DESCRIPTION = 5;
    public static final int COLUMN_FAASOS_RATING = 6;
    public static final int COLUMN_FAASOS_PRICE = 7;
    public static final int COLUMN_FAASOS_ISVEG = 8;
    public static final int COLUMN_FAASOS_LIKED = 9;





    private TextView mNameView;
    private ImageView mImageUrlView;
    private TextView msCategoryView;
    private TextView mSpicemeterView;
    private TextView mDescriptionView;
    private TextView mRatingView;
    private TextView mPriceView;
    private TextView mIsvegView;
//    private ImageView mIsvegView;
    private TextView mLikedView;
    private RatingBar mRatingBarView;


    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void loadmap();
    }
    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DetailFragment.DETAIL_URI);
        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        mImageUrlView = (ImageView) rootView.findViewById(R.id.ItemimageView);
        msCategoryView = (TextView) rootView.findViewById(R.id.detail_itemcategory_textview);
        mNameView = (TextView) rootView.findViewById(R.id.detail_itemname_textview);
        mDescriptionView = (TextView) rootView.findViewById(R.id.detail_itemdescription_textview);
        mSpicemeterView = (TextView) rootView.findViewById(R.id.detail_itemspicemeter_textview);
        mPriceView = (TextView) rootView.findViewById(R.id.detail_itemprice_textview);
//        mIsvegView = (ImageView) rootView.findViewById(R.id.veg_icon);
        mIsvegView = (TextView) rootView.findViewById(R.id.detail_itemisveg_textview);
        mLikedView = (TextView) rootView.findViewById(R.id.detail_itemlike_textview);
        mRatingBarView = (RatingBar) rootView.findViewById(R.id.ratingBar);

        mLikedView.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        //do what you want to do when button is clicked
        switch (v.getId()) {
            case R.id.detail_itemlike_textview:
            {
                ContentValues updateValues= new ContentValues();
                updateValues.put(FaasosDBContract.FaasosEntry.COLUMN_FAASOS_LIKED, "1");
                String whereCl= FaasosDBContract.FaasosEntry.COLUMN_FAASOS_NAME + "= \"" + mNameView.getText()+ "\"";
                getContext().getContentResolver().update(FaasosDBContract.FaasosEntry.CONTENT_URI,updateValues,whereCl,null);
                mLikedView.setText("Liked :)");
                break;
            }


        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.detailfragment, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // If onLoadFinished happens before this, we can go ahead and set the share intent now.
        if (mFasoosShare != null) {
            mShareActionProvider.setShareIntent(createShareParcelIntent());
        }
    }

    private Intent createShareParcelIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mFasoosShare + FASOOS_SHARE_HASHTAG);
        return shareIntent;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }
//
//    void onLocationChanged( String newLocation ) {
//        // replace the uri, since the location has changed
//        Uri uri = mUri;
//        if (null != uri) {
//            long date = WeatherContract.WeatherEntry.getDateFromUri(uri);
//            Uri updatedUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(newLocation, date);
//            mUri = updatedUri;
//            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
//        }
//    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if ( null != mUri ) {
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    DETAIL_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {

      // Read description from cursor and update view
//            new DownloadImageTask(mImageUrlView)
//                    .execute(data.getString(COLUMN_PARCEL_IMAGE_URL));

            Picasso.with(getContext()).load(data.getString(COLUMN_FAASOS_IMAGE_URL)).into(mImageUrlView);


            mNameView.setText(data.getString(COLUMN_FAASOS_NAME));
            msCategoryView.setText(data.getString(COLUMN_FAASOS_CATEGORY));
            mSpicemeterView.setText("I am "+data.getString(COLUMN_FAASOS_SPICEMETER)+"/5 spicy");
            mPriceView.setText("Rs. " + data.getString(COLUMN_FAASOS_PRICE));
            mIsvegView.setText("Veg: "+data.getString(COLUMN_FAASOS_ISVEG));

            if(Integer.parseInt(data.getString(COLUMN_FAASOS_LIKED)) == 1)
            {
                mLikedView.setText("Liked :)");
            }
            else
            {
                mLikedView.setText("Like");
            }

            mDescriptionView.setText(data.getString(COLUMN_FAASOS_DESCRIPTION));
            mRatingBarView.setMax(5);
            mRatingBarView.setIsIndicator(true);
            mRatingBarView.setRating(Float.parseFloat(data.getString(COLUMN_FAASOS_RATING)));

            mPriceView.setText("Price: " + data.getString(COLUMN_FAASOS_PRICE));


            mFasoosShare= String.format("%s - of: %s -pricing", data.getString(COLUMN_FAASOS_NAME), data.getString(COLUMN_FAASOS_DESCRIPTION));
            // If onCreateOptionsMenu has already happened, we need to update the share intent now.
            if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(createShareParcelIntent());
            }
        }

    }

    //method to download image asynchronysly
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
               Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }


}
