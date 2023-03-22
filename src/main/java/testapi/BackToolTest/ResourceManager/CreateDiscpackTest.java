package testapi.BackToolTest.ResourceManager;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import testapi.common.Result;
import testapi.common.resourceManager.Params;
import testapi.common.resourceManager.deviceInfo;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import java.util.ArrayList;
import testapi.common.resourceManager.Disc.opticalgroup;
import com.alibaba.fastjson.JSON;
import  testapi.common.api.ApiTest;
public class CreateDiscpackTest {

    ApiTest api=new ApiTest();

    @BeforeClass
    public static void beforeclass(){

    }



    @AfterClass
    public static void afterclass(){

    }

    @Test
    public void  TestCase_CreateDiscpack(){

        long time= System.currentTimeMillis();
        opticalgroup optical=new opticalgroup();
        optical.setName(String.valueOf(time));
        //optical.setOpticalServerID("AURA_E240018A047");
        optical.setNodeId(2);
        ArrayList list=new ArrayList();
        deviceInfo device=new deviceInfo();
        device.setNodeId(2);
        device.setInfoType("libraryInfo");
        Result resp=api.deviceInfo(JSON.toJSONString(device));
        System.out.println(resp.getCode());
        JSONObject c=  JSON.parseObject(resp.getBody());
        JSONArray b=c.getJSONArray("libraryIDList");
        String libraryType=b.getJSONObject(0).getString("libraryType");
        String libraryID=b.getJSONObject(0).getString("libraryID");
        optical.setOpticalServerID(libraryID);
        device.setLibId(libraryID);
        device.setSlotNumber(0);
        Params pams=new Params();
        pams.setSlotNum(0);
        pams.setLibId(libraryID);
        device.setParams(pams);
        resp=api.deviceInfo(JSON.toJSONString(device));
        device.setSlotNumber(1);
        pams.setSlotNum(1);
        device.setInfoType("slotInfo");
        device.setParams(pams);
        System.out.println(JSON.toJSONString(device));
        resp=api.deviceInfo(JSON.toJSONString(device));
        System.out.println(resp.getBody());
        c= JSON.parseObject(resp.getBody());
        b=c.getJSONArray("items");
        if(b.isEmpty())
            Assert.assertTrue(false);
        String slotId=b.getJSONObject(0).getString("slotId");
        list.add(slotId);
        optical.setSlotInfos(list);
        Object d=JSON.toJSONString(optical, SerializerFeature.WriteNullStringAsEmpty);
        System.out.println(d);
        resp=api.CreateDiscpack(d.toString());





    }

    @Test
    public void  TestCase_CreateDiscpack1() throws InterruptedException {

        for(int i=0;i<1000;i++)
        TestCase_CreateDiscpack();
        Thread.sleep(1000);


    }


}
