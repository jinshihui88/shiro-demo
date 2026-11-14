# shiro-demo —— Spring Boot 3 整合 Apache Shiro 权限实战项目

基于 Spring Boot 3.x + Apache Shiro 2.x 的登录认证与权限授权演示项目，前后端不分离（原生 HTML + fetch），实现了完整的「登录认证 → 会话管理 → 权限校验」闭环。

## 一、技术栈

| 组件 | 版本 | 说明 |
| --- | --- | --- |
| Spring Boot | 3.5.16 | Jakarta EE 基线 |
| Java | 17 | Boot 3 最低要求 |
| Apache Shiro | 2.2.1 | **必须带 `jakarta` classifier**（见下文踩坑） |
| MyBatis Spring Boot Starter | 3.0.5 | 适配 Boot 3 的 MyBatis 集成 |
| MySQL Connector/J | 由 Boot BOM 管理 | `com.mysql:mysql-connector-j` |
| Lombok | 由 Boot BOM 管理 | 简化实体类 |
| 前端 | 原生 HTML + fetch | 无 jQuery 等额外依赖 |

## 二、核心功能

1. **登录认证**：`UsernamePasswordToken` + 自定义 `UsernameRealm`，密码采用 **MD5 加盐哈希迭代 1024 次** 存储（`HashedCredentialsMatcher` 自动比对）
2. **会话管理**：Shiro Session + Cookie（`JSESSIONID`），登录状态跨请求保持
3. **URL 级权限拦截**：`ShiroFilterFactoryBean` 过滤器链，`anon` / `authc` / `perms[...]` 三级规则
4. **统一响应与全局异常**：`JsonResult` + `ResponseCode` + `@RestControllerAdvice`，认证失败/权限不足均返回统一 JSON
5. **无权限统一提示**：`setUnauthorizedUrl` 指向 `/user/unauthorized`，避免 Filter 层异常落到 Boot 默认错误页

## 三、快速开始

### 1. 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0（账号 root / 123456，可改 `application.yml`）

### 2. 初始化数据库

```sql
-- 执行建库建表脚本（含测试数据）
source src/main/resources/sql/shiro.sql
```

脚本会创建 `shiro` 库、`user` 表并插入测试账号：

| 用户名 | 密码 | 权限 |
| --- | --- | --- |
| mumu | mhxy1218 | `user:delete`、`user:update` |

> 密码在库中为 MD5 迭代 1024 次的哈希值，不是明文。

### 3. 启动应用

```bash
mvn clean package -DskipTests
java -jar target/shiro-demo-1.0.0.jar --server.port=8090
```

> ⚠️ 必须带 `--server.port=8090` 参数显式指定端口。部分 Windows 环境存在 `SERVER__PORT` 环境变量，会被 Spring Boot 宽松绑定劫持 `server.port`，导致启动端口错乱。

### 4. 访问验证

- 浏览器打开 `http://localhost:8090/` → 未登录自动 302 跳转登录页
- 输入 `mumu / mhxy1218` 登录 → 跳转 `index.html`
- 页面上点击「删除用户」「修改用户」按钮验证权限接口

## 四、工程结构

```
shiro-demo
├── pom.xml
├── src/main/java/com/example/shirodemo
│   ├── ShiroDemoApplication.java        # 启动类
│   ├── common
│   │   ├── ResponseCode.java             # 响应码枚举
│   │   ├── JsonResult.java               # 统一响应封装
│   │   ├── GlobalException.java          # 业务异常
│   │   └── GlobalExceptionHandler.java   # 全局异常处理
│   ├── config
│   │   ├── ShiroConfig.java              # Shiro 核心配置（重点）
│   │   └── MybatisConfig.java            # MyBatis @Mapper 扫描
│   ├── controller
│   │   └── UserController.java           # 登录/登出/权限测试接口
│   ├── dto
│   │   └── UserLoginDTO.java             # 登录入参（带 JSR-303 校验）
│   ├── entity
│   │   └── User.java                     # 用户实体
│   ├── mapper
│   │   └── UserMapper.java               # MyBatis Mapper
│   ├── realm
│   │   └── UsernameRealm.java            # 自定义 Realm（认证+授权）
│   └── service
│       ├── UserService.java
│       └── impl/UserServiceImpl.java     # 登录业务（Subject.login）
└── src/main/resources
    ├── application.yml
    ├── sql/shiro.sql                     # 建库建表+测试数据
    └── static
        ├── login.html                    # 登录页
        ├── index.html                    # 首页（权限测试按钮）
        └── js/login.js                   # fetch 登录逻辑
```

## 五、关键配置说明

### 1. Shiro 依赖（Boot 3 专用写法）

Shiro 2.x 主包默认仍是 **javax** 版本，Jakarta 版以 `jakarta` classifier 单独发布，且 **Maven classifier 不随传递依赖传导**。因此必须把整条依赖链逐个显式声明：

