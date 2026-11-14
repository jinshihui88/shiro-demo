package com.example.shirodemo.realm;

import com.example.shirodemo.entity.User;
import com.example.shirodemo.mapper.UserMapper;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * 自定义 Realm：认证时从数据库加载用户，授权时提供权限列表
 */
@Component
public class UsernameRealm extends AuthorizingRealm {

    private final UserMapper userMapper;

    public UsernameRealm(UserMapper userMapper) {
        this.userMapper = userMapper;

        // 指定密码校验规则：MD5 加密 1024 次，与数据库存储的密文对应
        HashedCredentialsMatcher matcher = new HashedCredentialsMatcher();
        matcher.setHashAlgorithmName("MD5");
        matcher.setHashIterations(1024);
        this.setCredentialsMatcher(matcher);
    }

    /**
     * 认证
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;

        // 得到用户名
        String username = token.getUsername();
        // 根据用户名查询用户信息
        User user = userMapper.selectByUsername(username);

        if (user != null) {
            if (user.getLockState() == 1) {
                throw new AuthenticationException("账号已被锁定，禁止登录！");
            }

            // 第三个参数是 realm 名称；credentials 传数据库中的密文，
            // 由 HashedCredentialsMatcher 将用户提交的明文加密后与之比对
            return new SimpleAuthenticationInfo(user, user.getPassword(), getName());
        } else {
            throw new AuthenticationException("登录失败，用户名不存在！");
        }
    }

    /**
     * 授权（演示用：直接写死权限；实际项目应从数据库按 用户->角色->权限 查询）
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        Set<String> permissions = new HashSet<>();

        permissions.add("user:delete");
        permissions.add("user:update");

        authorizationInfo.setStringPermissions(permissions);

        return authorizationInfo;
    }
}
