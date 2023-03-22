package testapi.common.DistributedStorage;

public class TableData {
    int id;
    String label;
    long freeDiskSpace;
    String quota;
    String type;
    String  quotaType;

    public void setQuotaType(String quotaType) {
        this.quotaType = quotaType;
    }

    public String getQuotaType() {
        return quotaType;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getFreeDiskSpace() {
        return freeDiskSpace;
    }

    public String getLabel() {
        return label;
    }

    public String getQuota() {
        return quota;
    }

    public void setFreeDiskSpace(long freeDiskSpace) {
        this.freeDiskSpace = freeDiskSpace;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setQuota(String quota) {
        this.quota = quota;
    }

}
