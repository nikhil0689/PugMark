package sdsu.cs.nikhil.pugmark;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by Nikhil on 4/24/2017.
 */

public class EmergencyAdapter extends ArrayAdapter implements PugMarkConstants {
    private Activity activity;
    private List<EmergencyDataPojo> userProperties;


    public EmergencyAdapter(@NonNull Activity activity, @LayoutRes int resource, @NonNull List objects) {
        super(activity, resource, objects);
        this.activity = activity;
        this.userProperties = objects;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Bitmap imageBitmap = null;
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.emergency_list_item, null);
        ImageView image = (ImageView) view.findViewById(R.id.image_in_adapter);
        TextView name = (TextView) view.findViewById(R.id.list_name);
        TextView empid = (TextView) view.findViewById(R.id.list_empid);
        TextView date = (TextView) view.findViewById(R.id.list_date);
        TextView time = (TextView) view.findViewById(R.id.list_time);
        TextView emergencyType = (TextView) view.findViewById(R.id.list_emergencyType);
        TextView emergencySeverity = (TextView) view.findViewById(R.id.list_emergencySeverity);
        TextView latitude = (TextView) view.findViewById(R.id.list_latitude);
        TextView longitude = (TextView) view.findViewById(R.id.list_longitude);
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
        name.setText(""+userProperties.get(position).getName());
        empid.setText(""+userProperties.get(position).getEmpid());
        date.setText(""+userProperties.get(position).getDate());
        time.setText(""+userProperties.get(position).getTime());
        emergencyType.setText(""+userProperties.get(position).getEmergencyType());
        emergencySeverity.setText(""+userProperties.get(position).getEmergencySeverity());
        if(emergencySeverity.getText().toString().equalsIgnoreCase(HIGH)){
            Log.d("rew","high");
            emergencySeverity.setTextColor(Color.RED);
        }
        latitude.setText(""+userProperties.get(position).getLatitude());
        longitude.setText(""+userProperties.get(position).getLongitude());
        return view;
    }

    public static Bitmap decodeFromFirebaseBase64(String image) throws IOException {
        byte[] decodedByteArray = android.util.Base64.decode(image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
    }


}
