package testapi.longSteady;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import testapi.common.JobLogRespModel;
import testapi.common.JobPlainStatusModel;
import testapi.common.JobStatusRespModel;
import testapi.common.Result;
import testapi.common.api.*;
import org.junit.*;
import testapi.framework.SSHExecutor;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import static org.apache.commons.codec.binary.StringUtils.newString;

public class backtool {

    static ApiTest api=new ApiTest();
    private static SSHExecutor sshcmd;
    String plainId="";
    private static SSHExecutor dstsshcmd;
    String srcpath=api.src_path+"/filter";

    @BeforeClass
    public  static void beforeclass(){
        sshcmd=api.ssh_login(api.ssh_src_username,api.ssh_src_password, api.ssh_login_src_ip, api.ssh_src_port);
        sshcmd.setHost_type(api.ssh_src_type);
        dstsshcmd=api.ssh_login(api.ssh_dest_username,api.ssh_dest_password, api.ssh_dst_ip, api.ssh_dest_port);
    }


    @AfterClass
    public static  void afterclass(){

    }

    @Test
    public  void TestCase_delmorejob(){
        for(int i=0;i<20;i++) {
            Result res=api.getjopplainList("","","");
            System.out.println(JSONObject.parseObject(res.getBody()));
            String id= JSONObject.parseObject(res.getBody()).getJSONArray("content").getJSONObject(0).getString("id");
            api.DeleteJobPlain(id);
        }
    }

    @Test//同一个时刻多个作业
    public  void TestCase_morejob1(){
        int count=10;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        ArrayList JobList=new ArrayList();
        ArrayList result=new ArrayList();
        for(int i=0;i<count;i++) {
            JobList.add(create_job_no_pack_one_thread(sdf.format(System.currentTimeMillis()+30*1000)));
        }
        for(int i=0;i<count;i++) {
            result.add(whait_for_job_end(JobList.get(i).toString()));

        }
        for(int i=0;i<count;i++) {
        }
        Assert.assertTrue(!result.contains(false));
    }

    public  Boolean whait_for_job_end(String JobName){
        try {
            int count = 1;
            while (count < 1800) {
                JobStatusRespModel JobStatusResp = api.GetJobStatus(JobName);
                if(JobStatusResp==null) {
                    Thread.sleep(1000);
                }
                if (JobStatusResp.getContent().size() == 0) {
                    Thread.sleep(1000);
                } else {

                    String status = JobStatusResp.getContent().get(0).getJobStatus();
                    if (status.equals("R") || status.equals("C")) {
                        Thread.sleep(1000);
                    } else if (status.equals("T")) {
                        break;
                    } else {

                        System.out.println("作业运行失败："+JobName);
                        return false;
                    }
                }
                count++;

            }
            if (count >= 1800) {
                System.out.println("作业运行超过了半个小时  "+JobName);
                return false;
            }
            JobLogRespModel joblog = api.GetJobLog(JobName);
            ArrayList list = new ArrayList();
            String desPathName = null;
            System.out.println(joblog.getContent());
            for (int a = 0; a < joblog.getContent().size(); a++) {
                if (!joblog.getContent().get(a).getStatus().equals("0")) {
                    System.out.println("BACK JOB ERROR " + JobName);
                    return false;
                }
                ;
                desPathName = joblog.getContent().get(a).getDesPathName();
                list.add(joblog.getContent().get(a).getFileName());
            }
            if (desPathName == null) {
                System.out.println("没有文件进行备份 "+JobName);
                return false;
            }
            Result res=api.compile_the_result( sshcmd, dstsshcmd, desPathName, api.src_path, api.src_login_path);
            if(res.getCode()==400)
            {
                System.out.printf("作业：%s结果比较失败,失败原因:%s\r\n",JobName,res.getBody());
            }
            Result resp = api.GetJobPlainByName(JobName);
            JSONArray content = JSONObject.parseObject(resp.getBody()).getJSONArray("content");
            String id = content.getJSONObject(0).getString("id");
            api.DeleteJobPlain(id);
        }
        catch (Exception e)
        {
            e.printStackTrace();

            return false;
        }
        return true;
    }


