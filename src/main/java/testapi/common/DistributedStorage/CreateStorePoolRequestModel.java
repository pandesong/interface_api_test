package testapi.common.DistributedStorage;

import java.util.ArrayList;

public class CreateStorePoolRequestModel {
    private String  name;
    private  int  poolType;
    private int hddStrategy;
    ArrayList hddUnits;
    String  ssdStrategy;
    ArrayList ssdUnits;
    int  hddProtectNum;
    int ssdProtectNum;
    Boolean useCache=null;

    public Boolean getUseCache() {
        return useCache;
    }

    public void setUseCache(Boolean useCache) {
        if (useCache=null)
            useCache=false;
        this.useCache = useCache;
    }


    public int getHddProtectNum() {
        return hddProtectNum;
    }

    public void setPoolType(int poolType) {
        this.poolType = poolType;
    }

    public int getPoolType() {
        return poolType;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public ArrayList getHddUnits() {
        return hddUnits;
    }

    public ArrayList getSsdUnits() {
        return ssdUnits;
    }

    public int getSsdProtectNum() {
        return ssdProtectNum;
    }

    public int getHddStrategy() {
        return hddStrategy;
    }

    public String getSsdStrategy() {
        return ssdStrategy;
    }

    public void setHddProtectNum(int hddProtectNum) {
        this.hddProtectNum = hddProtectNum;
    }

    public void setHddStrategy(int hddStrategy) {
        this.hddStrategy = hddStrategy;
    }

    public void setHddUnits(ArrayList hddUnits) {
        this.hddUnits = hddUnits;
    }

    public void setSsdProtectNum(int ssdProtectNum) {
        this.ssdProtectNum = ssdProtectNum;
    }

    public void setSsdStrategy(String ssdStrategy) {
        this.ssdStrategy = ssdStrategy;
    }

    public void setSsdUnits(ArrayList ssdUnits) {
        this.ssdUnits = ssdUnits;
    }
}
