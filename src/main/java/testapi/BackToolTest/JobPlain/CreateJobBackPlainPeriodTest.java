package testapi.BackToolTest.JobPlain;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.junit.*;
import testapi.common.JobLogRespModel;
import testapi.common.JobPlainStatusModel;
import testapi.common.JobStatusRespModel;
import testapi.common.Result;
import testapi.common.api.ApiTest;
import testapi.common.api.FileApi;
import testapi.common.api.SambaApi;
import testapi.framework.SSHExecutor;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static org.apache.commons.codec.binary.StringUtils.newString;


public class CreateJobBackPlainPeriodTest {
    static ApiTest api=new ApiTest();
    private static SSHExecutor sshcmd;
    String plainId="";
    private static SSHExecutor dstsshcmd;
    static  String medieId;
    static String  CliId;
    @BeforeClass
    public  static void beforeclass(){

        sshcmd=api.ssh_login(api.ssh_src_username,api.ssh_src_password, api.ssh_login_src_ip, api.ssh_src_port);
        sshcmd.setHost_type(api.ssh_src_type);
        dstsshcmd=api.ssh_login(api.ssh_dest_username,api.ssh_dest_password, api.ssh_dst_ip, api.ssh_dest_port);
        dstsshcmd.setHost_type(api.ssh_dst_type);
        medieId=api.Create_BackMedie(api.ssh_dst_ip,api.dst_path);
        CliId= api.Create_Client(api.ssh_src_ip);
    }

    @Before
    public  void beforetestcase(){




    }


    @AfterClass
    public static  void afterclass(){

    }





