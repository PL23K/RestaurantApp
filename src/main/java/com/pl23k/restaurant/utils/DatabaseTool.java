package com.pl23k.restaurant.utils;

/**
 * Created by HelloWorld on 2017/9/10.
 */

import com.pl23k.restaurant.common.SystemConfig;

import java.io.*;

public class DatabaseTool {

    /**
     * 备份数据库 ,控制台执行命令格式 "mysql的bin目录/mysqldump --databases  -h主机ip -P端口  -u用户名 -p密码  --default-character-set=字符集  数据库名
     *
     * @param mysqlPath  mysql路径
     * @param mysqlIp    mysql主机ip
     * @param mysqlPort  端口
     * @param userName   用户名
     * @param password   密码
     * @param database   数据库名
     * @param resultFile 备份文件全路径
     */
    public static void backup(String mysqlPath, String mysqlIp, String mysqlPort, String userName, String password, String database, String resultFile) {
        InputStream in = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        FileOutputStream fout = null;
        OutputStreamWriter writer = null;
        try {
            Runtime rt = Runtime.getRuntime();
            String cmd = "";
            if (mysqlPath.contains(" ")) {
                cmd = "\"" + mysqlPath + File.separator + "mysqldump\" -R --databases -h" + mysqlIp
                        + " -P" + mysqlPort + " -u" + userName + " -p" + password
                        + " --add-drop-database --default-character-set=utf8 " + database + " --result-file=" + resultFile;
            } else {
                cmd = mysqlPath + File.separator + "mysqldump -R --databases -h" + mysqlIp
                        + " -P" + mysqlPort + " -u" + userName + " -p" + password
                        + " --add-drop-database --default-character-set=utf8 " + database + " --result-file=" + resultFile;
            }

            Process process = rt.exec(cmd);
            // 设置导出编码为utf-8。这里必须是utf-8
            in = process.getInputStream();// 控制台的输出信息作为输入流
            ErrorStreamThread errStream = new ErrorStreamThread(process.getErrorStream()); //错误流另开线程，不然会阻塞
            errStream.start();

            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
                if (fout != null) {
                    fout.close();
                }
                if (br != null) {
                    br.close();
                }
                if (isr != null) {
                    isr.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}