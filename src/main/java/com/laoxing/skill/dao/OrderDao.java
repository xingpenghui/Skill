package com.laoxing.skill.dao;

import com.laoxing.skill.entity.Order;
import org.apache.ibatis.annotations.Param;

/**
 * @program: Skill
 * @description:
 * @author: Feri
 * @create: 2020-02-25 10:31
 */
public interface OrderDao {

    Order selectByUid(@Param("uid") int uid,@Param("gid") int gid);
    int insert(Order order);
    Order selectById(long oid);

    int update(@Param("oid") long oid,@Param("status") int status);
}
