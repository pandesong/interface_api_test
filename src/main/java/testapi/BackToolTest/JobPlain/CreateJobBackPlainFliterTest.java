package testapi.BackToolTest.JobPlain;

import com.alibaba.fastjson.JSON;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.*;
import org.junit.runner.RunWith;
import testapi.common.*;
import testapi.common.api.ApiTest;
import testapi.framework.SSHExecutor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RunWith(DataProviderRunner.class)
public class CreateJobBackPlainFliterTest {
    static  ApiTest  api=new ApiTest();
    static  String srcpath=api.src_path+"/filter";
    static SSHExecutor sshcmd=null;
    static SSHExecutor dstsshcmd=null;
    String tmp="";
    static  String medieId;
    static String  CliId;
    @BeforeClass
    public  static void beforeclass(){

        sshcmd=api.ssh_login(api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port);

        sshcmd.setHost_type(api.ssh_src_type);
        dstsshcmd=api.ssh_login(api.ssh_dest_username,api.ssh_dest_password, api.ssh_dst_ip, api.ssh_dest_port);
        dstsshcmd.setHost_type(api.ssh_dst_type);
        if(api.ssh_src_type.equals("windows")){
            srcpath= srcpath.replace("/","\\");
        }
        medieId=api.Create_BackMedie(api.ssh_dst_ip,api.dst_path);
        CliId= api.Create_Client(api.ssh_src_ip);
    }

    @AfterClass
    public static void afterclass(){

    }

    @DataProvider
    public static Object[][] dataProviderFilterStartWith() {
        return new Object[][]{
                {"1", "123"},
                {"1.", "1.abd"},
                {"1..", "1.."},
                {"1..", "1.."},
                {"a","a1.1.1"},
                {"!","!1.1"}
                ,{"&text","&text123456"},
                {"@","@a"},
                {"#ab1","#ab11"},
                {"*","*1"},
                {"-n","-n=="},
                //{"\"","\"-n=="},
                {"^","^123"},{"--","--1"}
        };
    }

    @DataProvider
    public static Object[][] dataProviderFilterEndWith() {
        return new Object[][]{
                {"1", "1231"}, {"1.", "1.abd1."}, {"1..", "a1.."},{"1..", "*1.."},{"a1","a1.1.1a1"},{"!","!1.1!"}
                ,{"&text","&text123456&"},{"@","@a@"},{"#ab1","#ab11#ab1"},{"*","*1*"},{"-n==","111-n=="},{"--","--365"}
        };
    }

