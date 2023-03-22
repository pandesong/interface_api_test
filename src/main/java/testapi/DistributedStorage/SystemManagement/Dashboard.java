package testapi.DistributedStorage.SystemManagement;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tngtech.java.junit.dataprovider.DataProvider;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import testapi.common.DistributedStorage.*;
import testapi.common.Result;
import testapi.common.api.PgApi;
import testapi.common.apiManager.DapiTest;
import testapi.framework.SSHExecutor;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
public class Dashboard {


    static DapiTest api=new DapiTest(false);
    static PgApi pg=new PgApi(api.database_ip,api.database_port,api.database_username,api.database_password,api.database_dbname);
    String storename="jjssmmtest211";
    int poolid=10;
    static SSHExecutor sshcmd=null;
    String password="Zj=34567800";
    String localfile="E:\\pandesong_项目\\存储文件备份\\testcode\\restapi\\config\\Dconfig.properties";
    static ArrayList<String> nodes=null;

    @BeforeClass
    public  static void beforeclass(){
        sshcmd=api.ssh_login(api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port);
        nodes = pg.execsqlex(String.format("SELECT  name,ip from  ame_storagenode  where  status=1  and  connect_status=1 and service_status=3;"));
    }

    @AfterClass
    public static void afterclass(){

    }

    @Test
    public  void check_alarm()  {
        Result resp = api.check_alarm();
        System.out.println(resp.getBody());
        Assert.assertTrue(resp.getCode()==200);
        Assert.assertTrue(resp.getBody()!=null);

    }

    @Test
    public  void check_storagenode()  {
        Result resp = api.check_storagenode();
        System.out.println(resp.getBody());
        Assert.assertTrue(resp.getCode()==200);
        Assert.assertTrue(resp.getBody()!=null);

    }

    @Test
    public  void check_getTopFsStatistics()  {
        Result resp = api.check_getTopFsStatistics();
        System.out.println(resp.getBody());
        Assert.assertTrue(resp.getCode()==200);
        Assert.assertTrue(resp.getBody()!=null);
    }

    @Test
    public  void check_getLDStatistics()  {
        Result resp = api.check_getLDStatistics();
        System.out.println(resp.getBody());
        Assert.assertTrue(resp.getCode()==200);
        Assert.assertTrue(resp.getBody()!=null);
    }
    @Test
    public  void check_getDiskStatistics()  {
        Result resp = api.check_getDiskStatistics();
        System.out.println(resp.getBody());
        Assert.assertTrue(resp.getCode()==200);
        Assert.assertTrue(resp.getBody()!=null);
    }

    @Test
    public  void check_getStoragepoolStatistics()  {
        Result resp = api.check_getStoragepoolStatistics();
        System.out.println(resp.getBody());
        Assert.assertTrue(resp.getCode()==200);
        Assert.assertTrue(resp.getBody()!=null);
    }

    @Test
    public  void check_getall()  {
        for(String node:nodes){
        String[]  nodde=node.split(",");
        String body=String.format("{\"hostid\":31,\"nodeip\":\"%s\",\"from\":3600,\"till\":0}",nodde[1]);
        Result resp = api.get_node(nodde[0]);
        String id=JSONObject.parseObject(resp.getBody()).getJSONArray("content").getJSONObject(0).getString("id");
        resp = api.check_getall(body.replace("\"hostid\":\"31","\"hostid\":\""+id));
        System.out.println(resp.getBody());
        Assert.assertTrue(resp.getCode()==200);
        Assert.assertTrue(resp.getBody()!=null);
        }
    }
}