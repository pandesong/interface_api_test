package testapi.common.DistributedStorage;

public class StoragePoolVos {
    int  poolId;
    String  poolName;
    long  newQuota;
    String  quotaType;

    public String getQuotaType() {
        return quotaType;
    }

    public void setQuotaType(String quotaType) {
        this.quotaType = quotaType;
    }

    public long getNewQuota() {
        return newQuota;
    }

    public int getPoolId() {
        return poolId;
    }

    public String getPoolName() {
        return poolName;
    }

    public void setPoolId(int poolId) {
        this.poolId = poolId;
    }

    public void setNewQuota(long newQuota) {
        this.newQuota = newQuota;
    }

    public void setPoolName(String poolName) {
        this.poolName = poolName;
    }


}
