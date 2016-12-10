package de.tehame;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    /**
     * ListView mit Photos und kurzer Beschreibung.
     * Siehe Layout photo_list_item.xml
     */
    private ListView photoListe = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        photoListe = (ListView) this.findViewById(R.id.photoListView);

        Intent intent = this.getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        ArrayList<Uri> uris = null;

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
        Toast.makeText(this, "Hallloooo", Toast.LENGTH_LONG).show();
    }
}