    @Test
    public  void TestCase_CreateJobPlainPack_RunOnce(){
        try {
            JobPlainStatusModel Model=new JobPlainStatusModel();
            String JobName="a"+String.valueOf(System.currentTimeMillis());
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
            Model.setDataSourceType("[\"普通文件\"]");
            Model.setMediumId(Integer.valueOf(medieId));
            Model.setClientId(Integer.valueOf(CliId));
            Result resp=api.CreateJobPlain(JSON.toJSONString(Model));
            if(resp.getCode()!=400){

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
            if(joblog==null){
                Thread.sleep(2000);
                joblog=api.GetJobLog(JobName);
            }

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
            Result res  =api.compile_the_result_whith_pack(sshcmd,dstsshcmd,desPathName, api.src_path, api.src_login_path);

            if(res.getCode()==400)
            {
                System.out.printf("作业：%s结果比较失败,失败原因:%s\r\n",JobName,res.getBody());
                Assert.assertTrue(resp.getCode()!=400);
            }
            resp = api.GetJobPlainByName(JobName);
            JSONArray content = JSONObject.parseObject(resp.getBody()).getJSONArray("content");
            String id = content.getJSONObject(0).getString("id");
            api.DeleteJobPlain(id);
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
            Model.setDataSourceType("[\"普通文件\"]");
            Model.setMediumId(Integer.valueOf(medieId));
            Model.setClientId(Integer.valueOf(CliId));
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
            Result res  =api.compile_the_result(sshcmd,dstsshcmd,desPathName, api.src_path, api.src_login_path);

            if(res.getCode()==400)
            {
                System.out.printf("作业：%s结果比较失败,失败原因:%s\r\n",JobName,res.getBody());
            }
            resp = api.GetJobPlainByName(JobName);
            JSONArray content = JSONObject.parseObject(resp.getBody()).getJSONArray("content");
            String id = content.getJSONObject(0).getString("id");
            api.DeleteJobPlain(id);

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
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            Model.setTaskBeginTime(sdf.format(System.currentTimeMillis()+60*1000));
            Model.setScheduleTypeReadOnly(false);
            Model.setFilterVisible(false);
            Model.setIsPackaging("0");
            Model.setFileSizeUnit("MB");
            Model.setThreadNum(1);
            Model.setSmallBatchCommitNum(1);
            Model.setFileCheckType("2");
            Model.setBackupType(1);
            Model.setDataSourceType("[\"普通文件\"]");
            Model.setMediumId(Integer.valueOf(medieId));
            Model.setClientId(Integer.valueOf(CliId));
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
            Result res  =api.compile_the_result(sshcmd,dstsshcmd,desPathName, api.src_path, api.src_login_path);
            if(res.getCode()==400)
            {
                System.out.printf("作业：%s结果比较失败,失败原因:%s\r\n",JobName,res.getBody());
            }
            resp = api.GetJobPlainByName(JobName);
            JSONArray content = JSONObject.parseObject(resp.getBody()).getJSONArray("content");
            String id = content.getJSONObject(0).getString("id");
            api.DeleteJobPlain(id);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public  void TestCase_CreateJobPlainPack_RunEveryDay(){
        try {
            JobPlainStatusModel Model=new JobPlainStatusModel();
            String JobName="pdstest"+String.valueOf(System.currentTimeMillis());
            Model.setTaskName(JobName);
            Model.setTaskType(2);
            Model.setSourcePath(api.src_path);
            Model.setsourceIp(api.ssh_src_ip);
            Model.setTargetPath(api.dst_path);
            Model.setTargetIp(api.ssh_dst_ip);
            Model.setBurnStatus(null);
            Model.setScheduleType(1);
            System.currentTimeMillis();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            Model.setTaskBeginTime(sdf.format(System.currentTimeMillis()+60*1000));
            Model.setScheduleTypeReadOnly(false);
            Model.setFilterVisible(false);
            Model.setIsPackaging("2");
            Model.setPackingLevel("0");
            Model.setPackingSize(1);
            Model.setPackingSizeUnit("MB");
            Model.setFileSizeUnit("MB");
            Model.setThreadNum(1);
            Model.setSmallBatchCommitNum(1);
            Model.setFileCheckType("2");
            Model.setBackupType(1);
            Model.setDataSourceType("[\"普通文件\"]");
            Model.setMediumId(Integer.valueOf(medieId));
            Model.setClientId(Integer.valueOf(CliId));
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
            if(desPathName==null) {
                Assert.assertTrue(false);
            }
            Result res  =api.compile_the_result_whith_pack(sshcmd,dstsshcmd,desPathName, api.src_path, api.src_login_path);
            if(res.getCode()==400)
            {
                System.out.printf("作业：%s结果比较失败,失败原因:%s\r\n",JobName,res.getBody());
            }
            resp = api.GetJobPlainByName(JobName);
            JSONArray content = JSONObject.parseObject(resp.getBody()).getJSONArray("content");
            String id = content.getJSONObject(0).getString("id");
            api.DeleteJobPlain(id);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Ignore
    public  void TestCase_CreateJobPlainRunEveryWeek(){
        try {

            JobPlainStatusModel Model=new JobPlainStatusModel();
            //ring  JobName= UUID.randomUUID().toString().replaceAll("-","");
            String JobName="pdstest"+String.valueOf(System.currentTimeMillis());
            Model.setTaskName(JobName);
            Model.setTaskType(2);
            Model.setSourcePath(api.src_path);
            Model.setsourceIp(api.ssh_src_ip);
            Model.setTargetPath(api.dst_path);
            Model.setTargetIp(api.ssh_dst_ip);
            Model.setBurnStatus(null);
            Model.setScheduleType(1);
            Model.setDayOfWeek("");
            Model.setMonth("");
            Model.setDay("");
            Model.setDelSourceFile(null);
            Model.setSoftConnect(null);
            System.currentTimeMillis();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            Model.setTaskBeginTime(sdf.format(System.currentTimeMillis()+60*1000));
            Model.setTimeOne("");
            Model.setTimeTwo("");
            Model.setFileName("");
            Model.setFileSuffixName("");
            Model.setFileSizeOne("");
            Model.setFileSizeTwo("");
            Model.setFilePathScope("");
            Model.setFilePath("");
            // Model.setFileOwnerScope("");
            Model.setScheduleTypeReadOnly(false);
            Model.setFileOwner("");
            Model.setFileSizeUnit("");
            Model.setFileSizeUnitTwo("");
            Model.setFilterVisible(false);
            Model.setIsPackaging("0");
            Model.setPackingLevel(null);
            Model.setPackingSize(0);
            Model.setFileSizeUnit("MB");
            Model.setThreadNum(1);
            Model.setSmallBatchCommitNum(1);
            Model.setFileCheckType("2");
            Model.setBackupType(1);
            Model.setDataSourceType("[\"普通文件\"]");
            Model.setMediumId(Integer.valueOf(medieId));
            Model.setClientId(Integer.valueOf(CliId));
            System.out.println(JSON.toJSONString(Model));
            Result resp=api.CreateJobPlain(JSON.toJSONString(Model));
            System.out.println(JSON.toJSONString(resp));
            int count=1;
            while (count <1800) {
                JobStatusRespModel JobStatusResp=api.GetJobStatus(JobName);
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
            // SSHExecutor ssh =  SSHExecutor.newInstance(api.ssh_dest_username,api.ssh_dest_password,api.ssh_dst_ip,Integer.valueOf(api.ssh_dest_port));
            System.out.println("================");
            long shell1 = 0;
            System.out.println("shell 1 执行了"+shell1+"ms");
            System.out.println("================");
            Result res = sshcmd.exec("find     "+desPathName+ " -type f"+"\n");
            if(res.getCode()==0){

                System.out.println("get job resulterror  "+JobName);
            }
            System.out.println(res.getBody());
            for(int a=0;a<list.size();a++)
            {
                if(!res.getBody().contains(list.get(a).toString())) {
                    Assert.assertTrue(false);
                }
            }
            //   res = sshcmd.exec("rm  -rf      "+desPathName+ " "+"\n");

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Ignore
    public  void TestCase_CreateJobPlainRunOnce2() throws IOException {
        ArrayList files=FileApi.listDirectory(new File("E:\\pandesong_项目\\测试数据"));
        for(int i=0;i<files.size();i++) {
            int pos1=files.get(i).toString().indexOf("E:\\pandesong_项目\\测试数据");
            String pos2=files.get(i).toString().substring("E:\\pandesong_项目\\测试数据".length());
            System.out.println(pos2.replace("\\","/"));
            SambaApi.Put(api.ssh_src_ip, "syl", "123456", "/countbug"+pos2.replace("\\","/"), files.get(i).toString());
        }
    }




    @Ignore
    public  void TestCase_CreateJobPlainRunOnce4() {


    }

    @Ignore
    public  void TestCase_CreateJobPlainRunOnce5() {

        for(int a=100;a<10000000;a++) {
            Result resp = api.GetJobList(String.valueOf(a), "100");
            System.out.println(String.valueOf(a));
            JSONArray content = JSONObject.parseObject(resp.getBody()).getJSONArray("content");
            for (int i = 0; i < content.size(); i++) {
                String id = content.getJSONObject(i).getString("id");
                String status = content.getJSONObject(i).getString("jobStatus");
                //System.out.println(content.getJSONObject(i).toString());
                if (status.contains("R")) {
                    //System.out.println(content.getJSONObject(i).toString());
                    resp=api.StopJob(content.getJSONObject(i).toString());
                    System.out.println(resp.getCode());
                }
                // Thread.sleep(3000);
                // System.out.println(resp.getCode());

            }

        }

    }


    @Ignore
    public  void TestCase_Delete_JobPlain() {

        while (true) {
            Result resp = api.GetJobPlainList();
            System.out.println(resp.getBody());
            JSONArray content = JSONObject.parseObject(resp.getBody()).getJSONArray("content");
            System.out.println(content);
            for (int i = 0; i < content.size(); i++) {
                String id = content.getJSONObject(i).getString("id");
                api.DeleteJobPlain(id);
                System.out.println(resp.getCode());
            }
        }

    }





}
