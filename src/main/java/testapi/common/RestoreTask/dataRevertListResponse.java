package testapi.common.RestoreTask;

import java.util.ArrayList;

public class dataRevertListResponse {


    ArrayList<RestoreStatusContentResModel> content;
    String totalElements;

    public dataRevertListResponse(){

    }
    public ArrayList<RestoreStatusContentResModel> getContent() {
        return content;
    }

    public String getTotalElements() {
        return totalElements;
    }

    public void setContent(ArrayList<RestoreStatusContentResModel> content) {
        this.content = content;
    }

    public void setTotalElements(String totalElements) {
        this.totalElements = totalElements;
    }



}
