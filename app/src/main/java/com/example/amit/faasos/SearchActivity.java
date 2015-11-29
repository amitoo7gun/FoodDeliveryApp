package com.example.amit.faasos;

import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.example.amit.faasos.data.FaasosDBContract;
import com.example.amit.fasoos.R;

/**
 * Created by amit on 7/9/2015.
 */
public class SearchActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor>{


    private final static String SCREEN_LABEL = "Search";
    private SimpleCursorAdapter adapter;
    private int mPosition = ListView.INVALID_POSITION;

    SearchView mSearchView = null;
    String mQuery = "";


    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri dateUri);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);




        String query = getIntent().getStringExtra(SearchManager.QUERY);
        query = query == null ? "" : query;
        mQuery = query;


        if (mSearchView != null) {
            mSearchView.setQuery(query, false);
        }

        overridePendingTransition(0, 0);

        handleIntent();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent();
    }
    private void handleIntent() {


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.search, menu);
        final MenuItem searchItem = menu.findItem(R.id.menu_search);
        if (searchItem != null) {
            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            final SearchView view = (SearchView) searchItem.getActionView();
            mSearchView = view;
            if (view == null) {

            } else {
                view.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
                view.setIconified(false);
                view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String s) {
                        view.clearFocus();

                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String s) {
                        //String query = getIntent().getStringExtra(SearchManager.QUERY);
                          fillData(s);
//                        TextView tv=(TextView)findViewById(R.id.search_query);
//                        tv.setText(s);
                        return true;
                    }
                });
                view.setOnCloseListener(new SearchView.OnCloseListener() {
                    @Override
                    public boolean onClose() {
                        finish();
                        return false;
                    }
                });
            }

            if (!TextUtils.isEmpty(mQuery)) {
                view.setQuery(mQuery, false);
            }
        }
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                FaasosDBContract.FaasosEntry._ID,
                FaasosDBContract.FaasosEntry.COLUMN_FAASOS_NAME,
                FaasosDBContract.FaasosEntry.COLUMN_FAASOS_CATEGORY };
        CursorLoader cursorLoader = new CursorLoader(this,
                FaasosDBContract.FaasosEntry.CONTENT_URI, projection, null, null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // data is not available anymore, delete reference
        adapter.swapCursor(null);
    }

    private void fillData(String q){
        ListView search_result_items=(ListView)findViewById(R.id.productsearch_listView2);
        String[] COLUMNS_TO_BE_BOUND  = new String[] {
                FaasosDBContract.FaasosEntry.COLUMN_FAASOS_NAME,
                FaasosDBContract.FaasosEntry.COLUMN_FAASOS_CATEGORY
        };

        int[] LAYOUT_ITEMS_TO_FILL = new int[] {
                android.R.id.text1,
                android.R.id.text2
        };
        //String query = getIntent().getStringExtra(SearchManager.QUERY);
        Uri search_db_query = FaasosDBContract.FaasosEntry.CONTENT_URI.buildUpon().build();
//        Toast toast = Toast.makeText(this, search_db_query.toString(), Toast.LENGTH_SHORT);
//        toast.show();
        String[] whereArgs = new String[] {
                q
        };
        getLoaderManager().initLoader(0, null, this);
        Cursor cursor=getContentResolver().query(search_db_query, null, "name LIKE '%"+q+"%' or "
                +FaasosDBContract.FaasosEntry.COLUMN_FAASOS_PRICE + "= \""+q+"\"", null, null);
        adapter = new SimpleCursorAdapter(this,
                android.R.layout.two_line_list_item,
                cursor,
                COLUMNS_TO_BE_BOUND,
                LAYOUT_ITEMS_TO_FILL,
                0);
        search_result_items.setAdapter(adapter);

        search_result_items.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
//                if (cursor != null) {
//                    ((Callback)this)
//                            .onItemSelected(FaasosDBContract.FaasosEntry.buildFaasosDetail(
//                                    cursor.getInt(COL_FAASOS_ID)
//                            ));
//                }

                Intent intent = new Intent(SearchActivity.this, DetailActivity.class)
                        .setData(FaasosDBContract.FaasosEntry.buildFaasosDetail(
                                   cursor.getInt(0)));
                startActivity(intent);
                mPosition = position;
            }
        });
    }
}
