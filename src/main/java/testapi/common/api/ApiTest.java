package testapi.common.api;
import org.python.core.PyFunction;
import org.python.util.PythonInterpreter;
import pds.api.sshapi;
import testapi.common.BackMedieModel.bmStoreRespModel;
import testapi.common.ClientManagerModel.RequestModel;
import testapi.common.ClientManagerModel.cmResponseModel;
import testapi.common.JobLogRespModel;
import testapi.framework.SSHExecutor;
import testapi.framework.http_api;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import testapi.common.JobStatusRespModel;
import testapi.common.PropertiesLoader;
import testapi.common.RestoreTask.dataRevertListResponse;
import testapi.common.Result;
import org.junit.Assert;
import org.apache.commons.codec.binary.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.X509EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.*;

public  class ApiTest {

    static   http_api ht = new http_api();
    static    http_api https = new http_api();
    sshapi  ssh=new sshapi();
    PropertiesLoader   properties=new PropertiesLoader();
    public Map<String, String> head=new HashMap<String, String>();
    static   String  url= PropertiesLoader.GetPropertie("BASE_URL");
    String  api_username= PropertiesLoader.GetPropertie("api_username");
    String  api_password= PropertiesLoader.GetPropertie("api_password");
    public  String dst_path=PropertiesLoader.GetPropertie("dst_path").toLowerCase();
    public String src_path=PropertiesLoader.GetPropertie("src_path").toLowerCase();
    public String ssh_dest_port=PropertiesLoader.GetPropertie("ssh_dest_port");
    public String ssh_src_port=PropertiesLoader.GetPropertie("ssh_src_port");
    public String ssh_dst_type=PropertiesLoader.GetPropertie("dest_host_type");
    public String ssh_src_type=PropertiesLoader.GetPropertie("src_host_type");
    public String ssh_src_ip=PropertiesLoader.GetPropertie("ssh_src_ip");
    public  String ssh_dst_ip=PropertiesLoader.GetPropertie("ssh_dest_ip");
    public  String  ssh_src_username=PropertiesLoader.GetPropertie("ssh_src_username");
    public String   ssh_src_password=PropertiesLoader.GetPropertie("ssh_src_password");
    public String ssh_dest_username=PropertiesLoader.GetPropertie("ssh_dest_username");
    public String ssh_dest_password=PropertiesLoader.GetPropertie("ssh_dest_password");
    public String  ssh_login_src_ip   =PropertiesLoader.GetPropertie("ssh_login_src_ip");
    public String   src_login_path =PropertiesLoader.GetPropertie("src_login_path");
    public String   api_vcode_url =PropertiesLoader.GetPropertie("api_vcode_url");
    public String hosts_xls=System.getProperty("user.dir")+"\\config\\hosts.xls";
    public String Restore_tmp_JobName="pds1618540809025";


