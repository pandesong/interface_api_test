package testapi.common.DistributedStorage;

import java.util.ArrayList;

public class MappingRequestModel {

    String name;
    boolean isChap;
    ArrayList blockLuns;
    ArrayList  hostGroups;
    ArrayList  nodes;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList getBlockLuns() {
        return blockLuns;
    }

    public ArrayList getHostGroups() {
        return hostGroups;
    }

    public boolean getIsChap() {
        return isChap;
    }

    public ArrayList getNodes() {
        return nodes;
    }

    public void setBlockLuns(ArrayList blocKLuns) {
        this.blockLuns = blocKLuns;
    }

    public void setHostGroups(ArrayList hostGroups) {
        this.hostGroups = hostGroups;
    }

    public void setIsChap(boolean isChap) {
        this.isChap = isChap;
    }

    public void setNodes(ArrayList nodes) {
        this.nodes = nodes;
    }

}
