package testapi.common.BackMedieModel;
public class RequestModel {

    private  String  mediumName;
    private String  mediumIp;
    private  String  mediumPort;
    private  int   mediumNetworkHeartBeat;
    private  int    mediumType;
    private   String  mediumBackupPath;
    private  String   mediumDescription;
    private Boolean    mediumTls;
    private   Boolean enabled;

    public Boolean getEnabled() {
        return enabled;
    }

    public Boolean getMediumTls() {
        return mediumTls;
    }

    public String getMediumBackupPath() {
        return mediumBackupPath;
    }

    public String getMediumDescription() {
        return mediumDescription;
    }

    public String getMediumIp() {
        return mediumIp;
    }

    public String getMediumName() {
        return mediumName;
    }

    public int getMediumNetworkHeartBeat() {
        return mediumNetworkHeartBeat;
    }

    public String getMediumPort() {
        return mediumPort;
    }

    public int getMediumType() {
        return mediumType;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public void setMediumBackupPath(String mediumBackupPath) {
        this.mediumBackupPath = mediumBackupPath;
    }

    public void setMediumDescription(String mediumDescription) {
        this.mediumDescription = mediumDescription;
    }

    public void setMediumIp(String mediumIp) {
        this.mediumIp = mediumIp;
    }

    public void setMediumName(String mediumName) {
        this.mediumName = mediumName;
    }

    public void setMediumNetworkHeartBeat(int mediumNetworkHeartBeat) {
        this.mediumNetworkHeartBeat = mediumNetworkHeartBeat;
    }

    public void setMediumPort(String mediumPort) {
        this.mediumPort = mediumPort;
    }

    public void setMediumTls(Boolean mediumTls) {
        this.mediumTls = mediumTls;
    }

    public void setMediumType(int mediumType) {
        this.mediumType = mediumType;
    }


}
