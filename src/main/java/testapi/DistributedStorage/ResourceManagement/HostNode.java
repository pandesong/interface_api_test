package testapi.DistributedStorage.ResourceManagement;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
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

public class HostNode {




    static DapiTest api=new DapiTest(false);
    static  PgApi pg=new PgApi(api.database_ip,api.database_port,api.database_username,api.database_password,api.database_dbname);
    static SSHExecutor sshcmd=null;
    static ArrayList<String> nodes=null;

    @BeforeClass
    public  static void beforeclass(){
        sshcmd=api.ssh_login(api.ssh_src_username,api.ssh_src_password, api.ssh_src_ip, api.ssh_src_port);
        nodes = pg.execsqlex(String.format("SELECT  name from  ame_storagenode ;"));
    }

    @AfterClass
    public static void afterclass(){

    }


    @DataProvider
    public static List<hostlist> hostlistdate() {
        try{
            ImportParams params = new ImportParams();
            params.setTitleRows(1);
            params.setHeadRows(1);
            List<hostlist> list = ExcelImportUtil.importExcel(new File(api.host_list), hostlist.class, params);
            return list;
        }catch (Exception e){
            //e.printStackTrace();
            return null;
        }
    }


    @Test
    public  void check_enable_DisableNode()  {
        for(String node:nodes){
        String body="{\"id\":21,\"pid\":null,\"name\":\"node-2\",\"serverId\":null,\"ip\":\"192.168.13.187\",\"ip2\":\"10.10.10.12\",\"ip3\":\"192.168.37.12\",\"ip4\":null,\"username\":\"root\",\"nodeType\":1,\"nodeModel\":\"XS-120\",\"info\":null,\"status\":0,\"connectStatus\":1,\"serviceStatus\":3,\"cacheStatus\":0,\"ctrQos\":null,\"createTime\":1635388705276,\"updateTime\":1635762600140,\"ipUpdateable\":false,\"ip2Mask\":null,\"ip2Gateway\":null,\"ip3Mask\":null,\"ip3Gateway\":null,\"cacheTotal\":null,\"cacheUsed\":null,\"initSort\":null,\"testConnectionLoading\":false,\"connected\":true}";
        Result resp = api.get_node(node);
        String id=JSONObject.parseObject(resp.getBody()).getJSONArray("content").getJSONObject(0).getString("id");
        System.out.println("check  node :"+node+"---------"+resp.getBody());
        body=body.replace("\"id\":21","\"id\":"+id);
        resp = api.disable_node(body);
        Assert.assertTrue(resp.getCode()==200);
        Assert.assertTrue(resp.getBody()!=null);
        String num = pg.execsql(String.format("SELECT  count(1)  from ame_storage_unit WHERE  pool_id is  NULL"));
        resp = api.automaticChoseStorageUnits(num,"1","1");
        Assert.assertTrue(resp.getBody().indexOf("node-2")<0);
        body="{\"id\":21,\"pid\":null,\"name\":\"node-1\",\"serverId\":null,\"ip\":\"192.168.13.187\",\"ip2\":\"10.10.10.12\",\"ip3\":\"192.168.37.12\",\"ip4\":null,\"username\":\"root\",\"nodeType\":1,\"nodeModel\":\"XS-120\",\"info\":null,\"status\":1,\"connectStatus\":1,\"serviceStatus\":3,\"cacheStatus\":0,\"ctrQos\":null,\"createTime\":1635388705276,\"updateTime\":1635922045166,\"ipUpdateable\":false,\"ip2Mask\":null,\"ip2Gateway\":null,\"ip3Mask\":null,\"ip3Gateway\":null,\"cacheTotal\":null,\"cacheUsed\":null,\"initSort\":null,\"testConnectionLoading\":false,\"connected\":true}";
        body=body.replace("\"id\":21","\"id\":"+id);
        resp = api.enable_node(body);

        Assert.assertTrue(resp.getCode()==200);
        Assert.assertTrue(resp.getBody()!=null);

        }
    }



