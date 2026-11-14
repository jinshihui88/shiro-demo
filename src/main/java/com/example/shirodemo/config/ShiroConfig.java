package com.example.shirodemo.config;

import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import com.example.shirodemo.realm.UsernameRealm;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * shiro 配置类
 */
@Configuration
public class ShiroConfig {

    /**
     * 配置安全管理器
     *
     * @param usernameRealm UsernameRealm
     * @return DefaultWebSecurityManager
     */
    @Bean
    public DefaultWebSecurityManager securityManager(UsernameRealm usernameRealm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();

        securityManager.setRealm(usernameRealm);

        return securityManager;
    }

    /**
     * 配置 Shiro 过滤器工厂
     *
     * @param securityManager 安全管理器
     * @return ShiroFilterFactoryBean
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(DefaultWebSecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();

        // 注册安全管理器
        shiroFilterFactoryBean.setSecurityManager(securityManager);

        /**
         * 设置登录页面的地址
         * 当用户访问认证资源的时候，如果用户没有登录，那么就会跳转到该属性指定的页面
         */
        shiroFilterFactoryBean.setLoginUrl("/login.html");

        /**
         * 设置无权限时的跳转地址
         * 当用户访问 perms 资源但没有对应权限时，会跳转到该属性指定的地址
         * （前后端分离场景下指向一个返回统一 JSON 的接口，避免返回默认错误页）
         */
        shiroFilterFactoryBean.setUnauthorizedUrl("/user/unauthorized");

        // 定义资源访问规则（LinkedHashMap 保证顺序，匹配规则自上而下生效）
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();

        /**
         * 过滤器说明
         * anon：不需要认证就可以访问的资源
         * authc：需要登录认证才能访问的资源
         * perms：需要指定权限才能访问的资源
         */

        // 不需要登录认证就可以访问的资源
        filterChainDefinitionMap.put("/user/login", "anon");
        filterChainDefinitionMap.put("/login.html", "anon");
        filterChainDefinitionMap.put("/js/**", "anon");

        // 需要登录认证才能访问的资源
        filterChainDefinitionMap.put("/", "authc");
        filterChainDefinitionMap.put("/index.html", "authc");

        // 需要指定权限才能访问的资源
        filterChainDefinitionMap.put("/user/delete", "perms[user:delete]");
        filterChainDefinitionMap.put("/user/update", "perms[user:update]");

        // 其余资源一律需要登录认证
        filterChainDefinitionMap.put("/**", "authc");

        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);

        return shiroFilterFactoryBean;
    }
}
