package testapi.common.resourceManager;

public class Config {
    Boolean coldIsSingle;
    int coldCopies;
    int hotCopies;
    int frozenTime;
    long maxWaitTime;
    long smallFileSize;
    long optimalInterval;
    long filePackSize;
    long packFileNumber;
    long watermarkLow;
    long watermarkHi;
    long tierDemoteFrequency;
    long tierPromoteFrequency;
    String configPromote;
    String configDemote;


    public void Config(){

        this.setColdIsSingle(false);
        this.setColdCopies(1);
        this.setHotCopies(1);
        this.setFrozenTime(30);
        this.setWatermarkHi(90);
        this.setWatermarkLow(75);
        this.setTierDemoteFrequency(3600);
        this.setTierPromoteFrequency(0);
        this.setConfigPromote("*");
        this.setConfigDemote("*");
    }
    public int getColdCopies() {
        return coldCopies;
    }

    public Boolean getColdIsSingle() {
        return coldIsSingle;
    }

    public int getFrozenTime() {
        return frozenTime;
    }

    public int getHotCopies() {
        return hotCopies;
    }

    public String getConfigDemote() {
        return configDemote;
    }

    public String getConfigPromote() {
        return configPromote;
    }

    public long getFilePackSize() {
        return filePackSize;
    }

    public long getMaxWaitTime() {
        return maxWaitTime;
    }

    public long getOptimalInterval() {
        return optimalInterval;
    }

    public long getPackFileNumber() {
        return packFileNumber;
    }

    public long getSmallFileSize() {
        return smallFileSize;
    }

    public long getTierDemoteFrequency() {
        return tierDemoteFrequency;
    }

    public long getTierPromoteFrequency() {
        return tierPromoteFrequency;
    }

    public long getWatermarkHi() {
        return watermarkHi;
    }

    public long getWatermarkLow() {
        return watermarkLow;
    }

    public void setColdCopies(int coldCopies) {
        this.coldCopies = coldCopies;
    }

    public void setColdIsSingle(Boolean coldIsSingle) {
        this.coldIsSingle = coldIsSingle;
    }

    public void setConfigDemote(String configDemote) {
        this.configDemote = configDemote;
    }

    public void setConfigPromote(String configPromote) {
        this.configPromote = configPromote;
    }

    public void setFilePackSize(Long filePackSize) {
        this.filePackSize = filePackSize;
    }

    public void setFrozenTime(int frozenTime) {
        this.frozenTime = frozenTime;
    }

    public void setHotCopies(int hotCopies) {
        this.hotCopies = hotCopies;
    }

    public void setMaxWaitTime(Long maxWaitTime) {
        this.maxWaitTime = maxWaitTime;
    }

    public void setOptimalInterval(Long optimalInterval) {
        this.optimalInterval = optimalInterval;
    }

    public void setPackFileNumber(Long packFileNumber) {
        this.packFileNumber = packFileNumber;
    }

    public void setSmallFileSize(Long smallFileSize) {
        this.smallFileSize = smallFileSize;
    }

    public void setTierDemoteFrequency(long tierDemoteFrequency) {
        this.tierDemoteFrequency = tierDemoteFrequency;
    }

    public void setTierPromoteFrequency(long tierPromoteFrequency) {
        this.tierPromoteFrequency = tierPromoteFrequency;
    }

    public void setWatermarkHi(long watermarkHi) {
        this.watermarkHi = watermarkHi;
    }

    public void setWatermarkLow(long watermarkLow) {
        this.watermarkLow = watermarkLow;
    }
}
