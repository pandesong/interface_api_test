package testapi.common.DistributedStorage;

import java.util.ArrayList;

public class GroupDetail {
    String id;
    String name;
    String status;
    String adminId;
    String createTime;
    String updateTime;
    ArrayList<StoragePoolVos> storagePoolVos;
    String poolName;
    String authenticationMethod;
    String totalCapacity;
    String  usedCapacity;
    String  queryIsSuccess;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setPoolName(String poolName) {
        this.poolName = poolName;
    }

    public String getPoolName() {
        return poolName;
    }

    public void setStoragePoolVos(ArrayList<StoragePoolVos> storagePoolVos) {
        this.storagePoolVos = storagePoolVos;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public String getCreateTime() {
        return createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public ArrayList<StoragePoolVos> getStoragePoolVos() {
        return storagePoolVos;
    }

    public String getAdminId() {
        return adminId;
    }

    public String getAuthenticationMethod() {
        return authenticationMethod;
    }

    public String getQueryIsSuccess() {
        return queryIsSuccess;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getTotalCapacity() {
        return totalCapacity;
    }

    public String getUsedCapacity() {
        return usedCapacity;
    }

    public void setAuthenticationMethod(String authenticationMethod) {
        this.authenticationMethod = authenticationMethod;
    }

    public void setQueryIsSuccess(String queryIsSuccess) {
        this.queryIsSuccess = queryIsSuccess;
    }

    public void setTotalCapacity(String totalCapacity) {
        this.totalCapacity = totalCapacity;
    }

    public void setUsedCapacity(String usedCapacity) {
        this.usedCapacity = usedCapacity;
    }
}
