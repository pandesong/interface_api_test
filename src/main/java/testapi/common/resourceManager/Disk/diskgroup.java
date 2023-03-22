package testapi.common.resourceManager.Disk;

import java.util.ArrayList;

public class diskgroup {

    String id=null;
    int nodeId;
    String opticalServerID;
    String sharePath;
    String name;
    String isRaid;
    String raRaid;
    String raidType;
    String   volumn;
    String volumnUsed;
    String  status;
    String  remark;
    String  createTime;
    String  updateTime;
    ArrayList diskInfos;


    public String getRaidType() {
        return raidType;
    }

    public void setRaidType(String raidType) {
        this.raidType = raidType;
    }

    public ArrayList getdiskInfos() {
        return diskInfos;
    }

    public String getCreateTime() {
        return createTime;
    }

    public String getId() {
        return id;
    }

    public String getIsRaid() {
        return isRaid;
    }

    public String getName() {
        return name;
    }

    public int getNodeId() {
        return nodeId;
    }

    public String getOpticalServerID() {
        return opticalServerID;
    }

    public String getRaRaid() {
        return raRaid;
    }

    public String getRemark() {
        return remark;
    }

    public String getSharePath() {
        return sharePath;
    }

    public String getStatus() {
        return status;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public String getVolumn() {
        return volumn;
    }

    public String getVolumnUsed() {
        return volumnUsed;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setIsRaid(String isRaid) {
        this.isRaid = isRaid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    public void setOpticalServerID(String opticalServerID) {
        this.opticalServerID = opticalServerID;
    }

    public void setRaRaid(String raRaid) {
        this.raRaid = raRaid;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void setSharePath(String sharePath) {
        this.sharePath = sharePath;
    }

    public void setdiskInfos(ArrayList diskInfos) {
        this.diskInfos = diskInfos;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public void setVolumn(String volumn) {
        this.volumn = volumn;
    }

    public void setVolumnUsed(String volumnUsed) {
        this.volumnUsed = volumnUsed;
    }



}
