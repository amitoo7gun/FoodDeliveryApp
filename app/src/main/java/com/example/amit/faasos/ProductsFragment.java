package com.example.amit.faasos;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.example.amit.faasos.data.FaasosDBContract;
import com.example.amit.faasos.sync.FaasosSyncAdapter;
import com.example.amit.fasoos.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ProductsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    public static final String LOG_TAG = ProductsFragment.class.getSimpleName();
    private ProductsAdapter mFasoosAdapter;

    private ListView mListView;
    private TextView mApiHitView;
    private TextView mTotal_itemView;
    private Switch VegSwitch;

    public static SharedPreferences sp;
    int item_count=0;

    private int mPosition = ListView.INVALID_POSITION;

    private static final String SELECTED_KEY = "selected_position";
// SQL query Pareameters
    public static String sortOrder=null;
    public static String whereClause=null;
    public static String whereArgs=null;


    private static final int FASOOS_LOADER = 0;
    // For the forecast view we're showing only a small subset of the stored data.
    // Specify the columns we need.
    private static final String[] FASOOS_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.

            FaasosDBContract.FaasosEntry._ID,
            FaasosDBContract.FaasosEntry.COLUMN_FAASOS_NAME,
            FaasosDBContract.FaasosEntry.COLUMN_FAASOS_IMAGE,
            FaasosDBContract.FaasosEntry.COLUMN_FAASOS_PRICE,
            FaasosDBContract.FaasosEntry.COLUMN_FAASOS_SPICEMETER
    };

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    static final int COL_FAASOS_ID = 0;
    static final int COL_FAASOS_IMAGE = 2;
    static final int COL_FAASOS_NAME = 1;
    static final int COL_FAASOS_PRICE = 3;
    static final int COL_FAASOS_SPICEMETER = 4;


    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri dateUri);
    }

    public ProductsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.products_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.menuSortPrice:
                updateProductsOrder(1);
                return true;
            case R.id.menuSortRating:
                updateProductsOrder(-1);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // The ForecastAdapter will take data from a source and
        // use it to populate the ListView it's attached to.
        mFasoosAdapter = new ProductsAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        mListView = (ListView) rootView.findViewById(R.id.listview_products);
        mApiHitView = (TextView) rootView.findViewById(R.id.api_hits_textview);
        mTotal_itemView= (TextView) rootView.findViewById(R.id.total_items_textview);
        mListView.setAdapter(mFasoosAdapter);

        sp=this.getActivity().getSharedPreferences("service_validation", Context.MODE_WORLD_READABLE);
        item_count=sp.getInt("TOTAL_ITEMS", item_count);
        mTotal_itemView.setText("Menu Item: " + "13");

        new getAPIHits().execute("https://faasos.0x10.info/api/faasos?type=json&query=api_hits");

        //Code for handling Veg Switch
        VegSwitch = (Switch)rootView.findViewById(R.id.VegSwitch);

        //set the switch to ON
        VegSwitch.setChecked(false);
        //attach a listener to check for changes in state
        VegSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if (isChecked) {
                    whereClause=FaasosDBContract.FaasosEntry.COLUMN_FAASOS_ISVEG + "= \"yes\"";
                    whereArgs="yes";
                    RestartLoaderFunc();
                    VegSwitch.setText("Only Veg");

                } else {
                    whereClause=null;
                    whereArgs=null;
                    RestartLoaderFunc();
                    VegSwitch.setText("Everything");
                }

            }
        });


        //current state before we display the screen
        if(VegSwitch.isChecked()){
            whereClause=FaasosDBContract.FaasosEntry.COLUMN_FAASOS_ISVEG + "= \"yes\"";
            whereArgs="yes";
            getLoaderManager().restartLoader(FASOOS_LOADER, null, this);
            VegSwitch.setText("Only Veg");
        }
        else {
            whereClause=null;
            whereArgs=null;
            getLoaderManager().restartLoader(FASOOS_LOADER, null, this);
            VegSwitch.setText("Everything");
        }



        // We'll call our MainActivity
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    ((Callback) getActivity())
                            .onItemSelected(FaasosDBContract.FaasosEntry.buildFaasosDetail(
                                    cursor.getInt(COL_FAASOS_ID)
                            ));
                }
                mPosition = position;
            }
        });

        // If there's instance state, mine it for useful information.
        // The end-goal here is that the user never knows that turning their device sideways
        // does crazy lifecycle related things.  It should feel like some stuff stretched out,
        // or magically appeared to take advantage of room, but data or place in the app was never
        // actually *lost*.
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            // The listview probably hasn't even been populated yet.  Actually perform the
            // swapout in onLoadFinished.
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }


        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(FASOOS_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    void RestartLoaderFunc(){
        getLoaderManager().restartLoader(FASOOS_LOADER, null, this);
    }

    // since we read the location when we create the loader, all we need to do is restart things
    void updateProductsOrder(int order ) {
       // updateProducts();
        if(order == 1)
            sortOrder= FaasosDBContract.FaasosEntry.COLUMN_FAASOS_PRICE+ " ASC";
        else if(order == -1)
            sortOrder= FaasosDBContract.FaasosEntry.COLUMN_FAASOS_RATING+ " DESC";

        getLoaderManager().restartLoader(FASOOS_LOADER, null, this);

    }



    private void updateProducts() {
        FaasosSyncAdapter.syncImmediately(getActivity());
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        // When tablets rotate, the currently selected list item needs to be saved.
        // When no item is selected, mPosition will be set to Listview.INVALID_POSITION,
        // so check for that before storing.
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // This is called when a new Loader needs to be created.  This
        // fragment only uses one loader, so we don't care about checking the id.

        // To only show current and future dates, filter the query to return weather only for
        // dates after or including today.

        // Sort order:  Ascending, by date.


    return new CursorLoader(getActivity(),
            FaasosDBContract.FaasosEntry.CONTENT_URI,
            FASOOS_COLUMNS,
            whereClause,
            null,
            sortOrder);


    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mFasoosAdapter.swapCursor(data);
        if (mPosition != ListView.INVALID_POSITION) {
            // If we don't need to restart the loader, and there's a desired position to restore
            // to, do so now.
            mListView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mFasoosAdapter.swapCursor(null);
    }


//Async task to get API Hit Count

    private class getAPIHits extends AsyncTask<String, String, String> {



        protected String doInBackground(String... urls) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String JsonStr = null;
            String BASE_URL = urls[0];
            String apihitcount = null;
            try {

                Uri builtUri = Uri.parse(BASE_URL).buildUpon().build();

                URL url = new URL(builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                //InputStream inputStream = getClass().getResourceAsStream("game_data.json");
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.

                }
                JsonStr = buffer.toString();
                apihitcount = getDataFromJson(JsonStr);
            }  catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            return apihitcount;
        }


        private String getDataFromJson(String JsonStr)
                throws JSONException {


            final String API_NAME = "api_hits";
            String apicount=null;

            try {
                JSONObject faasosJsonMainObject = new JSONObject(JsonStr);
                apicount= faasosJsonMainObject.getString(API_NAME);
            }
            catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return apicount;
        }
        protected void onPostExecute(String result) {
            mApiHitView.setText("API Hits: "+result);
        }
    }

}
