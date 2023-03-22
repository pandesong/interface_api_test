package testapi.common;

public class DataRevert {
String taskName;
int  backupId;
int  taskType;
int  revertPathType;
int  revertType;
int  revertMethod;
String   taskBeginTime;
String  revertFileIds;
String   targetPath;
String  jobName;
String  isPackaging;
int  threadNum;
int   smallBatchCommitNum;
String  fileCheckType;
String  unpack;
String  ip;

    public void setThreadNum(int threadNum) {
        this.threadNum = threadNum;
    }

    public void setTaskType(int taskType) {
        this.taskType = taskType;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public void setTaskBeginTime(String taskBeginTime) {
        this.taskBeginTime = taskBeginTime;
    }

    public void setTargetPath(String targetPath) {
        this.targetPath = targetPath;
    }

    public void setSmallBatchCommitNum(int smallBatchCommitNum) {
        this.smallBatchCommitNum = smallBatchCommitNum;
    }

    public void setIsPackaging(String isPackaging) {
        this.isPackaging = isPackaging;
    }

    public void setFileCheckType(String fileCheckType) {
        this.fileCheckType = fileCheckType;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getTaskBeginTime() {
        return taskBeginTime;
    }

    public String getTargetPath() {
        return targetPath;
    }

    public String getFileCheckType() {
        return fileCheckType;
    }

    public String getIsPackaging() {
        return isPackaging;
    }

    public int getBackupId() {
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

    public void setUnpack(String unpack) {
        this.unpack = unpack;
    }

    public int getSmallBatchCommitNum() {
        return smallBatchCommitNum;
    }

    public int getTaskType() {
        return taskType;
    }

    public int getThreadNum() {
        return threadNum;
    }

    public String getIp() {
        return ip;
    }

    public String getJobName() {
        return jobName;
    }

    public String getRevertFileIds() {
        return revertFileIds;
    }

    public void setBackupId(int backupId) {
        this.backupId = backupId;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getUnpack() {
        return unpack;
    }

    public void setRevertFileIds(String revertFileIds) {
        this.revertFileIds = revertFileIds;
    }

    public void setRevertMethod(int revertMethod) {
        this.revertMethod = revertMethod;
    }

    public void setRevertPathType(int revertPathType) {
        this.revertPathType = revertPathType;
    }

    public void setRevertType(int revertType) {
        this.revertType = revertType;
    }
}


