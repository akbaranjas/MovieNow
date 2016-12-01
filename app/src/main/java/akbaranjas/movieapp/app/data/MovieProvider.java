package akbaranjas.movieapp.app.data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import akbaranjas.movieapp.R;

/**
 * Created by akbaranjas on 28/11/16.
 */

public class MovieProvider extends ContentProvider {
    public static final int MOVIE_LIST = 100;
    public static final int MOVIE_DETAIL = 200;
    public static final int MOVIE_VIDEOS = 300;
    public static final int MOVIE_FAV = 400;
    MovieDBHelper movieDBHelper;
    UriMatcher uriMatcher;

    @Override
    public boolean onCreate() {
        String content_authority = getContext().getResources().getString(R.string.content_authority);
        movieDBHelper = new MovieDBHelper(getContext());
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(content_authority , MovieDBHelper.TBL_MOVIE + "/#", MOVIE_LIST);
        uriMatcher.addURI(content_authority, MovieDBHelper.TBL_MOVIE_DETAIL + "/#", MOVIE_DETAIL);
        uriMatcher.addURI(content_authority, MovieDBHelper.TBL_VIDEOS + "/#", MOVIE_VIDEOS);
        uriMatcher.addURI(content_authority, MovieDBHelper.TBL_MOVIE_DETAIL + "/FAV/#", MOVIE_FAV);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection,
                        String selection, String[] selectionargs, String sortorder) {
        Cursor retCursor = null;
        switch (uriMatcher.match(uri)){
            case MOVIE_LIST:
                retCursor = movieDBHelper.getReadableDatabase().query(
                        MovieDBHelper.TBL_MOVIE,
                        projection,
                        MovieDBHelper.TBL_MOVIE + "." + MovieDBHelper.COLUMN_PAGE + " = ?",
                        new String[] { uri.getPathSegments().get(1) },
                        null,
                        null,
                        sortorder
                );
                retCursor.setNotificationUri(getContext().getContentResolver(), uri);
                break;

            case MOVIE_DETAIL:
                retCursor = movieDBHelper.getReadableDatabase().query(
                        MovieDBHelper.TBL_MOVIE_DETAIL,
                        projection,
                        MovieDBHelper.TBL_MOVIE_DETAIL + "." + MovieDBHelper.COLUMN_MOVIE_ID + " = ?",
                        new String[] { uri.getPathSegments().get(1) },
                        null,
                        null,
                        sortorder
                );
                retCursor.setNotificationUri(getContext().getContentResolver(), uri);
                break;

            case MOVIE_VIDEOS:
                retCursor = movieDBHelper.getReadableDatabase().query(
                        MovieDBHelper.TBL_VIDEOS,
                        projection,
                        MovieDBHelper.TBL_VIDEOS + "." + MovieDBHelper.COLUMN_MOVIE_ID + " = ?",
                        new String[] { uri.getPathSegments().get(1) },
                        null,
                        null,
                        sortorder
                );
                retCursor.setNotificationUri(getContext().getContentResolver(), uri);
                break;

            case MOVIE_FAV:
                retCursor = movieDBHelper.getReadableDatabase().query(
                        MovieDBHelper.TBL_MOVIE_DETAIL,
                        projection,
                        MovieDBHelper.TBL_MOVIE_DETAIL + "." + MovieDBHelper.COLUMN_AS_FAVOURITE + " = ?",
                        new String[] { uri.getLastPathSegment() },
                        null,
                        null,
                        sortorder
                );
                retCursor.setNotificationUri(getContext().getContentResolver(), uri);
                break;
        }

        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        int match = uriMatcher.match(uri);
        switch (match) {
            case MOVIE_LIST:
                return ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + String.valueOf(R.string.content_authority) + "/"
                        + MovieDBHelper.TBL_MOVIE + "/";
            case MOVIE_DETAIL:
                return ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + String.valueOf(R.string.content_authority) + "/"
                        + MovieDBHelper.TBL_MOVIE_DETAIL + "/";
            case MOVIE_VIDEOS:
                return ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + String.valueOf(R.string.content_authority) + "/"
                        + MovieDBHelper.TBL_VIDEOS + "/";
            case MOVIE_FAV:
                return ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + String.valueOf(R.string.content_authority) + "/"
                        + MovieDBHelper.TBL_MOVIE_DETAIL + "/FAV/";
        }
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        switch (uriMatcher.match(uri)) {

            case MOVIE_LIST:
                long id = movieDBHelper.getWritableDatabase().insert(
                        MovieDBHelper.TBL_MOVIE,
                        null,
                        contentValues
                );
                return Uri.parse ("content://" +String.valueOf(R.string.content_authority) + "/" + MovieDBHelper.TBL_MOVIE +"/row/"+id );

            case MOVIE_DETAIL:
                long iddetail = movieDBHelper.getWritableDatabase().insert(
                        MovieDBHelper.TBL_MOVIE_DETAIL,
                        null,
                        contentValues
                );
                return Uri.parse ("content://" +String.valueOf(R.string.content_authority) + "/" + MovieDBHelper.TBL_MOVIE_DETAIL +"/row/"+iddetail );
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        switch (uriMatcher.match(uri)) {

            case MOVIE_LIST:
                int x = movieDBHelper.getWritableDatabase().delete(
                        MovieDBHelper.TBL_MOVIE,
                        selection,
                        selectionArgs
                );
                return x;

            case MOVIE_DETAIL:
                int i = movieDBHelper.getWritableDatabase().delete(
                        MovieDBHelper.TBL_MOVIE_DETAIL,
                        selection,
                        selectionArgs
                );
                return i;

            case MOVIE_VIDEOS:
                int z = movieDBHelper.getWritableDatabase().delete(
                        MovieDBHelper.TBL_VIDEOS,
                        selection,
                        selectionArgs
                );
                return z;

        }
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        switch (uriMatcher.match(uri)) {

            case MOVIE_DETAIL:
                int i = movieDBHelper.getWritableDatabase().update(
                        MovieDBHelper.TBL_MOVIE_DETAIL,
                        contentValues,
                        selection,
                        selectionArgs
                );
                return i;

        }
        return 0;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        SQLiteDatabase writableDatabase= movieDBHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {

            case MOVIE_LIST:
                int returnCount = 0;

                writableDatabase.beginTransaction();
                try {
                    for (ContentValues cv : values) {
                        writableDatabase.insert(
                                MovieDBHelper.TBL_MOVIE,
                                null,
                                cv
                        );
                        returnCount++;
                    }
                    writableDatabase.setTransactionSuccessful();
                }
                catch (Exception e) {
                    returnCount = 0;
                }
                finally {
                    writableDatabase.endTransaction();
                }
                return returnCount;


            case MOVIE_VIDEOS:
                int count = 0;

                writableDatabase.beginTransaction();
                try {
                    for (ContentValues cv : values) {
                        writableDatabase.insert(
                                MovieDBHelper.TBL_VIDEOS,
                                null,
                                cv
                        );
                        count++;
                    }
                    writableDatabase.setTransactionSuccessful();
                }
                catch (Exception e) {
                    count = 0;
                }
                finally {
                    writableDatabase.endTransaction();
                }
                return count;
        }
        return 0;
    }


}
