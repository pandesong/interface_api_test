package testapi.DistributedStorage.LogManager;



import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import testapi.common.DistributedStorage.*;
import testapi.common.Result;
import testapi.common.api.PgApi;
import testapi.common.apiManager.DapiTest;
import testapi.framework.SSHExecutor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@RunWith(DataProviderRunner.class)
public class Alarm {
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
    public  static void beforeclass() throws InterruptedException {
        CreateStorePool_danfuben();
        poolid=Integer.valueOf(pg.execsql(String.format("SELECT  id  from ame_storagepool where name='%s';",filepoolname)));
        sshcmd=api.ssh_login(api.ssh_dest_username,api.ssh_dest_password, api.ssh_dst_ip, api.ssh_dest_port);

    }

    @AfterClass
    public static void afterclass(){
        Result res =  api.disableStoreAgePool("{\"id\":%id%}".replace("%id%",String.valueOf(poolid)));
        assert res.getCode()==200;
        res =  api.deleteStoreAgePool(String.valueOf(poolid));
        assert res.getCode()==200;
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
              //  {"훌륭해요,아주잘해주셨어요,ひとつになって,繁體你好.txt", "123"},
        };
    }



    @Test
    public  void testcases_网络异常_告警() throws InterruptedException {
        Boolean FF=false;
        String  dst_md5_cmd=String.format("/usr/local/ames/ames_cfg.sh GET network-status-detail");
        Result  rest=sshcmd.exec(dst_md5_cmd);
        String network_detail= rest.getBody().toString().replace('\n',' ').split("\"data\":")[1];
        network_detail=network_detail.substring(0,network_detail.length()-1);
        JSONArray a=JSONArray.parseArray(network_detail);
        String card_name=a.getJSONObject(0).keySet().iterator().next();
        String purpose=JSONObject.parseObject(a.getJSONObject(0).values().iterator().next().toString()).getString("purpose");
        String ip=JSONObject.parseObject(a.getJSONObject(0).values().iterator().next().toString()).getString("ip");
        String type=JSONObject.parseObject(a.getJSONObject(0).values().iterator().next().toString()).getString("type");
        String mask=JSONObject.parseObject(a.getJSONObject(0).values().iterator().next().toString()).getString("mask");
        String status=JSONObject.parseObject(a.getJSONObject(0).values().iterator().next().toString()).getString("status");
        rest=sshcmd.exec(String.format("ifdown %s",card_name));
        rest=sshcmd.exec("date  +%s");
        api.exec_alarm_service("100002");//基础告警
        for(int i=0;i<1800;i++){
            String createtime=pg.execsql(String.format("SELECT  create_time  from  ame_mtalarm  where  alarm_title = '网络异常'  and alarm_content ilike '%%%s%%' and status=0 ORDER BY create_time  DESC  limit 1;",card_name));
            if(createtime!=""){
            String stamp=pg.execsql(String.format("select EXTRACT(epoch FROM CAST( '%s' AS TIMESTAMP))",createtime));
            if(Float.valueOf(rest.getBody())<Float.valueOf(stamp)-8*3600+120){
               // sshcmd.exec(String.format("ifup %s",card_name));
                FF=true;
                String id=pg.execsql(String.format("SELECT  id  from  ame_mtalarm  where  alarm_title = '网络异常'  and alarm_content ilike '%%%s%%'  and status=0 ORDER BY create_time  DESC  limit 1;",card_name));
                api.exec_alarm_confirm(id);
                break;
            }
            }
        Thread.sleep(1000);
        }
         rest=sshcmd.exec(String.format("ifup %s",card_name));
         Assert.assertTrue(FF);
         FF=false;
         card_name=a.getJSONObject(1).keySet().iterator().next();
         purpose=JSONObject.parseObject(a.getJSONObject(1).values().iterator().next().toString()).getString("purpose");
         ip=JSONObject.parseObject(a.getJSONObject(1).values().iterator().next().toString()).getString("ip");
         type=JSONObject.parseObject(a.getJSONObject(1).values().iterator().next().toString()).getString("type");
         mask=JSONObject.parseObject(a.getJSONObject(1).values().iterator().next().toString()).getString("mask");
         status=JSONObject.parseObject(a.getJSONObject(1).values().iterator().next().toString()).getString("status");
         rest=sshcmd.exec(String.format("ifdown %s",card_name));
         rest=sshcmd.exec(String.format("date  +%%s"));
        api.exec_alarm_service("100002");//基础告警
        for(int i=0;i<1800;i++){
            String createtime=pg.execsql(String.format("SELECT  create_time  from  ame_mtalarm  where  alarm_title = '网络异常'  and alarm_content ilike '%%%s%%'  and status=0 ORDER BY create_time  DESC  limit 1;",card_name));
            if(createtime!=""){
                String stamp=pg.execsql(String.format("select EXTRACT(epoch FROM CAST( '%s' AS TIMESTAMP))",createtime));
                if(Float.valueOf(rest.getBody())<Float.valueOf(stamp)-8*3600+120){
                   // rest=sshcmd.exec(String.format("ifup %s",card_name));
                    FF=true;
                    String id=pg.execsql(String.format("SELECT  id  from  ame_mtalarm  where  alarm_title = '网络异常'  and alarm_content ilike '%%%s%%' and  status=0 ORDER BY create_time  DESC  limit 1;",card_name));
                    api.exec_alarm_confirm(id);
                    break;
                }
            }
            Thread.sleep(1000);
        }
        rest=sshcmd.exec(String.format("ifup %s",card_name));
        assert  FF==true;

    }

    @Test
    public  void testcases_服务异常_告警_手动点击定时任务触发() throws InterruptedException, UnsupportedEncodingException {
        Boolean FF=false;
        String  dst_md5_cmd=String.format("/usr/local/ames/ames_cfg.sh GET service-status2");
        Result  rest=sshcmd.exec(dst_md5_cmd);
        JSONObject service_detail= JSONObject.parseObject(rest.getBody());
        if(service_detail.getString("ret").equals("OK")){
          JSONArray  a=service_detail.getJSONArray("data");
          for(Object c:a){
            FF=false;
            String displayName=JSONObject.parseObject(c.toString()).getString("displayName");
            String name=JSONObject.parseObject(c.toString()).getString("name");
            System.out.println(String.format("%s服务告警测试",name));
            String status=JSONObject.parseObject(c.toString()).getString("status");
            dst_md5_cmd=String.format("systemctl stop %s",name);
            rest=sshcmd.exec(dst_md5_cmd);
            //Thread.sleep(5000);
            dst_md5_cmd=String.format("date  +%%s");
            rest=sshcmd.exec(dst_md5_cmd);
            Result  res=api.exec_alarm_service("100004");
            for(int i=0;i<120;i++){
                String createtime=pg.execsql(String.format("SELECT  create_time  from  ame_mtalarm  where  alarm_title  ilike '%%服务异常%%'  and alarm_content ilike '%%%s%%'  and status=0  ORDER BY create_time  DESC  limit 1 ;",displayName));
                if(createtime!=""){
                    String stamp=pg.execsql(String.format("select EXTRACT(epoch FROM CAST( '%s' AS TIMESTAMP))",createtime));
                    if(Float.valueOf(rest.getBody())<Float.valueOf(stamp)-8*3600+240){
                        FF=true;
                        String id=pg.execsql(String.format("SELECT  id  from  ame_mtalarm  where  alarm_title ilike '%%服务异常%%'  and alarm_content ilike '%%%s%%'  and status=0  ORDER BY create_time  DESC  limit 1 ;",displayName));
                        res=api.exec_alarm_confirm(id);
                        break;
                    }
                }
                Thread.sleep(1000);
            }
            dst_md5_cmd=String.format("systemctl start %s",name);
            rest=sshcmd.exec(dst_md5_cmd);
            assert  FF==true;
          }
        }
        else assert false;
    }

    @Test
    public  void testcases_资源池告警() throws InterruptedException {
        Boolean FF=false;
        System.out.println("停止存在存储池，测试资源池告警");
        String  dst_md5_cmd=String.format("echo  'y'|/usr/local/amefs/sbin/ame lvg  stop    %s",filepoolname);
        Result  rest=sshcmd.exec(dst_md5_cmd);
        dst_md5_cmd=String.format("date  +%%s");
        rest=sshcmd.exec(dst_md5_cmd);
        Result  res=api.exec_alarm_service("100001");
        assert  res.getCode()==204 || res.getCode()==200;
                for(int i=0;i<120;i++){
                    System.out.println("等待资源池告警产生");
                    String createtime=pg.execsql(String.format("SELECT  create_time  from  ame_mtalarm  where  alarm_title = '文件存储池'  and alarm_content ilike '%%%s%%'  and status=0 ORDER BY create_time  DESC  limit 1 ;",filepoolname));
                    if(createtime!=""){
                        String stamp=pg.execsql(String.format("select EXTRACT(epoch FROM CAST( '%s' AS TIMESTAMP))",createtime));
                        if(Float.valueOf(rest.getBody())<Float.valueOf(stamp)-8*3600+120){
                            String id=pg.execsql(String.format("SELECT  id  from  ame_mtalarm  where  alarm_title = '文件存储池'  and alarm_content ilike '%%%s%%'  and status=0 ORDER BY create_time  DESC  limit 1 ;",filepoolname));
                            res=api.exec_alarm_confirm(id);
                            FF=true;
                            break;
                        }
                    }
                    Thread.sleep(1000);
                }
          dst_md5_cmd=String.format("echo  'y'|/usr/local/amefs/sbin/ame lvg  start   %s",filepoolname);
          rest=sshcmd.exec(dst_md5_cmd);
          assert  FF==true;
    }

    @Test
    public  void testcases_存储单元掉线告警() throws InterruptedException {
        Boolean FF=false;
        String  dst_md5_cmd=String.format("echo  'y'|/usr/local/amefs/sbin/ame lvg  start   %s",filepoolname);
        Result rest=sshcmd.exec(dst_md5_cmd);
        dst_md5_cmd=String.format("ps -ef|grep %s|grep  unit|grep pid|awk '{ print $2}'|xargs  -n1 -i kill  -9  {}",filepoolname);
        rest=sshcmd.exec(dst_md5_cmd);
        dst_md5_cmd=String.format("date  +%%s");
        System.out.println("杀掉存储池进程,测试存储单元告警");
        rest=sshcmd.exec(dst_md5_cmd);
        Result  res=api.exec_alarm_service("100001");
        assert  res.getCode()==204 || res.getCode()==200;
        for(int i=0;i<120;i++){
            System.out.println("等待存储池单元不在线告警产生");
            String createtime=pg.execsql(String.format("SELECT  create_time  from  ame_mtalarm  where  alarm_title ='文件存储池'  and alarm_content ilike '%%%s%%is%%not%%online'  and status=0 ORDER BY create_time  DESC  limit 1 ;",filepoolname));
            if(createtime!=""){
                String stamp=pg.execsql(String.format("select EXTRACT(epoch FROM CAST( '%s' AS TIMESTAMP))",createtime));
                if(Float.valueOf(rest.getBody())<Float.valueOf(stamp)-8*3600+120){
                    FF=true;
                    String id=pg.execsql(String.format("SELECT  id  from  ame_mtalarm  where  alarm_title ='文件存储池'  and alarm_content ilike '%%%s%%is%%not%%online'  and status=0 ORDER BY create_time  DESC  limit 1 ;",filepoolname));
                    res=api.exec_alarm_confirm(id);
                    break;
                }
            }
            Thread.sleep(1000);
        }
        dst_md5_cmd=String.format("echo  'y'|/usr/local/amefs/sbin/ame lvg  stop   %s",filepoolname);
        rest=sshcmd.exec(dst_md5_cmd);
        dst_md5_cmd=String.format("echo  'y'|/usr/local/amefs/sbin/ame lvg  start   %s",filepoolname);
        rest=sshcmd.exec(dst_md5_cmd);
        assert  FF==true;
    }



    @Test
    public  void testcases_内存告警() throws InterruptedException {
        Boolean FF=false;
        String  dst_md5_cmd=String.format("echo -e  'from ctypes import *\\nimport time\\nmem=create_string_buffer(1024*1024*1024*50)\\ntime.sleep(120)\\nmen=None'  > xx1.py&&nohup  python xx1.py &>/dev/null");
        Result  rest=sshcmd.exec(dst_md5_cmd);
        for(int i=0;i<360;i++){
            dst_md5_cmd=String.format("free  -g  |grep Mem|awk  '{ print $4}'");
            rest=sshcmd.exec(dst_md5_cmd);
            if(Integer.valueOf(rest.getBody())<10){
            FF=true;
            break;
            }
            Thread.sleep(1000);
        }
        assert FF=true;
        FF=false;
        dst_md5_cmd=String.format("date  +%%s");
        rest=sshcmd.exec(dst_md5_cmd);
        Result   res=api.exec_alarm_service("100002");//基础告警
        assert  res.getCode()==204 || res.getCode()==200;
        for(int i=0;i<120;i++){
            System.out.println("等待内存告警产生");
            String createtime=pg.execsql(String.format("SELECT  create_time  from  ame_mtalarm  where  alarm_title = '内存异常'  and alarm_content ilike '%%内存使用率为%%'  and status=0 ORDER BY create_time  DESC  limit 1 ;"));
            if(createtime!=""){
                String stamp=pg.execsql(String.format("select EXTRACT(epoch FROM CAST( '%s' AS TIMESTAMP))",createtime));
                if(Float.valueOf(rest.getBody())<Float.valueOf(stamp)-8*3600+120){
                    String id=pg.execsql(String.format("SELECT  id  from  ame_mtalarm  where  alarm_title = '内存异常'  and alarm_content ilike '%%内存使用率为%%'  and status=0 ORDER BY create_time  DESC  limit 1 ;"));
                    res=api.exec_alarm_confirm(id);
                    FF=true;
                    break;
                }
            }
            Thread.sleep(1000);
        }
        dst_md5_cmd=String.format("ps -ef|grep python|grep xx1.py|awk  '{ print $2}'|xargs  -n1 -i kill  -9  {}&&rm  -rf xx1.py");
        rest=sshcmd.exec(dst_md5_cmd);
        assert  FF==true;
    }



    @Test
    public  void testcases_CPU告警() throws InterruptedException {
        Boolean FF=false;
        String  dst_md5_cmd=String.format("echo  -e  \"from multiprocessing import Pool\\nimport multiprocessing\\nimport time,os\\ndef loop(name):\\n  x=0\\n  while True:\\n    x=x^1\\np=Pool(100)\\nfor i in range(multiprocessing.cpu_count()):\\n  p.apply_async(loop,args=(i,))\\np.close()\\np.join()\"   > xx1.py&&nohup  python xx1.py  & >/dev/null");
        Result  rest=sshcmd.exec(dst_md5_cmd);
        dst_md5_cmd=String.format("date  +%%s");
        rest=sshcmd.exec(dst_md5_cmd);
        Thread.sleep(5000);
        Result   res=api.exec_alarm_service("100002");//基础告警
        Thread.sleep(2000);
        assert  res.getCode()==204 || res.getCode()==200;
        for(int i=0;i<270;i++){
            System.out.println("等待cpu告警产生");
            String createtime=pg.execsql(String.format("SELECT  create_time  from  ame_mtalarm  where  alarm_title = 'CPU异常'  and alarm_content ilike '%%CPU使用率为%%'  and status=0 ORDER BY create_time  DESC  limit 1 ;"));
            if(createtime!=""){
                String stamp=pg.execsql(String.format("select EXTRACT(epoch FROM CAST( '%s' AS TIMESTAMP))",createtime));
                if(Float.valueOf(rest.getBody())<Float.valueOf(stamp)-8*3600+120){
                    String id=pg.execsql(String.format("SELECT  id  from  ame_mtalarm  where  alarm_title = 'CPU异常'  and alarm_content ilike '%%CPU使用率为%%'  and status=0 ORDER BY create_time  DESC  limit 1 ;"));
                    res=api.exec_alarm_confirm(id);
                    FF=true;
                    break;
                }
            }
            Thread.sleep(1000);
        }
        dst_md5_cmd=String.format("ps -ef|grep python|grep xx1.py|awk  '{ print $2}'|xargs  -n1 -i kill  -9  {}&&rm  -rf xx1.py");
        rest=sshcmd.exec(dst_md5_cmd);
        assert  FF==true;
    }

    @Test
    public  void testcases_磁盘告警() throws InterruptedException {
        Boolean FF=false;
        String  dst_md5_cmd=String.format("echo  -e  \"from multiprocessing import Pool\\nimport multiprocessing\\nimport time,os\\ndef loop(name):\\n  x=0\\n  while True:\\n    x=x^1\\np=Pool(100)\\nfor i in range(multiprocessing.cpu_count()):\\n  p.apply_async(loop,args=(i,))\\np.close()\\np.join()\"   > xx1.py&&nohup  python xx1.py  & >/dev/null");
        Result  rest=sshcmd.exec(dst_md5_cmd);
        dst_md5_cmd=String.format("date  +%%s");
        rest=sshcmd.exec(dst_md5_cmd);
        Thread.sleep(5000);
        Result   res=api.exec_alarm_service("100002");//基础告警
        Thread.sleep(2000);
        assert  res.getCode()==204 || res.getCode()==200;
        for(int i=0;i<270;i++){
            System.out.println("等待cpu告警产生");
            String createtime=pg.execsql(String.format("SELECT  create_time  from  ame_mtalarm  where  alarm_title = 'CPU异常'  and alarm_content ilike '%%CPU使用率为%%'  and status=0 ORDER BY create_time  DESC  limit 1 ;"));
            if(createtime!=""){
                String stamp=pg.execsql(String.format("select EXTRACT(epoch FROM CAST( '%s' AS TIMESTAMP))",createtime));
                if(Float.valueOf(rest.getBody())<Float.valueOf(stamp)-8*3600+120){
                    String id=pg.execsql(String.format("SELECT  id  from  ame_mtalarm  where  alarm_title = 'CPU异常'  and alarm_content ilike '%%CPU使用率为%%'  and status=0 ORDER BY create_time  DESC  limit 1 ;"));
                    res=api.exec_alarm_confirm(id);
                    FF=true;
                    break;
                }
            }
            Thread.sleep(1000);
        }
        dst_md5_cmd=String.format("ps -ef|grep python|grep xx1.py|awk  '{ print $2}'|xargs  -n1 -i kill  -9  {}&&rm  -rf xx1.py");
        rest=sshcmd.exec(dst_md5_cmd);
        assert  FF==true;
    }

}
