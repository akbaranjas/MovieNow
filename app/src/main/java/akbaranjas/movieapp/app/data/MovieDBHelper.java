package akbaranjas.movieapp.app.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by akbaranjas on 28/11/16.
 */

public class MovieDBHelper extends SQLiteOpenHelper {

    public static final String MOVIE_DB = "movie.db";
    public static final int VERSION = 1;
    public static final String COLUMN_MOVIE_ID = "movie_id";
    public static final String COLUMN_OVERVIEW = "overview";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_VOTE_AVERAGE = "vote_average";
    public static final String COLUMN_PAGE = "page";
    public static final String TBL_MOVIE = "tbl_movie";
    public static final String TBL_MOVIE_DETAIL = "tbl_movie_detail";
    public static final String COLUMN_POSTER_PATH = "poster_path";
    public static final String COLUMN_AS_FAVOURITE = "as_favourite";
    public static final String COLUMN_BACKDROP_PATH = "backdrop_path";
    public static final String COLUMN_RUNTIME = "runtime";
    public static final String COLUMN_RELEASE_DATE = "release_date";

    private static final String CREATE_TBL_MOVIE ="create table " + TBL_MOVIE + " (" +
            COLUMN_MOVIE_ID + " int, " +
            COLUMN_OVERVIEW + " text, " +
            COLUMN_TITLE + " text, " +
            COLUMN_POSTER_PATH + " text, " +
            COLUMN_VOTE_AVERAGE + " real, " +
            COLUMN_PAGE + " int, " +
            "unique ("+ COLUMN_MOVIE_ID + ") on conflict replace);";

    private static final String CREATE_TBL_MOVIE_DETAIL ="create table " + TBL_MOVIE_DETAIL + " (" +
            COLUMN_MOVIE_ID + " int, " +
            COLUMN_OVERVIEW + " text, " +
            COLUMN_TITLE + " text, " +
            COLUMN_RUNTIME + " int, " +
            COLUMN_RELEASE_DATE + " text, " +
            COLUMN_VOTE_AVERAGE + " real, " +
            COLUMN_BACKDROP_PATH + " text, " +
            COLUMN_POSTER_PATH + " text, " +
            COLUMN_AS_FAVOURITE + " int, " +
            "unique ("+ COLUMN_MOVIE_ID + ") on conflict replace);";

    public MovieDBHelper(Context context) {
        super(context, MOVIE_DB, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TBL_MOVIE);
        sqLiteDatabase.execSQL(CREATE_TBL_MOVIE_DETAIL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        buatUlangDB(sqLiteDatabase);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        buatUlangDB(db);
    }

    private void buatUlangDB(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("drop table if exists " + TBL_MOVIE);
        sqLiteDatabase.execSQL("drop table if exists " + TBL_MOVIE_DETAIL);

        onCreate(sqLiteDatabase);
    }


}
