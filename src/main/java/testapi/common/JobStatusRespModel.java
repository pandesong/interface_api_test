package testapi.common;

import java.util.ArrayList;

public class JobStatusRespModel {
    ArrayList<JobStatusContentResModel> content;
    String totalElements;

    public ArrayList<JobStatusContentResModel> getContent() {
        return content;
    }

    public String getTotalElements() {
        return totalElements;
    }

    public void setContent(ArrayList<JobStatusContentResModel> content) {
        this.content = content;
    }

    public void setTotalElements(String totalElements) {
        this.totalElements = totalElements;
    }



}