    @Test//节点的主控服务器界面
    public  void check_node_detail()  {
        for(String node:nodes) {
            String body = "{\"id\":21,\"deviceID\":0,\"adaptID\":0,\"slotid\":0,\"nodeModel\":\"XS-120\"}";
            Result resp = api.get_node(node);
            System.out.println("check  node :"+node+"---------"+resp.getBody());
            String id = JSONObject.parseObject(resp.getBody()).getJSONArray("content").getJSONObject(0).getString("id");
            System.out.println(body.replace("21", id));
            resp = api.check_disk(body.replace("21", id));
            Assert.assertTrue(resp.getCode() == 200);
            Assert.assertTrue(resp.getBody().indexOf("\"ret\":\"OK\"") >= 0);
        }
    }

    @Test
    public  void check_node_perfm()  {
        for(String node:nodes) {
            Result resp = api.get_node(node);
            String id = JSONObject.parseObject(resp.getBody()).getJSONArray("content").getJSONObject(0).getString("id");
            String body = "{\"key\":\"net_if_usage\",\"hostid\":\"3\"}".replace("\"hostid\":\"3", "\"hostid\":\"" + id);
            resp = api.check_selection(body);
            System.out.println("check  node :"+node+"---------"+resp.getBody());
            Assert.assertTrue(resp.getCode() == 200);
            Assert.assertTrue(resp.getBody().indexOf("{\"result\":") >= 0);

            body = "{\"hostid\":\"31\",\"from\":0,\"till\":0,\"filenumber\":31,\"nodeip\":\"192.168.13.188\"}".replace("\"hostid\":\"31", "\"hostid\":\"" + id);
            resp = api.check_monitor(body);
            Assert.assertTrue(resp.getCode() == 200);
            Assert.assertTrue(resp.getBody().indexOf("{\"cpuUserUse\":[{\"clock\":\"") >= 0);

            resp = api.check_condition(id);
            Assert.assertTrue(resp.getCode() == 200);
            Assert.assertTrue(resp.getBody().indexOf("\"connected\":true") >= 0);
            Assert.assertTrue(resp.getBody().indexOf("\"healthy\":\"健康\"") >= 0);
            Assert.assertTrue(resp.getBody().indexOf("\"status\":\"在线\"") >= 0);
        }
    }


    @Test
    public  void check_node_network()  {
        for(String node:nodes) {
            Result resp = api.get_node(node);
            String id = JSONObject.parseObject(resp.getBody()).getJSONArray("content").getJSONObject(0).getString("id");
            String body = "{\"id\":\"31\",\"ip\":\"192.168.13.188\"}".replace("\"id\":\"31", "\"id\":\"" + id);
            resp = api.check_network(body);
            System.out.println("check  node :"+node+"---------"+resp.getBody());
            Assert.assertTrue(resp.getCode() == 200);
            Assert.assertTrue("check node network  is not ok:"+node,resp.getBody().indexOf("\"result\":{\"ret\":\"OK\",\"") >= 0);
            body = "{\"key\":\"net_if_usage\",\"hostid\":\"31\"}".replace("\"hostid\":\"31", "\"hostid\":\"" + id);
            resp = api.check_selection(body);
            Assert.assertTrue(resp.getCode() == 200);
            Assert.assertTrue("check node network  is not ok:"+node,resp.getBody().indexOf("{\"result\":[") >= 0);

        }
    }

    @Test
    public  void check_node_service()  {
        for(String node:nodes) {
            Result resp = api.get_node(node);
            System.out.println("check  node :"+node+"---------"+resp.getBody());
            String id = JSONObject.parseObject(resp.getBody()).getJSONArray("content").getJSONObject(0).getString("id");
            resp = api.check_service(id);
            Assert.assertTrue(resp.getCode() == 200);
            Assert.assertTrue(String.format("check node %s service fail:%s",node,resp.getBody()),resp.getBody().indexOf("fail") < 0);
        }
    }


    @Test
    public  void check_node_diskCapacity()  {
        String  body="{\"id\":2,\"deviceID\":0,\"adaptID\":0,\"slotid\":0,\"nodeModel\":\"FT-120\"}";
        for(String node:nodes) {
            Result resp = api.get_node(node);
            String id = JSONObject.parseObject(resp.getBody()).getJSONArray("content").getJSONObject(0).getString("id");
            body=body.replace("2,",id+",");
            resp = api.get_diskCapacity(body);
            System.out.println("get_diskCapacity:"+node+"---------"+resp.getBody());
            Assert.assertTrue(resp.getCode() == 200);
            Assert.assertTrue(resp.getBody().indexOf("\"ret\":\"OK\"") >=0);
        }
    }







}