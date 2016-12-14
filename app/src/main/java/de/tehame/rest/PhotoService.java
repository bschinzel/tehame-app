package de.tehame.rest;

import android.util.TypedValue;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Aus diesem Interface generiert Retrofit einen Endpunkt.
 * Siehe https://square.github.io/retrofit/
 */
public interface PhotoService {

    // TODO https://github.com/square/okhttp/wiki/Interceptors für Auth

    // https://futurestud.io/tutorials/retrofit-2-how-to-upload-files-to-server

    @POST("tehame/rest/v1/photos")
    @Multipart
    Call<ResponseBody> addPhoto(@Part("photo") RequestBody photo,
                                @Header("email") String email,
                                @Header("passwort") String passwort,
                                @Header("zugehoerigkeit") int zugehoerigkeit);
}