```xml
<dependency>
    <groupId>org.apache.shiro</groupId>
    <artifactId>shiro-spring-boot-web-starter</artifactId>
    <version>2.2.1</version>
    <classifier>jakarta</classifier>
    <exclusions>
        <!-- 排除 javax 版传递依赖 -->
        <exclusion>
            <groupId>org.apache.shiro</groupId>
            <artifactId>shiro-spring-boot-starter</artifactId>
        </exclusion>
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-log4j2</artifactId>
        </exclusion>
    </exclusions>
</dependency>
<!-- shiro-spring-boot-starter / shiro-spring / shiro-web 同样显式声明 jakarta classifier -->
```

验证方法：`unzip -l target/shiro-demo-1.0.0.jar | grep shiro`，确认 `BOOT-INF/lib` 中 servlet 相关模块全部带 `-jakarta` 后缀且无 javax 版重复。

### 2. 过滤器链（ShiroConfig）

| 规则 | 说明 |
| --- | --- |
| `/user/login`、`/login.html`、`/js/**` | `anon` 匿名放行 |
| `/user/delete` | `perms[user:delete]` 需删除权限 |
| `/user/update` | `perms[user:update]` 需修改权限 |
| `/**` | `authc` 其余一律登录后访问 |

注意：**过滤器工厂 Bean 的方法名必须为 `shiroFilterFactoryBean`**，这是 Shiro starter 自动配置按名字查找的约定，命名为老教程常见的 `shiroFilter` 会启动报错。

### 3. 密码加密

```
明文 mhxy1218 --MD5 迭代 1024 次--> adfd5259371cd155896fbb30315593dd
```

`ShiroConfig` 中通过 `HashedCredentialsMatcher` 配置算法与迭代次数，Realm 认证时返回库中密文，Matcher 自动完成比对，业务代码不接触明文比对逻辑。

### 4. 无权限处理

`perms` 过滤器抛出的 `UnauthorizedException` 发生在 **Filter 层**，`@RestControllerAdvice` 拦截不到。因此配置：

```java
shiroFilterFactoryBean.setUnauthorizedUrl("/user/unauthorized");
```

由 `/user/unauthorized` 接口返回统一 JSON（`{"code":403,"message":"没有操作权限"}`），前端据此提示。

## 六、接口清单

| 接口 | 方法 | 鉴权 | 说明 |
| --- | --- | --- | --- |
| `/user/login` | POST | anon | 登录（JSON 入参，JSR-303 校验） |
| `/user/logout` | POST | authc | 登出并销毁会话 |
| `/user/delete` | POST | perms[user:delete] | 删除测试接口 |
| `/user/update` | POST | perms[user:update] | 修改测试接口 |
| `/user/unauthorized` | GET | anon | 无权限统一 JSON 提示 |

## 七、实测验证记录（10/10 通过）

| # | 场景 | 预期 | 结果 |
| --- | --- | --- | --- |
| 1 | 未登录访问 `/` | 302 跳转登录页 | ✅ |
| 2 | 访问 `/login.html` | 200 | ✅ |
| 3 | 空参数登录 | 400 参数校验 | ✅ |
| 4 | 错误密码登录 | 403 密码错误 | ✅ |
| 5 | 正确账号登录 | 200 + 会话 Cookie | ✅ |
| 6 | 登录后访问 `/` | 200 | ✅ |
| 7 | `/user/delete`（有权限） | 200 删除成功 | ✅ |
| 8 | `/user/update`（无权限时） | 403 统一 JSON | ✅ |
| 9 | 未登录调删除接口 | 302 跳转登录页 | ✅ |
| 10 | 登录后访问 `index.html` | 200 | ✅ |

## 八、升级踩坑备忘（Spring Boot 2.x → 3.x）

1. **Shiro 1.x 在 Boot 3 下完全不可用**：仅支持 `javax.servlet`，启动即 `ClassNotFoundException`；必须用 2.x + jakarta classifier
2. **classifier 传导陷阱**：只给 starter 加 `jakarta` classifier 无效，整条依赖链要逐个声明并排除 javax 传递依赖
3. **log4j2 冲突**：Shiro starter 自带 `spring-boot-starter-log4j2`，与 Boot 默认 logback 冲突，需 exclusion
4. **Bean 命名**：过滤器工厂 Bean 必须叫 `shiroFilterFactoryBean`（详见上文）
5. **Filter 层异常**：`@RestControllerAdvice` 拦不到 perms 过滤器异常，用 `unauthorizedUrl` 兜底

## 九、生产化改造建议

本项目的权限数据（`user:delete` / `user:update`）为演示写在 `UsernameRealm` 中。真实项目应：

- 建 RBAC 五表（用户/角色/权限 + 两张关联表），`doGetAuthorizationInfo` 中由数据库查询组装
- 密码算法升级为 BCrypt 或 SHA-256 + 随机盐（MD5 迭代仅作教学演示）
- 登录接口加验证码与防爆破限流
- 会话改用 Redis 存储（`RedisSessionDAO`）支持集群部署
- 前后端分离场景下改用 JWT 无状态方案（可参考同目录系列的 spring-security-demo）
