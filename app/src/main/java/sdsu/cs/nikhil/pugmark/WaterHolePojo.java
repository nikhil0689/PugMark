package sdsu.cs.nikhil.pugmark;

import java.io.Serializable;

/**
 * Created by Nikhil on 4/30/2017.
 */

public class WaterHolePojo implements Serializable{
    private String uid;
    private String waterHoleName;
    private String waterLevel;
    private String name,empid;
    private String dateValue;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDateValue() {
        return dateValue;
    }

    public void setDateValue(String dateValue) {
        this.dateValue = dateValue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmpid() {
        return empid;
    }

    public void setEmpid(String empid) {
        this.empid = empid;
    }

    public String getWaterHoleName() {
        return waterHoleName;
    }

    public void setWaterHoleName(String waterHoleName) {
        this.waterHoleName = waterHoleName;
    }

    public String getWaterLevel() {
        return waterLevel;
    }

    public void setWaterLevel(String waterLevel) {
        this.waterLevel = waterLevel;
    }
}
