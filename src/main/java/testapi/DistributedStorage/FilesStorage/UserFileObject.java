package testapi.DistributedStorage.FilesStorage;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.*;
import org.junit.runner.RunWith;
import testapi.common.DistributedStorage.*;
import testapi.common.Result;
import testapi.common.api.PgApi;
import testapi.common.apiManager.DapiTest;
import testapi.framework.SSHExecutor;
import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@RunWith(DataProviderRunner.class)
public class UserFileObject {
    static DapiTest api=new DapiTest(false);
    static PgApi pg=new PgApi(api.database_ip,api.database_port,api.database_username,api.database_password,api.database_dbname);
    static String password="Zjcc_123";
    static  String username="pdstest02";
    static  String  filepoolname="xinbanbenquanxiancestest";
    static String  groupname="justtestabcdef";
    static  int  poolid=0;
    static  String  test_ftp_export_path=String.format("/ftp_exports/%s_%s/%s",filepoolname,groupname,username);
    static  String  nfs_ftp_export_path=String.format("/exports/%s_%s/%s",groupname,username,filepoolname);
    static  String  test_export_path=String.format("/exports/%s/%s/%s",filepoolname,groupname,username);
    static SSHExecutor sshcmd=null;
    static String  token=null;
    static  String uid=null;
    static String id=null;

    static Map<String, String> userhead=new HashMap<>();
    public static String TestCase_CreateUserGroup(String groupname){
        Assert.assertTrue(groupname!=null);
        String JobName="pgroup"+String.valueOf(System.currentTimeMillis());
        CreateGroupRequestModel Gre=new CreateGroupRequestModel();
        Gre.setName(groupname);
        Gre.setIsEnable(true);
        Gre.setPsw(password);
        Gre.setCheckPass(password);
        Gre.setBillingMethod("1");
        Gre.setPassword(api.encrypt(password.getBytes()));
        ArrayList<StoragePoolVos>  pools=new ArrayList<StoragePoolVos>();
        StoragePoolVos pool=new StoragePoolVos();
        pool.setPoolId(poolid);
        pool.setPoolName(filepoolname);
        pool.setNewQuota(1073741824);
        pools.add(pool);
        ArrayList<TableData>  tds=new ArrayList<TableData>();
        TableData td=new TableData();
        td.setId(poolid);
        td.setLabel(filepoolname);
        td.setFreeDiskSpace(1073741824);
        td.setQuota("10");
        td.setType("2");
        tds.add(td);
        Gre.setStoragePoolVos(pools);
        Gre.setTableData(tds);
        Result res=api.CreateGroupe(JSON.toJSONString(Gre));
        // Assert.assertTrue(res.getCode()==200);
        return   pg.execsql(String.format("select  id from ame_fsusergroup where  name='%s'",groupname));
    }
    public static String CreateStorePool_danfuben() throws InterruptedException {
        filepoolname = "pool" + String.valueOf(System.currentTimeMillis() / 1000);
        CreateStorePoolRequestModel rm=new CreateStorePoolRequestModel();
        ArrayList unit=new ArrayList();
        JSONArray um = api.GetStoreagePoolUnusedUint("", "", "");
        for(Object ss:um){
            StoreagePoolUnusedUintResponsedetailModel    dd= JSONObject.parseObject(ss.toString(),StoreagePoolUnusedUintResponsedetailModel.class);
            unit.add(dd.getId());
        }
        rm.setName(filepoolname);
        rm.setHddUnits(unit);
        rm.setPoolType(0);
        rm.setHddStrategy(1);
        rm.setSsdStrategy("");
        rm.setSsdProtectNum(1);
        rm.setHddProtectNum(1);
        System.out.println(JSONObject.toJSON(rm).toString());
        Result resp = api.CreateStore(JSONObject.toJSON(rm).toString());
        Assert.assertTrue(resp.getCode()==200);
        Result res=api.getpooldetail(filepoolname);
        Pattern p= Pattern.compile("content\":\\[\\{\"id\":(\\d+)");
        Matcher m=p.matcher(res.getBody().toString());
        m.find();
        poolid=Integer.valueOf(m.group(1));
        api.enableStoreAgePool("{\"id\":%id%}".replace("%id%",m.group(1)));
        while (api.GetStoreAgePool(filepoolname).getBody().indexOf("\"status\":1")<0){
            Thread.sleep(4000);
        }
        return m.group(1);
    }
    @BeforeClass
    public static void beforeclass() throws InterruptedException {
        CreateStorePool_danfuben();
        poolid=Integer.valueOf(pg.execsql(String.format("SELECT  id  from ame_storagepool where name='%s';",filepoolname)));
        Thread.sleep(5000);
        username= "puser" + String.valueOf(System.currentTimeMillis() / 1000);
        groupname = "guser" + String.valueOf(System.currentTimeMillis() / 1000);
        id = TestCase_CreateUserGroup(groupname);
        Assert.assertTrue(id!=null);
        CreateUserRequestModel user = new CreateUserRequestModel();
        user.setName(username);
        user.setGroupId(Integer.valueOf(id));
        user.setPsw(password);
        user.setCheckPass(password);
        user.setQuotaType("1");
        user.setIsEnable(true);
        ArrayList<TableData> tds = new ArrayList<TableData>();
        TableData td = new TableData();
        td.setId(poolid);
        td.setLabel(filepoolname);
        td.setFreeDiskSpace(1073741824);
        td.setQuota("10");
        td.setType("2");
        td.setQuotaType("1");
        tds.add(td);
        ArrayList<storageUserPoolVos> pools = new ArrayList<storageUserPoolVos>();
        storageUserPoolVos pool = new storageUserPoolVos();
        pool.setPoolId(poolid);
        pool.setPoolName(filepoolname);
        pool.setNewQuota("1073741824");
        pool.setQuotaType("1");
        pools.add(pool);
        user.setStorageUserPoolVos(pools);
        user.setTableData(tds);
        user.setPassword(api.encrypt(password.getBytes()));
        Result resp = api.CreateUser(JSON.toJSONString(user));
        System.out.println(resp.getBody());
        Assert.assertTrue(resp.getCode() == 200);
        uid = pg.execsql(String.format("select  id from ame_fsuser where  group_id='%s'", id));
        Result res = api.EnableFtpShare(uid,"\"3\",\"2\",\"1\"");
        Assert.assertTrue(res.getCode()==200);
        test_ftp_export_path=String.format("/ftp_exports/%s_%s/%s",filepoolname,groupname,username);
        nfs_ftp_export_path=String.format("/exports/%s_%s/%s",groupname,username,filepoolname);
        test_export_path=String.format("/exports/%s/%s/%s",filepoolname,groupname,username);
        password=pg.execsql(String.format("select share_passwd from ame_fsuser where  name='%s';",username));
        sshcmd=api.ssh_login(api.ssh_dest_username,api.ssh_dest_password, api.ssh_dst_ip, api.ssh_dest_port);
        token=api.GetToken(username,password);
        for(int i=0;i<100;i++){
            if(token==null){
                Thread.sleep(2000);
                token=api.GetToken(username,password);
            }else break;
        }
        Assert.assertTrue(token!=null);
        userhead.put("Host",api.url.split("://")[1].replace('/',' '));
        userhead.put("Connection","keep-alive");
        userhead.put("Authorization","Bearer "+token);

    }

