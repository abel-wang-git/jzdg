package com.zkyf.com.demo.croe;


import com.alibaba.fastjson.JSON;
import com.zkyf.com.demo.po.Datasoruce;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wanghuiwen on 17-2-12.
 * 服务启动执行
 */
@Component
//@Order(value=2) 多个CommandLineRunner时 控制顺序
public class MyStartupRunner implements CommandLineRunner {
    public static  final List<Datasoruce> source = new ArrayList<Datasoruce>();
    public static  String conf=null;

    public void run(String... strings) throws Exception {}
}
