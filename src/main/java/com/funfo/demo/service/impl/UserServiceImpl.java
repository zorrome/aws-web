
// UserServiceImpl 实现类
package com.funfo.demo.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.funfo.demo.entity.User;
import com.funfo.demo.mapper.UserMapper;
import com.funfo.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
// ====== 1. StringRedisTemplate 相关导入 ======
import org.springframework.data.redis.core.StringRedisTemplate;
// ====== 2. ObjectMapper 相关导入 ======
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.concurrent.TimeUnit;

/**
 * 核心业务逻辑：缓存优先 + 数据库兜底（3.5.8 兼容）
 */
@Service
public class UserServiceImpl implements UserService {
    // 缓存 Key 前缀（规范命名）
    private static final String USER_CACHE_KEY_PREX = "user:";

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ObjectMapper objectMapper;


    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // 新增：纯查MySQL（无任何Redis操作）
    @Override
    public User getUserOnlyFromMysql(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID格式错误");
        }
        // 仅调用Mapper查MySQL，无Redis读取/写入
        return userMapper.selectUserById(id);
    }

    @Override
    public User getUserById(Integer id) {
        // 1. 参数校验
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID格式错误");
        }
        String key = getKey(id);

        // 2. 从 Redis 读 JSON 字符串（反序列化支持 LocalDateTime）
        String userJson = stringRedisTemplate.opsForValue().get(key);
        if (userJson != null) {
            try {
                System.out.printf("get key:"+key+" from redis");
                return objectMapper.readValue(userJson, User.class);
            } catch (JsonProcessingException e) {
                stringRedisTemplate.delete(key); // 删除错误缓存
                e.printStackTrace();
                return null;
            }
        }

        // 3. 查 MySQL（自动映射 timestamp → LocalDateTime）
        User user = userMapper.selectUserById(id);
        if (user == null) {
            System.out.printf("no id record:"+id);
            return null;
        }

        // 4. 写 Redis（序列化支持 LocalDateTime）
        try {
            stringRedisTemplate.opsForValue().set(
                    key,
                    objectMapper.writeValueAsString(user),
                    60,
                    TimeUnit.MINUTES
            );
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return user;
    }

    // 新增用户（数据库自动填充 create_time）
    @Override
    public User addUser(User user) {
        userMapper.insertUser(user); // 数据库 create_time 自动设为当前时间
        String key = getKey(user.getId());
        try {
            stringRedisTemplate.opsForValue().set(
                    key,
                    objectMapper.writeValueAsString(user),
                    30,
                    TimeUnit.MINUTES
            );
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public User updateUser(User user) {
        userMapper.updateUserById(user); //
        String key = getKey(user.getId());
        try {
            stringRedisTemplate.opsForValue().set(
                    key,
                    objectMapper.writeValueAsString(user),
                    30,
                    TimeUnit.MINUTES
            );
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public Boolean deleteUser(Integer id) {
        userMapper.deleteUserById(id); //
        String key = getKey(id);
        Boolean isDel = stringRedisTemplate.delete(key);
        System.out.printf(id+" is del:"+isDel);
        return isDel;
    }
    // 清空缓存
    @Override
    public void clearUserCache(Integer id) {
        stringRedisTemplate.delete("user:" + id);
    }

    /**
     *
     * @param id
     * @return
     */
    private String getKey(Integer id) {
        return USER_CACHE_KEY_PREX + id;
    }
}