    @AfterClass
    public static void afterclass(){
        Result res = api.DeleteUser(uid);
        assert res.getCode()==200;
        res = api.DeleteGroup(id);
        assert res.getCode()==200;
        res =  api.disableStoreAgePool("{\"id\":%id%}".replace("%id%",String.valueOf(poolid)));
        assert res.getCode()==200;
        res =  api.deleteStoreAgePool(String.valueOf(poolid));
        assert res.getCode()==200;
    }


    @After
    public  void aftertestcase(){

       String cmd=String.format("rm -rf  %s/*",test_export_path);
       //System.out.println(cmd);
       sshcmd.exec(cmd);
    }

    private   Boolean checkfileexist(String remotepath,String flag){
        Result resp = new Result();
        String cmd = String.format("ls -d '%s'|wc  -l", remotepath);
        for(int i=0;i<5;i++) {
            resp = sshcmd.exec(cmd);
            System.out.println(String.format("check file exist:%s,%s",cmd,resp.getBody()));
            if(resp.getBody().equals(flag))
                return true;
            try {
                Thread.sleep(2000);
            }catch (InterruptedException e){

            }
        }
        return resp.getBody().equals(flag);
    }



    @DataProvider
    public static List<data_file_list_data> data_file_list11() {
        try{
            ImportParams params = new ImportParams();
            params.setTitleRows(1);
            params.setHeadRows(1);
            List<data_file_list_data> list = ExcelImportUtil.importExcel(new File(api.data_file_list), data_file_list_data.class, params);
            return list;
        }catch (Exception e){
            return null;
        }
    }


