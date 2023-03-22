package testapi.DistributedStorage.FilesStorage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class test {

    public static void main(String[] args) {
        String str="{\"content\":[{\"id\":53,\"name\":\"pool1648694269\",\"poolType\":0,\"status\":0,\"lvgroupId\":\"f36db624-7726-4428-a269-7fb7d1c6118d\",\"createTime\":1648694269885,\"updateTime\":null,\"storageUnits\":[{\"id\":21,\"name\":\"sdt\",\"mediaType\":\"HDD\",\"totalCapacity\":3997479469056,\"usedCapacity\":4017012736,\"status\":0,\"target\":\"18\",\"adaptNo\":1,\"raidLevel\":\"Primary-5, Secondary-0\",\"writePolicy\":\"WriteBack\",\"readPolicy\":\"ReadAhead\",\"state\":\"Optimal\",\"zpstate\":\"running\",\"blkSize\":\"3.7T\",\"devName\":\"sdt\",\"devUuid\":\"64e998c6-2e8f-4112-8587-fa1f7829021f\",\"nodeName\":null,\"nodeIp\":\"172.16.26.188\",\"nodeId\":2,\"poolName\":null,\"poolType\":null,\"isCache\":true}],\"hddStrategy\":1,\"ssdStrategy\":null,\"ssdIsUninstall\":null,\"hddProtectNum\":1,\"ssdProtectNum\":null,\"totalDiskSpace\":null,\"freeDiskSpace\":null,\"hddTotalDiskSpace\":3997479469056,\"hddUsedDiskSpace\":4017012736,\"ssdTotalDiskSpace\":null,\"ssdUsedDiskSpace\":null,\"customPolicy\":n";

//        Pattern p= Pattern.compile("\"id\":(\\d+)",Pattern.CASE_INSENSITIVE);
//        Pattern p= Pattern.compile("id",Pattern.CASE_INSENSITIVE);

//        Pattern p=Pattern.compile("\\d+");
//        Matcher m=p.matcher("22bb23");
////        Matcher m=p.matcher(str);
////        String group = m.group();
//        Pattern pattern = m.pattern();
//        System.out.printf(group);

        String regex = "id\":\\d+";
        Pattern p= Pattern.compile(regex);
        Matcher m=p.matcher(str);
        while (m.find()) {
            System.out.printf(m.group());
       }

    }
}
