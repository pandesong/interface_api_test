package testapi.common.resourceManager;

public class deviceInfo {
    int nodeId;
    String libId;
    String infoType;
    int slotNumber;
    Params params;

    public void setLibId(String libId) {
        this.libId = libId;
    }

    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    public String getLibId() {
        return libId;
    }

    public int getNodeId() {
        return nodeId;
    }

    public Params getParams() {
        return params;
    }

    public String getInfoType() {
        return infoType;
    }

    public int getSlotNumber() {
        return slotNumber;
    }

    public void setInfoType(String infoType) {
        this.infoType = infoType;
    }

    public void setParams(Params params) {
        this.params = params;
    }

    public void setSlotNumber(int slotNumber) {
        this.slotNumber = slotNumber;
    }

}
