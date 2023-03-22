package testapi.common.DistributedStorage;

public class StoreagePoolUnusedUintResponsedetailModel {
    private String id;
    private String name;
    private  String  mediaType;
    private  String  totalCapacity;
    private  String   usedCapacity;
    private  String  status;
    private  String  target;
    private int adaptNo;
    private String raidLevel;
    private  String writePolicy;
    private  String state;
    private  String zpstate;
    private  String  blkSize;
    private String devName;
    private  String devUuid;
    private  String nodeName;
    private String nodeIp;
    private  String nodeId;
    private  String poolName;
    private  String poolType;

    public int getAdaptNo() {
        return adaptNo;
    }

    public String getBlkSize() {
        return blkSize;
    }

    public String getDevName() {
        return devName;
    }

    public String getDevUuid() {
        return devUuid;
    }

    public String getId() {
        return id;
    }

    public String getMediaType() {
        return mediaType;
    }

    public String getName() {
        return name;
    }

    public String getRaidLevel() {
        return raidLevel;
    }

    public String getNodeName() {
        return nodeName;
    }

    public String getState() {
        return state;
    }

    public String getStatus() {
        return status;
    }

    public String getTarget() {
        return target;
    }

    public String getTotalCapacity() {
        return totalCapacity;
    }

    public String getUsedCapacity() {
        return usedCapacity;
    }

    public String getWritePolicy() {
        return writePolicy;
    }

    public String getZpstate() {
        return zpstate;
    }

    public void setAdaptNo(int adaptNo) {
        this.adaptNo = adaptNo;
    }

    public void setBlkSize(String blkSize) {
        this.blkSize = blkSize;
    }

    public String getNodeIp() {
        return nodeIp;
    }

    public void setDevName(String devName) {
        this.devName = devName;

    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setDevUuid(String devUuid) {
        this.devUuid = devUuid;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRaidLevel(String raidLevel) {
        this.raidLevel = raidLevel;
    }

    public String getPoolName() {
        return poolName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public void setNodeIp(String nodeIp) {
        this.nodeIp = nodeIp;
    }

    public void setTotalCapacity(String totalCapacity) {
        this.totalCapacity = totalCapacity;
    }

    public void setUsedCapacity(String usedCapacity) {
        this.usedCapacity = usedCapacity;
    }

    public String getPoolType() {
        return poolType;
    }

    public void setWritePolicy(String writePolicy) {
        this.writePolicy = writePolicy;
    }

    public void setPoolName(String poolName) {
        this.poolName = poolName;
    }

    public void setZpstate(String zpstate) {
        this.zpstate = zpstate;
    }

    public void setPoolType(String poolType) {
        this.poolType = poolType;
    }

}
