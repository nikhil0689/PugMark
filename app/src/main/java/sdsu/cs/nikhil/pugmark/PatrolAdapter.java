package sdsu.cs.nikhil.pugmark;

import android.app.Activity;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Nikhil on 4/29/2017.
 */

public class PatrolAdapter extends ArrayAdapter implements PugMarkConstants{
    private Activity activity;
    private List<PatrolDataPojo> userProperties;

    public PatrolAdapter(@NonNull Activity activity, @LayoutRes int resource, @NonNull List objects) {
        super(activity, resource, objects);
        this.activity = activity;
        this.userProperties = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.patrol_list_item, null);
        TextView name = (TextView) view.findViewById(R.id.patrol_name);
        TextView empid = (TextView) view.findViewById(R.id.patrol_empid);
        TextView date = (TextView) view.findViewById(R.id.patrol_date);
        TextView startTime = (TextView) view.findViewById(R.id.patrol_start_time);
        TextView endTime = (TextView) view.findViewById(R.id.patrol_end_time);
        TextView timeDiff = (TextView) view.findViewById(R.id.patrol_time_diff);

        name.setText(userProperties.get(position).getName().toUpperCase());
        empid.setText(EMPLOYEE_ID+userProperties.get(position).getEmpid());
        date.setText(userProperties.get(position).getStartDate());
        startTime.setText(userProperties.get(position).getStartTime());
        endTime.setText(EMPTY_STRING_SPACE+"to"+EMPTY_STRING_SPACE+userProperties.get(position).getEndTime());
        DateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        try {
            Date formattedStartTime = sdf.parse(userProperties.get(position).getStartTime());
            Date formattedEndTime = sdf.parse(userProperties.get(position).getEndTime());
            long timeDifference = Math.abs((formattedEndTime.getTime() - formattedStartTime.getTime())/ (60 * 60 * 1000) % 24);
            long diffMinutes = Math.abs((formattedEndTime.getTime() - formattedStartTime.getTime()) / (60 * 1000) % 60);
            if(timeDifference > 12){
                timeDifference = 23-timeDifference;
                diffMinutes = 59-diffMinutes;
            }
            String patrolTime = String.valueOf(timeDifference).concat(" Hrs ").concat(String.valueOf(diffMinutes).concat(" Mins"));
            Log.d("rew","timeDifference"+patrolTime);
            timeDiff.setText(patrolTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return view;
    }
}
