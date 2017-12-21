package sdsu.cs.nikhil.pugmark;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

/**
 * Created by Nikhil on 5/7/2017.
 */

public class ImageAdapter extends ArrayAdapter {
    private Activity activity;
    private List<ImageDataPojo> userProperties;


    public ImageAdapter(@NonNull Activity activity, @LayoutRes int resource, @NonNull List objects) {
        super(activity, resource, objects);
        this.activity = activity;
        this.userProperties = objects;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Bitmap imageBitmap = null;
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.image_list_item, null);
        ImageView image = (ImageView) view.findViewById(R.id.imagelist_in_adapter);
        TextView name = (TextView) view.findViewById(R.id.image_list_name);
        TextView date = (TextView) view.findViewById(R.id.image_list_date);
        TextView time = (TextView) view.findViewById(R.id.image_list_time);
        if(userProperties.get(position).getImageUrl() != null){
            try {
                imageBitmap = decodeFromFirebaseBase64(userProperties.get(position).getImageUrl());
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(imageBitmap != null){
                image.setImageBitmap(imageBitmap);
            }
        }
        name.setText(userProperties.get(position).getName());
        date.setText(userProperties.get(position).getDate());
        time.setText(userProperties.get(position).getTime());
        return view;
    }

    public static Bitmap decodeFromFirebaseBase64(String image) throws IOException {
        byte[] decodedByteArray = android.util.Base64.decode(image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
    }
}
