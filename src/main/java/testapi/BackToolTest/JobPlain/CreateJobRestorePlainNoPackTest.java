package testapi.BackToolTest.JobPlain;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import testapi.common.JobLogRespModel;
import testapi.common.JobPlainStatusModel;
import testapi.common.JobStatusRespModel;
import testapi.common.RestoreTask.dataRevertListResponse;
import testapi.common.RestoreTask.dataRevertRequest;
import testapi.common.Result;
import testapi.common.api.ApiTest;
import testapi.framework.SSHExecutor;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CreateJobRestorePlainNoPackTest {


	private static SSHExecutor sshcmd;
	private static SSHExecutor dstsshcmd;
	static  ApiTest api=new ApiTest();
	static String RestoreDir=api.src_path+"/RestoreDir";
	String FileName="";
	static  String medieId;
	static String  CliId;

	@BeforeClass
	public static void beforeclass(){

		sshcmd=api.ssh_login(api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port);
		sshcmd.setHost_type(api.ssh_src_type);
		dstsshcmd=api.ssh_login(api.ssh_dest_username,api.ssh_dest_password, api.ssh_dst_ip, api.ssh_dest_port);
		dstsshcmd.setHost_type(api.ssh_dst_type);
		if(api.ssh_src_type.equals("windows")){
			RestoreDir= RestoreDir.replace("/","\\");
		}
		medieId=api.Create_BackMedie(api.ssh_dst_ip,api.dst_path);
		CliId= api.Create_Client(api.ssh_src_ip);
	}
	@AfterClass
	public static void afterclass(){

	}

	public  void CreateJobPlainNopack_RunOnce_filesize0(){
		try {
			if(sshcmd.getHost_type().equals("linux")) {
				RestoreDir = api.src_path + "/RestoreDir";
				FileName=RestoreDir+"/zero_file";
			}else {
				RestoreDir = api.src_path + "\\RestoreDir";
				FileName=RestoreDir+"\\zero_file";
			}
			JobPlainStatusModel Model=new JobPlainStatusModel();
			String JobName="backfile_to_restore"+String.valueOf(System.currentTimeMillis());
			api.Restore_tmp_JobName=JobName;
			Model.setTaskName(JobName);
			Model.setTaskType(2);
			Model.setSourcePath(RestoreDir);
			Model.setsourceIp(api.ssh_src_ip);
			Model.setTargetPath(api.dst_path);
			Model.setTargetIp(api.ssh_dst_ip);
			Model.setScheduleType(0);
			System.currentTimeMillis();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
			Model.setTaskBeginTime(sdf.format(System.currentTimeMillis()));
			Model.setScheduleTypeReadOnly(false);
			Model.setFilterVisible(false);
			Model.setFileSizeUnit("MB");
			Model.setThreadNum(1);
			api.ssh_exec(sshcmd,"mkdir  -p   "+RestoreDir);
			api.ssh_exec(sshcmd,"touch    "+FileName);
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
			Result res =api.GetDstFileSha256sum(sshcmd,desPathName);
			String dstfilesha256=res.getBody();
			System.out.println("=============================================================");
			res =api.GetSrcFileSha256sum(sshcmd,RestoreDir);
			String srcfilesha256=res.getBody();
			String[] srclist_tmp=srcfilesha256.replace("\r","").split("\n");
			String[] dstlist_tmp=dstfilesha256.replace("\r","").split("\n");
			for(int i=0;i<dstlist_tmp.length;i++)
			{
				dstlist_tmp[i]=dstlist_tmp[i].replace(desPathName,"");
				System.out.println(dstlist_tmp[i]);
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

		}
	}

	public  void CreateJobPlainNopack_RunOnce_filesize_NoN0(){
		try {
			if(sshcmd.getHost_type().equals("linux")) {
				RestoreDir = api.src_path + "/RestoreDir";
				FileName=RestoreDir+"/zero_file";
			}else {
				RestoreDir = api.src_path + "\\RestoreDir";
				FileName=RestoreDir+"\\zero_file";
			}
			JobPlainStatusModel Model=new JobPlainStatusModel();
			String JobName="backfile_to_restore"+String.valueOf(System.currentTimeMillis());
			api.Restore_tmp_JobName=JobName;
			Model.setTaskName(JobName);
			Model.setTaskType(2);
			Model.setSourcePath(RestoreDir);
			Model.setsourceIp(api.ssh_src_ip);
			Model.setTargetPath(api.dst_path);
			Model.setTargetIp(api.ssh_dst_ip);
			Model.setScheduleType(0);
			System.currentTimeMillis();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
			Model.setTaskBeginTime(sdf.format(System.currentTimeMillis()));
			Model.setScheduleTypeReadOnly(false);
			Model.setFilterVisible(false);
			Model.setFileSizeUnit("MB");
			Model.setThreadNum(1);
			api.ssh_exec_cmd(api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port,"mkdir  -p   "+RestoreDir);
			System.out.println(RestoreDir);
			api.ssh_exec_cmd(api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port,"touch    "+FileName);
			api.ssh_exec_cmd(api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port,"echo  asdfsdfsdfdsfds >    "+FileName);
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
			Result res =api.GetDstFileSha256sum(dstsshcmd,desPathName);
			String dstfilesha256=res.getBody();
			System.out.println("=============================================================");
			res =api.GetSrcFileSha256sum(sshcmd,RestoreDir);
			String srcfilesha256=res.getBody();
			String[] srclist_tmp=srcfilesha256.replace("\r","").split("\n");
			String[] dstlist_tmp=dstfilesha256.replace("\r","").split("\n");
			for(int i=0;i<dstlist_tmp.length;i++)
			{
				dstlist_tmp[i]=dstlist_tmp[i].replace(desPathName,"");
				System.out.println(dstlist_tmp[i]);
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

		}

	}

	@Test
	public  void CreateJobRestorePlain_NoPack_RestoreToOriginalPath_Cover_RestoreAll_ZeroFile()  {
		try{
			CreateJobPlainNopack_RunOnce_filesize0();
			String RestoreName="OriginalPath_Cover_RestoreAll"+String.valueOf(System.currentTimeMillis()/1000);
			dataRevertRequest  revertrequest=new dataRevertRequest();
			revertrequest.setFileCheckType("2");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
			revertrequest.setTaskBeginTime(sdf.format(System.currentTimeMillis()+30*1000));
			JobStatusRespModel jobDetail=api.GetJobDetailByName(api.Restore_tmp_JobName);
			System.out.println(JSONObject.toJSONString(jobDetail));
			String JobId=jobDetail.getContent().get(0).getJobId();
			String jobNameWeb=jobDetail.getContent().get(0).getJobNameWeb();
			String ip=jobDetail.getContent().get(0).getSourceIp();
			revertrequest.setJobName(jobNameWeb);
			revertrequest.setBackupId(JobId);
			revertrequest.setIp(ip);
			revertrequest.setTaskName(RestoreName);
			api.ssh_exec(sshcmd," echo 131321 > "+FileName);
			Result res=api.ssh_exec(sshcmd,"ls  -s  "+FileName+"|awk '{ print $1}' ");
			System.out.println(res.getBody().trimCLRF());
			Assert.assertTrue(Integer.valueOf(res.getBody().trimCLRF())!=0);
			Result result=api.CreateRestorePlain(JSONObject.toJSONString(revertrequest));
			System.out.println(result.getBody());
			int count=1;
			while (count<1800){
				dataRevertListResponse Restore=api.GetRestoreLitsByName(RestoreName);
				if(Restore.getContent().get(0).getRunStatus()!=null)

					if(Restore.getContent().get(0).getRunStatus().contains("R")||Restore.getContent().get(0).getRunStatus().contains("C"))
					{
						Thread.sleep(3000);
					}else if(Restore.getContent().get(0).getRunStatus().contains("T")){
						break;
					}
					else {
						System.out.println("还原状态既不是 R 也不是 T,认为是失败状态："+Restore.getContent().get(0).getRunStatus());
						Assert.assertTrue(false);
						break;
					}
				else {
					Thread.sleep(3000);
				}
			}
			if(count>=1800)
			{
				System.out.println("time out ");
				Assert.assertTrue(false);
			}
			res=api.ssh_exec(sshcmd,"ls  -s  "+FileName+"|awk '{ print $1}' ");

			System.out.println(res.getBody());
//欢迎之后文件大小为0
			Assert.assertTrue(Integer.valueOf(res.getBody().trimCLRF())==0);


		}catch (InterruptedException e) {

			e.printStackTrace();

		}finally {

			Result res=api.ssh_exec(sshcmd,"rm  -rf  "+FileName);
		}
	}

	@Test
	public  void CreateJobRestorePlain_NoPack_RestoreToOriginalPath_Cover_RestoreAll_NoZeroFILE()  {
		try{
			CreateJobPlainNopack_RunOnce_filesize_NoN0();
			String RestoreName="OriginalPath_Cover_RestoreAll"+String.valueOf(System.currentTimeMillis()/1000);
			dataRevertRequest  revertrequest=new dataRevertRequest();
			revertrequest.setFileCheckType("2");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
			revertrequest.setTaskBeginTime(sdf.format(System.currentTimeMillis()+30*1000));
			JobStatusRespModel jobDetail=api.GetJobDetailByName(api.Restore_tmp_JobName);
			System.out.println(JSONObject.toJSONString(jobDetail));
			String JobId=jobDetail.getContent().get(0).getJobId();
			String jobNameWeb=jobDetail.getContent().get(0).getJobNameWeb();
			String ip=jobDetail.getContent().get(0).getSourceIp();
			revertrequest.setJobName(jobNameWeb);
			revertrequest.setBackupId(JobId);
			revertrequest.setIp(ip);
			revertrequest.setTaskName(RestoreName);
			//使得源文件大小为0
			api.ssh_exec(sshcmd,"dd if=/dev/null of="+FileName);
			Result res=api.ssh_exec(sshcmd,"ls  -s  "+FileName+"|awk '{ print $1}' ");
			System.out.println(res.getBody());
			Assert.assertTrue(Integer.valueOf(res.getBody().trimCLRF())==0);
			Result result=api.CreateRestorePlain(JSONObject.toJSONString(revertrequest));
			System.out.println(result.getBody());
			int count=1;
			while (count<1800){
				dataRevertListResponse Restore=api.GetRestoreLitsByName(RestoreName);
				System.out.println("the status is :"+Restore.getContent().get(0).getRunStatus());
				if(Restore.getContent().get(0).getRunStatus()!=null) {
					if(Restore.getContent().get(0).getRunStatus().contains("R")||Restore.getContent().get(0).getRunStatus().contains("C"))
					{
						Thread.sleep(3000);
					}else if(Restore.getContent().get(0).getRunStatus().contains("T")){
						break;
					}
					else {
						System.out.println("还原状态既不是 R 也不是 T,认为是失败状态："+Restore.getContent().get(0).getRunStatus());
						Assert.assertTrue(false);
						break;
					}
				} else {
					Thread.sleep(3000);
				}
			}
			if(count>=1800)
			{
				System.out.println("time out ");
				Assert.assertTrue(false);
			}
			res=api.ssh_exec(sshcmd,"ls  -s  "+FileName+"|awk '{ print $1}' ");
			System.out.println(res.getBody());
			Assert.assertTrue(Integer.valueOf(res.getBody().trimCLRF())!=0);
		}catch (InterruptedException e) {
			e.printStackTrace();
		}finally {
			Result res=api.ssh_exec(sshcmd,"rm  -rf  "+FileName);
		}
	}


}
