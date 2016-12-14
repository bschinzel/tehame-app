package de.tehame;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import de.tehame.rest.PhotoService;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    /**
     * ListView mit Photos und kurzer Beschreibung.
     * Siehe Layout photo_list_item.xml
     */
    private ListView photoListe = null;

    /**
     * Die Media URIs der Fotos.
     */
    private ArrayList<Uri> uris = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        photoListe = (ListView) this.findViewById(R.id.photoListView);

        Intent intent = this.getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            // Ein Photo als Intent
            if (type.startsWith("image/")) {
                Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);

                uris = new ArrayList<Uri>();
                uris.add(imageUri);
            }
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
            // Mehrere Photos als Intent
            if (type.startsWith("image/")) {
                ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);

                if (imageUris != null) {
                    uris = imageUris;
                }
            }
        }

        if (uris != null) {
            photoListe.setAdapter(new PhotoAdapter(this, uris));
        }
    }

    public void senden(View view) {
        // AVD nach Hostsystem: 10.0.2.2
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080")
                .build();

        for (Uri uri : this.uris) {

            byte[] binary = null;
            String filename = this.getFileName(uri);

            try {
                InputStream stream = this.getContentResolver().openInputStream(uri);
                binary = InputStreamReader.readBytes(stream);
            } catch (FileNotFoundException e) {
                Toast.makeText(this, "Die Datei " + filename + " wurde nicht gefunden und" +
                        " kann nicht gesendet werden.", Toast.LENGTH_LONG).show();
                Log.e(MainActivity.class.toString(), e.getMessage(), e);
                break;
            } catch (IOException e) {
                Toast.makeText(this, "Ein Stream zur Datei " + filename
                        + "  konnte nicht gelesen werden.", Toast.LENGTH_LONG).show();
                Log.e(MainActivity.class.toString(), e.getMessage(), e);
            }

            RequestBody requestFile =
                    RequestBody.create(MediaType.parse("image/jpeg"), binary);

            PhotoService service = retrofit.create(PhotoService.class);
            Call<ResponseBody> call = service.addPhoto(requestFile, "admin_a@tehame.de", "a", 2);

            // Asynchroner Aufruf mit Callback im UI Thread
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Toast.makeText(MainActivity.this, response.message(), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "FAILURE", Toast.LENGTH_LONG).show();
                    Log.e(MainActivity.class.toString(), t.getMessage(), t);
                }
            });
        }
    }

    /**
     * Siehe auch https://developer.android.com/guide/topics/providers/document-provider.html
     */
    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
}
