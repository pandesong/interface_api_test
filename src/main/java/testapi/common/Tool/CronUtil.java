package testapi.common.Tool;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import testapi.common.Tool.timeObject;
/**
 * @Classname CronUtil
 * @Description TODO
 */
public class CronUtil {

    /**
     *
     *方法摘要：构建Cron表达式
     *@param  taskScheduleModel
     *@return String
     */
    public static String createCronExpression(TaskScheduleModel taskScheduleModel){
        StringBuffer cronExp = new StringBuffer("");

        if(null == taskScheduleModel.getJobType()) {
            System.out.println("执行周期未配置" );//执行周期未配置
        }

        if (null != taskScheduleModel.getSecond()
                && null == taskScheduleModel.getMinute()
                && null == taskScheduleModel.getHour()){
            //每隔几秒
            if (taskScheduleModel.getJobType().intValue() == 0) {
                cronExp.append("0/").append(taskScheduleModel.getSecond());
                cronExp.append(" ");
                cronExp.append("* ");
                cronExp.append("* ");
                cronExp.append("* ");
                cronExp.append("* ");
                cronExp.append("?");
            }

        }

        if (null != taskScheduleModel.getSecond()
                && null != taskScheduleModel.getMinute()
                && null == taskScheduleModel.getHour()){
            //每隔几分钟
            if (taskScheduleModel.getJobType().intValue() == 4) {
                cronExp.append("* ");
                cronExp.append("0/").append(taskScheduleModel.getMinute());
                cronExp.append(" ");
                cronExp.append("* ");
                cronExp.append("* ");
                cronExp.append("* ");
                cronExp.append("?");
            }

        }

        if (null != taskScheduleModel.getSecond()
                && null != taskScheduleModel.getMinute()
                && null != taskScheduleModel.getHour()) {
            //秒
            cronExp.append(taskScheduleModel.getSecond()).append(" ");
            //分
            cronExp.append(taskScheduleModel.getMinute()).append(" ");
            //小时
            cronExp.append(taskScheduleModel.getHour()).append(" ");

            //每天
            if(taskScheduleModel.getJobType().intValue() == 1){
                cronExp.append("* ");//日
                cronExp.append("* ");//月
                cronExp.append("?");//周
            }

            //按每周
            else if(taskScheduleModel.getJobType().intValue() == 3){
                //一个月中第几天
                cronExp.append("? ");
                //月份
                cronExp.append("* ");
                //周
                Integer[] weeks = taskScheduleModel.getDayOfWeeks();
                for(int i = 0; i < weeks.length; i++){
                    if(i == 0){
                        cronExp.append(weeks[i]);
                    } else{
                        cronExp.append(",").append(weeks[i]);
                    }
                }

            }

            //按每月
            else if(taskScheduleModel.getJobType().intValue() == 2){
                //一个月中的哪几天
                Integer[] days = taskScheduleModel.getDayOfMonths();
                for(int i = 0; i < days.length; i++){
                    if(i == 0){
                        cronExp.append(days[i]);
                    } else{
                        cronExp.append(",").append(days[i]);
                    }
                }
                //月份
                cronExp.append(" * ");
                //周
                cronExp.append("?");
            }

        }
        else {
            System.out.println("时或分或秒参数未配置" );//时或分或秒参数未配置
        }
        return cronExp.toString();
    }

