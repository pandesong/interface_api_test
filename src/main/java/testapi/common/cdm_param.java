package testapi.common;

import java.util.ArrayList;
import java.util.List;

public class cdm_param {

    public static class clusters_p{
        List<cluster> clusters= new ArrayList<cluster>();
        public List getClusters() {
            return clusters;
        }
        public void setClusters(List clusters) {
            this.clusters = clusters;
        }
    }

    public static class cluster{
        private Datastore datastore;

        private List<Instances> instances=new ArrayList<Instances>() ;

        private String updated;

        private Task task;

        private String name;

        private String created;

        private String id;

        private String status;

        private List actions=new ArrayList()  ;

        private String endpoint;

        private String publicEndpoint;

        private String isFrozen;

        private String statusDetail;

        private ActionProgress actionProgress;

        private String version;

        private String config_status;

        public void setDatastore(Datastore datastore){
            this.datastore = datastore;
        }
        public Datastore getDatastore(){
            return this.datastore;
        }
        public void setInstances(List<Instances> instances){
            this.instances = instances;
        }
        public List<Instances> getInstances(){
            return this.instances;
        }
        public void setUpdated(String updated){
            this.updated = updated;
        }
        public String getUpdated(){
            return this.updated;
        }
        public void setTask(Task task){
            this.task = task;
        }
        public Task getTask(){
            return this.task;
        }
        public void setName(String name){
            this.name = name;
        }
        public String getName(){
            return this.name;
        }
        public void setCreated(String created){
            this.created = created;
        }
        public String getCreated(){
            return this.created;
        }
        public void setId(String id){
            this.id = id;
        }
        public String getId(){
            return this.id;
        }
        public void setStatus(String status){
            this.status = status;
        }
        public String getStatus(){
            return this.status;
        }
        public void setActions(List actions){
            this.actions = actions;
        }
        public List getActions(){
            return this.actions;
        }
        public void setEndpoint(String endpoint){
            this.endpoint = endpoint;
        }
        public String getEndpoint(){
            return this.endpoint;
        }
        public void setPublicEndpoint(String publicEndpoint){
            this.publicEndpoint = publicEndpoint;
        }
        public String getPublicEndpoint(){
            return this.publicEndpoint;
        }
        public void setIsFrozen(String isFrozen){
            this.isFrozen = isFrozen;
        }
        public String getIsFrozen(){
            return this.isFrozen;
        }
        public void setStatusDetail(String statusDetail){
            this.statusDetail = statusDetail;
        }
        public String getStatusDetail(){
            return this.statusDetail;
        }
        public void setActionProgress(ActionProgress actionProgress){
            this.actionProgress = actionProgress;
        }
        public ActionProgress getActionProgress(){
            return this.actionProgress;
        }
        public void setVersion(String version){
            this.version = version;
        }
        public String getVersion(){
            return this.version;
        }
        public void setConfig_status(String config_status){
            this.config_status = config_status;
        }
        public String getConfig_status(){
            return this.config_status;
        }

    }

    public  static class Datastore {
        private String type;

        private String version;

        public void setType(String type){
            this.type = type;
        }
        public String getType(){
            return this.type;
        }
        public void setVersion(String version){
            this.version = version;
        }
        public String getVersion(){
            return this.version;
        }

    }
    public  static class Task {
        private String description;

        private String id;

        private String name;

        public void setDescription(String description){
            this.description = description;
        }
        public String getDescription(){
            return this.description;
        }
        public void setId(String id){
            this.id = id;
        }
        public String getId(){
            return this.id;
        }
        public void setName(String name){
            this.name = name;
        }
        public String getName(){
            return this.name;
        }

    }

    public static class ActionProgress {
String CREATING;

        public void setCREATING(String CREATING) {
            this.CREATING = CREATING;
        }

        public String getCREATING() {
            return CREATING;
        }
    }
    public static class Instances {
        private String flavor;
        private String volume;
        private String role;
        private String group;
        private String status;

        private List actions ;

        private String type;

        private String id;

        private String name;

        private String paramsGroupId;

        private String isFrozen;

        private String config_status;

        public void setFlavor(String flavor){
            this.flavor = flavor;
        }
        public String getFlavor(){
            return this.flavor;
        }
        public void setVolume(String volume){
            this.volume = volume;
        }
        public String getVolume(){
            return this.volume;
        }
        public void setRole(String role){
            this.role = role;
        }
        public String getRole(){
            return this.role;
        }
        public void setGroup(String group){
            this.group = group;
        }
        public String getGroup(){
            return this.group;
        }
        public void setStatus(String status){
            this.status = status;
        }
        public String getStatus(){
            return this.status;
        }
        public void setActions(List actions){
            this.actions = actions;
        }
        public List getActions(){
            return this.actions;
        }
        public void setType(String type){
            this.type = type;
        }
        public String getType(){
            return this.type;
        }
        public void setId(String id){
            this.id = id;
        }
        public String getId(){
            return this.id;
        }
        public void setName(String name){
            this.name = name;
        }
        public String getName(){
            return this.name;
        }
        public void setParamsGroupId(String paramsGroupId){
            this.paramsGroupId = paramsGroupId;
        }
        public String getParamsGroupId(){
            return this.paramsGroupId;
        }
        public void setIsFrozen(String isFrozen){
            this.isFrozen = isFrozen;
        }
        public String getIsFrozen(){
            return this.isFrozen;
        }
        public void setConfig_status(String config_status){
            this.config_status = config_status;
        }
        public String getConfig_status(){
            return this.config_status;
        }

    }




}
