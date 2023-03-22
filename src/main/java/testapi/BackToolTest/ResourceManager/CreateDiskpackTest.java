package  testapi.BackToolTest.ResourceManager;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import testapi.common.api.ApiTest;
import testapi.common.resourceManager.Disc.opticalgroup;
import testapi.framework.http_api;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CreateDiskpackTest {
    Object resp=null;
    http_api https = new http_api();
    static Map<String, String> head=new HashMap<String, String>();
    ApiTest api=new ApiTest();
    static  String Token="Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJyb290IiwiZXhwIjoxNjA5MTM5NzMwLCJpYXQiOjE2MDkxMTg3MzB9.SbOtY4-u10XM9FaVEfngkFs088T-AjAdaCK5g5Fc43KBdKaT_ylhRSW-X9a2wBL6nOUMnd7xJqMqmvV--48F4A";
    @BeforeClass
    public static void beforeclass(){
        head.put("Authorization", Token);
        head.put("Content-Type","application/json");
    }

    @AfterClass
    public static void afterclass(){

    }

    @Test
    public void  TestCase_CreateDiskpack(){
        opticalgroup optical=new opticalgroup();
        optical.setName("000010");
        optical.setNodeId(1);
        ArrayList list=new ArrayList();
        list.add("sdc9");
        optical.setSlotInfos(list);
        Object c=JSON.toJSONString(optical, SerializerFeature.WriteNullStringAsEmpty);
        System.out.println(c);
        resp=api.CreateDiskpack(c.toString());
        System.out.println(resp.toString());
        Assert.assertTrue(true);
    }


    
}