    /**
     *
     *方法摘要：生成计划的详细描述
     *@param  taskScheduleModel
     *@return String
     */
    public static String createDescription(TaskScheduleModel taskScheduleModel){
        StringBuffer description = new StringBuffer("");
        //计划执行开始时间
//      Date startTime = taskScheduleModel.getScheduleStartTime();

        if (null != taskScheduleModel.getSecond()
                && null != taskScheduleModel.getMinute()
                && null != taskScheduleModel.getHour()) {
            //按每天
            if(taskScheduleModel.getJobType().intValue() == 1){
                description.append("每天");
                description.append(taskScheduleModel.getHour()).append("时");
                description.append(taskScheduleModel.getMinute()).append("分");
                description.append(taskScheduleModel.getSecond()).append("秒");
                description.append("执行");
            }

            //按每周
            else if(taskScheduleModel.getJobType().intValue() == 3){
                if(taskScheduleModel.getDayOfWeeks() != null && taskScheduleModel.getDayOfWeeks().length > 0) {
                    String days = "";
                    for(int i : taskScheduleModel.getDayOfWeeks()) {
                        days += "周" + i;
                    }
                    description.append("每周的").append(days).append(" ");
                }
                if (null != taskScheduleModel.getSecond()
                        && null != taskScheduleModel.getMinute()
                        && null != taskScheduleModel.getHour()) {
                    description.append(",");
                    description.append(taskScheduleModel.getHour()).append("时");
                    description.append(taskScheduleModel.getMinute()).append("分");
                    description.append(taskScheduleModel.getSecond()).append("秒");
                }
                description.append("执行");
            }

            //按每月
            else if(taskScheduleModel.getJobType().intValue() == 2){
                //选择月份
                if(taskScheduleModel.getDayOfMonths() != null && taskScheduleModel.getDayOfMonths().length > 0) {
                    String days = "";
                    for(int i : taskScheduleModel.getDayOfMonths()) {
                        days += i + "号";
                    }
                    description.append("每月的").append(days).append(" ");
                }
                description.append(taskScheduleModel.getHour()).append("时");
                description.append(taskScheduleModel.getMinute()).append("分");
                description.append(taskScheduleModel.getSecond()).append("秒");
                description.append("执行");
            }

        }
        return description.toString();
    }

    public static timeObject erverCronDayTime(int  time1){
        timeObject  tt=new timeObject();
        int day=Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        int hour=Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int minute=Calendar.getInstance().get(Calendar.MINUTE);
        int year=Calendar.getInstance().get(Calendar.YEAR);
        int month=Calendar.getInstance().get(Calendar.MONTH);
        TaskScheduleModel taskScheduleModel = new TaskScheduleModel();
        taskScheduleModel.setJobType(1);//按每天
        taskScheduleModel.setHour(hour);
        taskScheduleModel.setMinute(minute+2);
        taskScheduleModel.setSecond(0);
        String cropExp = createCronExpression(taskScheduleModel);
      //  System.out.println(cropExp + ":" + createDescription(taskScheduleModel));
        tt.setCron(cropExp);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
        //Model.setTaskBeginTime(sdf.format(System.currentTimeMillis()+60*1000));
        String time=sdf.format(System.currentTimeMillis()+time1*1000);
        //String time=String.format("%s-%s-%s %s:%s:%s",year,month,day,hour,minute+2,0);
        tt.setTime(time);
        tt.setDispatchTime(time.split(" ")[1]);
        return tt;
    }

    public static timeObject erverCronWeekTime(){
        timeObject  tt=new timeObject();
        int day=Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        int hour=Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int minute=Calendar.getInstance().get(Calendar.MINUTE);
        int year=Calendar.getInstance().get(Calendar.YEAR);
        int week=Calendar.getInstance().get(Calendar.DAY_OF_WEEK)-1;
        TaskScheduleModel taskScheduleModel = new TaskScheduleModel();
        taskScheduleModel.setJobType(3);//按每周
        taskScheduleModel.setHour(hour);
        taskScheduleModel.setMinute(minute+2);
        taskScheduleModel.setSecond(0);
        Integer[] dayOfWeeks = new Integer[1];
        dayOfWeeks[0] = week+1;
        taskScheduleModel.setDayOfWeeks(dayOfWeeks);
        String cropExp = createCronExpression(taskScheduleModel);
     //   System.out.println(cropExp + ":" + createDescription(taskScheduleModel));
        tt.setCron(cropExp);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
        //Model.setTaskBeginTime(sdf.format(System.currentTimeMillis()+60*1000));
        String time=sdf.format(System.currentTimeMillis()+70*1000);
        tt.setTime(time);
        time=String.format("%s,%s",week,time.split(" ")[1]);
        tt.setDispatchTime(time);
        return tt;
    }