    public ApiTest()  {
        url= PropertiesLoader.GetPropertie("BASE_URL");
        String host=url.split("://")[1];
        head.put("Host",host);
        head.put("Content-Type","application/json");
        head.put("Accept","application/json, text/plain, */*");

        for(int i=0;i<10;i++)
        {
            Result re=Login();
            System.out.println(re.getCode());
            if(re.getCode()==200||re.getCode()==201){
                break;
            }
            else {
                try {
                    Thread.sleep(2000);
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Result CreateDiskpack(String c){
        try {
            Result resp=https.http_post(url+"/admin-system/api/opticalgroup",head,c);
            return resp;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Result CreateJobPlain(String c){
        try {
            Result resp=ht.http_post(url+"/admin-system/api/dataBackup",head,c);
            return resp;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
    public  String Create_BackMedie(String ssh_dst_ip,String dst_path ) {
        try {
            testapi.common.BackMedieModel.RequestModel Model = new testapi.common.BackMedieModel.RequestModel();
            String JobName = "BackMedie" + String.valueOf(System.currentTimeMillis());
            Model.setMediumName(JobName);
            Model.setMediumIp(ssh_dst_ip);
            Model.setMediumPort("22");
            Model.setEnabled(true);
            Model.setMediumType(2);
            Model.setMediumTls(false);
            Model.setMediumNetworkHeartBeat(1);
            Model.setMediumBackupPath(dst_path);
            Model.setMediumDescription("111111111");
            System.currentTimeMillis();
            Result  resp=creatClientMedie(JSON.toJSONString(Model));
            Assert.assertTrue(resp!=null);

            for(int i=0;i<100;i++){
                String a=getClientMedieList(JobName);
                if(a==null){
                    Thread.sleep(1000);
                }else return a;

            }
            Assert.assertTrue(false);

        }catch (Exception e){
e.printStackTrace();

        }
        return null;
    }

    public  String Create_Client(String ssh_src_ip) {
        try {

            RequestModel Model = new RequestModel();
            String JobName = "BackMedie" + String.valueOf(System.currentTimeMillis());
            Model.setClientIp(ssh_src_ip);
            Model.setClientName(JobName);
            Model.setClientPort("9103");
            Model.setClientNetworkHeartBeat(1);
            Model.setClientType(1);
            Model.setEnabled(true);
            Model.setClientDescription("pdstest");
            Model.setClientTls(true);
            System.currentTimeMillis();
            Date d = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            Result  resp=creatClientManager(JSON.toJSONString(Model));
            Assert.assertTrue(resp!=null);
            for(int i=0;i<10;i++)
            {
                String a=getClientList(JobName);
                if(a==null){
                    Thread.sleep(1000);
                }else return a;

            }
            Assert.assertTrue(false);

        }catch (Exception e){
e.printStackTrace();
        }
        return null;
    }



    public static String getURLEncoderString(String str)  {
        String result="";
        if(null==str){
            return "";
        }
        try {
            result = java.net.URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e){

        }
        return result;
    }

    public JobStatusRespModel GetJobStatus(String c){
        try {
            Result resp=ht.http_get(url+"/admin-system/api/dataBackupJobs?page=0&size=10&sort=id,desc&jobNameWeb="+c+"&taskType=2",head);

            if(resp.getCode()==200||resp.getCode()==201){
                JobStatusRespModel JobStatusResp=JSON.parseObject(resp.getBody(), JobStatusRespModel.class);
                return JobStatusResp;
            }else {
                Assert.assertTrue(false);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }
    public JobLogRespModel GetJobLog(String c){
        try {
            JobLogRespModel res;

            Result resp=https.http_get(url+"/admin-system/api/logBackups?page=0&size=10&jobNameWeb="+c+"&taskType=2",head);
            if(resp.getCode()!=400)
            {
                res=JSON.parseObject(resp.getBody(), JobLogRespModel.class);
                return res;
            }
            return null;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public  boolean WaitForJobEnd(String JobName){
        try {
            int count = 1;
            while (count < 1800) {
                JobStatusRespModel JobStatusResp = GetJobStatus(JobName);

                if (JobStatusResp.getContent().size() == 0) {
                    Thread.sleep(1000);
                } else {
                    String status = JobStatusResp.getContent().get(0).getJobStatus();

                    if (status.equals("R") || status.equals("C")) {
                        Thread.sleep(1000);
                    } else if (status.equals("T")) {
                        return true;
                    } else {

                        return false;
                    }
                }
                count++;
            }
            if(count>=1800)
            {
                System.out.println(JobName+"作业运行时间太长超过了半个小时");

            }
            return false;
        }
        catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }



    }




    public Result CreateDiscpack(String c){
        try {

            Result       resp=https.http_post(url+"/admin-system/api/opticalgroup",head,c.toString());
            return resp;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public Object shareExist(String c){

        try {
            Object       resp=https.http_post(url+"/admin-system/api/opticalgroup/shareExist",head,c.toString());
            return resp;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public Result deviceInfo(String c){
        try {
            Result resp=https.http_post(url+"/admin-system/api/storagenode/deviceInfo",head,c.toString());
            return resp;
        } catch (IOException e) {

            e.printStackTrace();
        }
        return null;
    }
    public Result CreateStoragePool(String c){
        try {

            Result resp=https.http_post(url+"/admin-system/api/storagepool",head,c.toString());
            return resp;

        } catch (IOException e) {
            e.printStackTrace();
        }

        Result resp=new Result();
        return null;

    }
    public Result DelStoragePool(String id){
        try {
            Result resp=https.http_delete(url+"/admin-system/api/opticalgroup/"+id,head);
            return resp;
        } catch (IOException e) {
            e.printStackTrace();
        }

        Result resp=new Result();
        return null;

    }
    public Result GetStorageDetail(String c){

        try {

            Result resp=https.http_post(url+"/admin-system/api/storagepool/detail",head,c.toString());
            return resp;

        } catch (IOException e) {

            e.printStackTrace();
        }
        Result resp=new Result();
        return null;

    }
    public Result GetStorageList(){
        try {

            Result resp= null;

            resp = https.http_get(url+"/admin-system/api/opticalgroupview?page=0&size=10000&sort=id,desc&status=0",head);

            return resp;

        } catch (IOException e) {

            e.printStackTrace();
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
        Result resp=new Result();
        return null;

    }
    public Result GetUserList(int page){
        try {
            Result resp= null;
            resp = https.http_get(url+"/admin-system/api/fsuser?page="+page+"&size=10&sort=id,desc&groupId=",head);
            return resp;
        } catch (IOException e) {

            e.printStackTrace();
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
        Result resp=new Result();
        return null;
    }
    public Result EnableUserShare(String c){

        try {

            Result resp= null;
            resp = https.http_post(url+"/admin-system/api/fsuser/share",head,c);
            return resp;

        } catch (IOException e) {

            e.printStackTrace();
        }
        Result resp=new Result();
        return null;
    }
    public Result unactivestorepool(String id){
        try {

            String body="{\"id\":${id}}";
            body=body.replace("${id}",id);
            Result resp= null;
            resp = https.http_post(url+"/admin-system/api/storagepool/unactive",head,body);
            return resp;

        } catch (IOException e) {

            e.printStackTrace();
        }
        Result resp=new Result();
        return null;
    }
    public Result getStoragePoolList(String id){
        try {

            String body="{\"id\":${id}}";
            Result resp= null;
            try {
                resp = https.http_get(url+"/admin-system/api/storagepool?page="+id+"&size=10&sort=id,desc",head);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return resp;

        } catch (IOException e) {

            e.printStackTrace();
        }
        Result resp=new Result();
        return null;
    }
    public Result delstorepool(String id){
        try {

            Result resp= null;
            resp = https.http_delete(url+"/admin-system/api/storagepool/"+id,head);
            return resp;

        } catch (IOException e) {

            e.printStackTrace();
        }
        Result resp=new Result();
        return null;
    }

    public Result  getjopplainList(String page,String size,String jobname){
        try {
            Result resp= null;
            resp = https.http_get(String.format("%s/admin-system/api/dataBackups?page=0&size=20&sort=id,desc&taskName=%s&taskType=2",url,jobname),head);
            return resp;

        } catch (IOException | InterruptedException e) {

            e.printStackTrace();
        }
        Result resp=new Result();
        return null;

    }
    public Result DeleteJobPlain(String id){
        try {

            Result resp= null;
            resp = https.http_delete(url+"/admin-system/api/dataBackup/"+id,head);
            return resp;

        } catch (IOException e) {

            e.printStackTrace();
        }
        Result resp=new Result();
        return null;
    }
    public Result StopJob(String body){
        try {

            Result resp= null;
            resp = https.http_put(url+"/admin-system/api/dataBackup/terminate",head,body);
            return resp;

        } catch (IOException e) {

            e.printStackTrace();
        }
        Result resp=new Result();
        return null;
    }

    public Result GetJobPlainList(){
        try {
            Result resp= null;
            resp = https.http_get(url+"/admin-system/api/dataBackups?page=0&size=10&sort=id,desc&taskType=2",head);
            return resp;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        Result resp=new Result();
        return null;
    }

    public Result GetJobList(String page,String size){
        try {
            Result resp= null;
            resp = https.http_get(url+"/admin-system/api/dataBackupJobs?page="+page+"&size="+size+"&sort=id,desc&taskType=2",head);
            return resp;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        Result resp=new Result();
        return null;
    }
    public JobStatusRespModel GetJobDetailByName(String Jobname){
        try {
            Result resp= https.http_get(url+"/admin-system/api/dataBackupJobs?page=0&size=10&sort=id,desc&jobNameWeb="+Jobname+"&taskType=2&type=revert&jobStatus=T",head);
            if(resp.getCode()==200||resp.getCode()==201) {
                return JSONObject.parseObject(resp.getBody(), JobStatusRespModel.class);
            }
            else {
                return null;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        Result resp=new Result();
        return null;
    }

    public Result GetJobPlainByName(String name){
        try {

            Result resp= null;
            resp = https.http_get(url+"/admin-system/api/dataBackups?page=0&size=10&sort=id,desc&taskName="+name+"&taskType=2",head);
            return resp;
        } catch (IOException | InterruptedException e) {

            e.printStackTrace();
        }
        Result resp=new Result();
        return null;
    }
    String getWeekOfDate(Date date) {
        String[] weekDays = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if(w < 0){
            w = 0;
            return weekDays[w];
        }
        return null;
    }

    public Result GetSrcFileSha256sum(SSHExecutor ssh,String path){
        Result res;
        System.out.printf(" ================主机  %s  : 路径 %s",path,ssh.getIp());
        System.out.println("================");
        if(ssh.getHost_type().contains("linux")) {
            res = ssh.exec("find     " + path + " -type f|xargs  -I  {}   sha256sum   {}    " + "\n");
        }else {
            path=path.replace("\\","/");
            String cmd="gfind     " + path + " -type f | xargs  -I  {}   sha256sum   {}    " ;
            res = ssh.exec(cmd);
        }
        return res;
    }

    public Result GetDstFileSha256sum(SSHExecutor ssh,String path){
        Result resule=new Result();
        try{
            System.out.printf(" ================主机  %s  : 路径 %s================\r\n",ssh.getIp(),path);

            if(ssh.getHost_type().contains("linux")) {
                resule = ssh.exec("find     " + path + " -type f  |xargs  -I  {}    sha256sum   {}  +" + "\n");
            }
            else {
                resule = ssh.exec("gfind     " + path + " -type f|xargs  -I  {}   sha256sum   {}    " + "\n");
            }
        }finally {
            // ssh.exec("rm  -rf      " + path.substring(0,path.length()-1) + "" + "\n");
        }

        return resule;
    }

    public Result Create_file(String path,String username,String password,String ip,String port,String size,String buf,String filename){

        SSHExecutor ssh=new SSHExecutor(ip, Integer.valueOf(port),username,password);
        Result res;
        System.out.println("================");
        long shell1 = 0;
        System.out.println("================");
        String cmd="dd if=/dev/zero of="+path+"/"+filename+"  bs="+size+" count="+buf;
        System.out.println("exce  ssh command : "+cmd);
        res = ssh.exec(cmd);
        return res;
    }

    public  Result creatClientMedie(String request){
        try {
            Result resp= null;
            resp = https.http_post(url+"/admin-system/api/amebkMedium",head,request);
            if(resp.getCode()==200||resp.getCode()==201){
                return  resp;
            }
            else {
                return null;
            }
        } catch (IOException e) {

            e.printStackTrace();
            return null;
        }

    }

    public  String getClientMedieList(String name){
        try {
            Result resp= null;
            String  urls=String.format("/admin-system/api/amebkMedium?page=0&size=1&sort=id,desc&mediumName=%s", name);
            resp = https.http_get(url+urls,head);
            if(resp.getCode()==200||resp.getCode()==201){
                bmStoreRespModel cm=(bmStoreRespModel)JSONObject.parseObject(resp.getBody(),bmStoreRespModel.class);
                if(cm.getContent().size()==0) return null;
                return cm.getContent().get(0).getMediumId();
            }
            else {
                return null;
            }
        } catch (Exception e) {
           return null;
        }

    }



        public  Result creatClientManager(String request){
        try {
            Result resp= null;
            resp = https.http_post(url+"/admin-system/api/amebkClient",head,request);
            if(resp.getCode()==200||resp.getCode()==201){

                return  resp;
            }
            else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public  String getClientList(String name){
        try {
            Result resp= null;
            String  urls=String.format("/admin-system/api/amebkClient?page=0&size=1&sort=id,desc&clientName=%s", name);
            resp = https.http_get(url+urls,head);
            if(resp.getCode()==200||resp.getCode()==201){
                cmResponseModel cm=(cmResponseModel)JSONObject.parseObject(resp.getBody(),cmResponseModel.class);
                if(cm.getContent().size()==0) return null;
                return cm.getContent().get(0).getClientId();
            }
            else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }

    }





    public Result Create_file_login(SSHExecutor ssh,String path,String size,String buf,String filename){
        String cmd="dd if=/dev/zero of="+path+"/"+filename+"  bs="+size+" count="+buf;
        Result res;
        if(ssh.getHost_type().equals("windows")){
            cmd="dd if=/dev/zero of="+path+"\\"+filename+"  bs="+size+" count="+buf;
        }
        System.out.printf("===========生成文件 ：%s========\r\n",filename);
        res = ssh.exec(cmd+"&&ls -alh  "+path);
        System.out.println(res.getBody());
        return res;


    }


    public SSHExecutor ssh_login(String username,String password,String ip,String port){
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
    public Result GetDstPackFileSha256sum(SSHExecutor ssh,String path){
        Result res ;
        String tarname= UUID.randomUUID().toString().replaceAll("-","");
        System.out.println("================");
        String cmd="";
        if(ssh.getHost_type().equals("linux")) {
            res = ssh.exec("cat     " + path + "/*.tar*   > " + path + "/" + tarname + ".tar\n");
            if (res.getBody().toLowerCase().contains("error")) {
                ssh = ssh_login(ssh_src_username, ssh_src_password, ssh_login_src_ip, ssh_src_port);
            }
            res = ssh.exec("tar   -xvf  " + path + "/" + tarname + ".tar    -C   " + path + " \n");

            res = ssh.exec("rm  -rf        " + path + "/*.tar*   " + " \n");

            res = ssh.exec("find     " + path + " -type f|xargs  -I  {}   sha256sum   {}" + "\n");
            ssh.exec("rm  -rf      " + path + " " + "\n");
        }
        else
        {
            cmd="cat     " + path + "/*.tar*   > " + path + "/" + tarname + ".tar\n";
            res = ssh.exec(cmd);
            if (res.getBody().toLowerCase().contains("error")) {
                ssh = ssh_login(ssh_src_username, ssh_src_password, ssh_login_src_ip, ssh_src_port);
            }
            cmd="tar   -xvf  " + path + "/" + tarname + ".tar    -C   " + path + " \n";
            res = ssh.exec(cmd);
            cmd="rm  -rf        " + path + "/*.tar*   " + " \n";
            res = ssh.exec(cmd);
            cmd="gfind     " + path + " -type f|xargs  -I  {}   sha256sum   {}" + "\n";
            res = ssh.exec(cmd);
            //  ssh.exec("rm  -rf      " + path + " " + "\n");
        }
        res.setCode(100);
        return res;

    }

    public Result GetvCode(String uri){
        try {

            Result resp= null;
            resp = https.http_get(url+api_vcode_url,head);
            return resp;

        } catch (IOException | InterruptedException e) {

            e.printStackTrace();
        }
        Result resp=new Result();
        return null;
    }

    static   public Result cmd_command(String cmd){
        String line = null;

        BufferedReader br = null;
        try {
            Process p = Runtime.getRuntime().exec(cmd);
            br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String tmp=null;
            while ((tmp = br.readLine()) != null) {
                line=line+tmp;

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
        resp.setBody(line);
        return resp;
    }
    public  Result Login(){
        Result resp=new Result();
        try {
            head.put("Origin",this.url);
            head.put("Referer",this.url+"/login?redirect=%2F");
            head.put("Connection","close");
            Result res=GetvCode(api_vcode_url);
            System.out.println(res.getHead());
            Object JSESSIONID=res.getHead().get("Set-Cookie");
            if(JSESSIONID==null) {
                System.out.println("JSESSIONID IS NULL");
                head.remove("Authorization");
                head.remove("Cookie");
                res.setCode(400);
                return res;
            }

            // PythonInterpreter interpreter = new PythonInterpreter();
            //  interpreter.execfile("test.py");
            // PyFunction func = (PyFunction) interpreter.get("adder", PyFunction.class);


            head.put("Cookie",JSESSIONID.toString());
            JSONObject a= (JSONObject) JSONObject.toJSON(res);
            JSONObject b=JSONObject.parseObject(a.getString("body"));
            System.out.println(b.get("img").toString());
            String uuid=b.get("uuid").toString();
            res=cmd_command("python  plib\\test.py  " +b.get("img").toString());
            System.out.println(res.getBody());
            if(res.getBody()==null){
                res.setCode(400);
                return res;
            }
            String Vcode=res.getBody().replace("null","");
            String pwd=encrypt(api_password.getBytes());
            //String pwd="rhXLdmMpb9SmSgR9HRD+KW4CJHcy6z/6AI0BskddrNmPVrXJhy4sOy12/65SEd1jsgXbmH2T/ZH28ekBTDO+Rg==";
            String body="{\"username\":\""+api_username+"\",\"password\":\""+pwd+"\",\"code\":\""+Vcode+"\",\"uuid\":\""+uuid+"\"}";
            //System.out.println(encrypt("Amethystum@bk".getBytes()));
            resp = https.http_post(url+"/admin-system/auth/login",head,body);
            System.out.println(JSON.toJSONString(resp));
            String token=JSONObject.parseObject(resp.getBody()).get("token").toString();
            head.put("Authorization", " Bearer "+token);
            head.remove("Referer");
            return resp;
        } catch (IOException e) {
            Result res=GetvCode(api_vcode_url);
            res.setCode(400);
            return res;
        } catch (Exception e) {
            //    e.printStackTrace();
            Result res1 = new Result();
            res1.setCode(400);
            return res1;

        }

    }
    static public  String encrypt(byte[] data)
            throws Exception {
        final String RSA = "RSA";// 非对称加密密钥算法
        final String ECB_PKCS1_PADDING = "RSA/ECB/PKCS1Padding";//加密填充方式
        final int DEFAULT_KEY_SIZE = 1024;//秘钥默认长度
        final byte[] DEFAULT_SPLIT = "#PART#".getBytes();    // 当要加密的内容超过bufferSize，则采用partSplit进行分块加密
        final int DEFAULT_BUFFERSIZE = (DEFAULT_KEY_SIZE / 8) - 11;// 当前秘钥支持加密的最大字节数
        RSAPrivateKey privateKey; // 私钥
        byte[] publicKey = Base64.decodeBase64(("MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBANL378k3RiZHWx5AfJqdH9xRNBmD9wGD\n2iRe41HdTNF8RUhNnHit5NpMNtGL0NPTSSpPjjI1kJfVorRvaQerUgkCAwEAAQ=="));
        //byte[] publicKey =("MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBANL378k3RiZHWx5AfJqdH9xRNBmD9wGD\n2iRe41HdTNF8RUhNnHit5NpMNtGL0NPTSSpPjjI1kJfVorRvaQerUgkCAwEAAQ==").getBytes(StandardCharsets.UTF_8);
        if (publicKey == null) {
            throw new Exception("加密公钥为空, 请设置");
        }
        Cipher cipher = null;
        try {
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKey);
            KeyFactory kf = KeyFactory.getInstance(RSA);
            PublicKey keyPublic = kf.generatePublic(keySpec);
            // 加密数据
            Cipher cp = Cipher.getInstance(RSA);
            cp.init(Cipher.ENCRYPT_MODE, keyPublic);
            //String pwd=new String(cp.doFinal(data));
            String pwd=Base64.encodeBase64String(cp.doFinal(data));
            return pwd;
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此加密算法");
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
            return null;
        } catch (InvalidKeyException e) {
            throw new Exception("加密公钥非法,请检查");
        } catch (IllegalBlockSizeException e) {
            throw new Exception("明文长度非法");
        } catch (BadPaddingException e) {
            throw new Exception("明文数据已损坏");
        }
    }

    public  Result CreateRestorePlain(String c){
        try {
            Result resp= null;
            resp = https.http_post(url+"/admin-system/api/dataRevert",head,c);
            return resp;
        } catch (IOException e) {
            e.printStackTrace();
        }
        Result resp=new Result();
        return null;
    }

    public  dataRevertListResponse GetRestoreLitsByName(String ResToreName){
        try {
            Result resp= null;
            resp = https.http_get(url+"/admin-system/api/dataReverts?page=0&size=10&sort=id,desc&taskName="+ResToreName+"&taskType=3",head);

            if(resp.getCode()==200||resp.getCode()==201){
                return  JSONObject.parseObject(resp.getBody(), dataRevertListResponse.class);

            }
            else {
                return null;
            }

        } catch (IOException | InterruptedException e) {

            e.printStackTrace();
        }
        return null;
    }

    public  Result  compile_the_result(SSHExecutor sshcmd,SSHExecutor dstsshcmd,String desPathName,String srcpath,String src_login_path){
        Result res=null;
        res = GetDstFileSha256sum(dstsshcmd,desPathName);
        if (res.getCode() == 0) {
            res.setCode(400);
            res.setBody("get job resulterror ");
            return res;
        }
        String dstfilesha256 = res.getBody();
        //dstfilesha256=dstfilesha256.replace(srcpath,src_login_path);
        dstfilesha256=dstfilesha256.replace("//","/");
        System.out.printf("===========================目的文件列表================================\r\n%s================================目的文件列表============================\r\n",dstfilesha256);
        res = GetSrcFileSha256sum(sshcmd,src_login_path);
        String srcfilesha256 = res.getBody();
        System.out.printf("===========================源文件列表================================\r\n%s===========================源文件列表================================\r\n",srcfilesha256);
        String[] srclist_tmp = srcfilesha256.replace("\r", "").split("\n");
        String[] dstlist_tmp = dstfilesha256.replace("\r", "").split("\n");
        for (int i = 0; i < dstlist_tmp.length; i++) {
            if(sshcmd.getHost_type().equals("linux"))
            {
                dstlist_tmp[i] = dstlist_tmp[i].replace(desPathName, "");
            }
            else {
                dstlist_tmp[i] = dstlist_tmp[i].replace(desPathName+"/".replace("//","/"), "");
                dstlist_tmp[i]=dstlist_tmp[i].replace("  "," ");
            }
        }
        List<String> srclist = Arrays.asList(srclist_tmp);
        List<String> dstlist = Arrays.asList(dstlist_tmp);
        if (srclist.size() == dstlist.size()) {
            for (int i = 0; i < srclist.size(); i++) {
                if(sshcmd.getHost_type().equals("windows")||dstsshcmd.getHost_type().equals("windows")){
                    srclist.set(i,srclist.get(i).toLowerCase());
                    dstlist.set(i,dstlist.get(i).toLowerCase());
                }
                String src_key=srclist.get(i).split(" ")[0];
                String src_value=srclist.get(i).split(" ")[1];
                String dst_key=dstlist.get(i).split(" ")[0];
                String dst_value=dstlist.get(i).split(" ")[1];
                if(dst_key.equals(src_key)&&dst_value.equals(src_value)){
                    continue;
                }else
                {
                    res.setCode(400);
                    res.setBody("src  file   get job result  error:"+srclist.get(i).toString()+":END\r\nsrc  file   get job result  error:"+dstlist.get(i).toString()+":END");
                    return res;
                }
            }
        } else {
            res.setCode(400);
            res.setBody("备份文件和原文件个数不相等");
            return res;
        }
        res.setCode(200);
        return res;
    }


    public  Result    compile_the_result_whith_pack(SSHExecutor sshcmd,SSHExecutor dstsshcmd,String desPathName,String srcpath,String src_login_path){
        Result res=null;
        try{
            res = GetDstPackFileSha256sum(dstsshcmd,desPathName);
            if (res.getCode() == 0) {
                res.setCode(400);
                res.setBody("get job resulterror ");
                return res;
            }
            String dstfilesha256 = res.getBody();
            dstfilesha256=dstfilesha256.replace(srcpath,src_login_path);
            dstfilesha256=dstfilesha256.replace("//","/");
            System.out.println("===========================目的文件列表================================");
            System.out.println(dstfilesha256);
            System.out.println("================================目的文件列表============================");
            Thread.sleep(3000);
            res = GetSrcFileSha256sum(sshcmd,src_login_path);
            String srcfilesha256 = res.getBody();
            System.out.println("===========================源文件列表================================");
            System.out.println(srcfilesha256);
            System.out.println("===========================源文件列表================================");

            String[] srclist_tmp = srcfilesha256.replace("\r", "").split("\n");
            String[] dstlist_tmp = dstfilesha256.replace("\r", "").split("\n");
            for (int i = 0; i < dstlist_tmp.length; i++) {
                if(sshcmd.getHost_type().equals("linux"))
                {

                    dstlist_tmp[i] = dstlist_tmp[i].replace(desPathName, "");

                }
                else {
                    dstlist_tmp[i] = dstlist_tmp[i].replace(desPathName+"/".replace("//","/"), "");
                    dstlist_tmp[i]=dstlist_tmp[i].replace("  "," ");

                }
            }
            List<String> srclist = Arrays.asList(srclist_tmp);
            List<String> dstlist = Arrays.asList(dstlist_tmp);
            if (srclist.size() == dstlist.size()) {
                for (int i = 0; i < srclist.size(); i++) {
                    if(sshcmd.getHost_type().equals("windows")||dstsshcmd.getHost_type().equals("windows")){
                        srclist.set(i,srclist.get(i).toLowerCase());
                        dstlist.set(i,dstlist.get(i).toLowerCase());
                    }
                    String src_key=srclist.get(i).split(" ")[0];
                    String src_value=srclist.get(i).split(" ")[1];
                    String dst_key=dstlist.get(i).split(" ")[0];
                    String dst_value=dstlist.get(i).split(" ")[1];
                    if(dst_key.equals(src_key)&&dst_value.equals(src_value)){
                        continue;
                    }else
                    {
                        res.setCode(400);
                        res.setBody("src  file   get job result  error:"+srclist.get(i).toString()+":END\r\nsrc  file   get job result  error:"+dstlist.get(i).toString()+":END");
                        return res;
                    }
                }
            } else {
                res.setCode(400);
                res.setBody("备份文件和原文件不相等");
                return res;
            }
            res.setCode(200);
            return res;}catch (InterruptedException e){

            e.printStackTrace();
            return null;

        }


    }

    public Result ssh_cmd(String type,SSHExecutor ssh,String command){
        switch (type){
            case "linux" :return ssh_exec(ssh,command);
            case "windows":return ssh_exec(ssh,command);
        }
        return null;

    }





}