    public static String getMD5Three(String path) {
        BigInteger bi = null;
        try {
            byte[] buffer = new byte[8192000];
            int len = 0;
            MessageDigest md = MessageDigest.getInstance("MD5");
            File f = new File(path);
            FileInputStream fis = new FileInputStream(f);
            while ((len = fis.read(buffer)) != -1) {
                md.update(buffer, 0, len);
            }
            fis.close();
            byte[] b = md.digest();
            bi = new BigInteger(1, b);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(bi==null) return null;
        return bi.toString(16);
    }

    @DataProvider
    public static Object[][] data_file_list() {
        String path=System.getProperty("user.dir");
        Object a=new Object[][]{};
        return new Object[][]{
               {path+"\\TestData\\wps.exe", "123"},
                {path+"\\TestData\\位图图像.bmp", "123"},
                {path+"\\TestData\\work测试文件.docx", "123"},
                {path+"\\TestData\\ppt测试文件.ppt", "123"},
                {path+"\\TestData\\doc文档.doc", "123"},
                {path+"\\TestData\\[].txt", "123"},
                {path+"\\TestData\\WinRaR压缩文件.rar", "123"},
                {path+"\\TestData\\RTF格式文件.rtf", "123"},
                {path+"\\TestData\\Miscrosoft.mpp", "123"},
               {path+"\\TestData\\Miscriosoft.vsdx", "123"},
                {path+"\\TestData\\MicrosoftExcel.xls", "123"},
                {path+"\\TestData\\2test.xls", "123"},
               {path+"\\TestData\\1test.xlsx", "d25083f598a22a8e1d67be1fadfa38b0"},
              {path+"\\TestData\\훌륭해요,아주잘해주셨어요,ひとつになって,繁體你好.txt", "123"},
        };
    }

    @DataProvider
    public static Object[][] data_dir_list() {
        String path=System.getProperty("user.dir");
        Object a=new Object[][]{};
        return new Object[][]{
              // {"你好", "123"},
                {"25525525525525525525525525525525525525525525525525525525525525525525525525525525525525525525525525525525525525525525525525525525525525525525525525525525525525525525525525525525525525525525525525525525525525525525525525525525525525525525525525525525525525", "254"},
                {"1", "123"},
                {"a"},
                {"!][@#$%^&()-=_+"},
              //  {"????,????????,ひとつになって,繁體你好.txt", "123"},
        };
    }

    @Test
    @UseDataProvider("data_file_list")
    public  void testcases_用户界面上传_文件_通过后台删除_下载(String filename_path,String md5sum) throws UnsupportedEncodingException {
        int  pos=filename_path.lastIndexOf("\\");
        String filename=filename_path.substring(pos+1,filename_path.length());
        String export_path=test_export_path;
        pos=api.url.indexOf("//");

        //获取本地文件md5值
        String src_md5=getMD5Three(filename_path);
        //上传文件

        Result  resp=api.uploadfile(filename_path,export_path,userhead);
        System.out.println(String.format("上传文件：%s,响应结果：%s",filename,resp.getBody().split("\r\n")[0]));
        Assert.assertTrue(resp.getBody(),resp.getCode()==200);//判断上传文件接口响应结果

        //通过后台判断文件是否上成功
        String  cmd=String.format("ls  %s/%s|wc  -l",export_path,filename);
        System.out.println(cmd);
        Result  rest=sshcmd.exec(cmd);
        System.out.println(rest.getBody());
        //通过接口判断文件是否上传成功
        resp=api.GetUserFileList(filename,export_path,userhead);
        System.out.println(String.format("判断文件是否上传成功:"+resp.getBody()));
        Assert.assertTrue("上传文件失败",resp.getBody().contains(filename));
        Assert.assertTrue(rest.getBody().equals("1"));
        //获取目的文件md5值
        String  dst_md5_cmd=String.format("md5sum  %s/%s",export_path,filename);
        rest=sshcmd.exec(dst_md5_cmd);
        String dst_md5=rest.getBody().split(" ")[0].replace(" ","");
        System.out.println(String.format("上传源md5值:%s,目的md5值：%s",src_md5,dst_md5));
        Assert.assertTrue("md5值不相等",src_md5.contains(dst_md5));//源文件和目的文件md5值一样
        String body=String.format("{\"fileName\":\"%s\",\"fileSize\":16187392,\"filePath\":\"%s\",\"fileType\":null,\"fileStorageType\":null,\"isDir\":false,\"parentPath\":\"%s\",\"status\":\"0\",\"createTime\":1650437362000,\"updateTime\":null,\"changePath\":null,\"startTime\":null,\"endTime\":null,\"nodeip\":null,\"paths\":null}",
                filename,export_path+"/"+filename,export_path);
        rest=api.download(filename,userhead,body);
        Assert.assertTrue(rest.getBody(),rest.getCode()==200);
        src_md5=getMD5Three(filename).replace(" ","");
        System.out.println(String.format("后台md5值:%s,下载md5值：%s",dst_md5,src_md5));
        String rm_cmd=String.format("rm  -rf  %s/%s",export_path,filename);
        rest=sshcmd.exec(rm_cmd);
        rest=sshcmd.exec(cmd);
        System.out.println(rest.getBody());
        Assert.assertTrue(rest.getBody().equals("0"));
        Assert.assertTrue("下载md5值不相等",dst_md5.contains(src_md5));
        System.out.println(String.format("删除文件 %s",filename));
        File file=new File(filename);
        file.delete();


    }

    @Test
    @UseDataProvider("data_file_list")
    public  void testcases_用户界面上传_文件_通过接口删除(String filename_path,String md5sum) throws InterruptedException {
        int  pos=filename_path.lastIndexOf("\\");
        String filename=filename_path.substring(pos+1,filename_path.length());
        String export_path=test_export_path;
        //获取本地文件md5值
        String src_md5=getMD5Three(filename_path);
        //上传文件
        Result  resp=api.uploadfile(filename_path,export_path,userhead);
        System.out.println(String.format("上传文件：%s,响应结果：%s",filename,resp.getBody().split("\r\n")[0]));
        Assert.assertTrue("上传文件失败",resp.getBody().contains("HTTP/1.1 200"));//判断上传文件接口响应结果
        //通过后台判断文件是否上成功
        String  cmd=String.format("ls  %s/%s|wc  -l",export_path,filename);
        System.out.println(cmd);
        Result  rest=sshcmd.exec(cmd);
        System.out.println(rest.getBody());
        Assert.assertTrue("用户界面上传文件失败："+filename,rest.getBody().equals("1"));
        //获取目的文件md5值
        String  dst_md5_cmd=String.format("md5sum  %s/%s",export_path,filename);
        rest=sshcmd.exec(dst_md5_cmd);
        String dst_md5=rest.getBody().split(" ")[0];
        Assert.assertTrue(src_md5.contains(dst_md5));//源文件和目的文件md5值一样
        System.out.println(rest.getBody());
        //通过接口删除已经上传的文件
        String  delete_body=String.format("{\"fileName\":\"%s\",\"fileSize\":13611,\"filePath\":\"%s\",\"fileType\":null,\"fileStorageType\":null,\"isDir\":false,\"parentPath\":\"%s\",\"status\":null,\"createTime\":1637115248000,\"updateTime\":null,\"changePath\":null,\"startTime\":null,\"endTime\":null,\"nodeip\":null,\"paths\":null}"
                ,filename,export_path+"/"+filename,export_path);
        resp =  api.deletefile(userhead,delete_body);
        Assert.assertTrue(resp.getCode()==200);
        Thread.sleep(2000);
        cmd=String.format("ls  %s/%s|wc  -l",export_path,filename);
        System.out.println(cmd);
        rest=sshcmd.exec(cmd);
        Assert.assertTrue(String.format("上传文件通过接口删除失败:%s ,%s ",cmd,rest.getBody()),rest.getBody().equals("0"));
    }


    @Test
    @UseDataProvider("data_dir_list")
    public  void testcases_用户界面创建_用户目录_并且_删除(String filename_path,String md5sum) throws UnsupportedEncodingException, InterruptedException {
        String body=String.format("{\"fileName\":\"%s\",\"filePath\":\"\",\"sort\":0,\"parentPath\":\"%s\"}",filename_path,test_export_path);
        Result resp=api.Mkuserdir(body);
        Assert.assertTrue(resp.getCode()==200);
        int pos=api.url.indexOf("//");
        String uri= api.url.substring(pos+2,api.url.length());
        //通过接口判断文件是否上传成功
        resp=api.GetUserFileList(filename_path,test_export_path,userhead);
        Assert.assertTrue(resp.getBody().contains(filename_path));
        String upload_filename_path= System.getProperty("user.dir")+"\\TestData\\wps.exe";
        pos=upload_filename_path.lastIndexOf("\\");
        String filename=upload_filename_path.substring(pos+1,upload_filename_path.length());
        //往目录中上传一个文件
        //上传文件
        resp=api.uploadfile(upload_filename_path,test_export_path+"//"+filename_path+"//",userhead);
        //resp=  upload_user_file( upload_filename_path, test_export_path+"//"+filename_path+"//", uri);
        System.out.println(String.format("上传文件：%s,响应结果：%s",filename,resp.getBody().split("\r\n")[0]));
        Assert.assertTrue("上传文件失败",resp.getBody().contains("HTTP/1.1 200"));//判断上传文件接口响应结果
        //判断文件上传成功
        resp=api.GetUserFileList(filename,test_export_path+"//"+filename_path,userhead);
        Assert.assertTrue("上传文件失败："+filename,resp.getBody().contains(filename));
        //通过接口删除文件和目录
        String  delete_body=String.format("{\"fileName\":\"%s\",\"fileSize\":13611,\"filePath\":\"%s\",\"fileType\":null,\"fileStorageType\":null,\"isDir\":false,\"parentPath\":\"%s\",\"status\":null,\"createTime\":1637115248000,\"updateTime\":null,\"changePath\":null,\"startTime\":null,\"endTime\":null,\"nodeip\":null,\"paths\":null}"
        ,filename,test_export_path+"//"+filename_path+"//"+filename,test_export_path+"//"+filename_path);
        resp=api.DeleteUserFile(delete_body);//删除文件
        Assert.assertTrue(resp.getCode()==200);
        delete_body=String.format("{\"fileName\":\"%s\",\"fileSize\":13611,\"filePath\":\"%s\",\"fileType\":null,\"fileStorageType\":null,\"isDir\":false,\"parentPath\":\"%s\",\"status\":null,\"createTime\":1637115248000,\"updateTime\":null,\"changePath\":null,\"startTime\":null,\"endTime\":null,\"nodeip\":null,\"paths\":null}"
                ,filename_path,test_export_path+"//"+filename_path,test_export_path);
        resp=api.deletefile(userhead,delete_body); //删除目录
        Assert.assertTrue(resp.getCode()==200);
        //Thread.sleep(2000);
        //String cmd=String.format("ls -d '%s/%s'|wc  -l",test_export_path,filename_path);
        //System.out.println(cmd);
        //resp=sshcmd.exec(cmd);
        Assert.assertTrue(String.format("上传文件通过接口删除失败"),checkfileexist(test_export_path+"/"+filename_path,"0"));
    }

    @Test
    @UseDataProvider("data_file_list")
    public  void testcases_用户界面用户文件_复制到子目录(String upload_filename_path,String md5sum) throws UnsupportedEncodingException {
        //String filename_path="userdir" + String.valueOf(System.currentTimeMillis() / 1000);
        String filename_path = "userdir" + RandomStringUtils.randomAlphanumeric(10);
        String body=String.format("{\"fileName\":\"%s\",\"filePath\":\"\",\"sort\":0,\"parentPath\":\"%s\"}",filename_path,test_export_path);
        Result resp=api.Mkuserdir(body);
        Assert.assertTrue(resp.getCode()==200);
        int pos=api.url.indexOf("//");
        String uri= api.url.substring(pos+2,api.url.length());
        pos=upload_filename_path.lastIndexOf("\\");
        String filename=upload_filename_path.substring(pos+1,upload_filename_path.length());
        resp=api.uploadfile(upload_filename_path, test_export_path,userhead);
        //resp=  upload_user_file( upload_filename_path, test_export_path+"/"+filename_path, uri);
        System.out.println(String.format("上传文件：%s,响应结果：%s",filename,resp.getBody().split("\r\n")[0]));
        Assert.assertTrue("上传文件失败",resp.getBody().contains("HTTP/1.1 200"));//判断上传文件接口响应结果
        //判断文件上传成功
        resp=api.GetUserFileList(filename,test_export_path,userhead);
        Assert.assertTrue("通过接口无法获取上传的文件："+filename,resp.getBody().contains(filename));
        String copyandmove_body=String.format("{\"paths\":[\"%s\"],\"parentPath\":\"%s\",\"changePath\":\"%s\",\"status\":1}",
                test_export_path+"//"+filename,test_export_path,test_export_path+"//"+filename_path);
        resp=api.copyandmove(copyandmove_body);
        Assert.assertTrue("移动到子目录失败",resp.getCode()==200);
        //判断文件是否移动成功
        resp=api.GetUserFileList(filename,test_export_path+"//"+filename_path,userhead);
        Assert.assertTrue(resp.getBody().contains(filename));
        String  delete_body=String.format("{\"fileName\":\"%s\",\"fileSize\":13611,\"filePath\":\"%s\",\"fileType\":null,\"fileStorageType\":null,\"isDir\":false,\"parentPath\":\"%s\",\"status\":null,\"createTime\":1637115248000,\"updateTime\":null,\"changePath\":null,\"startTime\":null,\"endTime\":null,\"nodeip\":null,\"paths\":null}"
                ,filename,test_export_path+"//"+filename,test_export_path);
        resp = api.deletefile(userhead,delete_body);
        Assert.assertTrue(resp.getCode()==200);
        delete_body=String.format("{\"fileName\":\"%s\",\"fileSize\":13611,\"filePath\":\"%s\",\"fileType\":null,\"fileStorageType\":null,\"isDir\":false,\"parentPath\":\"%s\",\"status\":null,\"createTime\":1637115248000,\"updateTime\":null,\"changePath\":null,\"startTime\":null,\"endTime\":null,\"nodeip\":null,\"paths\":null}"
                ,filename_path,test_export_path+"//"+filename_path,test_export_path);
        resp=api.deletefile(userhead,delete_body);//删除目录
        Assert.assertTrue("删除目录失败",resp.getCode()==200);
        //Thread.sleep(3000);
        //String  cmd=String.format("ls  %s/%s|wc  -l",test_export_path,filename);
        //System.out.println(cmd);
        //Result  rest=sshcmd.exec(cmd);
        Assert.assertTrue("源文件通过接口删除失败",checkfileexist(test_export_path+"/"+filename,"0"));

        //cmd=String.format("ls  %s/%s|wc  -l",test_export_path+"//"+filename_path,filename);
        //System.out.println(cmd);
        //rest=sshcmd.exec(cmd);
        Assert.assertTrue("复制文件通过接口删除失败",checkfileexist(test_export_path+"//"+filename_path+"/"+filename,"0"));
    }

    @Test
    @UseDataProvider("data_file_list")
    public  void testcases_用户界面用户文件_复制到父目录(String upload_filename_path,String md5sum) throws UnsupportedEncodingException {
        //String filename_path="userdir" + String.valueOf(System.currentTimeMillis() / 1000);
        String filename_path = "userdir" + RandomStringUtils.randomAlphanumeric(10);
        String body=String.format("{\"fileName\":\"%s\",\"filePath\":\"\",\"sort\":0,\"parentPath\":\"%s\"}",filename_path,test_export_path);
        Result resp=api.Mkuserdir(body);
        Assert.assertTrue(resp.getCode()==200);
        int pos=api.url.indexOf("//");
        String uri= api.url.substring(pos+2,api.url.length());
        pos=upload_filename_path.lastIndexOf("\\");
        String filename=upload_filename_path.substring(pos+1,upload_filename_path.length());
        resp=api.uploadfile(upload_filename_path, test_export_path+"/"+filename_path,userhead);
        //resp=  upload_user_file( upload_filename_path, test_export_path+"/"+filename_path, uri);
        System.out.println(String.format("上传文件：%s,响应结果：%s",filename,resp.getBody().split("\r\n")[0]));
        Assert.assertTrue(String.format("上传文件失败:响应结果：%s",filename,resp.getBody().split("\r\n")[0]),resp.getBody().contains("HTTP/1.1 200"));//判断上传文件接口响应结果
        //判断文件上传成功

        resp=api.GetUserFileList(filename,test_export_path+"/"+filename_path,userhead);
        Assert.assertTrue(resp.getBody().contains(filename));
        String copyandmove_body=String.format("{\"paths\":[\"%s\"],\"parentPath\":\"%s\",\"changePath\":\"%s\",\"status\":1}",
                test_export_path+"//"+filename_path+"//"+filename, test_export_path+"//"+filename_path,test_export_path);
        resp=api.copyandmove(copyandmove_body);
        Assert.assertTrue("移动到父目录失败",resp.getCode()==200);
        //判断文件是否移动成功
        resp=api.GetUserFileList(filename,test_export_path,userhead);
        Assert.assertTrue(resp.getBody().contains(filename));
        String  delete_body=String.format("{\"fileName\":\"%s\",\"fileSize\":13611,\"filePath\":\"%s\",\"fileType\":null,\"fileStorageType\":null,\"isDir\":false,\"parentPath\":\"%s\",\"status\":null,\"createTime\":1637115248000,\"updateTime\":null,\"changePath\":null,\"startTime\":null,\"endTime\":null,\"nodeip\":null,\"paths\":null}"
                ,filename,test_export_path+"//"+filename,test_export_path);
        resp = api.deletefile(userhead,delete_body);
        Assert.assertTrue(resp.getCode()==200);
        delete_body=String.format("{\"fileName\":\"%s\",\"fileSize\":13611,\"filePath\":\"%s\",\"fileType\":null,\"fileStorageType\":null,\"isDir\":false,\"parentPath\":\"%s\",\"status\":null,\"createTime\":1637115248000,\"updateTime\":null,\"changePath\":null,\"startTime\":null,\"endTime\":null,\"nodeip\":null,\"paths\":null}"
                ,filename_path,test_export_path+"//"+filename_path,test_export_path);
        resp=api.deletefile(userhead,delete_body);//删除目录
        Assert.assertTrue("删除目录失败",resp.getCode()==200);
        //Thread.sleep(2000);
        String  cmd=String.format("ls  %s/%s|wc  -l",test_export_path,filename);
        System.out.println(cmd);
        Result  rest=sshcmd.exec(cmd);
        Assert.assertTrue("源文件通过接口删除失败",rest.getBody().equals("0"));
        cmd=String.format("ls  %s/%s|wc  -l",test_export_path+"//"+filename_path,filename);
        System.out.println(cmd);
        rest=sshcmd.exec(cmd);
        Assert.assertTrue("复制文件通过接口删除失败",checkfileexist(test_export_path+"//"+filename_path+"/"+filename,"0"));

    }




    @Test
    @UseDataProvider("data_file_list")
    public  void testcases_用户界面用户文件_移动到父目录(String upload_filename_path,String md5sum) throws UnsupportedEncodingException {
       // String filename_path="userdir" + String.valueOf(System.currentTimeMillis() / 1000);
        String filename_path = "userdir" + RandomStringUtils.randomAlphanumeric(10);
        String body=String.format("{\"fileName\":\"%s\",\"filePath\":\"\",\"sort\":0,\"parentPath\":\"%s\"}",filename_path,test_export_path);
        Result resp=api.Mkuserdir(body);
        Assert.assertTrue(resp.getCode()==200);
        int pos=api.url.indexOf("//");
        String uri= api.url.substring(pos+2,api.url.length());
        //通过接口判断目录是否创建成功
        resp=api.GetUserFileList(filename_path,test_export_path,userhead);
        Assert.assertTrue(resp.getBody().contains(filename_path));
        pos=upload_filename_path.lastIndexOf("\\");
        String filename=upload_filename_path.substring(pos+1,upload_filename_path.length());
        //往目录中上传一个文件
        //上传文件
        resp=api.uploadfile(upload_filename_path, test_export_path+"/"+filename_path,userhead);
        System.out.println(String.format("上传文件：%s,响应结果：%s",filename,resp.getBody().split("\r\n")[0]));
        Assert.assertTrue("上传文件失败",resp.getBody().contains("HTTP/1.1 200"));//判断上传文件接口响应结果
        resp=api.getcopyandmovedir(filepoolname,groupname,username);
        Assert.assertTrue("可移动的父目录获取不到",resp.getBody().contains(test_export_path));
        String copyandmove_body=String.format("{\"paths\":[\"%s\"],\"parentPath\":\"%s\",\"changePath\":\"%s\",\"status\":1}",
                test_export_path+"//"+filename_path+"//"+filename, test_export_path+"//"+filename_path,test_export_path);
        resp =  api.copyandmove(copyandmove_body);
        Assert.assertTrue(String.format("移动复制文件失败：%s",resp.getBody()),resp.getCode()==200);
        //判断文件是否移动成功

        resp=api.GetUserFileList(filename,test_export_path,userhead);
        System.out.println("判断文件是否移动成功: "+resp.getBody());
        Assert.assertTrue(resp.getBody().contains(filename));
        String  delete_body=String.format("{\"fileName\":\"%s\",\"fileSize\":13611,\"filePath\":\"%s\",\"fileType\":null,\"fileStorageType\":null,\"isDir\":false,\"parentPath\":\"%s\",\"status\":null,\"createTime\":1637115248000,\"updateTime\":null,\"changePath\":null,\"startTime\":null,\"endTime\":null,\"nodeip\":null,\"paths\":null}"
                ,filename,test_export_path+"//"+filename,test_export_path);
        resp = api.deletefile(userhead,delete_body);
        Assert.assertTrue(resp.getCode()==200);
        delete_body=String.format("{\"fileName\":\"%s\",\"fileSize\":13611,\"filePath\":\"%s\",\"fileType\":null,\"fileStorageType\":null,\"isDir\":false,\"parentPath\":\"%s\",\"status\":null,\"createTime\":1637115248000,\"updateTime\":null,\"changePath\":null,\"startTime\":null,\"endTime\":null,\"nodeip\":null,\"paths\":null}"
                ,filename_path,test_export_path+"//"+filename_path,test_export_path);
        resp=api.deletefile(userhead,delete_body);//删除目录
        Assert.assertTrue(String.format("删除目录失败：%s",resp.getBody()),resp.getCode()==200);

        String  cmd=String.format("ls  %s/%s|wc  -l",test_export_path,filename);
        System.out.println(cmd);
        Result  rest=sshcmd.exec(cmd);
        Assert.assertTrue("源文件通过接口删除失败",rest.getBody().equals("0"));
        //cmd=String.format("ls  %s/%s|wc  -l",test_export_path+"//"+filename_path,filename);
        //System.out.println(cmd);
        //rest=sshcmd.exec(cmd);
        Assert.assertTrue("源文件还存在",checkfileexist(test_export_path+"/"+filename_path+"/"+filename,"0"));
    }



    @Test
    @UseDataProvider("data_file_list")
    public  void testcases_用户界面用户文件_移动到目录(String upload_filename_path,String md5sum) throws UnsupportedEncodingException {
        //String filename_path="userdir" + String.valueOf(System.currentTimeMillis() / 1000);
        String filename_path = "userdir" + RandomStringUtils.randomAlphanumeric(10);
        String body=String.format("{\"fileName\":\"%s\",\"filePath\":\"\",\"sort\":0,\"parentPath\":\"%s\"}",filename_path,test_export_path);
        Result resp=api.Mkuserdir(body);
        Assert.assertTrue(resp.getCode()==200);
        int pos=api.url.indexOf("//");
        String uri= api.url.substring(pos+2,api.url.length());
        //通过接口判断目录是否创建成功
        resp=api.GetUserFileList(filename_path,test_export_path,userhead);
        Assert.assertTrue("通过接口判断目录创建失败",resp.getBody().contains(filename_path));
        pos=upload_filename_path.lastIndexOf("\\");
        String filename=upload_filename_path.substring(pos+1,upload_filename_path.length());
        //往目录中上传一个文件
        //上传文件
        String cmd=String.format("ls  -d %s/%s|wc  -l",test_export_path,filename_path);
        Result rest=sshcmd.exec(cmd);
        Assert.assertTrue(String.format("目录在后台没有创建成功，判断结果：%s，通过接口获取的结果：%s",rest.getBody(),resp.getBody()),rest.getBody().contains("1"));
        resp=api.uploadfile(upload_filename_path, test_export_path,userhead);
        System.out.println(String.format("上传文件：%s,响应结果：%s",filename,resp.getBody().split("\r\n")[0]));
        Assert.assertTrue("上传文件失败",resp.getBody().contains("HTTP/1.1 200"));//判断上传文件接口响应结果
        resp=api.getcopyandmovedir(filepoolname,groupname,username);
        Assert.assertTrue("可移动的子目录获取不到",resp.getBody().contains(filename_path));
        String copyandmove_body=String.format("{\"paths\":[\"%s\"],\"parentPath\":\"%s\",\"changePath\":\"%s\",\"status\":1}",
                test_export_path+"/"+filename,test_export_path,test_export_path+"//"+filename_path);
        resp =  api.copyandmove(copyandmove_body);
        Assert.assertTrue(resp.getCode()==200);
        //判断文件是否移动成功
        resp=api.GetUserFileList(filename,test_export_path+"//"+filename_path,userhead);
        System.out.println("判断文件是否移动成功: "+resp.getBody());
        Assert.assertTrue(resp.getBody().contains(filename));
        String  delete_body=String.format("{\"fileName\":\"%s\",\"fileSize\":13611,\"filePath\":\"%s\",\"fileType\":null,\"fileStorageType\":null,\"isDir\":false,\"parentPath\":\"%s\",\"status\":null,\"createTime\":1637115248000,\"updateTime\":null,\"changePath\":null,\"startTime\":null,\"endTime\":null,\"nodeip\":null,\"paths\":null}"
                ,filename,test_export_path+"//"+filename_path+"//"+filename,test_export_path);
        resp = api.deletefile(userhead,delete_body);
        Assert.assertTrue(resp.getCode()==200);
        delete_body=String.format("{\"fileName\":\"%s\",\"fileSize\":13611,\"filePath\":\"%s\",\"fileType\":null,\"fileStorageType\":null,\"isDir\":false,\"parentPath\":\"%s\",\"status\":null,\"createTime\":1637115248000,\"updateTime\":null,\"changePath\":null,\"startTime\":null,\"endTime\":null,\"nodeip\":null,\"paths\":null}"
                ,filename_path,test_export_path+"//"+filename_path,test_export_path);
        resp=api.deletefile(userhead,delete_body);//删除目录
        Assert.assertTrue(resp.getCode()==200);
        cmd=String.format("ls  %s/%s",test_export_path,filename);
        System.out.println(cmd);
        rest=sshcmd.exec(cmd);
        System.out.println(rest.getBody());
        cmd=String.format("ls  %s/%s|wc  -l",test_export_path,filename);
        System.out.println(cmd);
        rest=sshcmd.exec(cmd);
        Assert.assertTrue("源文件通过接口删除文件，接口返回成功,后台文件实际还在："+rest.getBody(),rest.getBody().equals("0"));
        cmd=String.format("ls  -d %s/%s|wc  -l",test_export_path+"//"+filename_path,filename);
        System.out.println(cmd);
        rest=sshcmd.exec(cmd);
        Assert.assertTrue("源文件还存在",rest.getBody().equals("0"));
    }

    @Test
    public  void testcases_用户界面用户文件_创建目录测试() throws UnsupportedEncodingException {
        for(int i=0;i<10;i++) {
            //String filename_path = "userdir" + RandomStringUtils.randomAlphanumeric(10);
            String filename_path="userdir" + String.valueOf(System.currentTimeMillis() / 1000);
            String body = String.format("{\"fileName\":\"%s\",\"filePath\":\"\",\"sort\":0,\"parentPath\":\"%s\"}", filename_path, test_export_path);
            Result resp = api.Mkuserdir(body);
            Assert.assertTrue(String.format("创建目录失败:%s",resp.getBody()),resp.getCode() == 200);
            //通过接口判断目录是否创建成功
            resp = api.GetUserFileList(filename_path, test_export_path, userhead);
            Assert.assertTrue("通过接口判断目录创建失败", resp.getBody().contains(filename_path));
            String cmd = String.format("ls   %s", test_export_path);
            Result rest = sshcmd.exec(cmd);
            System.out.println(String.format("获取创建目录列表：%s",rest.getBody()));
            Assert.assertTrue(String.format("目录在后台没有创建成功，判断结果：%s，通过接口获取的结果：%s", rest.getBody(), resp.getBody()), rest.getBody().contains(filename_path));
            rest = sshcmd.exec(String.format("rm -rf  %s/%s",test_export_path,filename_path));
            Assert.assertTrue("删除重命名文件失败",checkfileexist(test_export_path+"/"+filename_path,"0"));
        }
    }



    @Test
    public  void testcases_用户界面重命名目录()  {
        String filename_path="userdir" + String.valueOf(System.currentTimeMillis() / 1000);
        String body=String.format("{\"fileName\":\"%s\",\"filePath\":\"\",\"sort\":0,\"parentPath\":\"%s\"}",filename_path,test_export_path);
        //创建目录
        Result resp=api.Mkuserdir(body);
        Assert.assertTrue("重命名目录失败："+resp.getBody(),resp.getCode()==200);
        int pos=api.url.indexOf("//");
        //重命名目录
        String rename_body=String.format("{\"fileName\":\"%s\",\"filePath\":\"%s\",\"parentPath\":\"%s\",\"changePath\":\"%s\",\"sort\":0}",
                filename_path+"2",test_export_path+"/"+filename_path,test_export_path,test_export_path+"/"+filename_path+"2");
        resp = api.rename(rename_body);
        System.out.println(resp.getBody());
        Assert.assertTrue(resp.getCode()==200);
    }


    @Test
    @UseDataProvider("data_file_list")
    public  void testcases_用户界面重命名文件(String upload_filename_path,String md5sum) throws UnsupportedEncodingException {
        //String filename_path = "userdir" + String.valueOf(System.currentTimeMillis() / 1000);
        String filename_path = "userdir" + RandomStringUtils.randomAlphanumeric(10);
        String body = String.format("{\"fileName\":\"%s\",\"filePath\":\"\",\"sort\":0,\"parentPath\":\"%s\"}", filename_path, test_export_path);
        Result resp = api.Mkuserdir(body);
        Assert.assertTrue(String.format("创建目录%s失败,请求消息体:%s,错误信息：%s",filename_path,body,resp.getBody()),resp.getCode() == 200);
        int pos = api.url.indexOf("//");
        String uri = api.url.substring(pos + 2, api.url.length());
        pos = upload_filename_path.lastIndexOf("\\");
        String filename = upload_filename_path.substring(pos + 1, upload_filename_path.length());
        //往目录中上传一个文件
        //上传文件
        String cmd=String.format("ls -d %s/%s|wc  -l",test_export_path,filename_path);
        Result rest=sshcmd.exec(cmd);
        Assert.assertTrue(String.format("目录在后台没有创建成功，判断结果：%s，通过接口获取的结果：%s",rest.getBody(),resp.getBody()),rest.getBody().contains("1"));
        resp=api.uploadfile(upload_filename_path, test_export_path + "/" + filename_path,userhead);
        System.out.println(String.format("重命名上传文件：%s,响应结果：%s", filename, resp.getBody().split("\r\n")[0]));
        Assert.assertTrue("上传文件失败",resp.getBody().contains("HTTP/1.1 200"));//判断上传文件接口响应结果
        //判断文件上传成功
        resp=api.GetUserFileList(filename,test_export_path+"/"+filename_path,userhead);
        Assert.assertTrue("上传文件失败，无法获取上传文件的详情："+filename,resp.getBody().contains(filename));
        //重命名文件
        String rename_name=test_export_path+"/"+filename_path+"/"+filename+"2";
        String filePath=test_export_path+"/"+filename_path+"/"+filename;
        String rename_body=String.format("{\"fileName\":\"%s\",\"filePath\":\"%s\",\"parentPath\":\"%s\",\"changePath\":\"%s\",\"sort\":0}",
                filename,filePath,test_export_path+"/"+filename_path,rename_name);
        System.out.println("重命名文件 "+rename_name);
        resp = api.rename(rename_body);
        System.out.println(resp.getBody());
        Assert.assertTrue("重命名文件",resp.getCode()==200);
        rest = sshcmd.exec("ls -d "+rename_name+"|wc -l");
        Assert.assertTrue(String.format("查看文件个数不为1:%s,filepath:%s",rest.getBody(),rename_name),rest.getBody().contains("1"));
        rest = sshcmd.exec("ls  -l "+rename_name+"|awk  '{ print $3}'");
        Assert.assertTrue("文件用户的属主不对",rest.getBody().contains(username));
        rest = sshcmd.exec("ls  -l "+rename_name+"|awk  '{ print $4}'");
        Assert.assertTrue("文件用户的属组不对",rest.getBody().contains(groupname));
        rest = sshcmd.exec("rm -rf  "+rename_name);
        rest = sshcmd.exec("ls -d "+rename_name+"|wc -l");
        Assert.assertTrue("删除重命名文件失败",rest.getBody().contains("0"));
    }


}
