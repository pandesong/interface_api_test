package testapi.framework;
import pds.api.sshapi;
import testapi.common.Result;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class SSHExecutor {
    sshapi sh=new sshapi();

    private  String ip;
    private  String port;
    private String pwd;
    String host_type="";
    long session=0;
    public  SSHExecutor (String hostname,int port ,String username,String password)  {
        this.setIp(hostname);
        this.setPort(String.valueOf(port));
        this.pwd=password;
        for(int i=0;i<3;i++){
            this.session=this.sh.ssh_login_password( hostname, port , username,password);
            if(this.session!=0){
                try {
                    System.out.printf("ssh 登陆失败 %s\r\n",hostname);
                    Thread.sleep(1000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                return;
            }
        }
    }
    public Result exec(String cmd){
        try {
            String cmd1=new String(cmd.getBytes(StandardCharsets.UTF_8),"GBK");
            Result res=new Result();
        if(this.session==0){
            String r= this.sh.ssh_exec_command(this.session,cmd1);
            res.setCode(200);
            res.setBody(r.trim());
            return res;
        }
        else {
            res.setCode(400);
            res.setBody("login error!");
            return res;
        }}
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Result res=new Result();
            res.setCode(400);
            res.setBody("login error!");
            return res;
        }



    }
    public Result  ssh_exec_super(String cmd){
        try {
            String cmd1=new String(cmd.getBytes(StandardCharsets.UTF_8),"GBK");
            Result res=new Result();
            if(this.session==0){
                String r= this.sh.ssh_exec_whith_super(this.session,cmd1,pwd);
                res.setCode(200);
                res.setBody(r.trim());
                return res;
            }
            else {
                res.setCode(400);
                res.setBody("login error!");
                return res;
            }}
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Result res=new Result();
            res.setCode(400);
            res.setBody("login error!");
            return res;
        }
    }
    public void setHost_type(String type){
        host_type=type;
    }

    public String getHost_type() {
        return host_type;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getIp() {
        return ip;
    }

}




