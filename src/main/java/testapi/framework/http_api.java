package testapi.framework;
import testapi.common.Result;
import pds.api.httpapi;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;


public class http_api {


    private Result  DealMesg(String res){
        String body="";
        String[]  arr=res.split("\r\n\r\n");
        Result re=new Result();
        HashMap  head=new HashMap();
        int code;
        if(res.equals("send error"))
        {
        re.setCode(400);
        re.setBody("send error");
        return re;
        }else if(res.equals("recv error")) {
        re.setCode(400);
        re.setBody("recv error");
        return re;
        }
        String [] nobody=arr[0].split("\r\n");
        String uri=nobody[0];
        code=Integer.valueOf(uri.split(" ")[1]);
        for(int i=1;i+1<nobody.length;i++){
            try {
                head.put(nobody[i].split(":")[0], nobody[i].split(":")[1]);
            }catch (Exception e){
                e.printStackTrace();
                continue;

            }
        }
        if(arr.length==1) {
            body=null;
        }else{
            body=arr[1];
            if(arr[0].contains("Transfer-Encoding: chunked")){
                if(body.split("\r\n").length<=1) body=null;
                else
                body=body.split("\r\n")[1];
            }
          //  System.out.println("================body=======================\r\b"+body);
        }
        re.setHead(head);
        re.setCode(code);
        re.setBody(body);
        return re;

    }


    public  Result http_get(String url,Map<String, String> httphead) throws IOException, InterruptedException {
        httpapi ht=new httpapi();
        String res=ht.http_get(url,httphead," ");

        return DealMesg(res);

    }

    public  Result http_delete(String url,Map<String, String> httphead) throws IOException {
        httpapi ht=new httpapi();
        String res=ht.http_delete(url,httphead,"");

        return DealMesg(res);
    }
    public  Result http_delete(String url,Map<String, String> httphead,String body) throws IOException {
        httpapi ht=new httpapi();
        String res=ht.http_delete(url,httphead,body);

        return DealMesg(res);
    }

    public  Result http_post(String url,Map<String, String> httphead,String body) throws IOException {
        httpapi ht=new httpapi();
        String res=ht.http_post(url,httphead,body);
         return DealMesg(res);

    }


    public  Result http_put(String url,Map<String, String> httphead,String body) throws IOException {
        httpapi ht=new httpapi();
        String res=ht.http_put(url,httphead,body);
        return DealMesg(res);
    }




    public static void  test() throws NoSuchAlgorithmException, KeyManagementException, IOException {





    }



}
