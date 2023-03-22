package testapi.common;

public class resourceparam {

    public static class disresource {

        public static  class param {
            String streamName = "a";
            String shardCount = "1";
            public String getStreamName(){
                return streamName;
            }
            public void setStreamName(String streamName){
                this.streamName=streamName;
            }

            public String getShardCount() {
                return shardCount;
            }

            public void setShardCount(String shardCount) {
                this.shardCount = shardCount;
            }
        }
        public static  class schedule {
            String scheduletype = "atonce";
            public String getScheduletype(){
                return scheduletype;
            }
            public void setScheduletype(String scheduletype){
                this.scheduletype=scheduletype;
            }

        }


        public static  class disresourceparam{
        String id;
        String name;
        String type;
        String mgrtype = "DELEGATE";
        param param;
        schedule schedule;
        public void setId(String id) {
            this.id = id;
        }
        public void setType(String type) {
            this.type = type;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String getName() {
            return name;
        }
        public String getId() {
            return id;
        }
        public String getType() {
            return type;
        }
        public param getParam() {
            return param;
        }
        public void setParam(param param) {
            this.param=param;
        }
        public schedule getSchedule() {
                return schedule;
            }
        public void setSchedule(schedule schedule) {
                this.schedule = schedule;
            }
        }
    }

    public static class  keyparis{
        public static class allkeyparis{
            keypair keypair;

            public keyparis.keypair getKeypair() {
                return keypair;
            }
            public void setKeypair(keyparis.keypair keypair) {
                this.keypair = keypair;
            }
        }

        public  static   class keypair{
            String fingerprint;
            String name;
            String public_key;

            public String getPublic_key() {
                return public_key;
            }

            public void setPublic_key(String public_key) {
                this.public_key = public_key;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getFingerprint(){
                return  this.fingerprint;
            }
            public void setFingerprint(String fingerprint) {
                this.fingerprint = fingerprint;
            }
        };



    }
}