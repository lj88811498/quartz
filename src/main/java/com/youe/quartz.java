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


public class quartz implements Job {
	Calendar c = Calendar.getInstance();
	// ①实例Job接口方法
	public void execute(JobExecutionContext jobCtx)
			throws JobExecutionException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date = sdf.format(new Date());
		System.out.println(jobCtx.getTrigger().getName()
				+ " triggered. time is:" + date);

//		Calendar c = Calendar.getInstance();
		int day = c.get(Calendar.DAY_OF_MONTH);
		int month = c.get(Calendar.MONTH) + 1;
		try {
			//String shpath="/home/cdyoue/backup.sh";   //程序路径
			//备份个数
			String size = "7";
			//存储的路径, 如果是linux，则需要把路径配成服务器存在的地址
//			String path = "/home/cdyoue";
			//linux地址
			String path = "/home/youedata/mysql";
			//本地地址
//			String path = "D:\\YouE\\雄安\\db";
			//数据库名
			String fileName = path + "/xadb" + month + "-" + day + "-" + System.currentTimeMillis();
			//String command = "/bin/sh " + shpath + " "  + fileName;
			//导出数据库ip
			String cmd = "mysqldump -h192.168.102.216 -uroot -p123456 --databases xiongan xiongan_appback xiongan_operationback xiongan_personback > " + fileName + ".sql";
			//删除最老大于7个的数据
//			cmd += "; SqlFileNumber=`ls " + path + "/*.sql|wc -l`; ";
//			cmd += "while (( $SqlFileNumber > " + size + " )); ";
//			cmd += "do ";
//			cmd += "OldFile=`ls -rt " + path + "/*.sql|head -1`; ";
//			cmd += "rm -f $OldFile; ";
//			cmd += "SqlFileNumber=`ls " + path + "/*.sql|wc -l`; ";
//			cmd += "done";
			long start =  System.currentTimeMillis();
			//执行命令
			//口令  需要有权限写入 或者直接在xshell执行命令看看此用户能否有权限
			//mysqldump -h192.168.102.216 -uroot -p123456 --databases xiongan_appback xiongan_operationback xiongan_personback > /home/youedata/mysql/xadb1-31.sql;
			// SqlFileNumber=`ls /home/youedata/mysql/*.sql|wc -l`; while (( $SqlFileNumber > 7 ));
			// do OldFile=`ls -rt /home/youedata/mysql/*.sql|head -1`; rm -f $OldFile; SqlFileNumber=`ls /home/youedata/mysql/*.sql|wc -l`; done
			runSSH("192.168.102.216", "root", "Bigdata@2017#", cmd);
			long end = System.currentTimeMillis();
			System.out.println("执行时间: " + (end-start) + "ms");
			runCmd();
			System.out.println(jobCtx.getTrigger().getName() + " run over!");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("调用失败");
		}

	}

	private void runCmd()   {
		System.out.println("start local backup!");
		String localPath =  "D:\\YouE\\雄安\\db\\";
		int day = c.get(Calendar.DAY_OF_MONTH);
		int month = c.get(Calendar.MONTH) + 1;
		String fileName = localPath + "/xadb" + month + "-" + day + "-" + System.currentTimeMillis();
		long start = System.currentTimeMillis();
		try {
			String sql = "mysqldump -h192.168.102.216 -uroot -p123456 --databases xiongan xiongan_appback xiongan_operationback xiongan_personback > " + fileName + ".sql" ;
			System.out.println("local sql: " + sql);
			Runtime.getRuntime().exec("cmd /c " + sql);
//			Runtime.getRuntime().exec(sql);
		} catch (Exception e) {
			System.out.println("本地执行命令错误！");
		}
		long end = System.currentTimeMillis();
		System.out.println("执行时间: " + (end-start) + "ms");
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


	private void test() throws IOException {
		Calendar c = Calendar.getInstance();
		int day = c.get(Calendar.DAY_OF_MONTH);
		int month = c.get(Calendar.MONTH) + 1;
		try {
			//String shpath="/home/cdyoue/backup.sh";   //程序路径
			//备份个数
			String size = "7";
			//存储的路径
//			String path = "/home/cdyoue";
			String path = "/home/youedata/mysql";
//			String path = "c:/test";
			//数据库名
			String fileName = path + "/xadb" + month + "-" + day;
			//String command = "/bin/sh " + shpath + " "  + fileName;
			//导出数据库ip
			String cmd = "mysqldump -h192.168.102.216 -uroot -p123456 --databases xiongan xiongan_appback xiongan_operationback xiongan_personback > " + fileName + ".sql";
//			String cmd = "mysqldump -h192.168.0.172 -uroot -pyoueDATA2016_ --databases sydb > " + fileName + ".sql";
			//删除最老大于7个的数据
//			cmd += "; SqlFileNumber=`ls " + path + "/*.sql|wc -l`; ";
//			cmd += "while (( $SqlFileNumber > " + size + " )); ";
//			cmd += "do ";
//			cmd += "OldFile=`ls -rt " + path + "/*.sql|head -1`; ";
//			cmd += "rm -f $OldFile; ";
//			cmd += "SqlFileNumber=`ls " + path + "/*.sql|wc -l`; ";
//			cmd += "done";
			long start = System.currentTimeMillis();
			//执行命令
			//口令  需要有权限写入 或者直接在xshell执行命令看看此用户能否有权限
			//mysqldump -h192.168.102.216 -uroot -p123456 --databases xiongan_appback xiongan_operationback xiongan_personback > /home/youedata/mysql/xadb1-31.sql;
			// SqlFileNumber=`ls /home/youedata/mysql/*.sql|wc -l`; while (( $SqlFileNumber > 7 ));
			// do OldFile=`ls -rt /home/youedata/mysql/*.sql|head -1`; rm -f $OldFile; SqlFileNumber=`ls /home/youedata/mysql/*.sql|wc -l`; done
//			runSSH("192.168.0.172", "root", "Bigdata@2017#", cmd);
			runSSH("192.168.102.216", "root", "Bigdata@2017#", cmd);
			long end = System.currentTimeMillis();
			System.out.println("执行时间: " + (end - start) + "ms");
			runCmd();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("调用失败");
		}
	}

	//打包错误就注释此main方法
//	public static void main(String[] args) {
//		try {
//
////			new quartz().test();
//			new quartz().runCmd();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

}