    public  String  create_job_no_pack_one_thread(String BeginTime){
        try {
            ArrayList JobList=new ArrayList();
            JobPlainStatusModel Model=new JobPlainStatusModel();
            String JobName="a"+String.valueOf(System.currentTimeMillis());

            Model.setTaskName(JobName);
            Model.setTaskType(2);
            //   api.ssh_src_ip="127.0.0.1";
            //  api.ssh_dst_ip="127.0.0.1";
            Model.setSourcePath(api.src_path);
            Model.setsourceIp(api.ssh_src_ip);
            Model.setTargetPath(api.dst_path);
            Model.setTargetIp(api.ssh_dst_ip);
            Model.setScheduleType(0);

            Date d = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            Model.setTaskBeginTime(BeginTime);
            Model.setScheduleTypeReadOnly(false);
            Model.setFilterVisible(false);
            Model.setIsPackaging("0");
            //  Model.setPackingLevel("0");
            //  Model.setFileSizeUnit("MB");
            //  Model.setPackingSize(1);
            Model.setThreadNum(1);
            Model.setSmallBatchCommitNum(1);
            Model.setFileCheckType("2");
            Model.setBackupType(1);


            Result resp=api.CreateJobPlain(JSON.toJSONString(Model));
            if(resp.getCode()!=400){

                return JobName;
            }
            else {
                System.out.println("===============================创建作业计划任务失败==========================");
                System.out.println(resp.getBody());
                Assert.assertTrue(false);
            }

        }catch (Exception e){

            e.printStackTrace();
        }


        return "";
    }

