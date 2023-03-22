package testapi.common.ClientManagerModel;

public class cmCliModel {
    private String clientId;
    private String  clientName;
    private  String  clientIp;
    private  String  clientPort;
    private   String  clientNetworkHeartBeat;
    private  String  clientType;
    private   String  clientDescription;
    private  String  clientTls;
    private  String  enabled;
    private  String  delFlag;
    private  String  createTime;
    private String  updateTime;
    private  String  createBy;
    private   String  updateBy;

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }

    public void setClientTls(String clientTls) {
        this.clientTls = clientTls;
    }

    public void setClientPort(String clientPort) {
        this.clientPort = clientPort;
    }

    public void setClientNetworkHeartBeat(String clientNetworkHeartBeat) {
        this.clientNetworkHeartBeat = clientNetworkHeartBeat;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public void setClientDescription(String clientDescription) {
        this.clientDescription = clientDescription;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public String getEnabled() {
        return enabled;
    }

    public String getClientType() {
        return clientType;
    }

    public String getClientTls() {
        return clientTls;
    }

    public String getClientPort() {
        return clientPort;
    }

    public String getClientNetworkHeartBeat() {
        return clientNetworkHeartBeat;
    }

    public String getClientName() {
        return clientName;
    }

    public String getClientDescription() {
        return clientDescription;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }

    public String getClientIp() {
        return clientIp;
    }

    public String getClientId() {
        return clientId;
    }

    public String getCreateBy() {
        return createBy;
    }

    public String getCreateTime() {
        return createTime;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

}
