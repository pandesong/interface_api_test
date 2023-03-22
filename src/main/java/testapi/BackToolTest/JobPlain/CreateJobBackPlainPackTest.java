package testapi.BackToolTest.JobPlain;

import com.alibaba.fastjson.JSON;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import testapi.common.JobLogRespModel;
import testapi.common.JobPlainStatusModel;
import testapi.common.JobStatusRespModel;
import testapi.common.Result;
import testapi.common.api.ApiTest;
import testapi.framework.SSHExecutor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class CreateJobBackPlainPackTest {

    static  ApiTest api=new ApiTest();
    static   String srcpath=api.src_path+"/filter";
    private static SSHExecutor dstsshcmd;
    private static SSHExecutor sshcmd;
    static  String medieId;
    static String  CliId;
    @BeforeClass
    public static void beforeclass(){
        sshcmd=api.ssh_login(api.ssh_dest_username,api.ssh_dest_password, api.ssh_dst_ip, api.ssh_dest_port);
        dstsshcmd=api.ssh_login(api.ssh_dest_username,api.ssh_dest_password, api.ssh_dst_ip, api.ssh_dest_port);
        if(api.ssh_src_type.equals("windows")){
            srcpath= srcpath.replace("/","\\");
        }
        medieId=api.Create_BackMedie(api.ssh_dst_ip,api.dst_path);
        CliId= api.Create_Client(api.ssh_src_ip);
    }

    @AfterClass
    public static void afterclass(){

    }

    @Test
    public  void TestCase_CreateJobPlainNopack_RunOnce_Packing_FileNum1_Size10GB(){

        try {
            JobPlainStatusModel Model=new JobPlainStatusModel();
            String JobName="Packing-"+String.valueOf(System.currentTimeMillis());
            Model.setTaskName(JobName);
            Model.setTaskType(2);
            Model.setSourcePath(api.src_path);
            Model.setsourceIp(api.ssh_src_ip);
            Model.setTargetPath(api.dst_path);
            Model.setTargetIp(api.ssh_dst_ip);
            Model.setScheduleType(0);
            Model.setIsPackaging("2");
            Model.setPackingLevel("0");
            Model.setPackingSize(10);
            Model.setPackingSizeUnit("GB");
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
            api.ssh_exec_cmd(api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port,"mkdir  -p   "+srcpath);
            String filename="no_back_file_1";
            Result res =api.Create_file(srcpath,api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port,"10M","1",filename);
            res =api.Create_file(srcpath,api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port,"10M","1","back_file");
            Model.setSmallBatchCommitNum(1);
            Model.setFileCheckType("2");
            Model.setBackupType(1);
            System.out.println(JSON.toJSONString(Model));
            Result resp=api.CreateJobPlain(JSON.toJSONString(Model));
            if(resp.getCode()==400) {
                System.out.println("创建备份计划失败");
                Assert.assertTrue(false);
            }
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
            if(desPathName==null) Assert.assertTrue(false);
            api.ssh_exec_cmd(api.ssh_dest_username,api.ssh_dest_password, api.ssh_dst_ip, api.ssh_dest_port,"cat "+desPathName+"/*.tar.*   > "+desPathName+"/pppp.tar");
            api.ssh_exec_cmd(api.ssh_dest_username,api.ssh_dest_password, api.ssh_dst_ip, api.ssh_dest_port,"rm  -rf "+desPathName+"/*.tar.*");
            api.ssh_exec_cmd(api.ssh_dest_username,api.ssh_dest_password, api.ssh_dst_ip, api.ssh_dest_port,"cd  "+desPathName+"&&tar  -xvf  "+"  pppp.tar");
            api.ssh_exec_cmd(api.ssh_dest_username,api.ssh_dest_password, api.ssh_dst_ip, api.ssh_dest_port,"rm  -rf  "+desPathName+"/pppp.tar");
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
                //System.out.println(dstlist_tmp[i]);
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
            } else {
                Assert.assertTrue(false);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            api.ssh_exec_cmd(api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port,"rm  -rf    "+srcpath);
        }
    }
    @Test
    public  void TestCase_CreateJobPlainNopack_RunOnce_Packing_FileNum1_Size1MB(){
        try {
            JobPlainStatusModel Model=new JobPlainStatusModel();
            String JobName="Packing-"+String.valueOf(System.currentTimeMillis());
            Model.setTaskName(JobName);
            Model.setTaskType(2);
            Model.setSourcePath(srcpath);
            Model.setsourceIp(api.ssh_src_ip);
            Model.setTargetPath(api.dst_path);
            Model.setTargetIp(api.ssh_dst_ip);
            Model.setScheduleType(0);
            Model.setIsPackaging("2");
            Model.setPackingLevel("0");
            Model.setPackingSize(1);
            Model.setPackingSizeUnit("MB");
            System.currentTimeMillis();
            Date d = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            Model.setTaskBeginTime(sdf.format(d));
            Model.setScheduleTypeReadOnly(false);
            Model.setFilterVisible(false);
            Model.setFileSizeUnit("MB");
            Model.setThreadNum(1);
            api.ssh_exec_cmd(api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port,"mkdir  -p   "+srcpath);
            String filename="no_back_file_1";
            Result res =api.Create_file(srcpath,api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port,"10M","1",filename);
            api.Create_file(srcpath,api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port,"10M","1","back_file");
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
            System.out.println(" exec cmd :  "+"cat "+desPathName+"/*.tar.*   > "+desPathName+"/pppp.tar&&ls  -l "+desPathName );
            api.ssh_exec_cmd(api.ssh_dest_username,api.ssh_dest_password, api.ssh_dst_ip, api.ssh_dest_port,"cat "+desPathName+"/*.tar.*   > "+desPathName+"/pppp.tar&&ls  -l "+desPathName);
            api.ssh_exec_cmd(api.ssh_dest_username,api.ssh_dest_password, api.ssh_dst_ip, api.ssh_dest_port,"rm  -rf "+desPathName+"/*.tar.*&&ls  -l");

            api.ssh_exec_cmd(api.ssh_dest_username,api.ssh_dest_password, api.ssh_dst_ip, api.ssh_dest_port,"cd  "+desPathName+"&&tar  -xvf  "+"  pppp.tar&&ls  -l");

            System.out.println(" rm  tar file pppp.tar   " );
            api.ssh_exec_cmd(api.ssh_dest_username,api.ssh_dest_password, api.ssh_dst_ip, api.ssh_dest_port,"rm  -rf  "+desPathName+"/pppp.tar&&ls  -l");

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
                //System.out.println(dstlist_tmp[i]);
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
            } else Assert.assertTrue(false);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            api.ssh_exec_cmd(api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port,"rm  -rf    "+srcpath);

        }
    }


}