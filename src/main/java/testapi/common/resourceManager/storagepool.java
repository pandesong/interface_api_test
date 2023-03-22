package testapi.common.resourceManager;

import java.util.ArrayList;

public class storagepool {

String id;
String name;
    int poolType;
String status;
String remark;
String createTime;
String updateTime;
String excludeDiskgroup;
String excludeOpticalgroup;
String diskgroup;
ArrayList opticalgroup;
Object config;

public void storagepool(){
    Config config=new Config();
    this.setId(null);
    this.setConfig(config);
    this.setPoolType(0);
    this.setOpticalgroup(null);
    this.setCreateTime("");

}

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public String getStatus() {
        return status;
    }

    public String getRemark() {
        return remark;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getCreateTime() {
        return createTime;
    }

    public ArrayList getOpticalgroup() {
        return opticalgroup;
    }

    public Object getConfig() {
        return config;
    }

    public String getDiskgroup() {
        return diskgroup;
    }

    public String getExcludeDiskgroup() {
        return excludeDiskgroup;
    }

    public String getExcludeOpticalgroup() {
        return excludeOpticalgroup;
    }

    public int getPoolType() {
        return poolType;
    }

    public void setConfig(Object config) {
        this.config = config;
    }

    public void setDiskgroup(String diskgroup) {
        this.diskgroup = diskgroup;
    }

    public void setExcludeDiskgroup(String excludeDiskgroup) {
        this.excludeDiskgroup = excludeDiskgroup;
    }

    public void setExcludeOpticalgroup(String excludeOpticalgroup) {
        this.excludeOpticalgroup = excludeOpticalgroup;
    }

    public void setOpticalgroup(ArrayList opticalgroup) {
        this.opticalgroup = opticalgroup;
    }

    public void setPoolType(int poolType) {
        this.poolType = poolType;
    }



}
