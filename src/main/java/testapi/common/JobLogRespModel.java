package testapi.common;

import java.util.ArrayList;

public class JobLogRespModel  {
    int totalElements;
    ArrayList<JobLogContentResModel>  content;

    public void setTotalElements(int totalElements) {
        this.totalElements = totalElements;
    }

    public void setContent(ArrayList<JobLogContentResModel> content) {
        this.content = content;
    }

    public int getTotalElements() {
        return totalElements;
    }

    public ArrayList<JobLogContentResModel> getContent() {
        return content;
    }

}
