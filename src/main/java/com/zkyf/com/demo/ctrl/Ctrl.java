package com.zkyf.com.demo.ctrl;


import com.alibaba.fastjson.JSON;
import com.zkyf.com.demo.croe.AjaxReturn;
import com.zkyf.com.demo.croe.Dbconnect;
import com.zkyf.com.demo.croe.Table;
import com.zkyf.com.demo.mapper.DatasourceMapper;
import com.zkyf.com.demo.po.Datasoruce;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;


@Controller
public class Ctrl {

    @Value("${ftp.dir}")
    private String ftpdir;
    @Value("${ftp.user}")
    private String user;
    @Value("${ftp.passwd}")
    private String passwd;
    @Value("${ftp.port}")
    private String port;
    @Value("${ftp.server}")
    private String server;

    @Resource
    private DatasourceMapper datasourceMapper;

    @RequestMapping(value = "/")
    public String index(Model model) {
        model.addAttribute("hosts",datasourceMapper.findAll());
        return "admin";
    }

    @PostMapping(value = "/add")
    @ResponseBody
    public AjaxReturn add(@RequestParam String datasource){
        Datasoruce d= JSON.parseObject(datasource,Datasoruce.class);
        datasourceMapper.save(d);
        return new AjaxReturn("操作成功");
    }
    @GetMapping(value = "/add")
    public String toadd(){
        return "add";
    }


//    主库实例启动状态检查
//    备库实例的启动状态检查
    @RequestMapping(value = "/start")
    @ResponseBody
    public AjaxReturn startStatus(@RequestParam()String d){

        try {
            Connection connection= Dbconnect.dbConnect(JSON.parseObject(d, Datasoruce.class));
            List l=Dbconnect.query(connection,"select instance_name,status from v$instance");
            Map data=new HashMap<>();
            data.put("ins",l);
            return new AjaxReturn(0,"",data);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return new AjaxReturn(1,"");
        } catch (SQLException e) {
            e.printStackTrace();
            return new AjaxReturn(1,"");
        }
    }
//    主库启动模式检查
//    备库启动模式检查
    @PostMapping(value = "/startmodel")
    @ResponseBody
    public AjaxReturn startModel(@RequestParam() String d){
        try {
            Connection connection= Dbconnect.dbConnect(JSON.parseObject(d, Datasoruce.class));
            List l=Dbconnect.query(connection,"select name,open_mode,database_role,switchover_status, protection_mode, protection_level from v$database");
            Map data=new HashMap<>();
            data.put("ins",l);
            return new AjaxReturn(0,"",data);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return new AjaxReturn(1,"");
        } catch (SQLException e) {
            e.printStackTrace();
            return new AjaxReturn(1,"");
        }

    }

//    主库用于控制日志同步的参数检查
//    备库用于控制日志同步的参数检查
    @PostMapping(value = "/logsync")
    @ResponseBody
    public AjaxReturn logSync(@RequestParam String d){
        try {
            Connection connection= Dbconnect.dbConnect(JSON.parseObject(d, Datasoruce.class));
            List l=Dbconnect.query(connection,"select name,VALUE from v$parameter WHERE name = 'log_archive_dest_2'");
            Map data=new HashMap<>();
            data.put("ins",l);
            return new AjaxReturn(0,"",data);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return new AjaxReturn(1,"");
        } catch (SQLException e) {
            e.printStackTrace();
            return new AjaxReturn(1,"");
        }
    }
//    主库上查看DG环境进程的状态
//    备库上查看DG环境特有进程的状态

