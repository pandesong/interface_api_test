package testapi.common.resourceManager.Disc;

import java.util.ArrayList;

public class opticalgroup {
    String id=null;
    int nodeId;
    String opticalServerId;
    String sharePath;
    String name;
    String isRaid;
    String raidType;
    String   volumn;
    String volumnUsed;
    String  status;
    String  remark;
    String  createTime;
    String  updateTime;
    ArrayList slotInfos;


    public String getRaidType() {
        return raidType;
    }

    public void setRaidType(String raidType) {
        this.raidType = raidType;
    }
    public opticalgroup(){

    }
    public ArrayList getSlotInfos() {
        return slotInfos;
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

    public String getopticalServerId() {
        return opticalServerId;
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

    public void setOpticalServerID(String opticalServerId) {
        this.opticalServerId = opticalServerId;
    }



    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void setSharePath(String sharePath) {
        this.sharePath = sharePath;
    }

    public void setSlotInfos(ArrayList slotInfos) {
        this.slotInfos = slotInfos;
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
