package com.funfo.demo.controller;

import com.funfo.demo.entity.User;
import com.funfo.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户 API 接口（3.5.8 兼容 Spring Web）
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * GET /api/user/{id} → 查询用户信息
     */
    @GetMapping("/{id}")
    public Map<String, Object> getUserById(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();
        User user = userService.getUserById(id);

        if (user == null) {
            result.put("code", 404);
            result.put("msg", "用户不存在");
            result.put("data", null);
            return result;
        }

        result.put("code", 200);
        result.put("msg", "success");
        result.put("data", user);
        return result;
    }

    // 3. 新增用户（补充的核心接口）
    @PostMapping("/add")
    public ResponseEntity<User> addUser(@RequestBody User user) {
        // 简单参数校验（可选，避免空数据入库）
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        User savedUser = userService.addUser(user);
        return ResponseEntity.ok(savedUser);
    }

    // 4. 清空用户缓存
    @GetMapping("/clear-cache/{id}")
    public String clearUserCache(@PathVariable Integer id) {
        userService.clearUserCache(id);
        return "缓存已清空：user:" + id;
    }

    // （可选）补充更新和删除接口，形成完整CRUD
    @PutMapping("/update")
    public User updateUser(@RequestBody User user) {
        return userService.updateUser(user);
    }

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Integer id) {
        Boolean result = userService.deleteUser(id);
        return id + "用户删除成功:" + result;
    }

    // 新增：纯查MySQL的接口（无Redis，核心用于验证MySQL）
    @GetMapping("/only-mysql/{id}")
    public User getUserOnlyFromMysql(@PathVariable Integer id) {
        return userService.getUserOnlyFromMysql(id);
    }
}
