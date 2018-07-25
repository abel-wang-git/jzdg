package com.zkyf.com.demo.mapper;

import com.zkyf.com.demo.po.Datasoruce;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface DatasourceMapper extends CrudRepository<Datasoruce, Integer> {

//    @Select("select *,user_name as userName,sys_user as sysUser,sys_pwd as sysPwd ,switch_over as switchOver  from datasoruce where switch_over=#{aswitch}")
//    Datasoruce findBySwitch(@Param("aswitch") String switc);

//    @Select("select * from datasoruce where id=#{id}")
    Optional<Datasoruce> findById(int id);

//    @Select("select * ,user_name as userName,sys_user as sysUser,sys_pwd as sysPwd ,switch_over as switchOver from datasoruce ")
//    List<Datasoruce> findByAll();

}
