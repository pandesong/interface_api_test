package testapi.common.DistributedStorage;

import java.util.ArrayList;

public class CreateGroupRequestModel {
    String    name;
    boolean     isEnable;
    String     psw;
    String      checkPass;
    String      billingMethod;
    ArrayList<StoragePoolVos>   storagePoolVos;
    ArrayList<TableData>   tableData;
    String       password;
    int groupId;
    String  quotaType;

    public String getQuotaType() {
        return quotaType;
    }

    public void setQuotaType(String quotaType) {
        this.quotaType = quotaType;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getName() {
        return name;
    }


    public void setTableData(ArrayList<TableData> tableData) {
        this.tableData = tableData;
    }


    public ArrayList getTableData() {
        return tableData;
    }


    public ArrayList getStoragePoolVos() {
        return storagePoolVos;
    }


    public String getBillingMethod() {
        return billingMethod;
    }


    public String getCheckPass() {
        return checkPass;
    }


    public boolean getIsEnable() {
        return isEnable;
    }


    public String getPsw() {
        return psw;
    }


    public void setBillingMethod(String billingMethod) {
        this.billingMethod = billingMethod;
    }


    public void setCheckPass(String checkPass) {
        this.checkPass = checkPass;
    }


    public void setIsEnable(boolean isEnable) {
        this.isEnable = isEnable;
    }


    public void setPsw(String psw) {
        this.psw = psw;
    }

    public void setStoragePoolVos(ArrayList<StoragePoolVos> storagePoolVos) {
        this.storagePoolVos = storagePoolVos;
    }



}
