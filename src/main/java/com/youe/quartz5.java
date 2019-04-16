package com.youe;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class quartz5 implements Job {
	Calendar c = Calendar.getInstance();
	// ①实例Job接口方法
	public void execute(JobExecutionContext jobCtx)
			throws JobExecutionException {
		try {
			long start =  System.currentTimeMillis();
			runCmd();

			long end =  System.currentTimeMillis();

			System.out.println("执行时间: " + (end-start) + "ms");

			System.out.println(jobCtx.getTrigger().getName() + " run over!");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("调用失败");
		}

	}

	private void runCmd()   {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date = sdf.format(new Date());
		System.out.println("start local backup! " + date);
//		String localPath =  "D:\\YouE\\雄安\\db\\";
		String localPath =  "D:\\YouE\\反邪教\\db\\back\\";
		int day = c.get(Calendar.DAY_OF_MONTH);
		int month = c.get(Calendar.MONTH) + 1;
		String fileName = localPath + "/fx" + "-" +  month + "-" + day + "-" + System.currentTimeMillis();
		long start = System.currentTimeMillis();
		try {
			String sql = "mysqldump -h172.16.0.5 -uroot -p1qaz@WSX --databases  fxplat > " + fileName + ".sql" ;
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


	//打包错误就注释此main方法
	public static void main(String[] args) {
		try {

//			new quartz().test();
			new quartz5().runCmd();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
