package com.example.amit.faasos.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;


/**
* Created by amit on 7/20/2015.
*/
public class FaasosDBContract {

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.example.amit.faasos";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    // For instance, content://com.example.android.sunshine.app/weather/ is a valid path for
    // looking at weather data. content://com.example.android.sunshine.app/givemeroot/ will fail,
    // as the ContentProvider hasn't been given any information on what to do with "givemeroot".
    // At least, let's hope not.  Don't be that dev, reader.  Don't be that dev.
    public static final String PATH_FAASOS = "faasos";



    /* Inner class that defines the table contents of the products table */
    public static final class FaasosEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAASOS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAASOS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAASOS;

        public static final String TABLE_NAME = "faasos";


        public static final String COLUMN_FAASOS_NAME = "name";
        public static final String COLUMN_FAASOS_IMAGE = "image";
        public static final String COLUMN_FAASOS_CATEGORY = "category";
        public static final String COLUMN_FAASOS_SPICEMETER = "spicemeter";
        public static final String COLUMN_FAASOS_DESCRIPTION = "description";
        public static final String COLUMN_FAASOS_PRICE = "price";
        public static final String COLUMN_FAASOS_RATING = "rating";
        public static final String COLUMN_FAASOS_ISVEG = "isveg";
        public static final String COLUMN_FAASOS_LIKED = "liked";


        public static Uri buildFaasosDetail(int faasosID) {
            return CONTENT_URI.buildUpon().appendPath(Integer.toString(faasosID)).build();
        }

        public static String getParcelIDIDFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

    }

}
