package com.funfo.demo.entity;

import java.time.LocalDateTime;

/**
 * 用户实体类（适配 3.5.8 时间序列化）
 */
public class User {
    private Integer id;         // 主键
    private String name;        // 用户名
    private Integer age;        // 年龄
    private String email;       // 邮箱
    private LocalDateTime createTime;  // 创建时间（JDK8 时间类型）

    // Getter + Setter（3.5.8 反射取值必须，不可省略）
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
