package testapi.common.apiManager;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.binary.Base64;
import testapi.common.DistributedStorage.GroupListRespModel;
import testapi.common.DistributedStorage.StoreagePoolUnusedUintResponsedetailModel;
import testapi.common.PropertiesLoader;
import testapi.common.Result;
import testapi.framework.SSHExecutor;
import testapi.framework.http_api;
import testapi.framework.*;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DapiTest  {


	PropertiesLoader   properties=new PropertiesLoader();
	public String  url= PropertiesLoader.GetPropertie("BASE_URL");
	private Map<String, String> head=new HashMap<String, String>();
	Map<String, String> head1=new HashMap<String, String>();
	http_api https = new http_api();
	libuploaddownload up=new libuploaddownload();

	public String   api_vcode_url =PropertiesLoader.GetPropertie("api_vcode_url");
	String  api_username= PropertiesLoader.GetPropertie("api_username");
	String  api_password= PropertiesLoader.GetPropertie("api_password");
	public String  poolname= PropertiesLoader.GetPropertie("poolname");
	public	String  groupname= PropertiesLoader.GetPropertie("groupname");
	public String   user_name_data=System.getProperty("user.dir")+"\\config\\user_name_data.xls";
	public String    host_list=System.getProperty("user.dir")+"\\config\\hostlist.xls";
	public String    data_file_list=System.getProperty("user.dir")+"\\config\\data_file_list.xls";
	public  String dst_path=PropertiesLoader.GetPropertie("dst_path").toLowerCase();
	public String src_path=PropertiesLoader.GetPropertie("src_path").toLowerCase();
	public String ssh_dest_port=PropertiesLoader.GetPropertie("ssh_dest_port");
	public String ssh_src_port=PropertiesLoader.GetPropertie("ssh_src_port");
	public String ssh_dst_type=PropertiesLoader.GetPropertie("dest_host_type");
	public String ssh_src_type=PropertiesLoader.GetPropertie("src_host_type");
	public String ssh_src_ip=PropertiesLoader.GetPropertie("ssh_src_ip");
	public String cluster01_ip=PropertiesLoader.GetPropertie("cluster01_ip");
	public  String ssh_dst_ip=PropertiesLoader.GetPropertie("ssh_dest_ip");
	public  String  ssh_src_username=PropertiesLoader.GetPropertie("ssh_src_username");
	public String   ssh_src_password=PropertiesLoader.GetPropertie("ssh_src_password");
	public String ssh_dest_username=PropertiesLoader.GetPropertie("ssh_dest_username");
	public String ssh_dest_password=PropertiesLoader.GetPropertie("ssh_dest_password");
	public String  ssh_login_src_ip   =PropertiesLoader.GetPropertie("ssh_login_src_ip");
	public String   src_login_path =PropertiesLoader.GetPropertie("src_login_path");
	public String database_ip=PropertiesLoader.GetPropertie("database_ip");
	public int database_port=Integer.valueOf(PropertiesLoader.GetPropertie("database_port"));
    public String database_username=PropertiesLoader.GetPropertie("database_username");
	 public String database_password=PropertiesLoader.GetPropertie("database_password");
	 public String database_dbname=PropertiesLoader.GetPropertie("database_dbname");


	public String hosts_xls=System.getProperty("user.dir")+"\\config\\hosts.xls";
	public String Restore_tmp_JobName="pds1618540809025";

	public  String encrypt(byte[] data)
	{
		try {
			final String RSA = "RSA";
			final String ECB_PKCS1_PADDING = "RSA/ECB/PKCS1Padding";
			final int DEFAULT_KEY_SIZE = 1024;
			final byte[] DEFAULT_SPLIT = "#PART#".getBytes();
			final int DEFAULT_BUFFERSIZE = (DEFAULT_KEY_SIZE / 8) - 11;
			RSAPrivateKey privateKey; // 私钥
			byte[] publicKey = Base64.decodeBase64(("MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBANL378k3RiZHWx5AfJqdH9xRNBmD9wGD\n2iRe41HdTNF8RUhNnHit5NpMNtGL0NPTSSpPjjI1kJfVorRvaQerUgkCAwEAAQ=="));
			if (publicKey == null) {
				System.out.println("加密公钥为空, 请设置");
			}
			Cipher cipher = null;
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKey);
			KeyFactory kf = KeyFactory.getInstance(RSA);
			PublicKey keyPublic = kf.generatePublic(keySpec);
			Cipher cp = Cipher.getInstance(RSA);
			cp.init(Cipher.ENCRYPT_MODE, keyPublic);
			String pwd=Base64.encodeBase64String(cp.doFinal(data));
			return pwd;
		} catch (NoSuchAlgorithmException e) {
			System.out.println("加密公钥为空, 请设置");
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
			return null;
		} catch (InvalidKeyException e) {
			System.out.println("加密公钥为空, 请设置");

		} catch (IllegalBlockSizeException e) {
			System.out.println("明文长度非法");

		} catch (BadPaddingException e) {
			System.out.println("明文数据已损坏");

		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}
		return null;
	}



	class OutSream extends   Thread{
		InputStream  is;
		String type;
		public   OutSream(InputStream is,String  type){
			this.is=is;
			this.type=type;
		}
		public void run(){
			String line = "";
			try{
				InputStreamReader  isr=new InputStreamReader(is);
				BufferedReader  br=new BufferedReader(isr);
				String  tmp=null;
				while ((tmp=br.readLine())!=null){
					if(type.equals("Error")){

						line=tmp+line;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			Result resp=new Result();
			resp.setCode(200);
			resp.setBody(line.replace("null",""));

		}

	}





	static   public Result cmd_command(String cmd){
		String line = "";
		BufferedReader br = null;
		BufferedReader br1 = null;
		try {
			Process p = Runtime.getRuntime().exec(cmd);
			br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			br1 = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			String tmp=null;
			while ((tmp = br.readLine()) != null) {
				line=line+tmp;
			}
			if(tmp==null)
			{
				while ((tmp = br1.readLine()) != null) {
					line=line+tmp;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		Result resp=new Result();
		resp.setCode(200);
		resp.setBody(line.replace("null",""));
		return resp;
	}

	public Result GetvCode( ){
		Result resp= new Result();
		resp  = up.get(api_vcode_url+"/auth/vCode",head," ");
		return resp;


	}

	public Result GetvCode1(Map<String, String> head1 ){
		try {

			Result resp= new Result();
			resp = up.get(api_vcode_url+"/auth/vCode",head1,"");
			return resp;
		} catch (Exception e) {

			//e.printStackTrace();
			return null;
		}

	}

	public  String GetToken(String username,String password) throws InterruptedException {
		Result resp=new Result();
		head1.put("Origin",api_vcode_url);
		head1.put("Host",api_vcode_url.split("//")[1]);
	    //	head1.put("Accept","application/json");
		head1.put("Referer",api_vcode_url+"/login?redirect=%2F");
		Result res=null;
		for(int i=0;i<100;i++){
		res=GetvCode1(head1);
		if(res!=null)
			break;
		Thread.sleep(1000);

		}
		System.out.println(res.getHead());
		System.out.println(res.getCode());
        System.out.println(res.getBody());
		JSONObject b=JSONObject.parseObject(res.getBody());
		System.out.println(b.get("img").toString());
		String uuid=b.get("uuid").toString();
		res=cmd_command("python  plib\\test.py  " +b.get("img").toString());
		//res.setBody("1234");
		System.out.println(res.getBody());
		if(res.getBody()==null){
			res.setCode(400);
			head1.remove("Authorization");
			head1.remove("Cookie");
			return null;
		}
		head1.put("Content-Type","application/json");
		head1.put("Host",api_vcode_url.split("//")[1]);
		String Vcode=res.getBody().replace("null","");
		String pwd=encrypt(password.getBytes());
		String body="{\"username\":\""+username+"\",\"password\":\""+pwd+"\",\"code\":\""+Vcode+"\",\"uuid\":\""+uuid+"\"}";
		try{
		resp = https.http_post(api_vcode_url+"/auth/login",head1,body);
		if(resp.getCode()==400){

			return null;
		}
		}
		catch (Exception e){
			return null;
		}
		System.out.println(JSON.toJSONString(resp));
		String token=JSONObject.parseObject(resp.getBody()).get("token").toString();
		head1.put("Authorization","Bearer "+token);
		return token;
	}


	public  Result Login(){
		Result resp=new Result();
		try {
			head.put("Origin",api_vcode_url);
			head.put("Referer",api_vcode_url+"/login?redirect=%2F");
			Result res=GetvCode();
			JSONObject a= (JSONObject) JSONObject.toJSON(res);
			JSONObject b=JSONObject.parseObject(a.getString("body"));
			System.out.println(b.get("img").toString());
			String uuid=b.get("uuid").toString();
			res=cmd_command("python  plib\\test.py  " +b.get("img").toString());
			System.out.println(res.getBody());
			if(res.getBody()==null){
				res.setCode(400);
				head.remove("Authorization");
				head.remove("Cookie");
				return res;
			}
			String Vcode=res.getBody().replace("null","");
			String pwd=encrypt(api_password.getBytes());
			String body="{\"username\":\""+api_username+"\",\"password\":\""+pwd+"\",\"code\":\""+Vcode+"\",\"uuid\":\""+uuid+"\"}";

			resp = https.http_post(api_vcode_url+"/auth/login",head,body);
			System.out.println(JSON.toJSONString(resp));
			String token=JSONObject.parseObject(resp.getBody()).get("token").toString();
			head.remove("Authorization");
			head.put("Authorization", " Bearer "+token);
			String host=url.split("://")[1];
			//head.put("Host",host);
			//head.remove("Referer");
			return resp;
		} catch (IOException e) {
			Result res=GetvCode();
			res.setCode(400);
			return res;
		} catch (Exception e) {
			//    e.printStackTrace();
			Result res=GetvCode();
			res.setCode(400);
			return res;

		}

	}
	public DapiTest(Boolean  flag) {
     if(flag) return;
	 up.init();
	 String host=url.split("://")[1];
		head.put("Host",host);
		head.put("Content-Type","application/json");
		head.put("Accept","application/json, text/plain, */*");
		for(int i=0;i<100;i++)
		{
			Result re=Login();
			if(re.getCode()==200||re.getCode()==201){
				System.out.println(String.format("登录成功"));
				break;
			}
			else {
				try {
					System.out.println(String.format("登录失败，重试中！"));
					System.out.println(re.getBody());
					Thread.sleep(2000);
				}catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

	}
	public Result  rename(String body){

		Result resp=new Result();
		resp = up.post(url+"/api/fshome/rename",head1,body);
		if(resp.getCode()==200||resp.getCode()==201){
			resp.setCode(200);
			return resp;
		}
		else {
			return resp;
		}

	}

	public Result  CreateMapping(String MappRequest){

		Result resp=new Result();
		try {
			resp = https.http_post(url+"/api/hostGroupLunMapping",head,MappRequest);
			if(resp.getCode()==200||resp.getCode()==201){
				resp.setCode(200);
				return resp;
			}
			else {
				return resp;
			}
		} catch (IOException e) {
			resp.setCode(400);
			resp.setBody(e.getStackTrace().toString());
			return resp;
		}

	}

	public  Result   StopShare(String  userid){
		Result resp=new Result();
		try {
			String body="{\"id\":$user_id$,\"accessType\":\"false\",\"checkList\":[\"3\",\"2\",\"1\"],\"tableData\":[]}".replace("$user_id$",userid);
			resp = https.http_post(url+"/api/fsuser/stopShare",head,body);
			if(resp.getCode()==200||resp.getCode()==201){
				resp.setCode(200);
				return resp;
			}
			else {
				return resp;
			}
		} catch (IOException e) {
			resp.setCode(400);
			resp.setBody(e.getStackTrace().toString());
			return resp;
		}
	}

	public  Result   uploadfile(String  filename,String  export_path,Map<String, String> userhead){
		Result resp=new Result();
		resp = up.uploadfile(url+"/api/files/upload?path="+export_path,filename,userhead,export_path);
		if(resp.getCode()==200){
			int code=200;
			resp.setCode(code);
			resp.setBody("HTTP/1.1 200");
			return resp;
		}else {
			resp.setCode(100);
			resp.setBody(resp.getBody());
			return resp;
		}
	}

	public  Result   download(String  filename,Map<String, String> userhead,String body){
		Result resp=new Result();
		Map<String, String> userhead1=new HashMap<>();
		userhead1.putAll(userhead);
		userhead1.remove("Content-Type");
		userhead1.put("Content-Type","application/json");
		resp = up.downloadfile(url+"/api/fshome/download",filename,userhead1,body);
		if(resp.getCode()==200){
			int code=200;
			resp.setCode(code);
			resp.setBody("HTTP/1.1 200");
			return resp;
		}else {
			resp.setCode(100);
			resp.setBody(resp.getBody());
			return resp;
		}
	}


	public  Result   deletefile(Map<String, String> userhead,String body){
		Result resp=new Result();
		Map<String, String> userhead1=new HashMap<>();
		userhead1.putAll(userhead);
		userhead1.remove("Content-Type");
		userhead1.put("Content-Type","application/json");
		resp = up.del(url+"/api/fshome",userhead1,body);
	   return resp;
	}

	public  Result   EnableFtpShare(String  userid,String type){
		Result resp=new Result();
		try {
			String body=String.format("{\"id\":$user_id$,\"accessType\":\"false\",\"checkList\":[%s],\"tableData\":[]}".replace("$user_id$",userid),type);
			resp = https.http_post(url+"/api/fsuser/share",head,body);
			if(resp.getCode()==200||resp.getCode()==201){
				resp.setCode(200);
				return resp;
			}
			else {
				return resp;
			}
		} catch (IOException e) {
			resp.setCode(400);
			resp.setBody(e.getStackTrace().toString());
			return resp;
		}
	}

	public  Result   automaticChoseStorageUnits(String hddProtectNum,String hddStrategy,String type){
		Result resp=new Result();
		try {
			String tmp=String.format(url+"/api/storageUnit/automaticChoseStorageUnits?hddProtectNum=%s&hddStrategy=%s&type=%s",hddProtectNum,hddStrategy,type);
			resp = https.http_get(tmp,head);
			if(resp.getCode()==200||resp.getCode()==201){
				resp.setCode(200);
				return resp;
			}
			else {
				System.out.println(resp.getBody());
				return resp;
			}
		} catch (IOException | InterruptedException e) {
			resp.setCode(400);
			resp.setBody(e.getStackTrace().toString());
			System.out.println(resp.getBody());
			return resp;
		}
	}

	public  Result   CreateGroupe(String  body){
		Result resp=new Result();
		try {
			resp = https.http_post(url+"/api/fsusergroup",head,body);

			if(resp.getCode()==200||resp.getCode()==201){
				resp.setCode(200);
				return resp;
			}
			else {
				System.out.println(resp.getBody());
				return resp;
			}
		} catch (IOException e) {
			resp.setCode(400);
			resp.setBody(e.getStackTrace().toString());
			System.out.println(resp.getBody());
			return resp;
		}
	}

	public  Result   rollbackSnapshot(String  snapshotid,String version){
		Result resp=new Result();
		try {
			resp = https.http_post(url+"/api/fsRollBack/"+snapshotid+"/"+version,head,"");
			if(resp.getCode()==200||resp.getCode()==201){
				resp.setCode(200);
				return resp;
			}
			else {
				System.out.println(resp.getBody());
				return resp;
			}
		} catch (IOException e) {
			resp.setCode(400);
			resp.setBody(e.getStackTrace().toString());
			System.out.println(resp.getBody());
			return resp;
		}
	}

	public  Result   CreateSnapshot(String  body){
		Result resp=new Result();
		try {
			resp = https.http_post(url+"/api/fileSnapshot",head,body);

			if(resp.getCode()==200||resp.getCode()==201){
				resp.setCode(200);
				return resp;
			}
			else {
				System.out.println(resp.getBody());
				return resp;
			}
		} catch (IOException e) {
			resp.setCode(400);
			resp.setBody(e.getStackTrace().toString());
			System.out.println(resp.getBody());
			return resp;
		}
	}

	public  Result   EditSnapshot(String  body){
		Result resp=new Result();
		try {
			resp = https.http_put(url+"/api/fileSnapshot",head,body);
			if(resp.getCode()==200||resp.getCode()==201||resp.getCode()==204){
				resp.setCode(200);
				return resp;
			}
			else {
				System.out.println(resp.getBody());
				return resp;
			}
		} catch (IOException e) {
			resp.setCode(400);
			resp.setBody(e.getStackTrace().toString());
			System.out.println(resp.getBody());
			return resp;
		}
	}


	public  Result   GetSnapshotList(String  name){
		Result resp=new Result();
		try {
			resp = https.http_get(url+"/api/fileSnapshot?storagepoolId=&fsusergroupId=&fsuserId=&dicName="+"&createStartTime=&createEndTime=&page=0&size=1&sort=id,desc",head);

			if(resp.getCode()==200||resp.getCode()==201){
				resp.setCode(200);
				return resp;
			}
			else {
				System.out.println(resp.getBody());
				return resp;
			}
		} catch (IOException | InterruptedException e) {
			resp.setCode(400);
			resp.setBody(e.getStackTrace().toString());
			System.out.println(resp.getBody());
			return resp;
		}
	}

	public  Result   GetUserFileList(String  filename,String filepath,Map<String, String> userhead) throws UnsupportedEncodingException {
		Result resp=new Result();
		//filename=new String(filename.getBytes(StandardCharsets.UTF_8),"UTF_8");
		filename=URLEncoder.encode(filename.replace(" ","+"));
		filepath=URLEncoder.encode(filepath);
		filename=filename.replace("%2C",",");
		String ul=url+String.format("/api/fshome?page=0&size=10&value=%s&fileName=%s&filePath=%s&status=1",filename,filename,filepath);
		System.out.println(String.format("获取上传文件信息url：%s",ul));
		Map<String, String> userhead1=new HashMap<>();
		userhead1.putAll(userhead);
		userhead1.put("Accept","application/json");
		userhead1.put("Content-Type","application/json");
		resp=up.get(ul,userhead1,"");
		return resp;

	}

	public  int   ftpupload(String  filename,String user,String passwd,String file){
		return up.ftpupload("ftp://"+url.split("://")[1].split(":")[0]+":21"+filename,user,passwd,file);
	}

	public  int   ftpdownload(String  filename,String user,String passwd,String file){
		return up.ftpdownload("ftp://"+url.split("://")[1].split(":")[0]+":21"+filename,user,passwd,file);
	}

	public  int   nfsmount3(String  filename,String user){
		return up.nfsmount(filename,user);
	}
	public  int   nfsclose(String  filename,String user){
		return up.nfsmount(filename,user);
	}
	public  long   nfsupload(String  filename,String user){
		return up.nfsupload(filename,user);
	}
	public  long   nfsdownload(String  filename,String user){
		return up.nfsdownload(filename,user);
	}


	public  Result   copyandmove(String  body){
		Result resp=new Result();
			resp = up.post(url+String.format("/api/fshome/copyormove"),head1,body);
			return resp;

	}


	public  Result   getcopyandmovedir(String poolname,String groupname,String username){
		Result resp=new Result();
		resp = up.get(url+String.format("/api/fshome/dirs?filePath=%%2Fexports%%2F%s%%2F%s%%2F%s",poolname,groupname,username),head1,"");
		return resp;
	}



	public  Result   DeleteUserFile(String  body){
		Result resp=new Result();
		try {
			resp = up.del(url+String.format("/api/fshome"),head1,body);
			if(resp.getCode()==200||resp.getCode()==201){
				resp.setCode(200);
				return resp;
			}
			else {
				System.out.println(resp.getBody());
				return resp;
			}
		} catch (Exception e) {
			resp.setCode(400);
			resp.setBody(e.getStackTrace().toString());
			System.out.println(resp.getBody());
			return resp;
		}
	}




	public  Result   Mkuserdir(String  body){
		Result resp=new Result();
		Map<String, String> userhead1=new HashMap<>();
		userhead1.putAll(head1);
		userhead1.remove("Content-Type");
		userhead1.put("Content-Type","application/json");
		resp = up.post(url+"/api/fshome",userhead1,body);
		if(resp.getCode()==200){
			int code=200;
			resp.setCode(code);
			resp.setBody("HTTP/1.1 200");
			return resp;
		}else {
			resp.setCode(100);
			resp.setBody(resp.getBody());
			return resp;
		}

	}


	public  Result   DelFileList(String  filename,String filepath){
		Result resp=new Result();
		try {
			resp = https.http_delete(url+String.format("/api/fshome?page=0&size=10&value=%s&fileName=%s&filePath=%s&status=1",filename,filename,filepath),head1);
			if(resp.getCode()==200||resp.getCode()==201){
				resp.setCode(200);
				return resp;
			}
			else {
				System.out.println(resp.getBody());
				return resp;
			}
		} catch (IOException  e) {
			resp.setCode(400);
			resp.setBody(e.getStackTrace().toString());
			System.out.println(resp.getBody());
			return resp;
		}
	}


	public  Result   GetSnapshotStatus(String  name){
		Result resp=new Result();
		try {
			resp = https.http_get(url+"/api/fsSnapshotVersion?page=0&size=6&sort=id,desc&versionName="+name,head);
			if(resp.getCode()==200||resp.getCode()==201){
				resp.setCode(200);
				return resp;
			}
			else {
				System.out.println(resp.getBody());
				return resp;
			}
		} catch (IOException | InterruptedException e) {
			resp.setCode(400);
			resp.setBody(e.getStackTrace().toString());
			System.out.println(resp.getBody());
			return resp;
		}
	}


	public  Result   GetSnapshotVersionStatus(String  name){
		Result resp=new Result();
		try {
			resp = https.http_get(url+"/api/fsSnapshotVersion?page=0&size=6&sort=id,desc&versionName="+name,head);

			if(resp.getCode()==200||resp.getCode()==201){
				resp.setCode(200);
				return resp;
			}
			else {
				System.out.println(resp.getBody());
				return resp;
			}
		} catch (IOException | InterruptedException e) {
			resp.setCode(400);
			resp.setBody(e.getStackTrace().toString());
			System.out.println(resp.getBody());
			return resp;
		}
	}


	public  Result   delSnapshotVersionStatus(String  id,String  name){
		Result resp=new Result();
		try {
			String body=String.format("{\"ids\":[1],\"names\":[\"%s\"],\"versionName\":\"%s\"}",id,name);
			resp = https.http_post(url+"/api/fsSnapshotVersion/delete",head,body);

			if(resp.getCode()==200||resp.getCode()==201){
				resp.setCode(200);
				return resp;
			}
			else {
				System.out.println(resp.getBody());
				return resp;
			}
		} catch (IOException e) {
			resp.setCode(400);
			resp.setBody(e.getStackTrace().toString());
			System.out.println(resp.getBody());
			return resp;
		}
	}

	public  Result   GetSnapshotVersion(String  id){
		Result resp=new Result();
		try {
			resp = https.http_get(url+"/api/fsSnapshotVersion/"+id,head);

			if(resp.getCode()==200||resp.getCode()==201){
				resp.setCode(200);
				return resp;
			}
			else {
				System.out.println(resp.getBody());
				return resp;
			}
		} catch (IOException | InterruptedException e) {
			resp.setCode(400);
			resp.setBody(e.getStackTrace().toString());
			System.out.println(resp.getBody());
			return resp;
		}
	}


	public  Result   DeleteSnapshot(String  id){
		Result resp=new Result();
		try {
			resp = https.http_delete(url+"/api/fileSnapshot/"+id,head);

			if(resp.getCode()==200||resp.getCode()==201){
				resp.setCode(200);
				return resp;
			}
			else {
				System.out.println(resp.getBody());
				return resp;
			}
		} catch (IOException e) {
			resp.setCode(400);
			resp.setBody(e.getStackTrace().toString());
			System.out.println(resp.getBody());
			return resp;
		}
	}



	public  Result   GetLunDetailByName(String  lunname){
		Result resp=new Result();
		String host=url.split("://")[1];
		try {
			head.remove("Host");
			resp = https.http_get(String.format("%s/api/blockLun?page=0&size=10&sort=id,desc&poolId=&name=%s&uuid=",url,lunname),head);
			head.put("Host",host);
			if(resp.getCode()==200||resp.getCode()==201){
				resp.setCode(200);
				return resp;
			}
			else {
				System.out.println(resp.getBody());
				return resp;
			}
		} catch (IOException | InterruptedException e) {
			resp.setCode(400);
			resp.setBody(e.getStackTrace().toString());
			System.out.println(resp.getBody());
			return resp;
		}
	}



	public  Result   DelLun(int  lunid){
		Result resp=new Result();
		try {
			resp = https.http_delete(String.format("%s/api/blockLun/%s",url,lunid),head);
			if(resp.getCode()==200||resp.getCode()==201){
				resp.setCode(200);
				return resp;
			}
			else {
				System.out.println(resp.getBody());
				return resp;
			}
		} catch (IOException e) {
			resp.setCode(400);
			resp.setBody(e.getStackTrace().toString());
			System.out.println(resp.getBody());
			return resp;
		}
	}

	public  Result   CreateLun(String  body){
		Result resp=new Result();
		try {
			resp = https.http_post(url+"/api/blockLun",head,body);
			if(resp.getCode()==200||resp.getCode()==201){
				resp.setCode(200);
				return resp;
			}
			else {
				System.out.println(resp.getBody());
				return resp;
			}
		} catch (IOException e) {
			resp.setCode(400);
			resp.setBody(e.getStackTrace().toString());
			System.out.println(resp.getBody());
			return resp;
		}
	}


	public SSHExecutor ssh_login(String username, String password, String ip, String port){
		SSHExecutor ssh=new SSHExecutor(ip, Integer.valueOf(port),username,password);
		return ssh;
	}

	public Result ssh_exec(SSHExecutor ssh,String cmd){
		Result res;
		res = ssh.exec(cmd);
		System.out.println(String.format("+++++++++++++++++++ssh:%s 命令执行结果+++++++++++++++++++",cmd));
		System.out.println(res.getBody());
		System.out.println(String.format("+++++++++++++++++++ssh:%s  命令执行结果+++++++++++++++++++",cmd));
		return res;
	}

	public Result ssh_exec_cmd(String username,String password,String ip,String port,String cmd){
		Result res;
		SSHExecutor ssh=new SSHExecutor(ip, Integer.valueOf(port),username,password);
		res = ssh.exec(""+cmd);
		System.out.println(res.getBody());
		return res;
	}



	public  Result   getpooldetail(String  poolname){
		Result resp=new Result();
		ArrayList<StoreagePoolUnusedUintResponsedetailModel> detail;
		resp = up.get(url+"/api/storagepool?page=0&size=10&sort=id,desc&name="+poolname,head,"");
		return resp;
	}

	public  Result   deleteStoreAgePool(String  poolid){
		Result resp=new Result();
		ArrayList<StoreagePoolUnusedUintResponsedetailModel> detail;
		try {
			resp = https.http_delete(url+"/api/storagepool/"+poolid,head);
			if(resp.getCode()==200||resp.getCode()==201){
				resp.setCode(200);
				return resp;
			}
			else {
				System.out.println(resp.getBody());
				return null;
			}
		} catch (IOException e) {
			resp.setCode(400);
			resp.setBody(e.getStackTrace().toString());
			System.out.println(resp.getBody());
			return null;
		}
	}


	public  Result   disableStoreAgePool(String  body){
		Result resp=new Result();
		ArrayList<StoreagePoolUnusedUintResponsedetailModel> detail;
		try {
			resp = https.http_post(url+"/api/storagepool/unactive",head,body);

			if(resp.getCode()>=200){
				resp.setCode(200);
				return resp;
			}
			else {
				System.out.println(resp.getBody());
				return resp;
			}
		} catch (IOException e) {
			resp.setCode(400);
			resp.setBody(e.getStackTrace().toString());
			System.out.println(resp.getBody());
			return resp;
		}
	}



	public  Result   enableStoreAgePool(String  body){
		Result resp=new Result();
		ArrayList<StoreagePoolUnusedUintResponsedetailModel> detail;
		try {
			resp = https.http_post(url+"/api/storagepool/active",head,body);

			if(resp.getCode()==200||resp.getCode()==201){
				resp.setCode(200);
				return resp;
			}
			else {
				System.out.println(resp.getBody());
				return null;
			}
		} catch (IOException e) {
			resp.setCode(400);
			resp.setBody(e.getStackTrace().toString());
			System.out.println(resp.getBody());
			return null;
		}
	}


	public  Result   GetStoreAgePool(String  poolname){
		Result resp=new Result();
		ArrayList<StoreagePoolUnusedUintResponsedetailModel> detail;
		try {
			resp = https.http_get(url+"/api/storagepool?page=0&size=10&sort=id,desc&name="+poolname,head);

			if(resp.getCode()==200||resp.getCode()==201){
				resp.setCode(200);
				return resp;
			}
			else {
				System.out.println(resp.getBody());
				return null;
			}
		} catch (IOException | InterruptedException e) {
			resp.setCode(400);
			resp.setBody(e.getStackTrace().toString());
			System.out.println(resp.getBody());
			return null;
		}
	}



	public  JSONArray   GetStoreagePoolUnusedUint(String  hddProtectNum,String hddStrategy,String type){
		Result resp=new Result();
		ArrayList<StoreagePoolUnusedUintResponsedetailModel> detail;
		try {
			resp = https.http_get(url+"/api/storageUnit/automaticChoseStorageUnits?hddProtectNum=1&hddStrategy=1&type=1",head);

			if(resp.getCode()==200||resp.getCode()==201){
				resp.setCode(200);
				int len=resp.getBody().length();
				JSONArray aa=JSONArray.parseArray(resp.getBody());
				return aa;
			}
			else {
				System.out.println(resp.getBody());
				return null;
			}
		} catch (IOException | InterruptedException e) {
			resp.setCode(400);
			resp.setBody(e.getStackTrace().toString());
			System.out.println(resp.getBody());
			return null;
		}
	}

	public  Result   CreateStore(String  body){
		Result resp=new Result();
		try {
			resp = https.http_post(url+"/api/storagepool",head,body);

			if(resp.getCode()==200||resp.getCode()==201){
				resp.setCode(200);
				return resp;
			}
			else {
				System.out.println(resp.getBody());
				return resp;
			}
		} catch (IOException e) {
			resp.setCode(400);
			resp.setBody(e.getStackTrace().toString());
			System.out.println(resp.getBody());
			return resp;
		}
	}

	//用户组列表接口
	public  Result   findAllEnableFsStoragepoolsCapacity(){
		Result resp=new Result();
		try {
			resp = https.http_get(url+"/api/storagepool/findAllEnableFsStoragepoolsCapacity",head);
			if(resp.getCode()==200||resp.getCode()==201){
				resp.setCode(200);
				return resp;
			}
			else {
				resp.setCode(400);
				resp.setBody(null);
				return resp;
			}
		}catch (IOException | InterruptedException e) {
			resp.setCode(400);
			resp.setBody(null);
			return resp;
		}
	}


	//用户组列表接口
	public  Result   getGroupList(){
		Result resp=new Result();
		try {
			resp = https.http_get(url+"/api/fsusergroup?page=0&size=10&sort=id,desc",head);
			if(resp.getCode()==200||resp.getCode()==201){
				resp.setCode(200);
				return resp;
			}
			else {
				resp.setCode(400);
				resp.setBody(null);
				return resp;
			}
		} catch (IOException | InterruptedException e) {
			resp.setCode(400);
			resp.setBody(null);
			return resp;
		}
	}

	//用户列表接口
	public  Result   getUserList(){
		Result resp=new Result();
		try {
			resp = https.http_get(url+"/api/fsuser?page=0&size=10&sort=id,desc",head);
			if(resp.getCode()==200||resp.getCode()==201){
				resp.setCode(200);
				return resp;
			}
			else {
				resp.setCode(400);
				resp.setBody(null);
				return resp;
			}
		} catch (IOException | InterruptedException e) {
			resp.setCode(400);
			resp.setBody(null);
			return resp;
		}
	}



	public  Result   CreateUser(String  body){
		Result resp=new Result();
		try {
			resp = https.http_post(url+"/api/fsuser",head,body);
			if(resp.getCode()==200||resp.getCode()==201){
				resp.setCode(200);
				return resp;
			}
			else {
				resp.setCode(400);
				resp.setBody(null);
				return resp;
			}
		} catch (IOException e) {
			resp.setCode(400);
			resp.setBody(null);
			return resp;
		}
	}

//仪表盘统计接口
	public  Result   check_alarm(){
		Result resp=new Result();
		try {
			resp = https.http_get(url+"/api/mtalarm/statistics",head);
			if(resp.getCode()==200||resp.getCode()==201){
				resp.setCode(200);
				return resp;
			}
			else {
				resp.setCode(400);
				resp.setBody(null);
				return resp;
			}
		} catch (IOException e) {
			resp.setCode(400);
			resp.setBody(null);
			return resp;
		} catch (InterruptedException e) {

			e.printStackTrace();
			resp.setCode(400);
			resp.setBody(null);
			return resp;
		}
	}
	public  Result   get_node(String nodename){
		Result resp=new Result();
		try {
			resp = https.http_get(url+"/api/storagenode?page=0&size=10&sort=id,desc&name="+nodename,head);
			if(resp.getCode()==200||resp.getCode()==201){
				resp.setCode(200);
				return resp;
			}
			else {
				resp.setCode(400);
				resp.setBody(null);
				return resp;
			}
		} catch (IOException e) {
			resp.setCode(400);
			resp.setBody(null);
			return resp;
		} catch (InterruptedException e) {

			e.printStackTrace();
			resp.setCode(400);
			resp.setBody(null);
			return resp;
		}
	}
	public  Result   get_diskCapacity(String body){
		Result resp=new Result();
		try {
			resp = https.http_post(url+"/api/storagenode/check/diskCapacity",head,body);
			if(resp.getCode()==200||resp.getCode()==201){
				resp.setCode(200);
				return resp;
			}
			else {
				resp.setCode(400);
				resp.setBody(null);
				return resp;
			}
		} catch (IOException e) {
			resp.setCode(400);
			resp.setBody(null);
			return resp;
		}
	}


	public  Result   check_service(String id){
		Result resp=new Result();
		try {
			resp = https.http_get(url+"/api/storagenode/service/"+id,head);
			if(resp.getCode()==200||resp.getCode()==201){
				resp.setCode(200);
				return resp;
			}
			else {
				resp.setCode(400);
				resp.setBody(null);
				return resp;
			}
		} catch (IOException | InterruptedException e) {
			resp.setCode(400);
			resp.setBody(null);
			return resp;
		}
	}




	public  Result   check_network(String body){
		Result resp=new Result();
		try {
			resp = https.http_post(url+"/api/storagenode/check/network",head,body);
			if(resp.getCode()==200||resp.getCode()==201){
				resp.setCode(200);
				return resp;
			}
			else {
				resp.setCode(400);
				resp.setBody(null);
				return resp;
			}
		} catch (IOException e) {
			resp.setCode(400);
			resp.setBody(null);
			return resp;
		}
	}


	public  Result   check_condition(String id){
		Result resp=new Result();
		try {
			resp = https.http_get(url+"/api/mtserver/condition/"+id,head);
			if(resp.getCode()==200||resp.getCode()==201){
				resp.setCode(200);
				return resp;
			}
			else {
				resp.setCode(400);
				resp.setBody(null);
				return resp;
			}
		} catch (IOException|InterruptedException e) {
			resp.setCode(400);
			resp.setBody(null);
			return resp;
		}
	}

	public  Result   check_monitor(String body){
		Result resp=new Result();
		try {
			resp = https.http_post(url+"/api/mtserver/monitor",head,body);
			if(resp.getCode()==200||resp.getCode()==201){
				resp.setCode(200);
				return resp;
			}
			else {
				resp.setCode(400);
				resp.setBody(null);
				return resp;
			}
		} catch (IOException e) {
			resp.setCode(400);
			resp.setBody(null);
			return resp;
		}
	}




	public  Result   check_selection(String body){
		Result resp=new Result();
		try {
			resp = https.http_post(url+"/api/mtserver/selection",head,body);
			if(resp.getCode()==200||resp.getCode()==201){
				resp.setCode(200);
				return resp;
			}
			else {
				resp.setCode(400);
				resp.setBody(null);
				return resp;
			}
		} catch (IOException e) {
			resp.setCode(400);
			resp.setBody(null);
			return resp;
		}
	}

	public  Result   check_disk(String body){
		Result resp=new Result();
		try {
			resp = https.http_post(url+"/api/storagenode/check/disk",head,body);
			if(resp.getCode()==200||resp.getCode()==201){
				resp.setCode(200);
				return resp;
			}
			else {
				resp.setCode(400);
				resp.setBody(null);
				return resp;
			}
		} catch (IOException e) {
			resp.setCode(400);
			resp.setBody(null);
			return resp;
		}
	}


	public  Result   disable_node(String body){
		Result resp=new Result();
		try {
			resp = https.http_post(url+"/api/storagenode/updateStatus",head,body);
			if(resp.getCode()==200||resp.getCode()==201){
				resp.setCode(200);
				return resp;
			}
			else {
				resp.setCode(400);
				resp.setBody(null);
				return resp;
			}
		} catch (IOException e) {
			resp.setCode(400);
			resp.setBody(null);
			return resp;
		}
	}


	public  Result   enable_node(String body){
		Result resp=new Result();
		try {
			resp = https.http_post(url+"/api/storagenode/updateStatus",head,body);
			if(resp.getCode()==200||resp.getCode()==201){
				resp.setCode(200);
				return resp;
			}
			else {
				resp.setCode(400);
				resp.setBody(null);
				return resp;
			}
		} catch (IOException e) {
			resp.setCode(400);
			resp.setBody(null);
			return resp;
		}
	}



	public  Result   check_storagenode(){
		Result resp=new Result();
		try {
			resp = https.http_get(url+"/api/storagenode?",head);
			if(resp.getCode()==200||resp.getCode()==201){
				resp.setCode(200);
				return resp;
			}
			else {
				resp.setCode(400);
				resp.setBody(null);
				return resp;
			}
		} catch (IOException e) {
			resp.setCode(400);
			resp.setBody(null);
			return resp;
		} catch (InterruptedException e) {

			e.printStackTrace();
			resp.setCode(400);
			resp.setBody(null);
			return resp;
		}
	}

	public  Result   check_getTopFsStatistics(){
		Result resp=new Result();
		try {
			resp = https.http_get(url+"/api/storageStatistics/getTopFsStatistics",head);
			if(resp.getCode()==200||resp.getCode()==201){
				resp.setCode(200);
				return resp;
			}
			else {
				resp.setCode(400);
				resp.setBody(null);
				return resp;
			}
		} catch (IOException e) {
			resp.setCode(400);
			resp.setBody(null);
			return resp;
		} catch (InterruptedException e) {

			e.printStackTrace();
			resp.setCode(400);
			resp.setBody(null);
			return resp;
		}
	}
	public  Result   check_getLDStatistics(){
		Result resp=new Result();
		try {
			resp = https.http_get(url+"/api/storageStatistics/getLDStatistics",head);
			if(resp.getCode()==200||resp.getCode()==201){
				resp.setCode(200);
				return resp;
			}
			else {
				resp.setCode(400);
				resp.setBody(null);
				return resp;
			}
		} catch (IOException e) {
			resp.setCode(400);
			resp.setBody(null);
			return resp;
		} catch (InterruptedException e) {

			e.printStackTrace();
			resp.setCode(400);
			resp.setBody(null);
			return resp;
		}
	}

//获取节点性能接口
	public  Result   check_getall(String body){
		Result resp=new Result();
		try {
			resp = https.http_post(url+"/api/mtserver/getall",head,body);
			if(resp.getCode()==200||resp.getCode()==201){
				resp.setCode(200);

				return resp;
			}
			else {
				resp.setCode(400);
				resp.setBody(null);
				return resp;
			}
		} catch (IOException e) {
			resp.setCode(400);
			resp.setBody(null);
			return resp;
		}
	}

	public  Result   check_getDiskStatistics(){
		Result resp=new Result();
		try {
			resp = https.http_get(url+"/api/storageStatistics/getDiskStatistics",head);
			if(resp.getCode()==200||resp.getCode()==201){
				resp.setCode(200);
				return resp;
			}
			else {
				resp.setCode(400);
				resp.setBody(null);
				return resp;
			}
		} catch (IOException e) {
			resp.setCode(400);
			resp.setBody(null);
			return resp;
		} catch (InterruptedException e) {

			e.printStackTrace();
			resp.setCode(400);
			resp.setBody(null);
			return resp;
		}
	}

	public  Result   check_getStoragepoolStatistics(){
		Result resp=new Result();
		try {
			resp = https.http_get(url+"/api/storageStatistics/getStoragepoolStatistics",head);
			if(resp.getCode()==200||resp.getCode()==201){
				resp.setCode(200);
				return resp;
			}
			else {
				resp.setCode(400);
				resp.setBody(null);
				return resp;
			}
		} catch (IOException e) {
			resp.setCode(400);
			resp.setBody(null);
			return resp;
		} catch (InterruptedException e) {

			e.printStackTrace();
			resp.setCode(400);
			resp.setBody(null);
			return resp;
		}
	}



	public  Result   DeleteUser(String  uid){
		Result resp=new Result();
		try {
			System.out.println(String.format("删除用户：%s",uid));
			resp = https.http_delete(url+"/api/fsuser/"+uid,head);
			if(resp.getCode()==200||resp.getCode()==201){
				resp.setCode(200);
				return resp;
			}
			else {
				return resp;
			}
		} catch (IOException e) {
			resp.setCode(400);
			resp.setBody(e.getStackTrace().toString());
			return resp;
		}
	}

	public  Result  exec_alarm_service(String alarmid){
		Result resp=new Result();
		try {

			resp = https.http_put(url+"/api/jobs/exec/"+alarmid,head,"");
			if(resp.getCode()==200||resp.getCode()==201){
				resp.setCode(200);
				return resp;
			}
			else {
				return resp;
			}
		} catch (IOException e) {
			resp.setCode(400);
			resp.setBody(e.getStackTrace().toString());
			return resp;
		}
	}
	public  Result  exec_alarm_confirm(String alarmid){
		Result resp=new Result();
		try {
			String body="{\"remark\":id,\"confirm\":\"s\"}".replace("id",alarmid);
			resp = https.http_post(url+"/api/mtalarm/confirm",head,body);
			if(resp.getCode()==200||resp.getCode()==201){
				resp.setCode(200);
				return resp;
			}
			else {
				return resp;
			}
		} catch (IOException e) {
			resp.setCode(400);
			resp.setBody(e.getStackTrace().toString());
			return resp;
		}
	}

	public  Result   DeleteGroup(String  uid){
		Result resp=new Result();
		try {
			resp = https.http_delete(url+"/api/fsusergroup/"+uid,head);
			if(resp.getCode()==200||resp.getCode()==201){
				resp.setCode(200);
				return resp;
			}
			else {
				return resp;
			}
		} catch (IOException e) {
			resp.setCode(400);
			resp.setBody(e.getStackTrace().toString());
			return resp;
		}
	}




	public  GroupListRespModel   GetGroupList(){
		Result resp=new Result();
		try {
			resp = https.http_get(url+"/api/fsusergroup?page=0&size=100&sort=id,desc",head);
			if(resp.getCode()==200||resp.getCode()==201){
				GroupListRespModel   gp=JSON.parseObject(resp.getBody(), GroupListRespModel.class);
				return gp;
			}
			else {
				return null;
			}
		} catch (IOException e) {
			resp.setCode(400);
			resp.setBody(e.getStackTrace().toString());

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

	public  Result   DelGroupe(String  id){
		Result resp=new Result();
		try {
			resp = https.http_delete(url+"/api/fsusergroup/"+id,head);
			if(resp.getCode()==200||resp.getCode()==201){
				resp.setCode(200);
				return resp;
			}
			else {
				return resp;
			}
		} catch (IOException e) {
			resp.setCode(400);
			resp.setBody(e.getStackTrace().toString());
			return resp;
		}
	}

}
