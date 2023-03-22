package testapi.BackToolTest.Dashboard;
import testapi.framework.http_api;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Assert;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DashboardTest {
    Object resp=null;
    http_api https = new http_api();
    static Map<String, String> head=new HashMap<String, String>();
     static  String Token="Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJyb290IiwiZXhwIjoxNjA4NzkyOTU4LCJpYXQiOjE2MDg3NzE5NTh9.1Tr70nH2aEB6ens2NbfFNQwCO5Q8KTI-sN93fFCYeS94sYCwubBR2VQvf0M04uHf6cQnPJ3MQyiDTMWNdLgYQw";
    @BeforeClass
    public static void beforeclass(){
        head.put("Authorization", Token);
    }
    @AfterClass
    public static void afterclass(){

    }

    @Test
    public void  TestCase_GetStoragenode(){
        //resp=https.http_delete("https://"+ commparam.ecs_host+"/v2/"+commparam.projectid+"/os-keypairs/"+name,head);
        try {
            resp=https.http_get("http://192.168.137.199:17980/admin-system/api/storagenode?",head);
            System.out.println(resp.toString());
            Assert.assertTrue(true);
        } catch (IOException e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
    }

    @Test
    public void  TestCase_Getstatistics() {
        try {
            resp = https.http_get("http://192.168.137.199:17980/admin-system/api/mtalarm/statistics", head);
            System.out.println(resp.toString());
            Assert.assertTrue(true);
        } catch (IOException e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
    }

        @Test
        public void  TestCase_GetdiscSummary(){
            try {
                resp=https.http_get("http://192.168.137.199:17980/admin-system/api/opticalgroupSlot/discSummary",head);
                System.out.println(resp.toString());
                Assert.assertTrue(true);
            } catch (IOException e) {
                e.printStackTrace();
                Assert.assertTrue(false);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Assert.assertTrue(false);
            }
    }

    @Test
    public void  TestCase_GetdiskSummary(){
        try {
            resp=https.http_get("http://192.168.137.199:17980/admin-system/api/diskgroupDisk/diskSummary",head);
            System.out.println(resp.toString());
            Assert.assertTrue(true);
        } catch (IOException e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
    }

    @Test
    public void  TestCase_Getopticalgroupsummary(){
        try {
            resp=https.http_get("http://192.168.137.199:17980/admin-system/api/opticalgroup/summary",head);
            System.out.println(resp.toString());
            Assert.assertTrue(true);
        } catch (IOException e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
    }

    @Test
    public void  TestCase_diskgroupsummary(){
        try {
            resp=https.http_get("http://192.168.137.199:17980/admin-system/api/diskgroup/summary",head);
            System.out.println(resp.toString());
            Assert.assertTrue(true);
        } catch (IOException e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
    }

    @Test
    public void  TestCase_storageAverageUsed(){
        try {
            resp=https.http_get("http://192.168.137.199:17980/admin-system/api/storageAverageUsed",head);
            System.out.println(resp.toString());

            Assert.assertTrue(true);
        } catch (IOException e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
    }
    @Test
    public void  TestCase_userProportion(){
        try {
            resp=https.http_get("http://192.168.137.199:17980/admin-system/api/ObjectStorageStatistics/userProportion",head);
            System.out.println(resp.toString());
            Assert.assertTrue(true);
        } catch (IOException e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
    }
    @Test
    public void  TestCase_mtservergetall(){
        try {
            resp=https.http_get("http://192.168.137.199:17980/admin-system/api/mtserver/getall",head);
            System.out.println(resp.toString());
            Assert.assertTrue(true);
        } catch (IOException e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
    }




}