    public static timeObject onceCronTime(int timeinterval){

        timeObject  tt=new timeObject();


       // "5 14 18 11 11 ? 2021";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
        //Model.setTaskBeginTime(sdf.format(System.currentTimeMillis()+60*1000));
        //"2021-11-11 18:14:05"
        String time=sdf.format(System.currentTimeMillis()+timeinterval);
        tt.setDispatchTime(time);
        String tmp1=time.split(" ")[0];
        String tmp2=time.split(" ")[1];
        String minute=tmp2.split(":")[1];
        String hour=tmp2.split(":")[0];
        String year=tmp1.split("-")[0];
        String month=tmp1.split("-")[1];
        String day=tmp1.split("-")[2];
        tt.setTime(time);
        time=String.format("%s %s %s %s %s ? %s",0,minute,hour,day,month,year);
        tt.setCron(time);
        return tt;

    }



    public static timeObject erverCronMonthTime(){
        timeObject  tt=new timeObject();
        int day=Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        int hour=Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int minute=Calendar.getInstance().get(Calendar.MINUTE);
        int month=Calendar.getInstance().get(Calendar.MONTH);
       // int week=Calendar.getInstance().get(Calendar.DAY_OF_WEEK)-1;
        TaskScheduleModel taskScheduleModel = new TaskScheduleModel();
        taskScheduleModel.setJobType(2);//按每月
        taskScheduleModel.setHour(hour);
        taskScheduleModel.setMinute(minute+2);
        taskScheduleModel.setSecond(0);

        Integer[] dayOfMonths = new Integer[1];
        dayOfMonths[0] = day;

        taskScheduleModel.setDayOfMonths(dayOfMonths);
        String cropExp = createCronExpression(taskScheduleModel);
     //   System.out.println(cropExp + ":" + createDescription(taskScheduleModel));
        tt.setCron(cropExp);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
        //Model.setTaskBeginTime(sdf.format(System.currentTimeMillis()+60*1000));
        String time=sdf.format(System.currentTimeMillis()+70*1000);
        tt.setTime(time);
        time=String.format("%s,%s",day,time.split(" ")[1]);
        tt.setDispatchTime(time);
        return tt;
    }



    //参考例子
    public static void main(String[] args) {


        //执行时间：每天的12时12分12秒 start
        TaskScheduleModel taskScheduleModel = new TaskScheduleModel();

        taskScheduleModel.setJobType(0);//按每秒
        taskScheduleModel.setSecond(30);
        String cronExp = createCronExpression(taskScheduleModel);
        System.out.println(cronExp);

        taskScheduleModel.setJobType(4);//按每分钟
        taskScheduleModel.setMinute(8);
        String cronExpp = createCronExpression(taskScheduleModel);
        System.out.println(cronExpp);

        taskScheduleModel.setJobType(1);//按每天
        Integer hour = 12; //时
        Integer minute = 12; //分
        Integer second = 12; //秒
        taskScheduleModel.setHour(hour);
        taskScheduleModel.setMinute(minute);
        taskScheduleModel.setSecond(second);
        String cropExp = createCronExpression(taskScheduleModel);
        System.out.println(cropExp + ":" + createDescription(taskScheduleModel));
        //执行时间：每天的12时12分12秒 end

        taskScheduleModel.setJobType(3);//每周的哪几天执行
        Integer[] dayOfWeeks = new Integer[3];
        dayOfWeeks[0] = 1;
        dayOfWeeks[1] = 2;
        dayOfWeeks[2] = 3;
        taskScheduleModel.setDayOfWeeks(dayOfWeeks);
        cropExp = createCronExpression(taskScheduleModel);
        System.out.println(cropExp + ":" + createDescription(taskScheduleModel));

        taskScheduleModel.setJobType(2);//每月的哪几天执行
        Integer[] dayOfMonths = new Integer[3];
        dayOfMonths[0] = 1;
        dayOfMonths[1] = 21;
        dayOfMonths[2] = 13;
        taskScheduleModel.setDayOfMonths(dayOfMonths);
        cropExp = createCronExpression(taskScheduleModel);
        System.out.println(cropExp + ":" + createDescription(taskScheduleModel));

    }
}

