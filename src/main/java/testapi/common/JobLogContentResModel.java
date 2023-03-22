package testapi.common;

public class JobLogContentResModel  extends  JobStatusContentResModel{
    String jobName;
    String srcPathName;
    String desPathName;
    String  fileName;
    String status;
    String  comment;
    String  taskEndTime;
    String burnStatus;
    String fileSize;
    String fileChangeTime;

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobName() {
        return jobName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setBurnStatus(String burnStatus) {
        this.burnStatus = burnStatus;
    }

    public String getFileName() {
        return fileName;
    }

    public String getBurnStatus() {
        return burnStatus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public String getDesPathName() {
        return desPathName;
    }

    public String getFileChangeTime() {
        return fileChangeTime;
    }

    public String getFileSize() {
        return fileSize;
    }

    public String getSrcPathName() {
        return srcPathName;
    }

    public String getTaskEndTime() {
        return taskEndTime;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setDesPathName(String desPathName) {
        this.desPathName = desPathName;
    }

    public void setFileChangeTime(String fileChangeTime) {
        this.fileChangeTime = fileChangeTime;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public void setSrcPathName(String srcPathName) {
        this.srcPathName = srcPathName;
    }

    public void setTaskEndTime(String taskEndTime) {
        this.taskEndTime = taskEndTime;
    }

}
