package com.example.amit.faasos;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.amit.fasoos.R;
import com.squareup.picasso.Picasso;

/**
 * Created by amit on 7/26/2015.
 */
public class ProductsAdapter extends CursorAdapter{


    // Flag to determine if we want to use a separate view for "today".
    private boolean mUseTodayLayout = true;

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {

        public final TextView nameView;
        public final TextView priceView;
        public final TextView spicemeterView;
        public final ImageView itemImage;


        public ViewHolder(View view) {
            nameView = (TextView) view.findViewById(R.id.list_item_name_textview);
            priceView = (TextView) view.findViewById(R.id.list_item_description_textview);
            spicemeterView = (TextView) view.findViewById(R.id.list_item_spicemeter_textview);
            itemImage = (ImageView)view.findViewById(R.id.item_imageView);

        }
    }

    public ProductsAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Choose the layout type
        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = -1;
        layoutId = R.layout.list_item_forecast;

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        int viewType = getItemViewType(cursor.getPosition());

        Picasso.with(context).load(cursor.getString(ProductsFragment.COL_FAASOS_IMAGE)).into(viewHolder.itemImage);
        String name = cursor.getString(ProductsFragment.COL_FAASOS_NAME);
        // Find TextView and set weather forecast on it
        viewHolder.nameView.setText(name);
        String price = cursor.getString(ProductsFragment.COL_FAASOS_PRICE);
        // Find TextView and set weather forecast on it
        viewHolder.priceView.setText(price);
        viewHolder.spicemeterView.setText("I am "+cursor.getString(ProductsFragment.COL_FAASOS_SPICEMETER)+"/5 spicy");


    }

}
