package akbaranjas.movieapp.app.url;

/**
 * Created by akbaranjas on 22/11/16.
 */

public class MovieURL {

    public static final String APP_KEY_ID = "20f1457d6557c7ae70058e0f95a7fcbf";
    public static final String  BASE_URL = "http://api.themoviedb.org/3/movie/";
    public static final String BASE_URL_IMG = "http://image.tmdb.org/t/p/";
    public static final String[] SIZE_IMAGE = {"w92", "w154", "w185", "w342", "w500", "w780", "original"};
    public static final String API_KEY_GOOGLE = "AIzaSyB3F_B58sD_BbV_AQ2zblCm-_I4kL04bVk";

    public static final String getMovieListURL(String category, String page){

        if(page == null){
            page = "1";
        }

        return BASE_URL + category + "?page=" + page + "&" +
                "api_key=" + APP_KEY_ID;
    }

    public static final String getMovieDetailURL(String id){

        return BASE_URL + id +
                "?api_key=" + APP_KEY_ID;
    }

    public static final String getImageURL(String image, String size){

        return BASE_URL_IMG + size + "/" +image;
    }


}
