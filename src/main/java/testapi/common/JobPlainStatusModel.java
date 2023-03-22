package testapi.common;
public class JobPlainStatusModel {
	String taskName;
	int  taskType;
	String  sourceIp;
	String  sourcePath;
	String  targetPath;
	String  targetIp;
	String  burnStatus;
	int  scheduleType;
	String  dayOfWeek;
	String jobName;
	String  month;
	String  day;
	String  delSourceFile;
	String  softConnect;
	String  taskBeginTime;
	String   timeOne;
	int   createBy;
	String  timeTwo;
	int  fileNameScope;
	String  fileName;
	int  fileSuffixScope;
	String  fileSuffixName;
	int  fileSizeScopeOne;
	int  fileSizeScopeTwo;
	String  fileSizeOne;
	String  fileSizeTwo;
	String  filePathScope;
	String  filePath;
	int  fileOwnerScope;
	Boolean  scheduleTypeReadOnly;
	String  fileOwner;
	String   fileSizeUnit;
	String   fileSizeUnitTwo;
	boolean   filterVisible;
	String    isPackaging;
	String   packingLevel;
	int    packingSize;
	String    packingSizeUnit;
	int   threadNum;
	int   smallBatchCommitNum;
	String   fileCheckType;
	int    backupType;
	int backupId;
	String  dataSourceType;
	int  mediumId;
	int  clientId;

	public void setMediumId(int mediumId) {
		this.mediumId = mediumId;
	}

	public int getClientId() {
		return clientId;
	}

	public String getJobName() {
		return jobName;
	}

	public String getSourceIp() {
		return sourceIp;
	}

