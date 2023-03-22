package testapi.DistributedStorage.FilesStorage;import cn.afterturn.easypoi.excel.ExcelImportUtil;import cn.afterturn.easypoi.excel.entity.ImportParams;import com.alibaba.fastjson.JSON;import com.alibaba.fastjson.JSONArray;import com.alibaba.fastjson.JSONObject;import com.tngtech.java.junit.dataprovider.DataProvider;import com.tngtech.java.junit.dataprovider.DataProviderRunner;import com.tngtech.java.junit.dataprovider.UseDataProvider;import org.junit.*;import org.junit.runner.RunWith;import org.junit.runners.MethodSorters;import org.python.util.PythonInterpreter;import testapi.common.DistributedStorage.*;import testapi.common.Result;import testapi.common.api.PgApi;import testapi.common.apiManager.DapiTest;import testapi.framework.SSHExecutor;import java.io.File;import java.util.ArrayList;import java.util.List;import static testapi.DistributedStorage.FilesStorage.UserFileObject.getMD5Three;@RunWith(DataProviderRunner.class)@FixMethodOrder(MethodSorters.NAME_ASCENDING)public class Perfmance {    static DapiTest api=new DapiTest(false);    static PgApi pg=new PgApi(api.database_ip,api.database_port,api.database_username,api.database_password,api.database_dbname);    static String storename;    static int poolid=4;    static SSHExecutor sshcmd=null;    static String password="Zjcc_123";    static  String username="pdstest02";    static String  groupname="pgrouptest";    static  String  test_ftp_export_path=String.format("/ftp_exports/%s_%s/%s",storename,groupname,username);    static  String  nfs_ftp_export_path=String.format("/exports/%s_%s/%s",groupname,username,storename);    static  String  test_export_path=String.format("/exports/%s/%s/%s",storename,groupname,username);    static String  uid=null;    static String  id=null;    static  long  size=607374182400l/5;    @BeforeClass    public  static void beforeclass(){        sshcmd=api.ssh_login(api.ssh_dest_username,api.ssh_dest_password, api.ssh_dst_ip, api.ssh_dest_port);        storename=api.poolname;        id= pg.execsql(String.format("select  id from ame_fsusergroup where  name='%s'",api.groupname));        poolid= Integer.valueOf(pg.execsql(String.format("SELECT  id  from ame_storagepool where name='%s'", storename)));    }    @DataProvider    public static Object[][] ftp_file_list() {        String path=System.getProperty("user.dir");        Object a=new Object[][]{};        return new Object[][]{                {path+"\\TestData\\wps.exe", "123"}        };    }    @AfterClass    public static void afterclass(){        Result res = api.DeleteUser(uid);        assert res.getCode()==200;        res = api.DeleteGroup(id);        assert res.getCode()==200;        api.disableStoreAgePool("{\"id\":%id%}".replace("%id%",String.valueOf(poolid)));        api.deleteStoreAgePool(String.valueOf(poolid));    }    @DataProvider    public static List<hostlist> hostlistdate() {        try{            ImportParams params = new ImportParams();            params.setTitleRows(1);            params.setHeadRows(1);            List<hostlist> list = ExcelImportUtil.importExcel(new File(api.host_list), hostlist.class, params);            return list;        }catch (Exception e){            //e.printStackTrace();            return null;        }    }    public  String TestCase_CreateUserGroup(String groupname){        Assert.assertTrue(groupname!=null);        String JobName="pgroup"+String.valueOf(System.currentTimeMillis());        CreateGroupRequestModel Gre=new CreateGroupRequestModel();        Gre.setName(groupname);        Gre.setIsEnable(true);        Gre.setPsw(password);        Gre.setCheckPass(password);        Gre.setBillingMethod("1");        Gre.setPassword(api.encrypt(password.getBytes()));        ArrayList<StoragePoolVos>  pools=new ArrayList<StoragePoolVos>();        StoragePoolVos pool=new StoragePoolVos();        pool.setPoolId(poolid);        pool.setPoolName(storename);        pool.setNewQuota(1073741824l);        pools.add(pool);        ArrayList<TableData>  tds=new ArrayList<TableData>();        TableData td=new TableData();        td.setId(poolid);        td.setLabel(storename);        td.setFreeDiskSpace(1073741824);        td.setQuota("10");        td.setType("2");        tds.add(td);        Gre.setStoragePoolVos(pools);        Gre.setTableData(tds);        Result res=api.CreateGroupe(JSON.toJSONString(Gre));        // Assert.assertTrue(res.getCode()==200);        return   pg.execsql(String.format("select  id from ame_fsusergroup where  name='%s'",groupname));    }    public String CreateStorePool_danfuben() throws InterruptedException {        storename = "pool" + String.valueOf(System.currentTimeMillis() / 1000);        CreateStorePoolRequestModel rm=new CreateStorePoolRequestModel();        ArrayList unit=new ArrayList();        JSONArray um = api.GetStoreagePoolUnusedUint("", "", "");        for(Object ss:um){            StoreagePoolUnusedUintResponsedetailModel    dd= JSONObject.parseObject(ss.toString(),StoreagePoolUnusedUintResponsedetailModel.class);            unit.add(dd.getId());        }        rm.setName(storename);        rm.setHddUnits(unit);        rm.setPoolType(0);        rm.setHddStrategy(1);        rm.setSsdStrategy("");        rm.setSsdProtectNum(1);        rm.setHddProtectNum(1);        System.out.println(JSONObject.toJSON(rm).toString());        Result resp = api.CreateStore(JSONObject.toJSON(rm).toString());        Assert.assertTrue(resp.getCode()==200);        Result res=api.getpooldetail(storename);        StoreageModel sm=  JSONObject.parseObject(res.getBody(),StoreageModel.class);        api.enableStoreAgePool("{\"id\":%id%}".replace("%id%",sm.getContent().get(0).getId()));        while (api.GetStoreAgePool(storename).getBody().indexOf("\"status\":1")<0){            Thread.sleep(4000);        }        poolid=Integer.valueOf(sm.getContent().get(0).getId());        return  sm.getContent().get(0).getId();    }    public static String CreateStorePool_danfuben1() {        storename = "pool" + String.valueOf(System.currentTimeMillis() / 1000);        CreateStorePoolRequestModel rm=new CreateStorePoolRequestModel();        ArrayList unit=new ArrayList();        JSONArray um = api.GetStoreagePoolUnusedUint("", "", "");        for(Object ss:um){            StoreagePoolUnusedUintResponsedetailModel    dd= JSONObject.parseObject(ss.toString(),StoreagePoolUnusedUintResponsedetailModel.class);            unit.add(dd.getId());        }        rm.setName(storename);        rm.setHddUnits(unit);        rm.setPoolType(0);        rm.setHddStrategy(1);        rm.setSsdStrategy("");        rm.setSsdProtectNum(1);        rm.setHddProtectNum(1);        System.out.println(JSONObject.toJSON(rm).toString());        Result resp = api.CreateStore(JSONObject.toJSON(rm).toString());        Assert.assertTrue(resp.getCode()==200);        Result res=api.getpooldetail(storename);        StoreageModel sm=  JSONObject.parseObject(res.getBody(),StoreageModel.class);        api.enableStoreAgePool("{\"id\":%id%}".replace("%id%",sm.getContent().get(0).getId()));        poolid=Integer.valueOf(sm.getContent().get(0).getId());        return  sm.getContent().get(0).getId();    }    @Test    public  void testDcases_danfuben_test1() {        for (int i = 0; i < 50; i++) {            String username = "puser" + String.valueOf(System.currentTimeMillis() / 1000);            //String groupname = "guser" + String.valueOf(System.currentTimeMillis() / 1000);           // String id = TestCase_CreateUserGroup(groupname);            Assert.assertTrue(id != null);            CreateUserRequestModel user = new CreateUserRequestModel();            user.setName(username);            user.setGroupId(Integer.valueOf(id));            user.setPsw(password);            user.setCheckPass(password);            user.setQuotaType("1");            user.setIsEnable(true);            ArrayList<TableData> tds = new ArrayList<TableData>();            TableData td = new TableData();            td.setId(poolid);            td.setLabel(storename);            td.setFreeDiskSpace(size);            td.setQuota("10");            td.setType("2");            td.setQuotaType("1");            tds.add(td);            ArrayList<storageUserPoolVos> pools = new ArrayList<storageUserPoolVos>();            storageUserPoolVos pool = new storageUserPoolVos();            pool.setPoolId(poolid);            pool.setPoolName(storename);            pool.setNewQuota(String.valueOf(size));            pool.setQuotaType("1");            pools.add(pool);            user.setStorageUserPoolVos(pools);            user.setTableData(tds);            user.setPassword(api.encrypt(password.getBytes()));            Result resp = api.CreateUser(JSON.toJSONString(user));            System.out.println(resp.getBody());            Assert.assertTrue(resp.getCode() == 200);            String uid = pg.execsql(String.format("select  id from ame_fsuser where  name='%s'", username));            Result res = api.EnableFtpShare(uid,"\"3\"");            Assert.assertTrue(res.getCode() == 200);            try {                Thread.sleep(20000);            } catch (InterruptedException e) {                e.printStackTrace();            }            }    }}