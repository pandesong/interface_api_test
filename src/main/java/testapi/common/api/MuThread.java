package testapi.common.api;

import com.alibaba.fastjson.JSON;
import com.jcraft.jsch.JSchException;
import testapi.common.JobLogRespModel;
import testapi.common.JobPlainStatusModel;
import testapi.common.JobStatusRespModel;
import testapi.common.Result;
import org.junit.Assert;
import testapi.framework.SSHExecutor;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
public class MuThread extends Thread {
    private String name;
    ApiTest api=new ApiTest();
    SSHExecutor sshcmd=null;
    public MuThread(String name) {
        this.name=name;
        sshcmd=api.ssh_login(api.ssh_dest_username,api.ssh_dest_password, api.ssh_dst_ip, api.ssh_dest_port);
    }
    @Override
    public void run() {




    }
}