    @Test
    public  void TestCase_CreateJobPlainNopack_RunOnce_filter_By_fileCreateTime(){
        try {
            JobPlainStatusModel Model=new JobPlainStatusModel();
            String JobName="filter_By_fileCreateTime"+String.valueOf(System.currentTimeMillis());
            Model.setTaskName(JobName);
            Model.setTaskType(2);
            Model.setSourcePath(api.src_path);
            Model.setsourceIp(api.ssh_src_ip);
            Model.setTargetPath(api.dst_path);
            Model.setTargetIp(api.ssh_dst_ip);
            Model.setScheduleType(0);
            System.currentTimeMillis();
            Date d = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            Model.setTaskBeginTime(sdf.format(d));
            Model.setScheduleTypeReadOnly(false);
            Model.setFilterVisible(false);
            Model.setFileSizeUnit("MB");
            Model.setThreadNum(1);
            Model.setDataSourceType("[\"普通文件\"]");
            Model.setMediumId(Integer.valueOf(medieId));
            Model.setClientId(Integer.valueOf(CliId));
            api.ssh_exec(sshcmd,"mkdir  -p   "+srcpath);
            String filename="no_back_file_1";
            Result res =api.Create_file_login(sshcmd,srcpath,"1M","1",filename);
            long currenttime=System.currentTimeMillis();
            Model.setTimeOne(sdf.format(currenttime-60*1000));
            Thread.sleep(120*1000);
            System.out.println(srcpath);
            res =api.Create_file_login(sshcmd,srcpath,"1M","1","back_file");
            Model.setTimeTwo(sdf.format(currenttime+180*1000));
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
                } else {
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
            if(desPathName==null) {
                Assert.assertTrue(false);
            }
            res =api.GetDstFileSha256sum(dstsshcmd,desPathName);
            String dstfilesha256=res.getBody();
            System.out.println("=============================================================");
            res =api.GetSrcFileSha256sum(sshcmd,srcpath);
            String srcfilesha256=res.getBody();
            String[] srclist_tmp=srcfilesha256.replace("\r","").split("\n");
            String[] dstlist_tmp=dstfilesha256.replace("\r","").split("\n");
            for(int i=0;i<dstlist_tmp.length;i++)
            {
                if(api.ssh_src_type.equals("windows")){
                    desPathName=desPathName+"/".replace("//","/");
                }
                dstlist_tmp[i]=dstlist_tmp[i].replace(desPathName,"");
                System.out.println(dstlist_tmp[i]);
            }
            List<String> srclist= Arrays.asList(srclist_tmp);
            List<String> dstlist= Arrays.asList(dstlist_tmp);
            Assert.assertTrue(dstlist.size()==1);
            if(srclist.size()==dstlist.size()+1) {
                for( int i=0;i<srclist.size();i++)
                {
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
            Assert.assertTrue(false);
        }finally {
            api.ssh_exec(sshcmd,"rm  -rf    "+srcpath);
        }

    }

    @Test
    public  void TestCase_CreateJobPlainNopack_RunOnce_filter_By_filesize_LeftBigerByte00_RightSmallerByte0(){
        try {
            JobPlainStatusModel Model=new JobPlainStatusModel();
            String JobName="pds"+String.valueOf(System.currentTimeMillis());
            Model.setTaskName(JobName);
            Model.setTaskType(2);
            Model.setSourcePath(srcpath);
            Model.setsourceIp(api.ssh_src_ip);
            Model.setTargetPath(api.dst_path);
            Model.setTargetIp(api.ssh_dst_ip);
            Model.setScheduleType(0);
            Model.setFileSizeScopeOne(2);
            Model.setFileSizeScopeTwo(2);
            Model.setFileSizeUnit("B");
            Model.setFileSizeUnitTwo("B");
            Model.setFileSizeOne("0.000");
            Model.setFileSizeTwo("0.0");
            System.currentTimeMillis();
            Date d = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            Model.setTaskBeginTime(sdf.format(d));
            Model.setScheduleTypeReadOnly(false);
            Model.setFilterVisible(false);
            Model.setFileSizeUnit("B");
            Model.setThreadNum(1);
            api.ssh_exec_cmd(api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port,"mkdir  -p   "+srcpath);
            String filename="no_back_file_1";
            //api.ssh_exec_cmd(api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port,"touch    "+srcpath+"/no_back_file_1");
            Result   res =api.Create_file_login(sshcmd,srcpath,"0","1","no_back_file_1");
            res =api.Create_file(srcpath,api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port,"10M","1","back_file");
            res =api.Create_file(srcpath,api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port,"10M","1","no_back_file_2");
            Model.setSmallBatchCommitNum(1);
            Model.setFileCheckType("2");
            Model.setBackupType(1);
            System.out.println(JSON.toJSONString(Model));
            Result resp=api.CreateJobPlain(JSON.toJSONString(Model));
            JobPlainResponseModel  JobPlain=JSON.parseObject(resp.getBody(),JobPlainResponseModel.class);
            System.out.println(JSON.toJSONString(resp));
            int count=1;
            while (count <1800) {
                JobStatusRespModel JobStatusResp=api.GetJobStatus(JobName);
                System.out.println(JSON.toJSONString(resp));
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
                    count++;
                }
            }
            if(count>=1800)
            {
                System.out.println("time out ");
                Assert.assertTrue(false);
            }
            JobLogRespModel JobLogResp =api.GetJobLog(JobName);
            System.out.println(resp.getBody());
            ArrayList list=new ArrayList();
            String desPathName=null;
            Assert.assertTrue(JobLogResp.getContent().size()!=0);
            for(int a=0;a<JobLogResp.getContent().size();a++)
            {
                if(!JobLogResp.getContent().get(a).getStatus().toString().equals("0"))
                {
                    System.out.println("BACK JOB ERROR "+JobName);
                    Assert.assertTrue(false);
                };
                desPathName=JobLogResp.getContent().get(a).getDesPathName();
                list.add(JobLogResp.getContent().get(a).getFileName());
            }
            res =api.GetDstFileSha256sum(dstsshcmd,desPathName);
            String dstfilesha256=res.getBody();
            System.out.println("=============================================================");
            res =api.GetSrcFileSha256sum(sshcmd,srcpath);
            String srcfilesha256=res.getBody();
            String[] srclist_tmp=srcfilesha256.replace("\r","").split("\n");
            String[] dstlist_tmp=dstfilesha256.replace("\r","").split("\n");
            for(int i=0;i<dstlist_tmp.length;i++)
            {
                if(api.ssh_src_type.equals("windows")){
                    desPathName=desPathName+"/".replace("//","/");
                }
                dstlist_tmp[i]=dstlist_tmp[i].replace(desPathName,"");
            }
            List<String> srclist= Arrays.asList(srclist_tmp);
            List<String> dstlist= Arrays.asList(dstlist_tmp);
            System.out.println(srclist);
            System.out.println(dstlist);
            Assert.assertTrue(srclist.contains(dstlist.get(0).replace("  "," "))&&dstlist.size()==1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            api.ssh_exec_cmd(api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port,"rm  -rf    "+srcpath);
        }

    }
    @Test
    public  void TestCase_CreateJobPlainNopack_RunOnce_filter_By_filesize_LeftBigByte00_RightSmallerByte0(){
        try {
            JobPlainStatusModel Model=new JobPlainStatusModel();
            String JobName="pds"+String.valueOf(System.currentTimeMillis());
            Model.setTaskName(JobName);
            Model.setTaskType(2);
            Model.setSourcePath(srcpath);
            Model.setsourceIp(api.ssh_src_ip);
            Model.setTargetPath(api.dst_path);
            Model.setTargetIp(api.ssh_dst_ip);
            Model.setScheduleType(0);
            Model.setFileSizeScopeOne(1);
            Model.setFileSizeScopeTwo(2);
            Model.setFileSizeUnit("B");
            Model.setFileSizeUnitTwo("B");
            Model.setFileSizeOne("0.000");
            Model.setFileSizeTwo("0.0");
            System.currentTimeMillis();
            Date d = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            Model.setTaskBeginTime(sdf.format(d));
            Model.setScheduleTypeReadOnly(false);
            Model.setFilterVisible(false);
            Model.setFileSizeUnit("B");
            Model.setThreadNum(1);
            api.ssh_exec_cmd(api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port,"mkdir  -p   "+srcpath);
            String filename="no_back_file_1";
            api.ssh_exec_cmd(api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port,"touch    "+srcpath+"/no_back_file_1");
            long currenttime=System.currentTimeMillis();
            Model.setTimeOne(sdf.format(currenttime+30*1000));
            Thread.sleep(30*1000);
            Result res =api.Create_file(srcpath,api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port,"10M","1","back_file");
            res =api.Create_file(srcpath,api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port,"10M","1","no_back_file_2");
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
                } else {
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

            JobLogRespModel  joblog=api.GetJobLog(JobName);
            String desPathName=null;
            Assert.assertTrue(joblog.getContent().size()==0);
            System.out.println(joblog.getContent());

        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {

            api.ssh_exec_cmd(api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port,"rm  -rf    "+srcpath);
        }

    }
    @Ignore("后台不校验")
    public  void TestCase_CreateJobPlainNopack_RunOnce_filter_By_filesize_LeftBigByte0_RitheSmallByte0(){
        try {
            JobPlainStatusModel Model=new JobPlainStatusModel();
            String JobName="pds"+String.valueOf(System.currentTimeMillis());
            Model.setTaskName(JobName);
            Model.setTaskType(2);
            Model.setSourcePath(srcpath);
            Model.setsourceIp(api.ssh_src_ip);
            Model.setTargetPath(api.dst_path);
            Model.setTargetIp(api.ssh_dst_ip);
            Model.setScheduleType(0);
            Model.setFileSizeScopeOne(1);
            Model.setFileSizeScopeTwo(1);
            Model.setFileSizeOne("0.000");
            Model.setFileSizeTwo("0.0");
            Model.setFileSizeUnit("B");
            Model.setThreadNum(1);
            Model.setFileSizeUnitTwo("B");

            System.currentTimeMillis();
            Date d = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            Model.setTaskBeginTime(sdf.format(d));
            Model.setScheduleTypeReadOnly(false);
            Model.setFilterVisible(false);

            api.ssh_exec_cmd(api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port,"mkdir  -p   "+srcpath);
            String filename="no_back_file_1";

            Result res =api.Create_file(srcpath,api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port,"1","1",filename);
            api.ssh_exec_cmd(api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port,"touch   "+srcpath+"/zerofile");
            res =api.Create_file(srcpath,api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port,"10M","1","no_back_file_2");


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
            res =api.GetDstFileSha256sum(dstsshcmd,desPathName);
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
            Assert.assertTrue(dstlist.size()==0);


        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {

            api.ssh_exec_cmd(api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port,"rm  -rf    "+srcpath);

        }

    }

    @Test
    public  void TestCase_CreateJobPlainNopack_RunOnce_filter_By_filesize_LeftBigerByte0_RightSmallerByte1(){
        try {

            JobPlainStatusModel Model=new JobPlainStatusModel();
            String JobName="pds"+String.valueOf(System.currentTimeMillis());
            Model.setTaskName(JobName);
            Model.setTaskType(2);
            Model.setSourcePath(srcpath);
            Model.setsourceIp(api.ssh_src_ip);
            Model.setTargetPath(api.dst_path);
            Model.setTargetIp(api.ssh_dst_ip);
            Model.setScheduleType(0);
            Model.setFileSizeScopeOne(1);
            Model.setFileSizeScopeTwo(2);
            Model.setFileSizeOne("0.000");
            Model.setFileSizeTwo("1");
            System.currentTimeMillis();
            Date d = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            Model.setTaskBeginTime(sdf.format(d));
            Model.setScheduleTypeReadOnly(false);
            Model.setFilterVisible(false);
            Model.setFileSizeUnit("B");
            Model.setFileSizeUnitTwo("B");
            Model.setThreadNum(1);

            api.ssh_exec_cmd(api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port,"mkdir  -p   "+srcpath);

            String filename="no_back_file_1";
            Result res =api.Create_file(srcpath,api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port,"1","1",filename);
            long currenttime=System.currentTimeMillis();
            res =api.Create_file(srcpath,api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port,"10M","1","back_file");
            res =api.Create_file(srcpath,api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port,"10M","1","no_back_file_2");

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
            res =api.GetDstFileSha256sum(dstsshcmd,desPathName);
            String dstfilesha256=res.getBody();


            System.out.println("=============================================================");
            res =api.GetSrcFileSha256sum(sshcmd,srcpath);
            String srcfilesha256=res.getBody();
            String[] srclist_tmp=srcfilesha256.replace("\r","").split("\n");
            String[] dstlist_tmp=dstfilesha256.replace("\r","").split("\n");

            for(int i=0;i<dstlist_tmp.length;i++)
            {
                if(api.ssh_src_type.equals("windows")){
                    desPathName=desPathName+"/".replace("//","/");
                }
                dstlist_tmp[i]=dstlist_tmp[i].replace(desPathName,"");

            }

            List<String> srclist= Arrays.asList(srclist_tmp);
            List<String> dstlist= Arrays.asList(dstlist_tmp);
            System.out.println(srclist);
            System.out.println(dstlist);
            Assert.assertTrue(dstlist.size()==1&&srclist.contains(dstlist.get(0)));

        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {

            api.ssh_exec_cmd(api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port,"rm  -rf    "+srcpath);

        }

    }
    @Test
    public  void TestCase_CreateJobPlainNopack_RunOnce_filter_By_filesize_LeftBigerByte0_RightSmallerM1(){
        try {

            JobPlainStatusModel Model=new JobPlainStatusModel();
            String JobName="pds"+String.valueOf(System.currentTimeMillis());
            Model.setTaskName(JobName);
            Model.setTaskType(2);
            Model.setSourcePath(srcpath);
            Model.setsourceIp(api.ssh_src_ip);
            Model.setTargetPath(api.dst_path);
            Model.setTargetIp(api.ssh_dst_ip);
            Model.setScheduleType(0);
            Model.setFileSizeScopeOne(1);
            Model.setFileSizeScopeTwo(2);
            Model.setFileSizeOne("1.0");
            Model.setFileSizeTwo("1");
            System.currentTimeMillis();
            Date d = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            Model.setTaskBeginTime(sdf.format(d));
            Model.setScheduleTypeReadOnly(false);
            Model.setFilterVisible(false);
            Model.setFileSizeUnit("B");
            Model.setFileSizeUnitTwo("MB");

            Model.setThreadNum(1);

            api.ssh_exec(sshcmd,"mkdir  -p   "+srcpath);

            String filename="no_back_file_1";
            Result res =api.Create_file(srcpath,api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port,"1","1",filename);
            long currenttime=System.currentTimeMillis();
            res =api.Create_file_login(sshcmd,srcpath,"1M","1","back_file");
            res =api.Create_file_login(sshcmd,srcpath,"10M","1","no_back_file_2");
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
                } else {
                    String status = JobStatusResp.getContent().get(0).getJobStatus();
                    if (status.equals("R") || status.equals("C")) {
                        Thread.sleep(1000);
                    } else if (status.equals("T") ) break;
                    else Assert.assertTrue(false);
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
            res =api.GetDstFileSha256sum(dstsshcmd,desPathName);
            String dstfilesha256=res.getBody();


            System.out.println("=============================================================");
            res =api.GetSrcFileSha256sum(sshcmd,srcpath);
            String srcfilesha256=res.getBody();
            String[] srclist_tmp=srcfilesha256.replace("\r","").split("\n");
            String[] dstlist_tmp=dstfilesha256.replace("\r","").split("\n");
            for(int i=0;i<dstlist_tmp.length;i++)
            {
                if(api.ssh_src_type.equals("windows")){
                    desPathName=desPathName+"/".replace("//","/");
                }
                dstlist_tmp[i]=dstlist_tmp[i].replace(desPathName,"");

            }
            List<String> srclist= Arrays.asList(srclist_tmp);
            List<String> dstlist= Arrays.asList(dstlist_tmp);

            System.out.println(srclist);
            System.out.println(dstlist);

            Assert.assertTrue(dstlist.size()==1&&srclist.contains(dstlist.get(0)));

        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {

            api.ssh_exec(sshcmd,"rm  -rf    "+srcpath);

        }

    }


    @Test
    public  void TestCase_CreateJobPlainNopack_RunOnce_filter_By_filesize_LeftBigerByte0_RightSmallerG1(){
        try {
            JobPlainStatusModel Model=new JobPlainStatusModel();
            String JobName="pds"+String.valueOf(System.currentTimeMillis());
            Model.setTaskName(JobName);
            Model.setTaskType(2);
            Model.setSourcePath(srcpath);
            Model.setsourceIp(api.ssh_src_ip);
            Model.setTargetPath(api.dst_path);
            Model.setTargetIp(api.ssh_dst_ip);
            Model.setScheduleType(0);
            Model.setFileSizeScopeOne(1);
            Model.setFileSizeScopeTwo(2);
            Model.setFileSizeOne("1.0");
            Model.setFileSizeTwo("1");
            System.currentTimeMillis();
            Date d = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            Model.setTaskBeginTime(sdf.format(d));
            Model.setScheduleTypeReadOnly(false);
            Model.setFilterVisible(false);
            Model.setFileSizeUnit("B");
            Model.setFileSizeUnitTwo("GB");
            Model.setThreadNum(1);
            api.ssh_exec(sshcmd,"mkdir  -p   "+srcpath);
            String filename="no_back_file_1";
            Result res =api.Create_file(srcpath,api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port,"1","1",filename);
            long currenttime=System.currentTimeMillis();
            res =api.Create_file_login(sshcmd,srcpath,"900M","1","back_file_09");
            res =api.Create_file_login(sshcmd,srcpath,"1G","1","back_file");
            res =api.Create_file_login(sshcmd,srcpath,"1100M","1","no_back_file_2");
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
            res =api.GetDstFileSha256sum(dstsshcmd,desPathName);
            String dstfilesha256=res.getBody();

            System.out.println("=============================================================");
            res =api.GetSrcFileSha256sum(sshcmd,srcpath);
            String srcfilesha256=res.getBody();
            String[] srclist_tmp=srcfilesha256.replace("\r","").split("\n");
            String[] dstlist_tmp=dstfilesha256.replace("\r","").split("\n");
            for(int i=0;i<dstlist_tmp.length;i++)
            {
                if(api.ssh_src_type.equals("windows")){
                    desPathName=desPathName+"/".replace("//","/");
                }
                dstlist_tmp[i]=dstlist_tmp[i].replace(desPathName,"");

            }
            List<String> srclist= Arrays.asList(srclist_tmp);
            List<String> dstlist= Arrays.asList(dstlist_tmp);
            System.out.println(srclist);
            System.out.println(dstlist);
            Assert.assertTrue(dstlist.size()==2&&srclist.contains(dstlist.get(0))&&srclist.contains(dstlist.get(1)));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {

            api.ssh_exec(sshcmd,"rm  -rf    "+srcpath);

        }

    }
    @Test
    public  void TestCase_CreateJobPlainNopack_RunOnce_filter_By_filesize_LeftBigerByte0_RightSmallerT1(){
        try {
            JobPlainStatusModel Model=new JobPlainStatusModel();
            String JobName="pds"+String.valueOf(System.currentTimeMillis());
            Model.setTaskName(JobName);
            Model.setTaskType(2);
            Model.setSourcePath(srcpath);
            Model.setsourceIp(api.ssh_src_ip);
            Model.setTargetPath(api.dst_path);
            Model.setTargetIp(api.ssh_dst_ip);
            Model.setScheduleType(0);
            Model.setFileSizeScopeOne(1);
            Model.setFileSizeScopeTwo(2);
            Model.setFileSizeOne("1.0");
            Model.setFileSizeTwo("1");
            System.currentTimeMillis();
            Date d = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            Model.setTaskBeginTime(sdf.format(d));
            Model.setScheduleTypeReadOnly(false);
            Model.setFilterVisible(false);
            Model.setFileSizeUnit("B");
            Model.setFileSizeUnitTwo("TB");
            Model.setThreadNum(1);
            sshcmd.exec("mkdir  -p   "+srcpath);
            String filename="no_back_file_1";
            Result res =api.Create_file(srcpath,api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port,"1","1",filename);
            long currenttime=System.currentTimeMillis();
            res =api.Create_file_login(sshcmd,srcpath,"900M","1","back_file_09");
            res =api.Create_file_login(sshcmd,srcpath,"1G","1","back_file");
            res =api.Create_file_login(sshcmd,srcpath,"1500M","1","no_back_file_2");
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
            res =api.GetDstFileSha256sum(dstsshcmd,desPathName);
            String dstfilesha256=res.getBody();
            System.out.println("=============================================================");
            res =api.GetSrcFileSha256sum(sshcmd,srcpath);
            String srcfilesha256=res.getBody();
            String[] srclist_tmp=srcfilesha256.replace("\r","").split("\n");
            String[] dstlist_tmp=dstfilesha256.replace("\r","").split("\n");
            for(int i=0;i<dstlist_tmp.length;i++)
            {
                if(api.ssh_src_type.equals("windows")){
                    desPathName=desPathName+"/".replace("//","/");
                }
                dstlist_tmp[i]=dstlist_tmp[i].replace(desPathName,"");
            }
            List<String> srclist= Arrays.asList(srclist_tmp);
            List<String> dstlist= Arrays.asList(dstlist_tmp);
            System.out.println(srclist);
            System.out.println(dstlist);
            Assert.assertTrue(dstlist.size()==3&&srclist.contains(dstlist.get(0)));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            sshcmd.exec("rm  -rf    "+srcpath);
        }
    }

    @Test
    public  void TestCase_CreateJobPlainNopack_RunOnce_filter_By_filesize_LeftBigerByte1024_RightSmallK1(){
        try {
            JobPlainStatusModel Model=new JobPlainStatusModel();
            String JobName="pds"+String.valueOf(System.currentTimeMillis());
            Model.setTaskName(JobName);
            Model.setTaskType(2);
            Model.setSourcePath(srcpath);
            Model.setsourceIp(api.ssh_src_ip);
            Model.setTargetPath(api.dst_path);
            Model.setTargetIp(api.ssh_dst_ip);
            Model.setScheduleType(0);
            Model.setFileSizeScopeOne(1);
            Model.setFileSizeScopeTwo(1);
            Model.setFileSizeOne("1024");
            Model.setFileSizeTwo("1");
            System.currentTimeMillis();
            Date d = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            Model.setTaskBeginTime(sdf.format(d));
            Model.setScheduleTypeReadOnly(false);
            Model.setFilterVisible(false);
            Model.setFileSizeUnit("KB");
            Model.setFileSizeUnitTwo("MB");
            Model.setThreadNum(1);
            sshcmd.exec("mkdir  -p   "+srcpath);
            String filename="no_back_file_1";
            Result res =api.Create_file(srcpath,api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port,"1","1",filename);
            long currenttime=System.currentTimeMillis();
            res =api.Create_file_login(sshcmd,srcpath,"1K","1","back_file_09");
            res =api.Create_file_login(sshcmd,srcpath,"1B","1","back_file");
            res =api.Create_file_login(sshcmd,srcpath,"2K","1","no_back_file_2");
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
            res =api.GetDstFileSha256sum(dstsshcmd,desPathName);
            String dstfilesha256=res.getBody();
            System.out.println("=============================================================");
            res =api.GetSrcFileSha256sum(sshcmd,srcpath);
            String srcfilesha256=res.getBody();
            String[] srclist_tmp=srcfilesha256.replace("\r","").split("\n");
            String[] dstlist_tmp=dstfilesha256.replace("\r","").split("\n");
            for(int i=0;i<dstlist_tmp.length&&desPathName!=null;i++)
            {
                if(api.ssh_src_type.equals("windows")){
                    desPathName=desPathName+"/".replace("//","/");
                }
                dstlist_tmp[i]=dstlist_tmp[i].replace(desPathName,"");
            }
            List<String> srclist= Arrays.asList(srclist_tmp);
            List<String> dstlist= Arrays.asList(dstlist_tmp);
            System.out.println(srclist);
            System.out.println(dstlist);
            Assert.assertTrue(dstlist.get(0).toString().length()==0);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        }finally {
            sshcmd.exec("rm  -rf    "+srcpath);
        }
    }

    @Test
    public  void TestCase_CreateJobPlainNopack_RunOnce_filter_By_filesize_LeftBigerM0_RightSmallerM1(){
        try {
            JobPlainStatusModel Model=new JobPlainStatusModel();
            String JobName="pds"+String.valueOf(System.currentTimeMillis());
            Model.setTaskName(JobName);
            Model.setTaskType(2);
            Model.setSourcePath(srcpath);
            Model.setsourceIp(api.ssh_src_ip);
            Model.setTargetPath(api.dst_path);
            Model.setTargetIp(api.ssh_dst_ip);
            Model.setScheduleType(0);
            Model.setFileSizeScopeOne(2);//大于等于1M
            Model.setFileSizeScopeTwo(1);//小于等于
            Model.setFileSizeOne("0");//1M
            Model.setFileSizeTwo("1.0");
            System.currentTimeMillis();
            Date d = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            Model.setTaskBeginTime(sdf.format(d));
            Model.setScheduleTypeReadOnly(false);
            Model.setFilterVisible(false);
            Model.setFileSizeUnit("MB");
            Model.setFileSizeUnitTwo("MB");
            Model.setThreadNum(1);
            sshcmd.exec("mkdir  -p   "+srcpath);
            String filename="no_back_file_1";
            Result res =api.Create_file(srcpath,api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port,"1","1",filename);
            long currenttime=System.currentTimeMillis();
            res =api.Create_file_login(sshcmd,srcpath,"1M","1","back_file_09");
            res =api.Create_file_login(sshcmd,srcpath,"1G","1","back_file");
            res =api.Create_file_login(sshcmd,srcpath,"1500M","1","no_back_file_2");
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
            res =api.GetDstFileSha256sum(dstsshcmd,desPathName);
            String dstfilesha256=res.getBody();
            System.out.println("=============================================================");
            res =api.GetSrcFileSha256sum(sshcmd,srcpath);
            String srcfilesha256=res.getBody();
            String[] srclist_tmp=srcfilesha256.replace("\r","").split("\n");
            String[] dstlist_tmp=dstfilesha256.replace("\r","").split("\n");
            for(int i=0;i<dstlist_tmp.length;i++)
            {
                if(api.ssh_src_type.equals("windows")){
                    desPathName=desPathName+"/".replace("//","/");
                }
                dstlist_tmp[i]=dstlist_tmp[i].replace(desPathName,"");
            }
            List<String> srclist= Arrays.asList(srclist_tmp);
            List<String> dstlist= Arrays.asList(dstlist_tmp);
            System.out.println(srclist);
            System.out.println(dstlist);
            Assert.assertTrue(dstlist.size()==1&&srclist.contains(dstlist.get(0)));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            sshcmd.exec("rm  -rf    "+srcpath);
        }

    }

    @Test
    public  void TestCase_CreateJobPlainNopack_RunOnce_filter_By_filesize_LeftBigerK0_RightSmallerK1(){
        try {
            JobPlainStatusModel Model=new JobPlainStatusModel();
            String JobName="pds"+String.valueOf(System.currentTimeMillis());
            Model.setTaskName(JobName);
            Model.setTaskType(2);
            Model.setSourcePath(srcpath);
            Model.setsourceIp(api.ssh_src_ip);
            Model.setTargetPath(api.dst_path);
            Model.setTargetIp(api.ssh_dst_ip);
            Model.setScheduleType(0);
            Model.setFileSizeScopeOne(2);//大于等于1M
            Model.setFileSizeScopeTwo(2);//小于等于
            Model.setFileSizeOne("0");//1M
            Model.setFileSizeTwo("1.0");
            System.currentTimeMillis();
            Date d = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            Model.setTaskBeginTime(sdf.format(d));
            Model.setScheduleTypeReadOnly(false);
            Model.setFilterVisible(false);
            Model.setFileSizeUnit("KB");
            Model.setFileSizeUnitTwo("KB");

            Model.setThreadNum(1);

            sshcmd.exec("mkdir  -p   "+srcpath);


            long currenttime=System.currentTimeMillis();
            Result   res =api.Create_file(srcpath,api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port,"1K","1","back_file_09");
            res =api.Create_file_login(sshcmd,srcpath,"1B","1","back_file");
            res =api.Create_file_login(sshcmd,srcpath,"2K","1","back_file1");
            res =api.Create_file_login(sshcmd,srcpath,"1500M","1","no_back_file_2");

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
            res =api.GetDstFileSha256sum(dstsshcmd,desPathName);
            String dstfilesha256=res.getBody();


            System.out.println("=============================================================");
            res =api.GetSrcFileSha256sum(sshcmd,srcpath);
            String srcfilesha256=res.getBody();
            String[] srclist_tmp=srcfilesha256.replace("\r","").split("\n");
            String[] dstlist_tmp=dstfilesha256.replace("\r","").split("\n");
            for(int i=0;i<dstlist_tmp.length;i++)
            {
                if(api.ssh_src_type.equals("windows")){
                    desPathName=desPathName+"/".replace("//","/");
                }
                dstlist_tmp[i]=dstlist_tmp[i].replace(desPathName,"");

            }
            List<String> srclist= Arrays.asList(srclist_tmp);
            List<String> dstlist= Arrays.asList(dstlist_tmp);
            System.out.println(srclist);
            System.out.println(dstlist);
            Assert.assertTrue(dstlist.size()==2);
            for(int i=0;i<dstlist.size();i++){
                System.out.println("判断当前文件目的路径下的文件是否把源文件都拷贝过去了 "+dstlist.get(i).toString());
                Assert.assertTrue(srclist.contains(dstlist.get(i)));
                //Assert.assertTrue(dstlist.get(i).toString().contains("abcd"));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {

            sshcmd.exec("rm  -rf    "+srcpath);
        }
    }

    @Test
    public  void TestCase_CreateJobPlainNopack_RunOnce_filter_By_filesize_LeftBigerK1_RightSmallerM1(){
        try {
            JobPlainStatusModel Model=new JobPlainStatusModel();
            String JobName="pds"+String.valueOf(System.currentTimeMillis());
            Model.setTaskName(JobName);
            Model.setTaskType(2);
            Model.setSourcePath(srcpath);
            Model.setsourceIp(api.ssh_src_ip);
            Model.setTargetPath(api.dst_path);
            Model.setTargetIp(api.ssh_dst_ip);
            Model.setScheduleType(0);
            Model.setFileSizeScopeOne(2);//大于等于1M
            Model.setFileSizeScopeTwo(2);//小于等于
            Model.setFileSizeOne("1");//1M
            Model.setFileSizeTwo("1.0");
            System.currentTimeMillis();
            Date d = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            Model.setTaskBeginTime(sdf.format(d));
            Model.setScheduleTypeReadOnly(false);
            Model.setFilterVisible(false);
            Model.setFileSizeUnit("KB");
            Model.setFileSizeUnitTwo("MB");
            Model.setThreadNum(1);
            api.ssh_exec(sshcmd,"mkdir  -p   "+srcpath);
            String filename="no_back_file_1";
            Result res =api.Create_file(srcpath,api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port,"1","1",filename);
            long currenttime=System.currentTimeMillis();
            res =api.Create_file_login(sshcmd,srcpath,"1K","1","back_file_09");
            res =api.Create_file_login(sshcmd,srcpath,"1B","1","back_file");
            res =api.Create_file_login(sshcmd,srcpath,"1M","1","no_back_file_2");
            res =api.Create_file_login(sshcmd,srcpath,"2M","1","no_back_file_3");
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
            res =api.GetDstFileSha256sum(dstsshcmd,desPathName);
            String dstfilesha256=res.getBody();


            System.out.println("=============================================================");
            res =api.GetSrcFileSha256sum(sshcmd,srcpath);
            String srcfilesha256=res.getBody();
            String[] srclist_tmp=srcfilesha256.replace("\r","").split("\n");
            String[] dstlist_tmp=dstfilesha256.replace("\r","").split("\n");
            for(int i=0;i<dstlist_tmp.length;i++)
            {
                if(api.ssh_src_type.equals("windows")){
                    desPathName=desPathName+"/".replace("//","/");
                }
                dstlist_tmp[i]=dstlist_tmp[i].replace(desPathName,"");

            }
            List<String> srclist= Arrays.asList(srclist_tmp);
            List<String> dstlist= Arrays.asList(dstlist_tmp);
            System.out.println(srclist);
            System.out.println(dstlist);
            Assert.assertTrue(dstlist.size()==2);
            for(int i=0;i<dstlist.size();i++){
                System.out.println("判断当前文件目的路径下的文件是否把源文件都拷贝过去了 "+dstlist.get(i).toString());
                Assert.assertTrue(srclist.contains(dstlist.get(i)));
                //Assert.assertTrue(dstlist.get(i).toString().contains("abcd"));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {

            sshcmd.exec("rm  -rf    "+srcpath);

        }

    }

    @Test
    public  void TestCase_CreateJobPlainNopack_RunOnce_filter_By_filesize_LeftBigerK1024_RightSmallerM1(){
        try {
            JobPlainStatusModel Model=new JobPlainStatusModel();
            String JobName="pds"+String.valueOf(System.currentTimeMillis());
            Model.setTaskName(JobName);
            Model.setTaskType(2);
            Model.setSourcePath(srcpath);
            Model.setsourceIp(api.ssh_src_ip);
            Model.setTargetPath(api.dst_path);
            Model.setTargetIp(api.ssh_dst_ip);
            Model.setScheduleType(0);
            Model.setFileSizeScopeOne(2);//大于等于1M
            Model.setFileSizeScopeTwo(2);//小于等于
            Model.setFileSizeOne("1024");//1M
            Model.setFileSizeTwo("1.0");
            System.currentTimeMillis();
            Date d = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            Model.setTaskBeginTime(sdf.format(d));
            Model.setScheduleTypeReadOnly(false);
            Model.setFilterVisible(false);
            Model.setFileSizeUnit("KB");
            Model.setFileSizeUnitTwo("MB");
            Model.setThreadNum(1);
            api.ssh_exec(sshcmd,"mkdir  -p   "+srcpath);
            String filename="no_back_file_1";
            Result res =api.Create_file(srcpath,api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port,"1","1",filename);
            long currenttime=System.currentTimeMillis();
            res =api.Create_file_login(sshcmd,srcpath,"1024K","1","back_file_09");
            res =api.Create_file_login(sshcmd,srcpath,"1B","1","back_file");
            res =api.Create_file_login(sshcmd,srcpath,"1M","1","no_back_file_2");
            res =api.Create_file_login(sshcmd,srcpath,"2M","1","no_back_file_3");
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
            res =api.GetDstFileSha256sum(dstsshcmd,desPathName);
            String dstfilesha256=res.getBody();
            System.out.println("=============================================================");
            res =api.GetSrcFileSha256sum(sshcmd,srcpath);
            String srcfilesha256=res.getBody();
            String[] srclist_tmp=srcfilesha256.replace("\r","").split("\n");
            String[] dstlist_tmp=dstfilesha256.replace("\r","").split("\n");
            for(int i=0;i<dstlist_tmp.length;i++)
            {
                if(api.ssh_src_type.equals("windows")){
                    desPathName=desPathName+"/".replace("//","/");
                }
                dstlist_tmp[i]=dstlist_tmp[i].replace(desPathName,"");
            }
            List<String> srclist= Arrays.asList(srclist_tmp);
            List<String> dstlist= Arrays.asList(dstlist_tmp);
            System.out.println(srclist);
            System.out.println(dstlist);
            Assert.assertTrue(dstlist.size()==2);
            for(int i=0;i<dstlist.size();i++){
                System.out.println("判断当前文件目的路径下的文件是否把源文件都拷贝过去了 "+dstlist.get(i).toString());
                Assert.assertTrue(srclist.contains(dstlist.get(i)));
                //Assert.assertTrue(dstlist.get(i).toString().contains("abcd"));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {

            api.ssh_exec(sshcmd,"rm  -rf    "+srcpath);

        }

    }

    @Test
    public  void TestCase_CreateJobPlainNopack_RunOnce_filter_By_filesize_LeftBigerK1023_99_RightSmallerM1(){
        try {
            JobPlainStatusModel Model=new JobPlainStatusModel();
            String JobName="pds"+String.valueOf(System.currentTimeMillis());
            Model.setTaskName(JobName);
            Model.setTaskType(2);
            Model.setSourcePath(srcpath);
            Model.setsourceIp(api.ssh_src_ip);
            Model.setTargetPath(api.dst_path);
            Model.setTargetIp(api.ssh_dst_ip);
            Model.setScheduleType(0);
            Model.setFileSizeScopeOne(2);//大于等于1M
            Model.setFileSizeScopeTwo(2);//小于等于
            Model.setFileSizeOne("1023.99");//1M
            Model.setFileSizeTwo("1.0");
            System.currentTimeMillis();
            Date d = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            Model.setTaskBeginTime(sdf.format(d));
            Model.setScheduleTypeReadOnly(false);
            Model.setFilterVisible(false);
            Model.setFileSizeUnit("KB");
            Model.setFileSizeUnitTwo("MB");

            Model.setThreadNum(1);

            api.ssh_exec(sshcmd,"mkdir  -p   "+srcpath);

            String filename="no_back_file_1";
            Result res =api.Create_file(srcpath,api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port,"1","1",filename);
            long currenttime=System.currentTimeMillis();
            res =api.Create_file(srcpath,api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port,"1023K","1","back_file_09");
            res =api.Create_file(srcpath,api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port,"1024K","1","back_file_09");
            res =api.Create_file(srcpath,api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port,"1B","1","back_file");
            res =api.Create_file(srcpath,api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port,"1M","1","no_back_file_2");
            res =api.Create_file(srcpath,api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port,"2M","1","no_back_file_3");
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
            res =api.GetDstFileSha256sum(dstsshcmd,desPathName);
            String dstfilesha256=res.getBody();


            System.out.println("=============================================================");
            res =api.GetSrcFileSha256sum(sshcmd,srcpath);
            String srcfilesha256=res.getBody();
            String[] srclist_tmp=srcfilesha256.replace("\r","").split("\n");
            String[] dstlist_tmp=dstfilesha256.replace("\r","").split("\n");
            for(int i=0;i<dstlist_tmp.length;i++)
            {
                if(api.ssh_src_type.equals("windows")){
                    desPathName=desPathName+"/".replace("//","/");
                }
                dstlist_tmp[i]=dstlist_tmp[i].replace(desPathName,"");

            }
            List<String> srclist= Arrays.asList(srclist_tmp);
            List<String> dstlist= Arrays.asList(dstlist_tmp);
            System.out.println(srclist);
            System.out.println(dstlist);
            Assert.assertTrue(dstlist.size()==2);
            for(int i=0;i<dstlist.size();i++){
                System.out.println("判断当前文件目的路径下的文件是否把源文件都拷贝过去了 "+dstlist.get(i).toString());
                Assert.assertTrue(srclist.contains(dstlist.get(i)));
                //Assert.assertTrue(dstlist.get(i).toString().contains("abcd"));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {

            api.ssh_exec(sshcmd,"rm  -rf    "+srcpath);

        }

    }


    @Test //所以的文件都满足条件
    public  void TestCase_CreateJobPlainNopack_RunOnce_filter_By_fileType_equal_doc_txt_xlsx_gzmore(){
        try {

            JobPlainStatusModel model=new JobPlainStatusModel();
            String JobName="pds"+String.valueOf(System.currentTimeMillis());
            model.setTaskName(JobName);

            model.setTaskType(2);
            model.setSourcePath(srcpath);
            model.setsourceIp(api.ssh_src_ip);
            model.setTargetPath(api.dst_path);
            model.setTargetIp(api.ssh_dst_ip);
            //周期任务 运行一次
            model.setScheduleRunOnce();

            model.setFileSuffixScope(1);
            model.setFileSuffixName(".doc,.pdf,.txt,.xlsx,.tar.gz,.zip");

            System.currentTimeMillis();
            Date d = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            model.setTaskBeginTime(sdf.format(d));
            model.setScheduleTypeReadOnly(false);
            model.setFilterVisible(false);
            model.setFileSizeUnit("B");
            model.setFileSizeUnitTwo("TB");

            model.setThreadNum(1);


            api.ssh_exec_cmd(api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port,"mkdir  -p   "+srcpath);


            Result res ;

            res =api.Create_file(srcpath,api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port,"64k","1","1.zip");
            System.out.println("create file  1.zip res:"+res.getBody());
            res =api.Create_file(srcpath,api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port,"64k","1","2.doc");
            System.out.println("create file  2.doc res:"+res.getBody());
            res =api.Create_file(srcpath,api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port,"64k","1","3.pdf");
            System.out.println("create file  3.pdf res:"+res.getBody());
            res =api.Create_file(srcpath,api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port,"64k","1","4.txt");
            System.out.println("create file  4.txt res:"+res.getBody());
            res =api.Create_file(srcpath,api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port,"64k","1","5.xlsx");
            System.out.println("create file  5.xlsx res:"+res.getBody());
            res =api.Create_file(srcpath,api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port,"64k","1","6.tar.gz");
            System.out.println("create file  6.tar.gz res:"+res.getBody());


            model.setSmallBatchCommitNum(1);
            model.setFileCheckType("2");
            model.setFullBackup();
            model.setFileCheckType("4");
            model.setCreateBy(1);


            System.out.println(JSON.toJSONString(model));
            Result resp=api.CreateJobPlain(JSON.toJSONString(model));
            System.out.println(JSON.toJSONString(resp));

            //等待作业运行完毕
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

            res =api.GetDstFileSha256sum(dstsshcmd,desPathName);
            String dstfilesha256=res.getBody();


            System.out.println("=============================================================");
            res =api.GetSrcFileSha256sum(sshcmd,srcpath);
            String srcfilesha256=res.getBody();
            String[] srclist_tmp=srcfilesha256.replace("\r","").split("\n");
            String[] dstlist_tmp=dstfilesha256.replace("\r","").split("\n");

            for(int i=0;i<dstlist_tmp.length;i++)
            {
                if(api.ssh_src_type.equals("windows")){
                    desPathName=desPathName+"/".replace("//","/");
                }
                dstlist_tmp[i]=dstlist_tmp[i].replace(desPathName,"");

            }
            List<String> srclist= Arrays.asList(srclist_tmp);
            List<String> dstlist= Arrays.asList(dstlist_tmp);

            System.out.println(srclist);
            System.out.println(dstlist);

            for(int i=0;i<srclist.size();i++){
                System.out.println("判断当前文件目的路径下的文件是否把源文件都拷贝过去了 "+srclist.get(i).toString());
                Assert.assertTrue(dstlist.contains(srclist.get(i)));
            }




        } catch (InterruptedException e) {

            e.printStackTrace();

        }finally {

            api.ssh_exec_cmd(api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port,"rm  -rf    "+srcpath);

        }

    }


    @Test   //一个条件都不满足
    public  void TestCase_CreateJobPlainNopack_RunOnce_filter_By_fileType_notequal_doc_txt_xlsx_gzmore(){
        try {
            JobPlainStatusModel model=new JobPlainStatusModel();
            String JobName="pds"+String.valueOf(System.currentTimeMillis());
            model.setTaskName(JobName);
            model.setTaskType(2);
            model.setSourcePath(srcpath);
            model.setsourceIp(api.ssh_src_ip);
            model.setTargetPath(api.dst_path);
            model.setTargetIp(api.ssh_dst_ip);
            //周期任务 运行一次
            model.setScheduleRunOnce();
            model.setFileSuffixScope(1);
            model.setFileSuffixName(".doc,.pdf,.txt,.xlsx,.tar.gz,.zip");
            System.currentTimeMillis();
            Date d = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            model.setTaskBeginTime(sdf.format(d));
            model.setScheduleTypeReadOnly(false);
            model.setFilterVisible(false);
            model.setFileSizeUnit("B");
            model.setFileSizeUnitTwo("TB");
            model.setThreadNum(1);
            api.ssh_exec_cmd(api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port,"mkdir  -p   "+srcpath);
            Result res ;
            res = api.Create_file(srcpath,api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port,"4K","1","back_file_09");
            model.setSmallBatchCommitNum(1);
            model.setFileCheckType("2");
            model.setBackupType(1);
            System.out.println(JSON.toJSONString(model));
            Result resp=api.CreateJobPlain(JSON.toJSONString(model));
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
            res =api.GetDstFileSha256sum(dstsshcmd,desPathName);
            String dstfilesha256=res.getBody();

            Assert.assertTrue(dstfilesha256.length()==0);//没有条件满足所以目地端文件没有

        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {

            api.ssh_exec_cmd(api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port,"rm  -rf    "+srcpath);

        }

    }

    @Test    //只有中间的添加不满足
    public  void TestCase_CreateJobPlainNopack_RunOnce_filter_By_fileType_notequalmidle_doc_txt_xlsx_gzmore(){
        try {
            JobPlainStatusModel model=new JobPlainStatusModel();
            String JobName="pds"+String.valueOf(System.currentTimeMillis());
            model.setTaskName(JobName);
            model.setTaskType(2);
            model.setSourcePath(srcpath);
            model.setsourceIp(api.ssh_src_ip);
            model.setTargetPath(api.dst_path);
            model.setTargetIp(api.ssh_dst_ip);
            //周期任务 运行一次
            model.setScheduleRunOnce();
            model.setFileSuffixScope(1);
            model.setFileSuffixName(".doc,.pdf,.txt,.xlsx,.tar.gz,.zip");
            System.currentTimeMillis();
            Date d = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            model.setTaskBeginTime(sdf.format(d));
            model.setScheduleTypeReadOnly(false);
            model.setFilterVisible(false);
            model.setFileSizeUnit("B");
            model.setFileSizeUnitTwo("TB");
            model.setThreadNum(1);

            api.ssh_exec(sshcmd,"mkdir  -p   "+srcpath);
            Result res ;

            res = api.Create_file_login(sshcmd,srcpath,"4K","1","a..doc");
            res = api.Create_file_login(sshcmd,srcpath,"4K","1","c..doc.txt");

            model.setSmallBatchCommitNum(1);
            model.setFileCheckType("2");
            model.setBackupType(1);
            System.out.println(JSON.toJSONString(model));
            Result resp=api.CreateJobPlain(JSON.toJSONString(model));
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

                list.add(joblog.getContent().get(a).getFileName());
            }
            desPathName=joblog.getContent().get(0).getDesPathName();

            res =api.GetDstFileSha256sum(dstsshcmd,desPathName);
            String dstfilesha256=res.getBody();

            System.out.println("=============================================================");
            res =api.GetSrcFileSha256sum(sshcmd,srcpath);
            String srcfilesha256=res.getBody();
            String[] srclist_tmp=srcfilesha256.replace("\r","").split("\n");
            String[] dstlist_tmp=dstfilesha256.replace("\r","").split("\n");
            for(int i=0;i<dstlist_tmp.length;i++)
            {
                if(api.ssh_src_type.equals("windows")){
                    desPathName=desPathName+"/".replace("//","/");
                }
                dstlist_tmp[i]=dstlist_tmp[i].replace(desPathName,"");
            }
            List<String> srclist= Arrays.asList(srclist_tmp);
            List<String> dstlist= Arrays.asList(dstlist_tmp);
            System.out.println(srclist);
            System.out.println(dstlist);

            Assert.assertTrue(dstlist.size()==2);
            for(int i=0;i<dstlist.size();i++){
                System.out.println("判断当前文件目的路径下的文件是否把源文件都拷贝过去了 "+dstlist.get(i).toString());
                Assert.assertTrue(srclist.contains(dstlist.get(i)));
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            api.ssh_exec(sshcmd,"rm  -rf    "+srcpath);
        }
    }

    @Test    // 中间模糊匹配
    public  void TestCase_CreateJobPlainNopack_RunOnce_filter_By_fileType_equal_midle(){
        try {
            JobPlainStatusModel model=new JobPlainStatusModel();
            String JobName="pds"+String.valueOf(System.currentTimeMillis());
            model.setTaskName(JobName);
            model.setTaskType(2);
            model.setSourcePath(srcpath);
            model.setsourceIp(api.ssh_src_ip);
            model.setTargetPath(api.dst_path);
            model.setTargetIp(api.ssh_dst_ip);
            //周期任务 运行一次
            model.setScheduleRunOnce();
            model.setFileSuffixScope(1);
            model.setFileSuffixName(".doc");
            System.currentTimeMillis();
            Date d = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            model.setTaskBeginTime(sdf.format(d));
            model.setScheduleTypeReadOnly(false);
            model.setFilterVisible(false);
            model.setFileSizeUnit("B");
            model.setFileSizeUnitTwo("TB");
            model.setThreadNum(1);

            api.ssh_exec(sshcmd,"mkdir  -p   "+srcpath);
            Result res ;

            res = api.Create_file_login(sshcmd,srcpath,"4K","1","a..doc");
            res = api.Create_file_login(sshcmd,srcpath,"4K","1","c..doc.txt");
            res = api.Create_file_login(sshcmd,srcpath,"4K","1","c.b.doc.txt");

            model.setSmallBatchCommitNum(1);
            model.setFileCheckType("2");
            model.setBackupType(1);
            System.out.println(JSON.toJSONString(model));
            Result resp=api.CreateJobPlain(JSON.toJSONString(model));
            System.out.println(resp.getBody());
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
            res =api.GetDstFileSha256sum(dstsshcmd,desPathName);
            String dstfilesha256=res.getBody();

            System.out.println("=============================================================");
            res =api.GetSrcFileSha256sum(sshcmd,srcpath);
            String srcfilesha256=res.getBody();
            String[] srclist_tmp=srcfilesha256.replace("\r","").split("\n");
            String[] dstlist_tmp=dstfilesha256.replace("\r","").split("\n");
            for(int i=0;i<dstlist_tmp.length;i++)
            {
                if(api.ssh_src_type.equals("windows")){
                    desPathName=desPathName+"/".replace("//","/");
                }
                dstlist_tmp[i]=dstlist_tmp[i].replace(desPathName,"");
            }
            List<String> srclist= Arrays.asList(srclist_tmp);
            List<String> dstlist= Arrays.asList(dstlist_tmp);
            System.out.println(srclist);
            System.out.println(dstlist);

            Assert.assertTrue(dstlist.size()==1);
            for(int i=0;i<dstlist.size();i++){
                System.out.println("判断当前文件目的路径下的文件是否把源文件都拷贝过去了 "+dstlist.get(i).toString());
                Assert.assertTrue(srclist.contains(dstlist.get(i)));
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            api.ssh_exec(sshcmd,"rm  -rf    "+srcpath);
        }
    }

    @Test    //尾部模糊匹配
    public  void TestCase_CreateJobPlainNopack_RunOnce_filter_By_fileType_equal_end(){
        try {
            JobPlainStatusModel model=new JobPlainStatusModel();
            String JobName="pds"+String.valueOf(System.currentTimeMillis());
            model.setTaskName(JobName);
            model.setTaskType(2);
            model.setSourcePath(srcpath);
            model.setsourceIp(api.ssh_src_ip);
            model.setTargetPath(api.dst_path);
            model.setTargetIp(api.ssh_dst_ip);
            //周期任务 运行一次
            model.setScheduleRunOnce();
            model.setFileSuffixScope(1);
            model.setFileSuffixName(".doc");
            System.currentTimeMillis();
            Date d = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            model.setTaskBeginTime(sdf.format(d));
            model.setScheduleTypeReadOnly(false);
            model.setFilterVisible(false);
            model.setFileSizeUnit("B");
            model.setFileSizeUnitTwo("TB");
            model.setThreadNum(1);

            api.ssh_exec_cmd(api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port,"mkdir  -p   "+srcpath);
            Result res ;

            res = api.Create_file(srcpath,api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port,"4K","1","a..doc");
            res = api.Create_file(srcpath,api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port,"4K","1","c..doc$");
            res = api.Create_file(srcpath,api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port,"4K","1","c.b.doc.txt");

            model.setSmallBatchCommitNum(1);
            model.setFileCheckType("2");
            model.setBackupType(1);
            System.out.println(JSON.toJSONString(model));
            Result resp=api.CreateJobPlain(JSON.toJSONString(model));
            System.out.println(resp.getBody());
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
            res =api.GetDstFileSha256sum(dstsshcmd,desPathName);
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
            System.out.println(srclist);
            System.out.println(dstlist);

            Assert.assertTrue(dstlist.size()==1);
            for(int i=0;i<dstlist.size();i++){
                System.out.println("判断当前文件目的路径下的文件是否把源文件都拷贝过去了 "+dstlist.get(i).toString());
                Assert.assertTrue(srclist.contains(dstlist.get(i)));
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            api.ssh_exec_cmd(api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port,"rm  -rf    "+srcpath);
        }
    }

    @Test    //文件名称包含逗号
    public  void TestCase_CreateJobPlainNopack_RunOnce_filter_By_fileType_FilenameContainComma(){
        try {
            JobPlainStatusModel model=new JobPlainStatusModel();
            String JobName="pds"+String.valueOf(System.currentTimeMillis());
            model.setTaskName(JobName);
            model.setTaskType(2);
            model.setSourcePath(srcpath);
            model.setsourceIp(api.ssh_src_ip);
            model.setTargetPath(api.dst_path);
            model.setTargetIp(api.ssh_dst_ip);
            //周期任务 运行一次
            model.setScheduleRunOnce();
            model.setFileSuffixScope(1);
            model.setFileSuffixName(".doc");
            System.currentTimeMillis();
            Date d = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            model.setTaskBeginTime(sdf.format(d));
            model.setScheduleTypeReadOnly(false);
            model.setFilterVisible(false);
            model.setFileSizeUnit("B");
            model.setFileSizeUnitTwo("TB");
            model.setThreadNum(1);

            api.ssh_exec_cmd(api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port,"mkdir  -p   "+srcpath);
            Result res ;

            res = api.Create_file(srcpath,api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port,"4K","1","a,.doc");
            res = api.Create_file(srcpath,api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port,"4K","1","c..doc,");
            res = api.Create_file(srcpath,api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port,"4K","1","c.b.doc.txt");

            model.setSmallBatchCommitNum(1);
            model.setFileCheckType("2");
            model.setBackupType(1);
            System.out.println(JSON.toJSONString(model));
            Result resp=api.CreateJobPlain(JSON.toJSONString(model));
            System.out.println(resp.getBody());
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
            res =api.GetDstFileSha256sum(dstsshcmd,desPathName);
            String dstfilesha256=res.getBody();

            System.out.println("=============================================================");
            res =api.GetSrcFileSha256sum(sshcmd,srcpath);
            String srcfilesha256=res.getBody();
            String[] srclist_tmp=srcfilesha256.replace("\r","").split("\n");
            String[] dstlist_tmp=dstfilesha256.replace("\r","").split("\n");
            for(int i=0;i<dstlist_tmp.length;i++)
            {
                if(api.ssh_src_type.equals("windows")){
                    desPathName=desPathName+"/".replace("//","/");
                }
                dstlist_tmp[i]=dstlist_tmp[i].replace(desPathName,"");
            }
            List<String> srclist= Arrays.asList(srclist_tmp);
            List<String> dstlist= Arrays.asList(dstlist_tmp);
            System.out.println(srclist);
            System.out.println(dstlist);

            Assert.assertTrue(dstlist.size()==1);
            for(int i=0;i<dstlist.size();i++){
                System.out.println("判断当前文件目的路径下的文件是否把源文件都拷贝过去了 "+dstlist.get(i).toString());
                Assert.assertTrue(srclist.contains(dstlist.get(i)));
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            api.ssh_exec_cmd(api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port,"rm  -rf    "+srcpath);
        }
    }


    @Test    //文件名称包含点，根据点过滤
    public  void TestCase_CreateJobPlainNopack_RunOnce_filter_By_fileNameContain_Spot_FilenameContainSpot(){
        try {

            JobPlainStatusModel model=new JobPlainStatusModel();
            String JobName="pds"+String.valueOf(System.currentTimeMillis());
            model.setTaskName(JobName);
            model.setTaskType(2);
            model.setSourcePath(srcpath);
            model.setsourceIp(api.ssh_src_ip);
            model.setTargetPath(api.dst_path);
            model.setTargetIp(api.ssh_dst_ip);
            //周期任务 运行一次
            model.setScheduleRunOnce();

            model.setFileNameScope(1);
            model.setFileName(".");


            Date d = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            model.setTaskBeginTime(sdf.format(d));
            model.setScheduleTypeReadOnly(false);
            model.setFilterVisible(false);
            model.setFileSizeUnit("B");
            model.setFileSizeUnitTwo("TB");
            model.setThreadNum(1);

            api.ssh_exec_cmd(api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port,"mkdir  -p   "+srcpath);
            Result res ;

            res = api.Create_file(srcpath,api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port,"4K","1",".a.");
            res = api.Create_file(srcpath,api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port,"4K","1","c..doc,");
            res = api.Create_file(srcpath,api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port,"4K","1","c.b.doc.txt");
            res = api.Create_file(srcpath,api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port,"4K","1","c.b.doc.txt.$");

            model.setSmallBatchCommitNum(1);
            model.setFileCheckType("2");
            model.setBackupType(1);
            System.out.println(JSON.toJSONString(model));
            Result resp=api.CreateJobPlain(JSON.toJSONString(model));
            System.out.println(resp.getBody());
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
            res =api.GetDstFileSha256sum(dstsshcmd,desPathName);
            String dstfilesha256=res.getBody();

            System.out.println("=============================================================");
            res =api.GetSrcFileSha256sum(sshcmd,srcpath);
            String srcfilesha256=res.getBody();
            String[] srclist_tmp=srcfilesha256.replace("\r","").split("\n");
            String[] dstlist_tmp=dstfilesha256.replace("\r","").split("\n");
            for(int i=0;i<dstlist_tmp.length;i++)
            {
                if(api.ssh_src_type.equals("windows")){
                    desPathName=desPathName+"/".replace("//","/");
                }
                dstlist_tmp[i]=dstlist_tmp[i].replace(desPathName,"");
            }
            List<String> srclist= Arrays.asList(srclist_tmp);
            List<String> dstlist= Arrays.asList(dstlist_tmp);
            System.out.println(srclist);
            System.out.println(dstlist);

            Assert.assertTrue(dstlist.size()==4);
            for(int i=0;i<dstlist.size();i++){
                System.out.println("判断当前文件目的路径下的文件是否把源文件都拷贝过去了 "+dstlist.get(i).toString());
                Assert.assertTrue(srclist.contains(dstlist.get(i)));
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            api.ssh_exec_cmd(api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port,"rm  -rf    "+srcpath);
        }
    }


    @Test    //文件名称包含$，根据$过滤,运行的时候会导致file_client挂掉
    public  void TestCase_CreateJobPlainNopack_RunOnce_filter_By_fileNameContain_Spot_FilenameContainDollar(){
        try {

            JobPlainStatusModel model=new JobPlainStatusModel();
            String JobName="pds"+String.valueOf(System.currentTimeMillis());
            model.setTaskName(JobName);
            model.setTaskType(2);
            model.setSourcePath(srcpath);
            model.setsourceIp(api.ssh_src_ip);
            model.setTargetPath(api.dst_path);
            model.setTargetIp(api.ssh_dst_ip);
            //周期任务 运行一次
            model.setScheduleRunOnce();

            model.setFileNameScope(1);
            model.setFileName("$");

            Date d = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            model.setTaskBeginTime(sdf.format(d));
            model.setScheduleTypeReadOnly(false);
            model.setFilterVisible(false);
            model.setFileSizeUnit("B");
            model.setFileSizeUnitTwo("TB");
            model.setThreadNum(1);

            sshcmd.exec("mkdir  -p   "+srcpath);
            Result res ;

            res = api.Create_file_login(sshcmd,srcpath,"4K","1",".a.");
            res = api.Create_file_login(sshcmd,srcpath,"4K","1","c..doc,");
            res = api.Create_file_login(sshcmd,srcpath,"4K","1","c.b.\\$doc.txt");


            model.setSmallBatchCommitNum(1);
            model.setFileCheckType("2");
            model.setBackupType(1);
            System.out.println(JSON.toJSONString(model));
            Result resp=api.CreateJobPlain(JSON.toJSONString(model));
            System.out.println(resp.getBody());
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

                        System.out.println("作业任务运行失败！");
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
            res =api.GetDstFileSha256sum(dstsshcmd,desPathName);
            String dstfilesha256=res.getBody();

            System.out.println("=============================================================");
            res =api.GetSrcFileSha256sum(sshcmd,srcpath);
            String srcfilesha256=res.getBody();
            String[] srclist_tmp=srcfilesha256.replace("\r","").split("\n");
            String[] dstlist_tmp=dstfilesha256.replace("\r","").split("\n");
            for(int i=0;i<dstlist_tmp.length;i++)
            {
                if(api.ssh_src_type.equals("windows")){
                    desPathName=desPathName+"/".replace("//","/");
                }
                dstlist_tmp[i]=dstlist_tmp[i].replace(desPathName,"");
            }
            List<String> srclist= Arrays.asList(srclist_tmp);
            List<String> dstlist= Arrays.asList(dstlist_tmp);
            System.out.println(srclist);
            System.out.println(dstlist);

            Assert.assertTrue(dstlist.size()==1);
            for(int i=0;i<dstlist.size();i++){
                System.out.println("判断当前文件目的路径下的文件是否把源文件都拷贝过去了 "+dstlist.get(i).toString());
                Assert.assertTrue(srclist.contains(dstlist.get(i)));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            sshcmd.exec("rm  -rf    "+srcpath);
        }
    }

    @Test    //文件名称包含..，根据..过滤
    public  void TestCase_CreateJobPlainNopack_RunOnce_filter_By_fileNameContain_Spot2_FilenameContainSpot2(){
        try {
            JobPlainStatusModel model=new JobPlainStatusModel();
            String JobName="pds"+String.valueOf(System.currentTimeMillis());
            model.setTaskName(JobName);
            model.setTaskType(2);
            model.setSourcePath(srcpath);
            model.setsourceIp(api.ssh_src_ip);
            model.setTargetPath(api.dst_path);
            model.setTargetIp(api.ssh_dst_ip);
            //周期任务 运行一次
            model.setScheduleRunOnce();

            //文件名称包含
            model.setFileNameScope(1);
            model.setFileName("..");


            Date d = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            model.setTaskBeginTime(sdf.format(d));
            model.setScheduleTypeReadOnly(false);
            model.setFilterVisible(false);
            model.setFileSizeUnit("B");
            model.setFileSizeUnitTwo("TB");
            model.setThreadNum(1);

            sshcmd.exec("mkdir  -p   "+srcpath);
            Result res ;

            res = api.Create_file_login(sshcmd,srcpath,"4K","1",".a.");
            res = api.Create_file_login(sshcmd,srcpath,"4K","1","c..doc,");
            res = api.Create_file_login(sshcmd,srcpath,"4K","1","c.b.$doc.txt");


            model.setSmallBatchCommitNum(1);
            model.setFileCheckType("2");
            model.setBackupType(1);
            System.out.println(JSON.toJSONString(model));
            Result resp=api.CreateJobPlain(JSON.toJSONString(model));
            System.out.println(resp.getBody());
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
            res =api.GetDstFileSha256sum(dstsshcmd,desPathName);
            String dstfilesha256=res.getBody();

            System.out.println("=============================================================");
            res =api.GetSrcFileSha256sum(sshcmd,srcpath);
            String srcfilesha256=res.getBody();
            String[] srclist_tmp=srcfilesha256.replace("\r","").split("\n");
            String[] dstlist_tmp=dstfilesha256.replace("\r","").split("\n");
            Assert.assertTrue(dstlist_tmp.length>0);
            for(int i=0;i<dstlist_tmp.length;i++)
            {
                if(api.ssh_src_type.equals("windows")){
                    desPathName=desPathName+"/".replace("//","/");
                }
                dstlist_tmp[i]=dstlist_tmp[i].replace(desPathName,"");
            }
            List<String> srclist= Arrays.asList(srclist_tmp);
            List<String> dstlist= Arrays.asList(dstlist_tmp);
            System.out.println(srclist);
            System.out.println(dstlist);

            Assert.assertTrue(dstlist.size()==1);
            for(int i=0;i<dstlist.size();i++){
                System.out.println("判断当前文件目的路径下的文件是否把源文件都拷贝过去了 "+dstlist.get(i).toString());
                Assert.assertTrue(srclist.contains(dstlist.get(i)));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            sshcmd.exec("rm  -rf    "+srcpath);
        }
    }

    @Test    //文件名称包aaa，长度特别长
    public  void TestCase_CreateJobPlainNopack_RunOnce_filter_By_fileNameContain_Latter_FilenameContainLatterlong(){

        try {
            JobPlainStatusModel model=new JobPlainStatusModel();
            String JobName="pds"+String.valueOf(System.currentTimeMillis());
            model.setTaskName(JobName);
            model.setTaskType(2);
            model.setSourcePath(srcpath);
            model.setsourceIp(api.ssh_src_ip);
            model.setTargetPath(api.dst_path);
            model.setTargetIp(api.ssh_dst_ip);
            //周期任务 运行一次
            model.setScheduleRunOnce();
            model.setFileNameScope(1);
            model.setFileName("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
            Date d = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            model.setTaskBeginTime(sdf.format(d));
            model.setScheduleTypeReadOnly(false);
            model.setFilterVisible(false);
            model.setFileSizeUnit("B");
            model.setFileSizeUnitTwo("TB");
            model.setThreadNum(1);

            api.ssh_exec_cmd(api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port,"mkdir  -p   "+srcpath);
            Result res ;

            res = api.Create_file_login(sshcmd,srcpath,"4K","1","aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
            res = api.Create_file_login(sshcmd,srcpath,"4K","1","c..doc,");
            res = api.Create_file_login(sshcmd,srcpath,"4K","1","c.b.doc.txt");
            res = api.Create_file_login(sshcmd,srcpath,"4K","1","c.b.doc.txt.$");

            model.setSmallBatchCommitNum(1);
            model.setFileCheckType("2");
            model.setBackupType(1);
            System.out.println(JSON.toJSONString(model));
            Result resp=api.CreateJobPlain(JSON.toJSONString(model));
            if(resp.getCode()==400){
                System.out.println("创建备份计划失败 失败原因："+resp.getBody());
                Assert.assertTrue(false);
            }
            System.out.println(resp.getBody());
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
            res =api.GetDstFileSha256sum(dstsshcmd,desPathName);
            String dstfilesha256=res.getBody();

            System.out.println("=============================================================");
            res =api.GetSrcFileSha256sum(sshcmd,srcpath);
            String srcfilesha256=res.getBody();
            String[] srclist_tmp=srcfilesha256.replace("\r","").split("\n");
            String[] dstlist_tmp=dstfilesha256.replace("\r","").split("\n");
            for(int i=0;i<dstlist_tmp.length;i++)
            {
                if(api.ssh_src_type.equals("windows")){
                    desPathName=desPathName+"/".replace("//","/");
                }
                dstlist_tmp[i]=dstlist_tmp[i].replace(desPathName,"");
            }
            List<String> srclist= Arrays.asList(srclist_tmp);
            List<String> dstlist= Arrays.asList(dstlist_tmp);
            System.out.println(srclist);
            System.out.println(dstlist);

            Assert.assertTrue(dstlist.size()==1);
            for(int i=0;i<dstlist.size();i++){
                System.out.println("判断当前文件目的路径下的文件是否把源文件都拷贝过去了 "+dstlist.get(i).toString());
                Assert.assertTrue(srclist.contains(dstlist.get(i)));
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            sshcmd.exec("rm  -rf    "+srcpath);
        }
    }

    @Test    //文件名称不包含.  根据.过滤
    public  void TestCase_CreateJobPlainNopack_RunOnce_filter_By_fileNameNotContain_Spot_FilenameContainSpot(){
        try {
            JobPlainStatusModel model=new JobPlainStatusModel();
            String JobName="pds"+String.valueOf(System.currentTimeMillis());
            model.setTaskName(JobName);
            model.setTaskType(2);
            model.setSourcePath(srcpath);
            model.setsourceIp(api.ssh_src_ip);
            model.setTargetPath(api.dst_path);
            model.setTargetIp(api.ssh_dst_ip);
            //周期任务 运行一次
            model.setScheduleRunOnce();

            //文件名称不包含
            model.setFileNameScope(2);
            model.setFileName(".");



            Date d = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            model.setTaskBeginTime(sdf.format(d));
            model.setScheduleTypeReadOnly(false);
            model.setFilterVisible(false);
            model.setFileSizeUnit("B");
            model.setFileSizeUnitTwo("TB");
            model.setThreadNum(1);
            sshcmd.exec("mkdir  -p   "+srcpath);
            Result res ;
            res = api.Create_file_login(sshcmd,srcpath,"4K","1",".a.");
            res = api.Create_file_login(sshcmd,srcpath,"4K","1","111111");
            res = api.Create_file_login(sshcmd,srcpath,"4K","1","c..doc,");
            res = api.Create_file_login(sshcmd,srcpath,"4K","1","c.b.$doc.txt");


            model.setSmallBatchCommitNum(1);
            model.setFileCheckType("2");
            model.setBackupType(1);
            System.out.println(JSON.toJSONString(model));
            Result resp=api.CreateJobPlain(JSON.toJSONString(model));
            System.out.println(resp.getBody());
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
            res =api.GetDstFileSha256sum(dstsshcmd,desPathName);
            String dstfilesha256=res.getBody();

            System.out.println("=============================================================");
            res =api.GetSrcFileSha256sum(sshcmd,srcpath);
            String srcfilesha256=res.getBody();
            String[] srclist_tmp=srcfilesha256.replace("\r","").split("\n");
            String[] dstlist_tmp=dstfilesha256.replace("\r","").split("\n");
            for(int i=0;i<dstlist_tmp.length;i++)
            {
                if(api.ssh_src_type.equals("windows")){
                    desPathName=desPathName+"/".replace("//","/");
                }
                dstlist_tmp[i]=dstlist_tmp[i].replace(desPathName,"");
            }
            List<String> srclist= Arrays.asList(srclist_tmp);
            List<String> dstlist= Arrays.asList(dstlist_tmp);
            System.out.println(srclist);
            System.out.println(dstlist);

            Assert.assertTrue(dstlist.size()==1);
            for(int i=0;i<dstlist.size();i++){
                System.out.println("判断当前文件目的路径下的文件是否把源文件都拷贝过去了 "+dstlist.get(i).toString());
                Assert.assertTrue(srclist.contains(dstlist.get(i)));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            sshcmd.exec("rm  -rf    "+srcpath);
        }
    }


    @Test    //文件名称不包含字母  根据letter过滤
    public  void TestCase_CreateJobPlainNopack_RunOnce_filter_By_fileNameNotContain_letter_FilenameContainLetter(){
        try {
            JobPlainStatusModel model=new JobPlainStatusModel();
            String JobName="pds"+String.valueOf(System.currentTimeMillis());
            model.setTaskName(JobName);
            model.setTaskType(2);
            model.setSourcePath(srcpath);
            model.setsourceIp(api.ssh_src_ip);
            model.setTargetPath(api.dst_path);
            model.setTargetIp(api.ssh_dst_ip);
            //周期任务 运行一次
            model.setScheduleRunOnce();

            //文件名称不包含
            model.setFileNameScope(2);
            model.setFileName("abdeffg");



            Date d = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            model.setTaskBeginTime(sdf.format(d));
            model.setScheduleTypeReadOnly(false);
            model.setFilterVisible(false);
            model.setFileSizeUnit("B");
            model.setFileSizeUnitTwo("TB");
            model.setThreadNum(1);

            sshcmd.exec("mkdir  -p   "+srcpath);
            Result res ;

            res = api.Create_file_login(sshcmd,srcpath,"4K","1",".a.");
            res = api.Create_file_login(sshcmd,srcpath,"4K","1","111111abdeffg....");
            res = api.Create_file_login(sshcmd,srcpath,"4K","1","c..doc,");
            res = api.Create_file_login(sshcmd,srcpath,"4K","1","c.b.$doc.txt");


            model.setSmallBatchCommitNum(1);
            model.setFileCheckType("2");
            model.setBackupType(1);
            System.out.println(JSON.toJSONString(model));
            Result resp=api.CreateJobPlain(JSON.toJSONString(model));
            System.out.println(resp.getBody());
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
            res =api.GetDstFileSha256sum(dstsshcmd,desPathName);
            String dstfilesha256=res.getBody();

            System.out.println("=============================================================");
            res =api.GetSrcFileSha256sum(sshcmd,srcpath);
            String srcfilesha256=res.getBody();
            String[] srclist_tmp=srcfilesha256.replace("\r","").split("\n");
            String[] dstlist_tmp=dstfilesha256.replace("\r","").split("\n");
            for(int i=0;i<dstlist_tmp.length;i++)
            {
                if(api.ssh_src_type.equals("windows")){
                    desPathName=desPathName+"/".replace("//","/");
                }
                dstlist_tmp[i]=dstlist_tmp[i].replace(desPathName,"");
            }
            List<String> srclist= Arrays.asList(srclist_tmp);
            List<String> dstlist= Arrays.asList(dstlist_tmp);
            System.out.println(srclist);
            System.out.println(dstlist);

            Assert.assertTrue(dstlist.size()==3);
            for(int i=0;i<dstlist.size();i++){
                System.out.println("判断当前文件目的路径下的文件是否把源文件都拷贝过去了 "+dstlist.get(i).toString());
                Assert.assertTrue(srclist.contains(dstlist.get(i)));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            sshcmd.exec("rm  -rf    "+srcpath);
        }
    }


    @Test    //文件名称包aaa，长度特别长
    public  void TestCase_CreateJobPlainNopack_RunOnce_filter_By_fileNameNotContain_Latter_FilenameContainLatterlong(){
        try {
            JobPlainStatusModel model=new JobPlainStatusModel();
            String JobName="pds"+String.valueOf(System.currentTimeMillis());
            model.setTaskName(JobName);
            model.setTaskType(2);
            model.setSourcePath(srcpath);
            model.setsourceIp(api.ssh_src_ip);
            model.setTargetPath(api.dst_path);
            model.setTargetIp(api.ssh_dst_ip);

            //周期任务 运行一次
            model.setScheduleRunOnce();

            model.setFileNameScope(2);
            model.setFileName("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");


            Date d = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            model.setTaskBeginTime(sdf.format(d));
            model.setScheduleTypeReadOnly(false);
            model.setFilterVisible(false);
            model.setFileSizeUnit("B");
            model.setFileSizeUnitTwo("TB");
            model.setThreadNum(1);

            sshcmd.exec("mkdir  -p   "+srcpath);
            Result res ;
            res = api.Create_file_login(sshcmd,srcpath,"4K","1","aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
            res = api.Create_file_login(sshcmd,srcpath,"4K","1","c..doc,");
            res = api.Create_file_login(sshcmd,srcpath,"4K","1","c.b.doc.txt");
            res = api.Create_file_login(sshcmd,srcpath,"4K","1","c.b.doc.txt.$");

            model.setSmallBatchCommitNum(1);
            model.setFileCheckType("2");
            model.setBackupType(1);
            System.out.println(JSON.toJSONString(model));
            Result resp=api.CreateJobPlain(JSON.toJSONString(model));
            if(resp.getCode()==400){
                System.out.println("创建备份计划失败 失败原因："+resp.getBody());
                Assert.assertTrue(false);
            }
            System.out.println(resp.getBody());
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
            res =api.GetDstFileSha256sum(dstsshcmd,desPathName);
            String dstfilesha256=res.getBody();

            System.out.println("=============================================================");
            res =api.GetSrcFileSha256sum(sshcmd,srcpath);
            String srcfilesha256=res.getBody();
            String[] srclist_tmp=srcfilesha256.replace("\r","").split("\n");
            String[] dstlist_tmp=dstfilesha256.replace("\r","").split("\n");
            for(int i=0;i<dstlist_tmp.length;i++)
            {
                if(api.ssh_src_type.equals("windows")){
                    desPathName=desPathName+"/".replace("//","/");
                }
                dstlist_tmp[i]=dstlist_tmp[i].replace(desPathName,"");
            }
            List<String> srclist= Arrays.asList(srclist_tmp);
            List<String> dstlist= Arrays.asList(dstlist_tmp);
            System.out.println(srclist);
            System.out.println(dstlist);

            Assert.assertTrue(dstlist.size()==3);
            for(int i=0;i<dstlist.size();i++){
                System.out.println("判断当前文件目的路径下的文件是否把源文件都拷贝过去了 "+dstlist.get(i).toString());
                Assert.assertTrue(srclist.contains(dstlist.get(i)));
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            sshcmd.exec("rm  -rf    "+srcpath);
        }
    }


    @Test    //文件名称不包含数字  根据letter过滤
    public  void TestCase_CreateJobPlainNopack_RunOnce_filter_By_fileNameNotContain_Number_FilenameContainNumber(){
        try {
            JobPlainStatusModel model=new JobPlainStatusModel();
            String JobName="pds"+String.valueOf(System.currentTimeMillis());
            model.setTaskName(JobName);
            model.setTaskType(2);
            model.setSourcePath(srcpath);
            model.setsourceIp(api.ssh_src_ip);
            model.setTargetPath(api.dst_path);
            model.setTargetIp(api.ssh_dst_ip);
            //周期任务 运行一次
            model.setScheduleRunOnce();

            //文件名称不包含
            model.setFileNameScope(2);
            model.setFileName("123123123");



            Date d = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            model.setTaskBeginTime(sdf.format(d));
            model.setScheduleTypeReadOnly(false);
            model.setFilterVisible(false);
            model.setFileSizeUnit("B");
            model.setFileSizeUnitTwo("TB");
            model.setThreadNum(1);

            api.ssh_exec_cmd(api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port,"mkdir  -p   "+srcpath);
            Result res ;

            res = api.Create_file_login(sshcmd,srcpath,"4K","1",".a.");
            res = api.Create_file_login(sshcmd,srcpath,"4K","1","123123123abdeffg....");
            res = api.Create_file_login(sshcmd,srcpath,"4K","1","c..doc12312312,");
            res = api.Create_file_login(sshcmd,srcpath,"4K","1","c.12323123b.$doc.txt");


            model.setSmallBatchCommitNum(1);
            model.setFileCheckType("2");
            model.setBackupType(1);
            System.out.println(JSON.toJSONString(model));
            Result resp=api.CreateJobPlain(JSON.toJSONString(model));
            System.out.println(resp.getBody());
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
            res =api.GetDstFileSha256sum(dstsshcmd,desPathName);
            String dstfilesha256=res.getBody();

            System.out.println("=============================================================");
            res =api.GetSrcFileSha256sum(sshcmd,srcpath);
            String srcfilesha256=res.getBody();
            String[] srclist_tmp=srcfilesha256.replace("\r","").split("\n");
            String[] dstlist_tmp=dstfilesha256.replace("\r","").split("\n");
            for(int i=0;i<dstlist_tmp.length;i++)
            {
                if(api.ssh_src_type.equals("windows")){
                    desPathName=desPathName+"/".replace("//","/");
                }
                dstlist_tmp[i]=dstlist_tmp[i].replace(desPathName,"");
            }
            List<String> srclist= Arrays.asList(srclist_tmp);
            List<String> dstlist= Arrays.asList(dstlist_tmp);
            System.out.println(srclist);
            System.out.println(dstlist);

            Assert.assertTrue(dstlist.size()==3);
            for(int i=0;i<dstlist.size();i++){
                System.out.println("判断当前文件目的路径下的文件是否把源文件都拷贝过去了 "+dstlist.get(i).toString());
                Assert.assertTrue(srclist.contains(dstlist.get(i)));
                Assert.assertTrue(!dstlist.get(i).toString().contains("123123123"));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            sshcmd.exec("rm  -rf    "+srcpath);
        }
    }

    @Test    // 文件名称以 。。。 开始
    @UseDataProvider("dataProviderFilterStartWith")//满足条件进行了备份
    public  void TestCase_CreateJobPlainNopack_RunOnce_filter_By_fileNameStartWith_Number_FilenameContainNumber(String filesymple,String filename){
        try {
            JobPlainStatusModel model=new JobPlainStatusModel();
            String JobName="pds"+String.valueOf(System.currentTimeMillis());
            model.setTaskName(JobName);
            model.setTaskType(2);
            model.setSourcePath(srcpath);
            model.setsourceIp(api.ssh_src_ip);
            model.setTargetPath(api.dst_path);
            model.setTargetIp(api.ssh_dst_ip);
            //周期任务 运行一次
            model.setScheduleRunOnce();

            //文件名称始于
            model.setFileNameScope(3);
            model.setFileName(filesymple);
            Date d = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            model.setTaskBeginTime(sdf.format(d));
            model.setScheduleTypeReadOnly(false);
            model.setFilterVisible(false);
            model.setFileSizeUnit("B");
            model.setFileSizeUnitTwo("TB");
            model.setThreadNum(1);

            sshcmd.exec("mkdir  -p   "+srcpath);
            Result res ;

            res = api.Create_file_login(sshcmd,srcpath,"4K","1","'"+filename+"'");
            //res = api.Create_file(srcpath,api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port,"4K","1","123123123abdeffg....");
            // res = api.Create_file(srcpath,api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port,"4K","1","c..doc12312312,");
            // res = api.Create_file(srcpath,api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port,"4K","1","c.12323123b.$doc.txt");


            model.setSmallBatchCommitNum(1);
            model.setFileCheckType("2");
            model.setBackupType(1);
            System.out.println(JSON.toJSONString(model));
            Result resp=api.CreateJobPlain(JSON.toJSONString(model));
            System.out.println(resp.getBody());
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
            Assert.assertTrue(desPathName!=null);
            res =api.GetDstFileSha256sum(dstsshcmd,desPathName);
            String dstfilesha256=res.getBody();

            System.out.println("=============================================================");
            res =api.GetSrcFileSha256sum(sshcmd,srcpath);
            String srcfilesha256=res.getBody();
            String[] srclist_tmp=srcfilesha256.replace("\r","").split("\n");
            String[] dstlist_tmp=dstfilesha256.replace("\r","").split("\n");
            Assert.assertTrue(dstlist_tmp.length!=0);
            for(int i=0;i<dstlist_tmp.length;i++)
            {
                if(api.ssh_src_type.equals("windows")){
                    desPathName=desPathName+"/".replace("//","/");
                }
                dstlist_tmp[i]=dstlist_tmp[i].replace(desPathName,"");
            }
            List<String> srclist= Arrays.asList(srclist_tmp);
            List<String> dstlist= Arrays.asList(dstlist_tmp);
            System.out.println(srclist);
            System.out.println(dstlist);
            Assert.assertTrue(dstlist.size()==1);
            for(int i=0;i<dstlist.size();i++){
                System.out.println("判断当前文件目的路径下的文件是否把源文件都拷贝过去了 "+dstlist.get(i).toString());
                Assert.assertTrue(srclist.contains(dstlist.get(i)));
                Assert.assertTrue(dstlist.get(i).toString().contains(filename));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            sshcmd.exec("rm  -rf    "+srcpath);
        }
    }


    @Test    // 文件名称以 。。。  结尾
    @UseDataProvider("dataProviderFilterEndWith")//满足条件进行了备份
    public  void TestCase_CreateJobPlainNopack_RunOnce_filter_By_fileNameEndWith_Number_FilenameContainNumber(String filesymple,String filename){
        try {
            JobPlainStatusModel model=new JobPlainStatusModel();
            String JobName="pds"+String.valueOf(System.currentTimeMillis());
            model.setTaskName(JobName);
            model.setTaskType(2);
            model.setSourcePath(srcpath);
            model.setsourceIp(api.ssh_src_ip);
            model.setTargetPath(api.dst_path);
            model.setTargetIp(api.ssh_dst_ip);
            //周期任务 运行一次
            model.setScheduleRunOnce();
            //文件名称始于
            model.setFileNameScope(4);
            model.setFileName(filesymple);
            Date d = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            model.setTaskBeginTime(sdf.format(d));
            model.setScheduleTypeReadOnly(false);
            model.setFilterVisible(false);
            model.setFileSizeUnit("B");
            model.setFileSizeUnitTwo("TB");
            model.setThreadNum(1);
            sshcmd.exec("mkdir  -p   "+srcpath);
            Result res ;
            res = api.Create_file_login(sshcmd,srcpath,"4K","1",filename);
            model.setSmallBatchCommitNum(1);
            model.setFileCheckType("2");
            model.setBackupType(1);
            System.out.println(JSON.toJSONString(model));
            Result resp=api.CreateJobPlain(JSON.toJSONString(model));
            System.out.println("vvvvvvvvvvvvvvvvvvvvvvv创建作业计划返回结果vvvvvvvvvvvvvvvvvvvvvvv");
            System.out.println(resp.getBody());
            System.out.println("^^^^^^^^^^^^^^^^^^^^^^^创建作业计划返回结果^^^^^^^^^^^^^^^^^^^^^^^");
            Assert.assertTrue(api.WaitForJobEnd(JobName));
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
            Assert.assertTrue(desPathName!=null);
            res =api.GetDstFileSha256sum(dstsshcmd,desPathName);
            String dstfilesha256=res.getBody();


            System.out.println("=============================================================");
            res =api.GetSrcFileSha256sum(sshcmd,srcpath);
            String srcfilesha256=res.getBody();
            String[] srclist_tmp=srcfilesha256.replace("\r","").split("\n");
            String[] dstlist_tmp=dstfilesha256.replace("\r","").split("\n");
            Assert.assertTrue(dstlist_tmp.length!=0);
            for(int i=0;i<dstlist_tmp.length;i++)
            {
                if(api.ssh_src_type.equals("windows")){
                    desPathName=desPathName+"/".replace("//","/");
                }
                dstlist_tmp[i]=dstlist_tmp[i].replace(desPathName,"");
            }
            List<String> srclist= Arrays.asList(srclist_tmp);
            List<String> dstlist= Arrays.asList(dstlist_tmp);
            System.out.println(srclist);
            System.out.println(dstlist);
            Assert.assertTrue(dstlist.size()==1);
            for(int i=0;i<dstlist.size();i++){
                System.out.println("判断当前文件目的路径下的文件是否把源文件都拷贝过去了 "+dstlist.get(i).toString());
                Assert.assertTrue(srclist.contains(dstlist.get(i).replace("  "," ")));
                Assert.assertTrue(dstlist.get(i).contains(filename));
            }

        } finally {

            sshcmd.exec("rm  -rf    "+srcpath);
        }
    }


    @Test    //文件属主过滤
    public  void TestCase_CreateJobPlainNopack_RunOnce_filter_By_fileOwnerEqual_FilenameOwner(){
        try {
            JobPlainStatusModel model=new JobPlainStatusModel();
            String JobName="pds"+String.valueOf(System.currentTimeMillis());
            model.setTaskName(JobName);
            model.setTaskType(2);
            model.setSourcePath(srcpath);
            model.setsourceIp(api.ssh_src_ip);
            model.setTargetPath(api.dst_path);
            model.setTargetIp(api.ssh_dst_ip);
            //周期任务 运行一次
            model.setScheduleRunOnce();

            //文件属主过滤
            model.setFileOwnerScope(1);
            model.setFileOwner(JobName);


            Date d = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            model.setTaskBeginTime(sdf.format(d));
            model.setScheduleTypeReadOnly(false);
            model.setFilterVisible(false);
            model.setFileSizeUnit("B");
            model.setFileSizeUnitTwo("TB");
            model.setThreadNum(1);

            sshcmd.exec("mkdir  -p   "+srcpath);
            Result res ;

            res = api.Create_file_login(sshcmd,srcpath,"4K","1","abcd");

            api.ssh_exec(sshcmd,"useradd -g "+JobName);
            Thread.sleep(2000);
            api.ssh_exec(sshcmd,String.format("chown %s.root  %s/%s",JobName,srcpath,"abcd"));

            tmp=JobName;
            System.out.println(res.getBody());

            res = api.Create_file_login(sshcmd,srcpath,"4K","1","123123123abdeffg....");
            res = api.Create_file_login(sshcmd,srcpath,"4K","1","c..doc12312312,");
            res = api.Create_file_login(sshcmd,srcpath,"4K","1","c.12323123b.$doc.txt");


            model.setSmallBatchCommitNum(1);
            model.setFileCheckType("2");
            model.setBackupType(1);
            System.out.println(JSON.toJSONString(model));
            Result resp=api.CreateJobPlain(JSON.toJSONString(model));

            System.out.println("vvvvvvvvvvvvvvvvvvvvvvv创建作业计划返回结果vvvvvvvvvvvvvvvvvvvvvvv\r\n"+resp.getBody()+"^^^^^^^^^^^^^^^^^^^^^^^创建作业计划返回结果^^^^^^^^^^^^^^^^^^^^^^^");


            Assert.assertTrue(api.WaitForJobEnd(JobName));
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




            res =api.GetDstFileSha256sum(dstsshcmd,desPathName);
            String dstfilesha256=res.getBody();

            System.out.println("=============================================================");
            res =api.GetSrcFileSha256sum(sshcmd,srcpath);
            String srcfilesha256=res.getBody();
            String[] srclist_tmp=srcfilesha256.replace("\r","").split("\n");
            String[] dstlist_tmp=dstfilesha256.replace("\r","").split("\n");
            for(int i=0;i<dstlist_tmp.length;i++)
            {
                if(api.ssh_src_type.equals("windows")){
                    desPathName=desPathName+"/".replace("//","/");
                }
                dstlist_tmp[i]=dstlist_tmp[i].replace(desPathName,"");
            }
            List<String> srclist= Arrays.asList(srclist_tmp);
            List<String> dstlist= Arrays.asList(dstlist_tmp);
            System.out.println(srclist);
            System.out.println(dstlist);

            Assert.assertTrue(dstlist.size()==1);
            for(int i=0;i<dstlist.size();i++){
                System.out.println("判断当前文件目的路径下的文件是否把源文件都拷贝过去了 "+dstlist.get(i).toString());
                Assert.assertTrue(srclist.contains(dstlist.get(i)));
                Assert.assertTrue(dstlist.get(i).toString().contains("abcd"));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            api.ssh_exec(sshcmd,"rm  -rf    "+srcpath);
            api.ssh_exec(sshcmd,"userdel  -r   "+tmp);
        }
    }


    @Test    //文件属主过滤不包含
    public  void TestCase_CreateJobPlainNopack_RunOnce_filter_By_fileOwnerNotEqual_FilenameOwner(){
        try {
            JobPlainStatusModel model=new JobPlainStatusModel();
            String JobName="pds"+String.valueOf(System.currentTimeMillis());
            model.setTaskName(JobName);
            model.setTaskType(2);
            model.setSourcePath(srcpath);
            model.setsourceIp(api.ssh_src_ip);
            model.setTargetPath(api.dst_path);
            model.setTargetIp(api.ssh_dst_ip);
            //周期任务 运行一次
            model.setScheduleRunOnce();
            //文件属主过滤
            model.setFileOwnerScope(2);
            model.setFileOwner(JobName);
            Date d = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            model.setTaskBeginTime(sdf.format(d));
            model.setScheduleTypeReadOnly(false);
            model.setFilterVisible(false);
            model.setFileSizeUnit("B");
            model.setFileSizeUnitTwo("TB");
            model.setThreadNum(1);
            sshcmd.exec("mkdir  -p   "+srcpath);
            Result res ;
            res = api.Create_file_login(sshcmd,srcpath,"4K","1","abcd");
            api.ssh_exec(sshcmd,"useradd -g root "+JobName);
            Thread.sleep(2000);
            api.ssh_exec(sshcmd,String.format("chown %s.root  %s/%s",JobName,srcpath,"abcd"));
            tmp=JobName;
            System.out.println(res.getBody());
            res = api.Create_file_login(sshcmd,srcpath,"4K","1","123123123abdeffg....");
            res = api.Create_file_login(sshcmd,srcpath,"4K","1","c..doc12312312,");
            res = api.Create_file_login(sshcmd,srcpath,"4K","1","c.12323123b.$doc.txt");
            model.setSmallBatchCommitNum(1);
            model.setFileCheckType("2");
            model.setBackupType(1);
            System.out.println(JSON.toJSONString(model));
            Result resp=api.CreateJobPlain(JSON.toJSONString(model));
            System.out.println("vvvvvvvvvvvvvvvvvvvvvvv创建作业计划返回结果vvvvvvvvvvvvvvvvvvvvvvv\r\n"+resp.getBody()+"^^^^^^^^^^^^^^^^^^^^^^^创建作业计划返回结果^^^^^^^^^^^^^^^^^^^^^^^");
            Assert.assertTrue(api.WaitForJobEnd(JobName));
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
            res =api.GetDstFileSha256sum(dstsshcmd,desPathName);
            String dstfilesha256=res.getBody();
            System.out.println("=============================================================");
            res =api.GetSrcFileSha256sum(sshcmd,srcpath);
            String srcfilesha256=res.getBody();
            String[] srclist_tmp=srcfilesha256.replace("\r","").split("\n");
            String[] dstlist_tmp=dstfilesha256.replace("\r","").split("\n");
            for(int i=0;i<dstlist_tmp.length;i++)
            {
                if(api.ssh_src_type.equals("windows")){
                    desPathName=desPathName+"/".replace("//","/");
                }
                dstlist_tmp[i]=dstlist_tmp[i].replace(desPathName,"");
            }
            List<String> srclist= Arrays.asList(srclist_tmp);
            List<String> dstlist= Arrays.asList(dstlist_tmp);
            System.out.println(srclist);
            System.out.println(dstlist);
            Assert.assertTrue(dstlist.size()==3);
            for(int i=0;i<dstlist.size();i++){
                System.out.println("判断当前文件目的路径下的文件是否把源文件都拷贝过去了 "+dstlist.get(i).toString());
                Assert.assertTrue(srclist.contains(dstlist.get(i)));
                Assert.assertTrue(!dstlist.get(i).toString().contains("abcd"));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            api.ssh_exec(sshcmd,"rm  -rf    "+srcpath);
            api.ssh_exec(sshcmd,"userdel  -r   "+tmp);
        }
    }


}
