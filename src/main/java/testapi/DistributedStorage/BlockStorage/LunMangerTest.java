package testapi.DistributedStorage.BlockStorage;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import testapi.common.DistributedStorage.CreateStorePoolRequestModel;
import testapi.common.DistributedStorage.StoreageModel;
import testapi.common.DistributedStorage.StoreagePoolUnusedUintResponsedetailModel;
import testapi.common.Result;
import testapi.common.apiManager.DapiTest;
import testapi.framework.SSHExecutor;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@RunWith(DataProviderRunner.class)
public class LunMangerTest {

    static DapiTest api=new DapiTest(false);
    static  int  size=1073741824;

    static  String poolname="blockstoreage";
    static   int poolid=10;
    static  int lun_id;
    static SSHExecutor sshcmd=null;


    public static String CreateStorePool_danfuben() throws InterruptedException {
        poolname = "pool" + String.valueOf(System.currentTimeMillis() / 1000);
        CreateStorePoolRequestModel rm=new CreateStorePoolRequestModel();
        ArrayList unit=new ArrayList();
        JSONArray um = api.GetStoreagePoolUnusedUint("", "", "");
        for(Object ss:um){
            StoreagePoolUnusedUintResponsedetailModel dd= JSONObject.parseObject(ss.toString(),StoreagePoolUnusedUintResponsedetailModel.class);
            unit.add(dd.getId());
        }
        rm.setName(poolname);
        rm.setHddUnits(unit);
        rm.setPoolType(3);
        rm.setHddStrategy(1);
        rm.setSsdStrategy("");
        rm.setSsdProtectNum(1);
        rm.setHddProtectNum(1);
        System.out.println(JSONObject.toJSON(rm).toString());
        Result resp = api.CreateStore(JSONObject.toJSON(rm).toString());
        Assert.assertTrue(resp.getCode()==200);
        Result res=api.getpooldetail(poolname);
        StoreageModel sm=  JSONObject.parseObject(res.getBody(),StoreageModel.class);
        api.enableStoreAgePool("{\"id\":%id%}".replace("%id%",sm.getContent().get(0).getId()));
        while (api.GetStoreAgePool(poolname).getBody().indexOf("\"status\":1")<0){
            Thread.sleep(4000);
        }
        poolid=Integer.valueOf(sm.getContent().get(0).getId());
        return  sm.getContent().get(0).getId();
    }



    @BeforeClass
    public  static void beforeclass() throws InterruptedException {
        sshcmd=api.ssh_login(api.ssh_dest_username,api.ssh_dest_password, api.ssh_dst_ip, api.ssh_dest_port);


        CreateStorePool_danfuben();



    }

    @AfterClass
    public static void afterclass(){
        Result res=api.disableStoreAgePool("{\"id\":%id%}".replace("%id%",String.valueOf(poolid)));
        assert res.getCode()==200;
        res=api.deleteStoreAgePool(String.valueOf(poolid));
        assert res.getCode()==200;
    }

    @DataProvider
    public static Object[][] lun_name_list() {
        Object a=new Object[][]{};
        return new Object[][]{

                {"1a"},
                {"a"},
                {"aA"},
                {"aA111aA111aA111aA111aA111aA111aA"},

        };
    }

    @Test
    @UseDataProvider("lun_name_list")
    public  void TestCase_创建非批量卷并且删除_不支持快照(String lunname) throws InterruptedException {
        //String  lunname="luntest"+String.valueOf(System.currentTimeMillis());
        String reques_body=String.format("{\"name\":\"%s\",\"poolId\":%d,\"lunType\":2,\"quota\":%d}",
                lunname,poolid,size
        );
        Result res=api.CreateLun(reques_body);
        Assert.assertTrue(res.getCode()==200);
        for(int i=0;i<1800;i++){
            res=api.GetLunDetailByName(lunname);
            JSONArray test= JSON.parseObject(res.getBody()).getJSONArray("content");
            int status=test.getJSONObject(0).getInteger("createStatus");
            int lunid=test.getJSONObject(0).getInteger("id");
            lun_id=lunid;
            String uuid=test.getJSONObject(0).getString("uuid");
            if(status==1){
                System.out.println(String.format("创建卷 %s 成功",lunname));
                //检查后台是否有对应的卷生成
                String cmd=String.format("ls /exports/%s/%s | wc -l",poolname,uuid);
                Result   rest=sshcmd.exec(cmd);
                Assert.assertTrue(rest.getBody().equals("1"));
                res=api.DelLun(lunid);
                Assert.assertTrue(res.getCode()==200);
                System.out.println(String.format("删除卷 %s 成功",lunname));
                Thread.sleep(3000);
                //后台卷删除成功
                rest=sshcmd.exec(cmd);
                Assert.assertTrue(rest.getBody().equals("0"));
                break;
            }
            else if(status==2 || status==4){
                Assert.assertTrue(false);
            }
        }
    }


