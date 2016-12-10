package de.tehame;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    /**
     * ListView mit Photos und kurzer Beschreibung.
     * Siehe Layout photo_list_item.xml
     */
    private ListView photoListe = null;

    private ArrayList<String> beschreibungen = null;
    private ArrayList<Bitmap> photos = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        photoListe = (ListView) this.findViewById(R.id.photoListView);

        Intent intent = this.getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        this.beschreibungen = new ArrayList<>();
        this.photos = new ArrayList<>();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            // Ein Photo als Intent
            if (type.startsWith("image/")) {
                Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);

                if (imageUri != null) {
                    ladePhoto(imageUri);
                }
            }
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
            // Mehrere Photos als Intent
            if (type.startsWith("image/")) {
                ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);

                if (imageUris != null) {
                    for (Uri uri : imageUris) {
                        ladePhoto(uri);
                    }
                }
            }
        }

        photoListe.setAdapter(new PhotoAdapter(this, beschreibungen, photos));
    }

    /**
     * Füge ein Photo der Liste hinzu.
     * @param imageUri Medien URI.
     */
    private void ladePhoto(Uri imageUri) {
        String beschreibung = imageUri.toString();

        try {
            InputStream stream = this.getContentResolver().openInputStream(imageUri);
            byte[] binary = this.readBytes(stream);
            Bitmap bitmap = this.erstelleBitmapAusByteArray(binary);

            beschreibung += " " + (binary.length / 1024) + "KB";

            this.photos.add(bitmap);
            this.beschreibungen.add(beschreibung);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Liest einen InputStream aus und erstellt daraus ein Byte Array.
     * @param inputStream Input Stream.
     * @return Byte Array.
     * @throws IOException I/O Error.
     */
    public byte[] readBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }

        return byteBuffer.toByteArray();
    }

    /**
     * Wandelt ein ByteArray in eine Bitmap um, welche in einem ImageView angezeigt werden kann.
     * @param byteArray Byte Array.
     * @return Erstellt eine Bitmap.
     */
    private Bitmap erstelleBitmapAusByteArray(byte[] byteArray) {
        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        return bmp;
    }
}
