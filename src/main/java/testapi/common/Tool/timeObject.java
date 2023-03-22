package testapi.common.Tool;

public class timeObject {
    public String  cron;
    public String dispatchTime;
    public String  time;

    public void setTime(String time) {
        this.time = time;
    }

    public void setDispatchTime(String dispatchTime) {
        this.dispatchTime = dispatchTime;
    }

    public String getTime() {
        return time;
    }

    public String getDispatchTime() {
        return dispatchTime;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public String getCron() {
        return cron;
    }

}
