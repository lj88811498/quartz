package quartz;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @Author: Monkey
 * @Date: Created in 14:15  2019/4/16.
 * @Description:
 */
public class testSSH {

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

    public static void main(String[] args) throws Exception{
        String cmd = "mkdir -p /home/2.txt";
        runSSH("10.0.43.106", "root", "monkey", cmd);
    }


}
