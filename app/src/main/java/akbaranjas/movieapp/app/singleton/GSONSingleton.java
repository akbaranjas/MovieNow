package akbaranjas.movieapp.app.singleton;

import com.google.gson.Gson;

/**
 * Created by akbaranjas on 22/11/16.
 */

public class GSONSingleton {
    static Gson gson;
    public static Gson getGson(){
        if (null == gson) {
            return new Gson();
        }
        return gson;
    }
}