    @Test
    public  void TestCase_CreateJobPlainPack_RunOnce(){
        try {
            JobPlainStatusModel Model=new JobPlainStatusModel();
            String JobName="a"+String.valueOf(System.currentTimeMillis());
            // JobName=new String(JobName.getBytes("UTF-8"),"UTF-8");
            Model.setTaskName(JobName);
            Model.setTaskType(2);
            Model.setSourcePath(api.src_path);
            Model.setsourceIp(api.ssh_src_ip);
            Model.setTargetPath(api.dst_path);
            Model.setTargetIp(api.ssh_dst_ip);
            Model.setScheduleType(0);
            Date d = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            Model.setTaskBeginTime(sdf.format(d));
            Model.setScheduleTypeReadOnly(false);
            Model.setFilterVisible(false);
            Model.setIsPackaging("2");
            Model.setPackingLevel("0");
            Model.setFileSizeUnit("MB");
            Model.setPackingSize(1);
            Model.setThreadNum(1);
            Model.setSmallBatchCommitNum(1);
            Model.setFileCheckType("2");
            Model.setBackupType(1);
            //  System.out.println(JSON.toJSONString(Model));


            Result resp=api.CreateJobPlain(JSON.toJSONString(Model));
            if(resp.getCode()!=400){
                //System.out.println(resp.getBody());
            }
            else {
                System.out.println("===============================创建作业计划任务失败==========================");
                System.out.println(resp.getBody());
                Assert.assertTrue(false);
            }
            int count=1;
            while (count <1800) {
                JobStatusRespModel JobStatusResp=api.GetJobStatus(api.getURLEncoderString(JobName));
                if(JobStatusResp.getContent().size()==0) {
                    Thread.sleep(1000);
                } else {
                    System.out.println("====================作业运行状态："+JobStatusResp.getContent().get(0).getJobStatus()+"====================");
                    String status = JobStatusResp.getContent().get(0).getJobStatus();
                    if (status.equals("R") || status.equals("C")) {
                        Thread.sleep(1000);
                    } else if (status.equals("T") ) {
                        break;
                    } else {
                        Assert.assertTrue(false);
                    }
                    count++;
                }
            }
            if(count>=1800)
            {
                System.out.println("time out ");
                Assert.assertTrue(false);
            }
            JobLogRespModel joblog=api.GetJobLog(JobName);
            ArrayList list=new ArrayList();
            String desPathName=null;
            System.out.println(joblog.getContent());
            for(int a=0;a<joblog.getContent().size();a++)
            {
                if(!joblog.getContent().get(a).getStatus().equals("0"))
                {
                    System.out.println("BACK JOB ERROR "+JobName);
                    Assert.assertTrue(false);
                };
                desPathName=joblog.getContent().get(a).getDesPathName();
                list.add(joblog.getContent().get(a).getFileName());
            }

            Result res =api.GetDstPackFileSha256sum(dstsshcmd,desPathName);

            if(res.getCode()==0){

                System.out.println("get job resulterror  "+JobName);
            }
            if(desPathName==null) Assert.assertTrue(false);
            String dstfilesha256=res.getBody();

            System.out.println("=============================================================");
            res =api.GetSrcFileSha256sum(sshcmd,srcpath);
            String srcfilesha256=res.getBody();

            String[] srclist_tmp=srcfilesha256.replace("\r","").split("\n");
            String[] dstlist_tmp=dstfilesha256.replace("\r","").split("\n");

            for(int i=0;i<dstlist_tmp.length;i++)
            {
                dstlist_tmp[i]=dstlist_tmp[i].replace(desPathName,"");
            }

            List<String> srclist= Arrays.asList(srclist_tmp);
            List<String> dstlist= Arrays.asList(dstlist_tmp);
            System.out.println(desPathName);

            if(srclist.size()==dstlist.size()) {
                for( int i=0;i<srclist.size();i++)
                {
                    int src_pos_sha256=srclist.get(i).indexOf(" ");
                    String src_file_sha256=srclist.get(i).substring(0,src_pos_sha256);
                    String src_file=srclist.get(i).substring(src_pos_sha256+1).trim().trim();
                    if(!dstlist.contains(srclist.get(i)))
                    {
                        System.out.printf("src  file   get job result  error  %s\n",srclist.get(i).toString());

                        System.out.printf("dst  file   get job result  error  %s\n",dstlist.get(i).toString());

                    }

                }
            } else {
                Assert.assertTrue(false);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }




    }

    @Test
    public  void TestCase_CreateJobPlainNopack_RunOnce(){
        try {

            String s_iso88591 = newString(api.src_path.getBytes("UTF-8"),"ISO8859-1");
            String s_utf8 = newString(s_iso88591.getBytes("ISO8859-1"),"UTF-8");
            JobPlainStatusModel Model=new JobPlainStatusModel();
            String JobName="pds"+String.valueOf(System.currentTimeMillis());
            api.Restore_tmp_JobName=JobName;
            Model.setTaskName(JobName);
            Model.setTaskType(2);
            Model.setSourcePath(s_utf8);
            Model.setsourceIp(api.ssh_src_ip);
            Model.setTargetPath(api.dst_path);
            Model.setTargetIp(api.ssh_dst_ip);
            Model.setBurnStatus(null);
            Model.setScheduleType(0);
            Model.setDelSourceFile(null);
            Model.setSoftConnect(null);
            System.currentTimeMillis();
            Date d = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            Model.setTaskBeginTime(sdf.format(d));
            Model.setScheduleTypeReadOnly(false);
            Model.setFilterVisible(false);
            Model.setFileSizeUnit("MB");
            Model.setThreadNum(1);
            Model.setSmallBatchCommitNum(1);
            Model.setFileCheckType("2");
            Model.setBackupType(1);
            System.out.println(JSON.toJSONString(Model));
            Result resp=api.CreateJobPlain(JSON.toJSONString(Model));
            System.out.println(JSON.toJSONString(resp));
            int count=1;
            while (count <1800) {

                JobStatusRespModel JobStatusResp=api.GetJobStatus(JobName);
                Assert.assertTrue(JobStatusResp!=null);
                if(JobStatusResp.getContent().size()==0) {
                    Thread.sleep(1000);
                } else {
                    String status = JobStatusResp.getContent().get(0).getJobStatus();
                    if (status.equals("R") || status.equals("C")) {
                        Thread.sleep(1000);
                    } else if (status.equals("T") ) {
                        break;
                    } else {
                        Assert.assertTrue(false);
                    }
                }

                count++;

            }
            if(count>=1800)
            {
                System.out.println("time out ");
                Assert.assertTrue(false);
            }
            JobLogRespModel joblog=api.GetJobLog(JobName);
            ArrayList list=new ArrayList();
            String desPathName=null;
            System.out.println(joblog.getContent());
            for(int a=0;a<joblog.getContent().size();a++)
            {
                if(!joblog.getContent().get(a).getStatus().equals("0"))
                {
                    System.out.println("BACK JOB ERROR "+JobName);
                    Assert.assertTrue(false);
                };
                desPathName=joblog.getContent().get(a).getDesPathName();
                list.add(joblog.getContent().get(a).getFileName());
            }

            Result res =api.GetDstFileSha256sum(dstsshcmd,desPathName);

            String dstfilesha256=res.getBody();
            //System.out.println(dstfilesha256);
            System.out.println("=============================================================");
            res =api.GetSrcFileSha256sum(sshcmd,api.src_path);
            String srcfilesha256=res.getBody();
            //System.out.println(srcfilesha256);
            String[] srclist_tmp=srcfilesha256.replace("\r","").split("\n");
            String[] dstlist_tmp=dstfilesha256.replace("\r","").split("\n");
            for(int i=0;i<dstlist_tmp.length;i++)
            {
                dstlist_tmp[i]=dstlist_tmp[i].replace(desPathName,"");

            }
            List<String> srclist= Arrays.asList(srclist_tmp);
            List<String> dstlist= Arrays.asList(dstlist_tmp);
            if(srclist.size()==dstlist.size()) {
                for( int i=0;i<srclist.size();i++)
                {

                    if(!dstlist.contains(srclist.get(i)))
                    {
                        System.out.printf("src  file   get job result  error  %s\n",srclist.get(i).toString());
                        System.out.printf("dst  file   get job result  error  %s\n",dstlist.get(i).toString());
                    }

                }

            }
            else {

                Assert.assertTrue(false);
            }


        } catch (InterruptedException e) {

            e.printStackTrace();

        }catch (IOException e) {

            e.printStackTrace();
        }
    }

    @Test
    public  void TestCase_CreateJobPlainNopack_RunEveryDay(){
        try {
            JobPlainStatusModel Model=new JobPlainStatusModel();
            String JobName="pdstest"+String.valueOf(System.currentTimeMillis());
            Model.setTaskName(JobName);
            Model.setTaskType(2);
            Model.setSourcePath(api.src_path);
            Model.setsourceIp(api.ssh_src_ip);
            Model.setTargetPath(api.dst_path);
            Model.setTargetIp(api.ssh_dst_ip);
            Model.setScheduleType(1);
            System.currentTimeMillis();
            //Date d = new Date(System.currentTimeMillis()+60*1000);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            //String time_Date = sdf.format(d);
            Model.setTaskBeginTime(sdf.format(System.currentTimeMillis()+60*1000));
            Model.setScheduleTypeReadOnly(false);
            Model.setFilterVisible(false);
            Model.setIsPackaging("0");
            Model.setFileSizeUnit("MB");
            Model.setThreadNum(1);
            Model.setSmallBatchCommitNum(1);
            Model.setFileCheckType("2");
            Model.setBackupType(1);
            System.out.println(JSON.toJSONString(Model));
            Result resp=api.CreateJobPlain(JSON.toJSONString(Model));
            System.out.println(JSON.toJSONString(resp));
            int count=1;
            while (count <1800) {
                JobStatusRespModel JobStatusResp=api.GetJobStatus(JobName);
                if(JobStatusResp.getContent().size()==0) {
                    Thread.sleep(1000);
                }
                else {
                    String status = JobStatusResp.getContent().get(0).getJobStatus();
                    if (status.equals("R") || status.equals("C")) {
                        Thread.sleep(1000);
                    } else if (status.equals("T") ) {
                        break;
                    } else {
                        Assert.assertTrue(false);
                    }
                }

                count++;
            }
            if(count==1800) {
                Assert.assertTrue(false);
            }
            JobLogRespModel joblog=api.GetJobLog(JobName);
            ArrayList list=new ArrayList();
            String desPathName=null;
            System.out.println(joblog.getContent());
            for(int a=0;a<joblog.getContent().size();a++)
            {
                if(!joblog.getContent().get(a).getStatus().equals("0"))
                {
                    System.out.println("BACK JOB ERROR "+JobName);
                    Assert.assertTrue(false);
                };
                desPathName=joblog.getContent().get(a).getDesPathName();
                list.add(joblog.getContent().get(a).getFileName());
            }
            Result res =api.GetDstFileSha256sum(dstsshcmd,desPathName);
            if(res.getCode()==0){
                System.out.println("get job resulterror  "+JobName);
            }
            String dstfilesha256=res.getBody().toString();
            System.out.println(dstfilesha256);
            for(int a = 0; a < list.size(); a++) {

                if (!res.getBody().toString().contains(list.get(a).toString()))
                {
                    System.out.println("不包含此文件："+list.get(a).toString());
                    Assert.assertTrue(false);
                    return;
                }
            }
            System.out.println("=============================================================");
            res =api.GetSrcFileSha256sum(sshcmd,srcpath);
            String srcfilesha256=res.getBody();
            System.out.println(srcfilesha256);
            resp=api.GetJobPlainByName(JobName);
            JSONArray content = JSONObject.parseObject(resp.getBody()).getJSONArray("content");
            String id = content.getJSONObject(0).getString("id");
            api.DeleteJobPlain(id);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }







}