    @Test
    public  void TestCase_创建批量卷并且删除_数量1个_不支持快照() throws InterruptedException {
        String  lunname="luntest"+String.valueOf(System.currentTimeMillis());
        String reques_body=String.format("{\"name\":\"%s\",\"poolId\":%s,\"prefix\":\"prefix\",\"nums\":\"1\",\"lunType\":2,\"quota\":%d}",
                lunname,poolid,size
        );
        Result res=api.CreateLun(reques_body);
        Assert.assertTrue(res.getCode()==200);
        String search_name="prefix"+lunname;
        ArrayList lunids=new ArrayList();
        boolean flag=true;
        for(int i=0;i<1800;i++){
            int count=0;
            res=api.GetLunDetailByName(search_name);
            JSONArray test= JSON.parseObject(res.getBody()).getJSONArray("content");
            Assert.assertTrue(test.size()==1);
            for(Object ob:test){
                JSONObject ob1 = (JSONObject) ob;
                int status=ob1.getInteger("createStatus");
                int lunid=ob1.getInteger("id");
                lunids.add(lunid);
                if(status==1){
                    count++;
                }
                else if(status==2 || status==4){
                    flag=false;
                }
            }
            Assert.assertTrue(flag);
            if(count==1) break;
            Thread.sleep(1000);
        }

        for(Object lunid:lunids){
            res=api.DelLun((int)lunid);
            Assert.assertTrue(res.getCode()==200);}
    }

    @Test
    public  void TestCase_创建批量卷并且删除_数量5个_不支持快照() throws InterruptedException {
        String  lunname="luntest"+String.valueOf(System.currentTimeMillis());
        int batch_lun_number=5;
        String reques_body=String.format("{\"name\":\"%s\",\"poolId\":%s,\"prefix\":\"prefix\",\"nums\":\"%d\",\"lunType\":2,\"quota\":%d}",
                lunname,poolid,batch_lun_number,size
        );
        Result res=api.CreateLun(reques_body);
        Assert.assertTrue(res.getCode()==200);
        String search_name="prefix"+lunname;
        ArrayList lunids=new ArrayList();
        ArrayList uuids=new ArrayList();
        boolean flag=true;
        for(int i=0;i<1800;i++){
            int count=0;
            for(int x=0;x<batch_lun_number;x++){
                res=api.GetLunDetailByName(search_name+String.valueOf(x));
                JSONArray test= JSON.parseObject(res.getBody()).getJSONArray("content");
                Assert.assertTrue(test.size()==1);
                int status=test.getJSONObject(0).getInteger("createStatus");
                int lunid=test.getJSONObject(0).getInteger("id");
                String uuid=test.getJSONObject(0).getString("uuid");
                lunids.add(lunid);
                uuids.add(uuid);
                if(status==1){

                    String cmd=String.format("ls /exports/%s/%s | wc -l",poolname,uuid);
                    Result   rest=sshcmd.exec(cmd);
                    Assert.assertTrue(rest.getBody().equals("1"));
                    count++;

                }
                else if(status==2 || status==4){
                    System.out.println("创建卷失败："+lunname);
                    Assert.assertTrue(false);
                    //flag=false;
                }
            }

            Assert.assertTrue(flag);
            if(count==batch_lun_number) break;
            Thread.sleep(1000);
        }
        List<String> newList = new ArrayList<String>(new HashSet<String>(lunids));
        List<String> uuids1 = new ArrayList<String>(new HashSet<String>(uuids));
        for(Object lunid:newList){
            res=api.DelLun((int)lunid);
            Assert.assertTrue(res.getCode()==200);
        }
        Thread.sleep(3000);
        for(Object uuid:uuids1){
            String cmd=String.format("ls /exports/%s/%s | wc -l",poolname,uuid);
            Result   rest=sshcmd.exec(cmd);
            Assert.assertTrue(rest.getBody().equals("0"));
        }

    }


    @Test
    @UseDataProvider("lun_name_list")
    public  void TestCase_创建非批量卷并且删除_支持快照(String lunname) throws InterruptedException {
        //String  lunname="luntest"+String.valueOf(System.currentTimeMillis());
        String reques_body=String.format("{\"name\":\"%s\",\"poolId\":%d,\"lunType\":1,\"quota\":%d}",
                lunname,poolid,size
        );
        Result res=api.CreateLun(reques_body);
        Assert.assertTrue(res.getCode()==200);
        for(int i=0;i<1800;i++){
            res=api.GetLunDetailByName(lunname);
            JSONArray test= JSON.parseObject(res.getBody()).getJSONArray("content");
            int status=test.getJSONObject(0).getInteger("createStatus");
            int lunid=test.getJSONObject(0).getInteger("id");
            lun_id=lunid;
            String uuid=test.getJSONObject(0).getString("uuid");
            if(status==1){
                System.out.println(String.format("创建卷 %s 成功",lunname));
                //检查后台是否有对应的卷生成
                String cmd=String.format("ls /exports/%s/%s | wc -l",poolname,uuid);
                Result   rest=sshcmd.exec(cmd);
                Assert.assertTrue(rest.getBody().equals("1"));
                res=api.DelLun(lunid);
                Assert.assertTrue(res.getCode()==200);
                System.out.println(String.format("删除卷 %s 成功",lunname));
                Thread.sleep(3000);
                //后台卷删除成功
                rest=sshcmd.exec(cmd);
                Assert.assertTrue(rest.getBody().equals("0"));
                break;
            }
            else if(status==2 || status==4){
                Assert.assertTrue(false);
            }
        }
    }


}
