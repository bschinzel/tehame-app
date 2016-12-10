package de.tehame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class PhotoAdapter extends BaseAdapter {
    private Context context = null;
    private static LayoutInflater inflater = null;
    private ArrayList<Uri> uris = null;

    public PhotoAdapter(Context ctx, ArrayList<Uri> uris) {

        this.uris = uris;
        this.context = ctx;

        this.inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return uris.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class PhotoHolder {
        TextView beschreibung = null;
        ImageView photo = null;
        int position = 0;
    }

    /**
     * Holder Pattern:
     * Siehe https://developer.android.com/training/improving-layouts/smooth-scrolling.html
     */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        // Der View muss eventuell zum ersten mal inflated werden
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.photo_list_item, parent, false);
        }

        PhotoHolder photoHolder = new PhotoHolder();
        photoHolder.beschreibung = (TextView) convertView.findViewById(R.id.beschreibung);
        photoHolder.photo = (ImageView) convertView.findViewById(R.id.photo);
        photoHolder.position = position;

        // Setze Inhalt
        // TODO Asynchrones Laden
        Bitmap bitmap = this.ladePhoto(this.uris.get(position));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            photoHolder.beschreibung.setText(this.uris.get(position).toString() + ": "
                    + (bitmap.getAllocationByteCount() / 1024) + "KB");
        } else {
            photoHolder.beschreibung.setText(this.uris.get(position).toString());
        }

        photoHolder.photo.setImageBitmap(bitmap);

        convertView.setTag(photoHolder);

        return convertView;
    }

    /**
     * Erstellt ein Bitmap von der Medien URI.
     * @param imageUri Medien URI.
     * @return Bitmap.
     */
    private Bitmap ladePhoto(Uri imageUri) {

        try {
            InputStream stream = this.context.getContentResolver().openInputStream(imageUri);
            byte[] binary = this.readBytes(stream);
            Bitmap bitmap = this.erstelleBitmapAusByteArray(binary);

            return bitmap;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
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
