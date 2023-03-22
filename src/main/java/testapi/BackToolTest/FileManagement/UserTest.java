package testapi.BackToolTest.FileManagement;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import testapi.common.Result;
import testapi.common.api.ApiTest;
import testapi.common.shareModel;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class UserTest {
    ApiTest api=new ApiTest();

    @BeforeClass
    public static void beforeclass(){

    }



    @AfterClass
    public static void afterclass(){

    }

    @Test
    public  void TestCase_UserTest(){
        shareModel share=new shareModel();


        for(int d=2;d<9;d++){
        Result re=api.GetUserList(d);
        System.out.println(re.getBody());
        JSONObject a= JSON.parseObject(re.getBody());
        JSONArray b=a.getJSONArray("content");
        for(int i=0;i<b.size();i++)
        {
            int id=b.getJSONObject(i).getInteger("id");
            share.setId(id);
            re=api.EnableUserShare(JSON.toJSON(share).toString());
            System.out.println(re.getBody());
        }


    }

    }






}
