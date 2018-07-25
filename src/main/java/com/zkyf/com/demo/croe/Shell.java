package com.zkyf.com.demo.croe;

import com.jcraft.jsch.*;
import com.zkyf.com.demo.po.Datasoruce;
import com.zkyf.com.demo.po.SysUserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Vector;

public class Shell {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private String ipAddress;

    private String username;

    private String password;

    public static final int DEFAULT_SSH_PORT = 22;

    private Vector<String> stdout;

    public Shell(final String ipAddress, final String username, final String password) {
        this.ipAddress = ipAddress;
        this.username = username;
        this.password = password;
        stdout = new Vector<String>();
    }
    public Shell(Datasoruce datasoruce){
        this.ipAddress=datasoruce.getIP();
        this.username=datasoruce.getSysUser();
        this.password=datasoruce.getSysPwd();
        stdout=new Vector<String>();
    }

    public String execute(final String command) {
        int returnCode = 0;
        JSch jsch = new JSch();
        SysUserInfo userInfo = new SysUserInfo();
        StringBuffer str = new StringBuffer();

        try {
            // Create and connect session.
            Session session = jsch.getSession(username, ipAddress, DEFAULT_SSH_PORT);
            session.setPassword(password);
            session.setUserInfo(userInfo);
            session.setConfig("userauth.gssapi-with-mic", "no");
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            // Create and connect channel.
            Channel channel = session.openChannel("exec");
            ((ChannelExec) channel).setEnv("ORACLE_BASE","/u01/app/oracle");
            ((ChannelExec) channel).setEnv("PATH","$PATH:$ORACLE_HOME/bin");
            ((ChannelExec) channel).setEnv("ORACLE_HOME","/u01/app/oracle/product/11.2.0/dbhome_1");
            ((ChannelExec) channel).setCommand(command);

            channel.setInputStream(null);
            BufferedReader input = new BufferedReader(new InputStreamReader(channel
                    .getInputStream()));

            channel.connect();
            logger.info(command);

            // Get the output of remote command.
            String line;
            while ((line = input.readLine()) != null) {
                str.append(line+'\n');
            }

            input.close();

            // Get the return code only after the channel is closed.
            if (channel.isClosed()) {
                returnCode = channel.getExitStatus();
            }

            // Disconnect the channel and session.
            channel.disconnect();
            session.disconnect();
        } catch (Exception e) {
            logger.error(Util.getMessage(e));
        }
        return str.toString();
    }

    public Vector<String> getStandardOutput() {
        return stdout;
    }

    public static void main(final String [] args) {
        Shell sshExecutor = new Shell("192.168.88.33", "oracle", "123456");
        String s=sshExecutor.execute("source /home/oracle/.bash_profile && lsnrctl status | grep Start");
        System.out.println(s);

    }
}