	public int getMediumId() {
		return mediumId;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public void setSourceIp(String sourceIp) {
		this.sourceIp = sourceIp;
	}


	public void setClientId(int clientId) {
		this.clientId = clientId;
	}

	public void setDataSourceType(String dataSourceType) {
		this.dataSourceType = dataSourceType;
	}

	public String getDataSourceType() {
		return dataSourceType;
	}

	public  JobPlainStatusModel(){
		this.setIsPackaging("0");
	}

	public void setBackupId(int backupId) {
		this.backupId = backupId;
	}

	public int getBackupId() {
		return backupId;
	}

	public void setjobName(String jobName) {
		this.jobName = jobName;
	}

	public String getjobName() {
		return jobName;
	}

	public int getCreateBy() {
		return createBy;
	}

	public void setCreateBy(int createBy) {
		this.createBy = createBy;
	}


	public String getBurnStatus() {
		return burnStatus;
	}

	public String getDay() {
		return day;
	}

	public String getDayOfWeek() {
		return dayOfWeek;
	}

	public String getDelSourceFile() {
		return delSourceFile;
	}

	public String getFileName() {
		return fileName;
	}

	public int getFileNameScope() {
		return fileNameScope;
	}

	public String getFileOwner() {
		return fileOwner;
	}

	public int getFileOwnerScope() {
		return fileOwnerScope;
	}

	public String getFilePath() {
		return filePath;
	}

	public String getFilePathScope() {
		return filePathScope;
	}

	public String getFileSizeOne() {
		return fileSizeOne;
	}

	public int getFileSizeScopeOne() {
		return fileSizeScopeOne;
	}

	public int getFileSizeScopeTwo() {
		return fileSizeScopeTwo;
	}

	public String getFileSizeTwo() {
		return fileSizeTwo;
	}

	public String getFileSizeUnit() {
		return fileSizeUnit;
	}

	public String getFileSizeUnitTwo() {
		return fileSizeUnitTwo;
	}

	public String getFileSuffixName() {
		return fileSuffixName;
	}

	public int getFileSuffixScope() {
		return fileSuffixScope;
	}

	public boolean getFilterVisible() {
		return filterVisible;
	}

	public String getIsPackaging() {
		return isPackaging;
	}

	public String getMonth() {
		return month;
	}

	public int getScheduleType() {
		return scheduleType;
	}

	public String getPackingLevel() {
		return packingLevel;
	}

	public int getPackingSize() {
		return packingSize;
	}

	public String getPackingSizeUnit() {
		return packingSizeUnit;
	}

	public Boolean getScheduleTypeReadOnly() {
		return scheduleTypeReadOnly;
	}

	public int getBackupType() {
		return backupType;
	}

	public String getSoftConnect() {
		return softConnect;
	}

	public String getSourcePath() {
		return sourcePath;
	}

	public String getFileCheckType() {
		return fileCheckType;
	}

	public int getSmallBatchCommitNum() {
		return smallBatchCommitNum;
	}

	public String getsourceIp() {
		return sourceIp;
	}

	public String getTargetPath() {
		return targetPath;
	}

	public String getTargetIp() {
		return targetIp;
	}

	public void setTargetIp(String targetIp) {
		this.targetIp = targetIp;
	}

	public String getTaskBeginTime() {
		return taskBeginTime;
	}

	public String getTaskName() {
		return taskName;
	}

	public int getTaskType() {
		return taskType;
	}

	public int getThreadNum() {
		return threadNum;
	}

	public String getTimeOne() {
		return timeOne;
	}

	public String getTimeTwo() {
		return timeTwo;
	}

	public void setBurnStatus(String burnStatus) {
		this.burnStatus = burnStatus;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public void setBackupType(int backupType) {
		this.backupType = backupType;
	}
	public  void  setFullBackup(){
		this.setBackupType(1);


	}
	public void setDayOfWeek(String dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	public void setDelSourceFile(String delSourceFile) {
		this.delSourceFile = delSourceFile;
	}

	public void setFileCheckType(String fileCheckType) {
		this.fileCheckType = fileCheckType;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setFileNameScope(int fileNameScope) {
		this.fileNameScope = fileNameScope;
	}

	public void setFileOwner(String fileOwner) {
		this.fileOwner = fileOwner;
	}

	public void setFileOwnerScope(int fileOwnerScope) {
		this.fileOwnerScope = fileOwnerScope;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public void setFilePathScope(String filePathScope) {
		this.filePathScope = filePathScope;
	}

	public void setFileSizeOne(String fileSizeOne) {
		this.fileSizeOne = fileSizeOne;
	}

	public void setFileSizeScopeOne(int fileSizeScopeOne) {
		this.fileSizeScopeOne = fileSizeScopeOne;
	}

	public void setFileSizeScopeTwo(int fileSizeScopeTwo) {
		this.fileSizeScopeTwo = fileSizeScopeTwo;
	}

	public void setFileSizeTwo(String fileSizeTwo) {
		this.fileSizeTwo = fileSizeTwo;
	}

	public void setFileSizeUnit(String fileSizeUnit) {
		this.fileSizeUnit = fileSizeUnit;
	}

	public void setFileSizeUnitTwo(String fileSizeUnitTwo) {
		this.fileSizeUnitTwo = fileSizeUnitTwo;
	}

	public void setFileSuffixName(String fileSuffixName) {
		this.fileSuffixName = fileSuffixName;
	}

	public void setFileSuffixScope(int fileSuffixScope) {
		this.fileSuffixScope = fileSuffixScope;
	}

	public void setFilterVisible(boolean filterVisible) {
		this.filterVisible = filterVisible;
	}

	public void setIsPackaging(String isPackaging) {
		this.isPackaging = isPackaging;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public void setPackingLevel(String packingLevel) {
		this.packingLevel = packingLevel;
	}

	public void setPackingSize(int packingSize) {
		this.packingSize = packingSize;
	}

	public void setPackingSizeUnit(String packingSizeUnit) {
		this.packingSizeUnit = packingSizeUnit;
	}

	public void setScheduleType(int scheduleType) {
		this.scheduleType = scheduleType;
	}
	public  void setScheduleRunOnce(){
		this.setScheduleType(0);

	}

	public void setScheduleTypeReadOnly(Boolean scheduleTypeReadOnly) {
		this.scheduleTypeReadOnly = scheduleTypeReadOnly;
	}

	public void setSmallBatchCommitNum(int smallBatchCommitNum) {
		this.smallBatchCommitNum = smallBatchCommitNum;
	}

	public void setSoftConnect(String softConnect) {
		this.softConnect = softConnect;
	}

	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}

	public void setsourceIp(String sourceIp) {
		this.sourceIp = sourceIp;
	}

	public void setTargetPath(String targetPath) {
		this.targetPath = targetPath;
	}

	public void setTaskBeginTime(String taskBeginTime) {
		this.taskBeginTime = taskBeginTime;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public void setTaskType(int taskType) {
		this.taskType = taskType;
	}


	public void setThreadNum(int threadNum) {
		this.threadNum = threadNum;
	}

	public void setTimeOne(String timeOne) {
		this.timeOne = timeOne;
	}

	public void setTimeTwo(String timeTwo) {
		this.timeTwo = timeTwo;
	}

}

