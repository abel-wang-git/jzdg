package com.zkyf.com.demo.croe;


import com.zkyf.com.demo.mapper.DatasourceMapper;
import com.zkyf.com.demo.po.Datasoruce;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class Dbconnect {

    @Autowired
    private DatasourceMapper datasourceMapper;

    public static Connection dbConnect(Datasoruce datasoruce) throws ClassNotFoundException, SQLException {

            Class.forName("oracle.jdbc.driver.OracleDriver");
            //根据数据库连接字符，名称，密码给conn赋值
            return DriverManager.getConnection("jdbc:oracle:thin:@" + datasoruce.getIP() + ":" + datasoruce.getProt() + ":" + datasoruce.getSid()
                    , datasoruce.getUserName(), datasoruce.getPasswd());

    }

    public  Datasoruce getDataSource(int id) {
        Datasoruce datasoruce = datasourceMapper.findById(id).get();

        return datasoruce;
    }

    public static List query(Connection connection,String sql) throws SQLException {
        List listOfRows = new ArrayList();
        ResultSet res=null;
        Statement s=null;
        s= connection.createStatement();
        res= s.executeQuery(sql);
        ResultSetMetaData md = res.getMetaData();
        int num = md.getColumnCount();
        while (res.next()) {
            Map mapOfColValues = new HashMap(num);
            for (int i = 1; i <= num; i++) {
                mapOfColValues.put(md.getColumnName(i), res.getObject(i));
            }
            listOfRows.add(mapOfColValues);
        }
        return listOfRows;
    }
}
