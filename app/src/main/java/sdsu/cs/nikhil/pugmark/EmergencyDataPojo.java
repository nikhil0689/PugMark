package sdsu.cs.nikhil.pugmark;

import android.net.Uri;

import java.io.Serializable;

/**
 * Created by Nikhil on 4/24/2017.
 */

public class EmergencyDataPojo implements Serializable {
    private String name;
    private String empid;
    private String emergencyType;
    private String emergencySeverity;
    private String date;
    private String time;
    private String imageUrl;
    private String uid;
    private double latitude,longitude;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
    public String getEmpid() {
        return empid;
    }

    public void setEmpid(String empid) {
        this.empid = empid;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmergencyType() {
        return emergencyType;
    }

    public void setEmergencyType(String emergencyType) {
        this.emergencyType = emergencyType;
    }

    public String getEmergencySeverity() {
        return emergencySeverity;
    }

    public void setEmergencySeverity(String emergencySeverity) {
        this.emergencySeverity = emergencySeverity;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
