package testapi.common;

import java.util.ArrayList;

public class shareModel {
    int id;
    String accessType;
    ArrayList checkList;
    ArrayList tableData;

    public shareModel(){
        this.setAccessType("false");
        ArrayList a=new ArrayList();
        a.add("2");
        a.add("1");
        this.setCheckList(a);
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public ArrayList getCheckList() {
        return checkList;
    }

    public ArrayList getTableData() {
        return tableData;
    }

    public String getAccessType() {
        return accessType;
    }

    public void setAccessType(String accessType) {
        this.accessType = accessType;
    }

    public void setCheckList(ArrayList checkList) {
        this.checkList = checkList;
    }

    public void setTableData(ArrayList tableData) {
        this.tableData = tableData;
    }
}
