package com.zkyf.com.demo;

import java.io.IOException;
 import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class DemoApplicationTests {

	public static void main(String[] args) throws IOException {
//		String ipAddr="192.168.0.108";
//			Connection conn = null;
//
//			Session sess = null;
//
//			InputStream stdout = null;
//
//			BufferedReader br = null;
//
//			StringBuffer buffer = new StringBuffer("exec result:");
//			buffer.append(System.getProperty("line.separator"));// 换行
//			try {
//
//				conn = new Connection(ipAddr, 22);
//				conn.connect();
//				boolean isAuthenticated = conn.authenticateWithPassword("wanghuiwen",
//
//						"123");
//				sess = conn.openSession();
//
//				sess.execCommand("top ");
//
//				stdout = new StreamGobbler(sess.getStdout());
//
//				br = new BufferedReader(new InputStreamReader(stdout));
//
//				while (true) {
//					String line = br.readLine();
//					if (line == null)
//						break;
//					buffer.append(line);
//					buffer.append(System.getProperty("line.separator"));// 换行
//
//				}
//				System.out.println(buffer);
//
//			} finally {
//
//			}


		java.sql.Connection conn = null;
		Statement smt = null;
		ResultSet rs = null;
		try{
			//1.加载数据库驱动
			//Class.forName("oracle.jdbc.driver.OracleDriver");//会抛出ClassNotFoundException
			Class.forName("oracle.jdbc.OracleDriver");//会抛出ClassNotFoundException
			//2.使用DriverManager获取数据库连接
			conn = DriverManager.getConnection("jdbc:oracle:thin:@192.168.1.180:1521:orcl", "scott", "tiger");//会抛出SQLException
			System.out.println("数据库连接成功: "+conn);
			//3.使用Connection来创建一个Statement对象
			smt = conn.createStatement();//会抛出SQLException
			//4.使用Statement对象执行SQL语句
            /*
            Statement有三种执行sql语句的方法：
            1. execute可执行任何SQL语句--返回一个boolean值
               如果执行后的第一个结果是ResultSet，则返回true，否则返回false
            2. executeQuery 执行Select语句--返回查询到的结果集
            3. executeUpdate 用于执行DML语句和DDL语句--返回一个整数，执行DML代表被SQL语句影响的记录条数；执行DDL语句返回0
            */
			rs = smt.executeQuery("select * from emp");//会抛出SQLException
			//5.操作结果集
            /*
            ResultSet对象有两类方法操作查询结果集
            1. next()将记录指针下移一行,first(),last()等
            2. getXxx(列索引|列名)获取有记录指针指向行，特定列的值
            */
			while(rs.next()){//会抛出SQLException
				System.out.println(rs.getInt(1) + "\t"
						+ rs.getString(2)+"\t"
						+ rs.getString(3));
			}
		}
		catch(ClassNotFoundException e){
			e.printStackTrace();
		}
		catch(SQLException e){
			e.printStackTrace();
		} finally{
			try{
				//6.回收数据库资源
				if(rs != null){
					//关闭ResultSet
					rs.close();//会抛出SQLException
				}
				if(smt != null){
					//关闭Statement
					smt.close();
				}
				if(conn != null){
					//关闭Connection
					conn.close();
				}
			}
			catch(SQLException e){
				e.printStackTrace();
			}
		}
	}
}




