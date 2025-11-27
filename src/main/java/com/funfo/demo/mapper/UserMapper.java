package com.funfo.demo.mapper;

import com.funfo.demo.entity.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

/**
 * 用户 Mapper（3.5.8 兼容 MyBatis 注解）
 */
@Repository
public interface UserMapper {
    /**
     * 根据 ID 查询用户
     */
    @Select("SELECT id, name, age, email, create_time FROM users WHERE id = #{id}")
    User selectUserById(Integer id);

    // 新增：自动填充 create_time（数据库默认 CURRENT_TIMESTAMP，无需传值）
    @Insert("INSERT INTO users(name, age, email) VALUES(#{name}, #{age}, #{email})")
    @Options(useGeneratedKeys = true, keyProperty = "id") // 自增主键回填
    int insertUser(User user);

    // 更新：不修改 create_time
    @Update("UPDATE users SET name=#{name}, age=#{age}, email=#{email} WHERE id=#{id}")
    int updateUserById(User user);

    // 删除
    @Delete("DELETE FROM users WHERE id=#{id}")
    int deleteUserById(Integer id);
}