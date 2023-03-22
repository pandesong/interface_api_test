package testapi.common.SystemManager.Dashboard;

import java.util.ArrayList;

public class storagenode {
    ArrayList content= new ArrayList<storagenodeMode>();
    int totalElements;

    public ArrayList getContent() {
        return content;
    }

    public int getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(int totalElements) {
        this.totalElements = totalElements;
    }

    public void setContent(ArrayList content) {
        this.content = content;
    }
}
