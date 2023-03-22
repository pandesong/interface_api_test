package testapi.common.api;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class FileApi {

    public static ArrayList listDirectory(File dir)throws IOException {
        ArrayList filelist=new ArrayList();
        ArrayList A=new ArrayList();
        if(!dir.exists())
            throw new IllegalArgumentException("目录："+dir+"不存在.");
        if(!dir.isDirectory()){
            throw new IllegalArgumentException(dir+"不是目录。");
        }
        File[] files=dir.listFiles();
        if(files!=null&&files.length>0){
            for(File file:files){

                if(file.isDirectory()) {
                    ArrayList list = Lists.newArrayList();
                    A = listDirectory(file);
                    if (A.size() != 0) {
                        Iterables.addAll(list, listDirectory(file));
                        filelist=list;
                    }
                }
                else if(file.isFile()){
                    System.out.println(file);
                    filelist.add(file);
                }
            }
        }

        return filelist;
    }














}