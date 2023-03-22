package testapi.common.SnapShotModel;

public class SsrequestModel {

public String name;
public int  storagepoolId;
public   int fsusergroupId;
public   int fsuserId;
public String  dicName;
public String  snaDesc;
public  String  dispatchCron;
public  String   time;
public String  week;
public   int  day;
public  String   dispatchTime;
public   String dispatchStyle;
public   int  id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public   int versionDeletestrategy;

    public int getVersionDeletestrategy() {
        return versionDeletestrategy;
    }

    public void setVersionDeletestrategy(int versionDeletestrategy) {
        this.versionDeletestrategy = versionDeletestrategy;
    }

    public String getDispatchStyle() {
        return dispatchStyle;
    }

    public void setDispatchStyle(String dispatchStyle) {
        this.dispatchStyle = dispatchStyle;
    }

    public int getDay() {
        return day;
    }

    public String getDicName() {
        return dicName;
    }

    public String getDispatchCron() {
        return dispatchCron;
    }

    public String getDispatchTime() {
        return dispatchTime;
    }

    public int getFsusergroupId() {
        return fsusergroupId;
    }

    public int getFsuserId() {
        return fsuserId;
    }

    public String getName() {
        return name;
    }

    public String getSnaDesc() {
        return snaDesc;
    }

    public int getStoragepoolId() {
        return storagepoolId;
    }

    public String getTime() {
        return time;
    }

    public String getWeek() {
        return week;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void setDicName(String dicName) {
        this.dicName = dicName;
    }

    public void setDispatchCron(String dispatchCron) {
        this.dispatchCron = dispatchCron;
    }

    public void setDispatchTime(String dispatchTime) {
        this.dispatchTime = dispatchTime;
    }


    public void setFsusergroupId(int fsusergroupId) {
        this.fsusergroupId = fsusergroupId;
    }

    public void setFsuserId(int fsuserId) {
        this.fsuserId = fsuserId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSnaDesc(String snaDesc) {
        this.snaDesc = snaDesc;
    }

    public void setStoragepoolId(int storagepoolId) {
        this.storagepoolId = storagepoolId;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setWeek(String week) {
        this.week = week;
    }



}
