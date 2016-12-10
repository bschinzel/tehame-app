package de.tehame;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class PhotoAdapter extends BaseAdapter {
    private Context context = null;
    private static LayoutInflater inflater = null;
    private ArrayList<String> beschreibungen = null;
    private ArrayList<Bitmap> photos = null;

    public PhotoAdapter(Context ctx,
                        ArrayList<String> beschreibungen,
                        ArrayList<Bitmap> photos) {

        this.beschreibungen = beschreibungen;
        this.context = ctx;
        this.photos = photos;

        this.inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return photos.size();
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
        photoHolder.beschreibung.setText(this.beschreibungen.get(position));
        photoHolder.photo.setImageBitmap(this.photos.get(position));

        convertView.setTag(photoHolder);

        return convertView;
    }
}
