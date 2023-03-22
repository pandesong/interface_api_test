package testapi.BackToolTest.ResourceManager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializerFeature;
import testapi.common.Result;
import testapi.common.api.ApiTest;
import testapi.common.resourceManager.Config;
import testapi.common.resourceManager.StorageDetail;
import testapi.common.resourceManager.storagepool;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CreateStorepoolTest {


    static  ApiTest api=new ApiTest();


    @JSONField(serializeUsing = ListMapSerializer.class, serialzeFeatures = SerializerFeature.WriteMapNullValue)
    private List<Map<String, Object>> list;

    // 自定义序列化
    public static class ListMapSerializer implements ObjectSerializer {
        @Override
        public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) {
            // 如果为空，调用默认
            if(object == null) {
                serializer.write(object);
                return;
            }
            // 不为空，调用features的序列化
            Class<?> clazz = object.getClass();
            ObjectSerializer writer = serializer.getObjectWriter(clazz);
            try {
                writer.write(serializer, object, null, null, features);
            } catch (IOException e) {
                throw new JSONException(e.getMessage(), e);
            }
        }
    }




    @BeforeClass
    public static void beforeclass(){

    }



    @AfterClass
    public static void afterclass(){

    }
    @Test
    public  void TestCase_CreateStorepool(){

        //Config testapi.config=new Config();
        storagepool pool=new storagepool();
        pool.setName(String.valueOf(System.currentTimeMillis()));
        StorageDetail detail=new StorageDetail();
        Result resp=api.GetStorageDetail(JSON.toJSONString(detail));
        JSONObject a=JSON.parseObject(resp.getBody());
        JSONArray b=a.getJSONArray("opticalgroupList");
        ArrayList t=new ArrayList();
        for(int i=0;i<1;i++)
            t.add(b.getJSONObject(i).getIntValue("id"));
        pool.setOpticalgroup(t);
        pool.setCreateTime("");
        Config config=new Config();
        pool.setId(null);
        config.setColdIsSingle(false);
        config.setColdCopies(1);
        config.setHotCopies(1);
        config.setFrozenTime(30);
        config.setWatermarkLow(75);
        config.setWatermarkHi(90);
        config.setTierDemoteFrequency(3600);
        config.setTierPromoteFrequency(0);
        config.setConfigDemote("*");
        config.setConfigPromote("*");
        String tmp=JSON.toJSONString(config);
        System.out.println(tmp);
        //JSON.parseObject(tmp,Config.class);
        pool.setConfig(JSON.parseObject(tmp));
        System.out.println(JSON.toJSONString(pool));
        resp=api.CreateStoragePool(JSON.toJSONString(pool,SerializerFeature.WriteMapNullValue));
        System.out.println(resp.getCode());
        System.out.println(resp.getBody());
        Assert.assertTrue(resp.getCode()==201);


    }

    @Test
    public void  Test_unactiveStoragePoolList(){

        Result re=api.getStoragePoolList("1");
        JSONObject a= JSON.parseObject(re.getBody());
        JSONArray b=a.getJSONArray("content");
        for(int i=0;i<b.size();i++)
        {
            String id=b.getJSONObject(i).getString("id");
            System.out.println(b.getJSONObject(i).getString("name").contains("11111atest") && b.getJSONObject(i).getInteger("status")==1);
            System.out.println(b.getJSONObject(i).getString("name").contains("11111atest") && b.getJSONObject(i).getInteger("status")==1);
            if(b.getJSONObject(i).getString("name").contains("11111atest") && b.getJSONObject(i).getInteger("status")==1) {
                re = api.unactivestorepool(id);
                re=api.delstorepool(id);
                System.out.println(re.getBody());
            }
        }
    }

    public void  Test_unactiveStoragePoolList(String a1){

        Result re=api.getStoragePoolList(a1);
        JSONObject a= JSON.parseObject(re.getBody());
        JSONArray b=a.getJSONArray("content");
        for(int i=0;i<b.size();i++)
        {
            String id=b.getJSONObject(i).getString("id");
            if(b.getJSONObject(i).getString("name").contains("11111atest") && b.getJSONObject(i).getInteger("status")==1) {
              re = api.unactivestorepool(id);
                re=api.delstorepool(id);
                System.out.println(re.getBody());
            }
        }
    }

    @Test
    public  void TestCase_CreateStorepool2(){
        for(int i=0;i<1000;i++) {
            Test_unactiveStoragePoolList(String.valueOf(i));
        }

    }



    @Test
    public  void TestCase_CreateStorepool1(){
        Result re=api.GetStorageList();
        System.out.println(re.getBody());
        JSONObject a= JSON.parseObject(re.getBody());
        JSONArray b=a.getJSONArray("content");
        for(int i=0;i<b.size();i++)
        {
            String id=b.getJSONObject(i).getString("id");
            re=api.DelStoragePool(id);
            System.out.println(re.getBody());
        }
    }


}
