package sdsu.cs.nikhil.pugmark;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Nikhil on 4/30/2017.
 */

public class WaterHoleAdapter extends ArrayAdapter implements PugMarkConstants{
    private Activity activity;
    private List<WaterHolePojo> userProperties;

    public WaterHoleAdapter(@NonNull Activity activity, @LayoutRes int resource, @NonNull List objects) {
        super(activity, resource, objects);
        this.activity = activity;
        this.userProperties = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.waterhole_list_item, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.image_in_water_adapter);
        TextView tankName = (TextView) view.findViewById(R.id.water_hole_name_list);
        TextView level = (TextView) view.findViewById(R.id.level_list);
        TextView date = (TextView) view.findViewById(R.id.update_time_list);
        TextView name = (TextView) view.findViewById(R.id.update_name_list);
        AssetManager assetManager = getContext().getAssets();
        try {
            InputStream is =  assetManager.open(DEFAULT_WATERHOLE_IMAGE);
            Drawable d = Drawable.createFromStream(is, null);
            // set image to ImageView
            imageView.setImageDrawable(d);
        } catch (IOException e) {
            e.printStackTrace();
        }

        tankName.setText(userProperties.get(position).getWaterHoleName().toUpperCase());
        level.setText(userProperties.get(position).getWaterLevel());
        date.setText(userProperties.get(position).getDateValue());
        name.setText(userProperties.get(position).getName().toUpperCase());
        if(level.getText().toString().equalsIgnoreCase(DANGER)){
            Log.d("rew","high");
            level.setTextColor(Color.RED);
        }
        return view;
    }
}