    /**
     *  这里，process就是进程名，包括ARCH, RFS, MRP0等，对应英文解释如下：
     * @param d
     * @return
     * status
     *  ALLOCATED: 正在准备但还未连接主库
        ATTACHED: 正在连接到主库
        CONNECTED:已经连接到主库
        IDLE:空闲
        ERROR：失败的进程，需要关注
        RECEIVING:归档日志接收中
        OPENING:归档日志处理中
        CLOSING:归档日志处理完，正在收尾中
        WRITING: 进程在将REDO数据写向归档文件中
        WAIT_FOR_LOG:等待新的REDO归档数据中
        WAIT_FOR_GAP:归档有中断，正在等待中断的那部分REDO数据.
        APPLYING_LOG:正在应用REDO归档数据到备库
     */
    @PostMapping(value = "/process")
    @ResponseBody
    public Table processStatus(@RequestParam String d){
        try {
            Connection connection= Dbconnect.dbConnect(datasourceMapper.findById(Integer.parseInt(d)).get());
            List l=Dbconnect.query(connection,"select process,status,client_process,sequence# from v$managed_standby");
            return new Table(l.size(),l);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;

        }
    }
//    主库上查看DG的状态信息
//    备库上查看DG环境的状态信息
    @PostMapping(value = "/dg")
    @ResponseBody
    public Table dgStatus(@RequestParam String d){
        try {
            Connection connection= Dbconnect.dbConnect(datasourceMapper.findById(Integer.parseInt(d)).get());
            List l=Dbconnect.query(connection,"select message_num,message,SEVERITY,FACILITY from v$dataguard_status");
            return new Table(l.size(),l);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;

        }
    }
//    主库上查询归档日志的应用情况
//    备库上查看同步过来的归档日志的应用情况
    @PostMapping(value = "/logapply")
    @ResponseBody
    public Table logApply(@RequestParam String d){
        try {
            Connection connection= Dbconnect.dbConnect(datasourceMapper.findById(Integer.parseInt(d)).get());
            List l=Dbconnect.query(connection,"select name,SEQUENCE# as seq,APPLIED from v$archived_log order by sequence#");
            return new Table(l.size(),l);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;

        }
    }

    /**
     * v$archive_dest_status
     * 归档文件文件路径配置信息及REDO的应用情况
     * @param d
     * @return
     */
    @PostMapping(value = "/archivedeststatus")
    @ResponseBody
    public Table test(@RequestParam String d){
        try {
            Connection connection= Dbconnect.dbConnect(JSON.parseObject(d, Datasoruce.class));
            List l=Dbconnect.query(connection,"select * from v$archive_dest_status order by sequence#");
            return new Table(l.size(),l);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * v$log_history
     * 查询所有以被应用的归档文件的信息
     * @param d
     * @return
     */
    @PostMapping(value = "/loghistory")
    @ResponseBody
    public Table logHistory(@RequestParam String d){
        try {
            Connection connection= Dbconnect.dbConnect(JSON.parseObject(d, Datasoruce.class));
            List l=Dbconnect.query(connection,"select * from v$log_history order by sequence#");
            return new Table(l.size(),l);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * rman备份信息
     */

    @PostMapping(value = "/rman")
    @ResponseBody
    public  Table rman(@RequestParam String d){
        List l= null;
        try {
            Connection connection= Dbconnect.dbConnect(datasourceMapper.findById(Integer.parseInt(d)).get());

            l = Dbconnect.query(connection,"select * from V$RMAN_STATUS where OPERATION='BACKUP'");
            Map data=new HashMap<>();
            data.put("ins",l);
            return new Table(l.size(),l);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }


    @PostMapping(value = "/file")
    @ResponseBody
    public AjaxReturn up(){
        FTPClient ftpClient = new FTPClient();
        String[] ftpdirs=ftpdir.split(",");
        try {
            ftpClient.connect(server,Integer.parseInt(port));
            ftpClient.login(user,passwd);
            int reply=ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
               return new AjaxReturn(1,"认证失败");
            }
            Map<String,Object> map= new HashMap<>();
            map.put("dir",ftpdirs);
            for (String p:ftpdirs) {
                List<FTPFile> ftp= Arrays.asList(ftpClient.listFiles(p));
                for (FTPFile af:ftp) {
                    Long day= (af.getTimestamp().getTime().getTime()-new Date().getTime())/86400000;
                    //显示７天以内的文件
                    if(day < -7)ftp.remove(af);
                }
                map.put(p,ftp);
            }

            ftpClient.logout();
            return new AjaxReturn(0,"",map);

        } catch (IOException e) {
            e.printStackTrace();
            return new AjaxReturn(1,"链接异常");
        }
    }

}

