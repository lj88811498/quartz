package com.youe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;


public class quartz2 implements Job {
	// ①实例Job接口方法
	public void execute(JobExecutionContext jobCtx)
			throws JobExecutionException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date = sdf.format(new Date());
		System.out.println(jobCtx.getTrigger().getName()
				+ " triggered. time is:" + date);

		Calendar c = Calendar.getInstance();
		int day = c.get(Calendar.DAY_OF_MONTH);
		int month = c.get(Calendar.MONTH) + 1;
		try {
			//String shpath="/home/cdyoue/backup.sh";   //程序路径
			//备份个数
			String size = "7";
			//存储的路径
			//盐城
//			String path = "/home/cdyoue";
//			String dbName =  "entms";
			//北京沈阳
			String path = "/home/youedata/sydb";
			String dbName =  "sydb";
			//沈阳
//			String path = "/home/cdyoue/sydb";
//			String dbName =  "sydb";
			//沈阳
//			String path = "/home/cdyoue/tcj";
//			String dbName =  "tcjdb";
			//数据库名
			String fileName = path + "/" + dbName + month + "-" + day;
			//String command = "/bin/sh " + shpath + " "  + fileName;
			//导出数据库ip
			String cmd = "mysqldump -h192.168.30.245 -uroot -p1qaz@WSX " + dbName + " > " + fileName + ".sql; ";
//			String cmd = "mysqldump -h192.168.0.172 -uroot -pyoueDATA2016_ " + dbName + " > " + fileName + ".sql; ";
			//删除最老大于7个的数据
			cmd += "SqlFileNumber=`ls " + path + "/*.sql|wc -l`; ";
			cmd += "while (( $SqlFileNumber > " + size + " )); ";
			cmd += "do ";
			cmd += "OldFile=`ls -rt " + path + "/*.sql|head -1`; ";
			cmd += "rm -f $OldFile; ";
			cmd += "SqlFileNumber=`ls " + path + "/*.sql|wc -l`; ";
			cmd += "done";
			long start =  System.currentTimeMillis();
			//执行命令
			//口令
			//成都数据库
			runSSH("192.168.30.245", "cdyoue", "Abc123456", cmd);
			//北京沈阳
//			runSSH("192.168.0.172", "root", "Bigdata@2017#", cmd);
			long end = System.currentTimeMillis();
			System.out.println("执行时间: " + (end-start) + "ms");
			backup231();
			System.out.println(jobCtx.getTrigger().getName() + "run over!");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("调用");
		}

	}

	private void backup231() {
		Calendar c = Calendar.getInstance();
		int day = c.get(Calendar.DAY_OF_MONTH);
		int month = c.get(Calendar.MONTH) + 1;
		try {
			System.out.println("开始备份231数据库tcjdb");
			long start =  System.currentTimeMillis();
			String str = "mysqldump --user=root --host=192.168.30.231 --password=aq1sw2de3  tcjdb >e:\\231backup\\tcjdb" + month + day +".sql";
			Runtime.getRuntime().exec("cmd /k " + str);// cmd /c执行完关闭窗口 cmd /k 不关闭
			long end = System.currentTimeMillis();
			System.out.println("231执行时间: " + (end-start) + "ms");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	   * Run SSH command.
	   * @param host
	   * @param username
	   * @param password
	   * @param cmd
	   * @return exit status
	   * @throws IOException
	   */
	 public static int runSSH(String host, String username, String password,
	      String cmd) throws IOException {
		System.out.println("running SSH cmd [" + cmd + "]");
	 
	    Connection conn = getOpenedConnection(host, username, password);
	    Session sess = conn.openSession();
	    sess.execCommand(cmd);
	 
	    InputStream stdout = new StreamGobbler(sess.getStdout());
	    BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
	 
	    while (true) {
	      String line = br.readLine();
	      if (line == null){
	        break;
	      }
	      System.out.println(line);
	    }
	 
	    sess.close();
	    conn.close();
	    return sess.getExitStatus().intValue();
	 }
	 /**
	   * return a opened Connection
	   * @param host
	   * @param username
	   * @param password
	   * @return
	   * @throws IOException
	   */
	 private static Connection getOpenedConnection(String host, String username,
	      String password) throws IOException {
	     
	    System.out.println("connecting to " + host + " with user " + username
	          + " and pwd " + password);
	    //添加SSH连接端口，默认22
	    Connection conn = new Connection(host, 22);
	    conn.connect(); // make sure the connection is opened
	    boolean isAuthenticated = conn.authenticateWithPassword(username,
	        password);
	    if (isAuthenticated == false)
	      throw new IOException("Authentication failed.");
	    return conn;
	 }	 
	  
}
