package com.iyeeku.spark.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * @ClassName VersionMain
 * @Description TODO
 * @Author YangQuan
 * @Date 2020/7/24 20:09
 * @Version 1.0
 **/
public class VersionMain {

    public static void main(String[] args) {

        if (!(args.length == 1 && "--version".equals(args[0]))){
            System.out.println("Usage : java -jar iyeeku-spark-common.jar --version");
            System.exit(0);
        }

        InputStream inputStream = null;
        InputStreamReader reader = null;

        try {
            inputStream = VersionMain.class.getClass().getResourceAsStream("/git.properties");
            reader = new InputStreamReader(inputStream,"UTF-8");
            Properties properties = new Properties();
            properties.load(reader);
            String git_commit_user_email = properties.getProperty("git.commit.user.email");
            String git_commit_id_abbrev = properties.getProperty("git.commit.id.abbrev");
            String git_branch = properties.getProperty("git.branch");
            String git_commit_id_full = properties.getProperty("git.commit.id.full");
            String git_commit_user_name = properties.getProperty("git.commit.user.name");
            System.out.println("git.commit.user.email="+git_commit_user_email + "\ngit.commit.id.abbrev="+git_commit_id_abbrev
                    +"\ngit.branch="+git_branch+"\ngit.commit.id.full="+git_commit_id_full+"\ngit.commit.user.name="+git_commit_user_name);
        }catch (Exception e){
            System.out.println("读取jar包中的git.properties文件失败，或git.properties文件不存在");
        }finally {
            try {
                if (reader != null)reader.close();
                if (inputStream != null)inputStream.close();
            }catch (IOException ioe){
                ioe.printStackTrace();
            }
        }
    }

}
