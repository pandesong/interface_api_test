package testapi.common.RestoreTask;
public class dataRevertRequest {


    public String taskName;
    public String backupId;
    public  int taskType;
    public  int revertPathType;
    public int  revertType;
    public int revertMethod;
    public  String taskBeginTime;
    public  String revertFileIds;
    public  String targetPath;
    public  String jobName;
    public  String isPackaging;
    public  int threadNum;
    public  int smallBatchCommitNum;
    public  String fileCheckType;
    public String unpack;
    public String ip;

    public dataRevertRequest(){

        this.setIp("");
        this.setJobName("");
        this.setIsPackaging(null);
        this.setRevertPathType(1);
        this.setRevertFileIds("");
        this.setTargetPath("");
        this.setRevertType(1);
        this.setSmallBatchCommitNum(1);
        this.setTaskBeginTime("");
        this.setThreadNum(1);
        this.setUnpack("0");
        this.setRevertMethod(1);
        this.setTaskType(3);


    }

    public String getJobName() {

        return jobName;


    }

    public void setJobName(String jobName) {

        this.jobName = jobName;
    }

    public int getTaskType() {

        return taskType;

    }

    public void setTaskType(int taskType) {

        this.taskType = taskType;

    }

    public void setTargetPath(String targetPath) {

        this.targetPath = targetPath;
    }

    public void setTaskName(String taskName) {

        this.taskName = taskName;

    }

    public String getTaskName() {
        return taskName;
    }

    public String getTargetPath() {
        return targetPath;
    }

    public void setRevertType(int revertType) {
        this.revertType = revertType;
    }

    public void setRevertPathType(int revertPathType) {
        this.revertPathType = revertPathType;
    }

    public void setRevertMethod(int revertMethod) {
        this.revertMethod = revertMethod;
    }

    public void setRevertFileIds(String revertFileIds) {
        this.revertFileIds = revertFileIds;
    }

    public String getUnpack() {
        return unpack;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setBackupId(String backupId) {
        this.backupId = backupId;
    }

    public String getRevertFileIds() {
        return revertFileIds;
    }

    public String getIp() {
        return ip;
    }

    public void setUnpack(String unpack) {
        this.unpack = unpack;
    }

    public String getIsPackaging() {
        return isPackaging;
    }

    public String getFileCheckType() {
        return fileCheckType;
    }

    public String getTaskBeginTime() {
        return taskBeginTime;
    }

    public void setFileCheckType(String fileCheckType) {
        this.fileCheckType = fileCheckType;
    }

    public void setIsPackaging(String isPackaging) {
        this.isPackaging = isPackaging;
    }

    public void setSmallBatchCommitNum(int smallBatchCommitNum) {
        this.smallBatchCommitNum = smallBatchCommitNum;
    }

    public void setTaskBeginTime(String taskBeginTime) {
        this.taskBeginTime = taskBeginTime;
    }

    public void setThreadNum(int threadNum) {
        this.threadNum = threadNum;
    }

    public int getThreadNum() {
        return threadNum;
    }

    public int getSmallBatchCommitNum() {
        return smallBatchCommitNum;
    }

    public String getBackupId() {
        return backupId;
    }

    public int getRevertMethod() {
        return revertMethod;
    }

    public int getRevertPathType() {
        return revertPathType;
    }

    public int getRevertType() {
        return revertType;
    }


}
