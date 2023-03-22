package testapi.BackToolTest.JobPlain;
import com.alibaba.fastjson.JSON;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import testapi.common.JobLogRespModel;
import testapi.common.JobPlainStatusModel;
import testapi.common.Result;
import testapi.common.api.ApiTest;
import testapi.framework.SSHExecutor;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class CreateJobBackPlainDifferenceBackupTest {
	static ApiTest api=new ApiTest();
	String srcpath=api.src_path+"/filter";
	static SSHExecutor sshcmd=null;
	private static SSHExecutor dstsshcmd;
	String tmp="";
	String fullbackupname="";
	int  fullbackupid=0;
	static  String medieId;
	static String  CliId;
	@BeforeClass
	public  static void beforeclass(){

		sshcmd=api.ssh_login(api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port);
		sshcmd.setHost_type(api.ssh_src_type);
		dstsshcmd=api.ssh_login(api.ssh_dest_username,api.ssh_dest_password, api.ssh_dst_ip, api.ssh_dest_port);
		dstsshcmd.setHost_type(api.ssh_dst_type);
		medieId=api.Create_BackMedie(api.ssh_dst_ip,api.dst_path);
		CliId= api.Create_Client(api.ssh_src_ip);
	}


	@AfterClass
	public static void afterclass(){

	}

	private  Boolean  CreateJobPlainFullBackupFilter_RunOnce(){
		try {
			JobPlainStatusModel model=new JobPlainStatusModel();
			String JobName="pds"+String.valueOf(System.currentTimeMillis());
			fullbackupname=JobName;
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
			//model.setDataSourceType("[\"普通文件\"]");
			model.setMediumId(Integer.valueOf(medieId));
			model.setClientId(Integer.valueOf(CliId));
			api.ssh_exec(sshcmd,"mkdir  -p   "+srcpath);
			Result res ;
			res = api.Create_file_login(sshcmd,srcpath,"4K","1","abcd");

			api.ssh_exec(sshcmd,"useradd  "+JobName);
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

			JobLogRespModel  joblog=api.GetJobLog(JobName);
			String desPathName=null;
			ArrayList list=new ArrayList();
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
			fullbackupid=Integer.valueOf(joblog.getContent().get(0).getJobId());

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

			Assert.assertTrue(dstlist.size()==3);
			for(int i=0;i<dstlist.size();i++){
				System.out.println("判断当前文件目的路径下的文件是否把源文件都拷贝过去了 "+dstlist.get(i).toString());
				Assert.assertTrue(srclist.contains(dstlist.get(i)));
				Assert.assertTrue(!dstlist.get(i).toString().contains("abcd"));
			}
			return true;
		} catch (InterruptedException e) {
			e.printStackTrace();
			api.ssh_exec(sshcmd,"rm  -rf    "+srcpath);
			api.ssh_exec(sshcmd,"userdel  -r   "+tmp);
			return false;
		}

	}

	private  Boolean  CreateJobPlainFullBackup_RunOnce(){
		try {
			JobPlainStatusModel model=new JobPlainStatusModel();
			String JobName="pds"+String.valueOf(System.currentTimeMillis());
			fullbackupname=JobName;
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
			model.setMediumId(Integer.valueOf(medieId));
			model.setClientId(Integer.valueOf(CliId));
			api.ssh_exec(sshcmd,"mkdir  -p   "+srcpath);
			Result res ;

			res = api.Create_file_login(sshcmd,srcpath,"4K","1","abcd");

			api.ssh_exec(sshcmd,"useradd -g root "+JobName);

			Thread.sleep(2000);
			api.ssh_exec(sshcmd,String.format("chown %s.root  %s/%s",JobName,srcpath.replace("/","//"),"abcd"));

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

			JobLogRespModel  joblog=api.GetJobLog(JobName);
			String desPathName=null;
			ArrayList list=new ArrayList();
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

			fullbackupid=Integer.valueOf(joblog.getContent().get(0).getJobId());
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
			Assert.assertTrue(dstlist.size()==3);
			for(int i=0;i<dstlist.size();i++){
				System.out.println("判断当前文件目的路径下的文件是否把源文件都拷贝过去了 "+dstlist.get(i).toString());
				Assert.assertTrue(srclist.contains(dstlist.get(i)));
				Assert.assertTrue(!dstlist.get(i).toString().contains("abcd"));
			}
			return true;
		} catch (InterruptedException e) {
			e.printStackTrace();
			api.ssh_exec(sshcmd,"rm  -rf    "+srcpath);
			api.ssh_exec(sshcmd,"userdel  -r   "+tmp);
			return false;
		}

	}


	@Test    //差异备份
	public  void TestCase_CreateJobPlainDifferenceBackup_RunOnce(){
		try {
			Assert.assertTrue(this.CreateJobPlainFullBackup_RunOnce());
			JobPlainStatusModel model=new JobPlainStatusModel();
			String JobName="pds"+String.valueOf(System.currentTimeMillis());
			model.setTaskName(JobName);
			//1
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
			//1为全量备份，2为增量备份，3为差异备份
			model.setBackupType(3);
			model.setjobName(fullbackupname);
			model.setFileCheckType("3");
			Date d = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
			model.setTaskBeginTime(sdf.format(d));
			model.setScheduleTypeReadOnly(false);
			model.setFilterVisible(false);
			model.setFileSizeUnit("B");
			model.setFileSizeUnitTwo("TB");
			model.setThreadNum(1);
			model.setMediumId(Integer.valueOf(medieId));
			model.setClientId(Integer.valueOf(CliId));
			api.ssh_exec(sshcmd,"mkdir  -p   "+srcpath);
			Result res ;
			res = api.Create_file_login(sshcmd,srcpath,"4K","1","abcd");
			api.ssh_exec(sshcmd,"useradd  "+JobName);
			tmp=JobName;
			System.out.println(res.getBody());
			res = api.Create_file_login(sshcmd,srcpath,"4K","1","123123123abdeff");
			api.ssh_exec(sshcmd,String.format("chown %s.%s  %s/%s",JobName,JobName,srcpath,"123123123abdeff"));

			model.setSmallBatchCommitNum(1);
			model.setFileCheckType("4");

			model.setBackupId(fullbackupid);

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
				dstlist_tmp[i]=dstlist_tmp[i].replace(desPathName,"");
			}
			List<String> srclist= Arrays.asList(srclist_tmp);
			List<String> dstlist= Arrays.asList(dstlist_tmp);
			System.out.println(srclist);
			System.out.println(dstlist);

			//Assert.assertTrue(dstlist.size()==1);
			for(int i=0;i<dstlist.size();i++){
				System.out.println("判断当前文件目的路径下的文件是否把源文件都拷贝过去了 "+dstlist.get(i).toString());
				Assert.assertTrue(srclist.contains(dstlist.get(i)));
				Assert.assertTrue(!dstlist.get(i).toString().contains("123123123abdeff"));
			}
		} finally {
			api.ssh_exec(sshcmd,"rm  -rf    "+srcpath);
			api.ssh_exec(sshcmd,"userdel  -r   "+tmp);
		}
	}


}
