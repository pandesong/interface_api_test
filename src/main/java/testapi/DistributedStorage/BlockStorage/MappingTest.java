package testapi.DistributedStorage.BlockStorage;

import com.alibaba.fastjson.JSON;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import testapi.common.Result;
import testapi.common.apiManager.DapiTest;
import testapi.common.DistributedStorage.MappingRequestModel;

import java.util.ArrayList;

public class MappingTest {

    static DapiTest api=new DapiTest(false);

    @BeforeClass
    public  static void beforeclass(){



    }

    @AfterClass
    public static void afterclass(){



    }


    @Test
    public  void TestCase_CreateMapAndDelete(){
        ArrayList nodes=new ArrayList();
        nodes.add(1);
        nodes.add(2);
        ArrayList hosts=new ArrayList();
        hosts.add(8);
        ArrayList blockslun=new ArrayList();
        blockslun.add(3);
        MappingRequestModel map=new MappingRequestModel();
        String JobName="filter_By_fileCreateTime"+String.valueOf(System.currentTimeMillis());
        map.setName(JobName);
        map.setIsChap(false);
        map.setNodes(nodes);
        map.setBlockLuns(blockslun);
        map.setHostGroups(hosts);
        String request=JSON.toJSONString(map);
        Result res=api.CreateMapping(request);
        if(res.getCode()!=200) {
            System.out.println(res.getBody());
            Assert.assertTrue(false);
        }
    }

    @Test
    public  void TestCase_CreateMapAndDelete1(){
        TestCase_CreateMapAndDelete();
        TestCase_CreateMapAndDelete();

    }
}
