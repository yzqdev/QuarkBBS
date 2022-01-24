package com.quark.admin.config;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import com.quark.admin.service.PermissionService;
import com.quark.admin.shiro.MyShiroRealm;
import com.quark.common.entity.Permission;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by lhr on 17-8-1.
 */
@Configuration
public class ShiroConfig {
    @Resource(name = "permissionService")
    private PermissionService permissionService;

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    @Value("${spring.redis.timeout}")
    private int timeout;

    @Value("${spring.redis.password}")
    private String password;

    @Bean
    public MyShiroRealm getMyShiroRealm(){
        MyShiroRealm mShiroRealm = new MyShiroRealm();
        mShiroRealm.setCredentialsMatcher(hashedCredentialsMatcher());
        return mShiroRealm;
    }



    /**
     * thymeleaf里使用shiro的标签的bean
     *
     * @return
     */
    @Bean
    public ShiroDialect shiroDialect() {
        return new ShiroDialect();
    }

    /**
     *
     * 处理拦截资源文件问题。
     *
     * @return {@link ShiroFilterChainDefinition}
     */
    @Bean
    public ShiroFilterChainDefinition shirFilter( ) {
        DefaultShiroFilterChainDefinition filterChainDefinitionMap= new DefaultShiroFilterChainDefinition();

        // 设置 SecurityManager
        //shiroFilterFactoryBean.setSecurityManager(securityManager);
        //
        //// 如果不设置默认会自动寻找Web工程根目录下的"/login.html"页面
        //shiroFilterFactoryBean.setLoginUrl("/login");
        //// 登录成功后要跳转的链接
        //shiroFilterFactoryBean.setSuccessUrl("/initPage");
        ////未授权界面
        //shiroFilterFactoryBean.setUnauthorizedUrl("/403");
        //拦截器.

        filterChainDefinitionMap.addPathDefinition("/favicon.png","anon");//解决弹出favicon.ico下载
        filterChainDefinitionMap.addPathDefinition("/logout", "logout");
        filterChainDefinitionMap.addPathDefinition("/css/**", "anon");
        filterChainDefinitionMap.addPathDefinition("/js/**", "anon");
        filterChainDefinitionMap.addPathDefinition("/img/**", "anon");
        filterChainDefinitionMap.addPathDefinition("/font-awesome/**", "anon");

        //自定义加载权限资源关系
        List<Permission> list = permissionService.findAll();
        for (Permission p : list) {
            if (!p.getPerurl().isEmpty()) {
                String permission = "perms[" + p.getPerurl() + "]";
                filterChainDefinitionMap.addPathDefinition(p.getPerurl(), permission);
            }
        }


        //过滤链定义，从上向下顺序执行，一般将 /**放在最为下边
        filterChainDefinitionMap.addPathDefinition("/**", "authc");
        return filterChainDefinitionMap;
    }


    @Bean
    public DefaultWebSecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        //设置realm.
        securityManager.setRealm(getMyShiroRealm());
        // 自定义缓存实现 使用redis
        //securityManager.setCacheManager(cacheManager());
        // 自定义session管理 使用redis
        securityManager.setSessionManager(sessionManager());
        return securityManager;
    }

    /**
     * 凭证匹配器
     * @return
     */
    @Bean
    public HashedCredentialsMatcher hashedCredentialsMatcher(){
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();

        hashedCredentialsMatcher.setHashAlgorithmName("md5");
        hashedCredentialsMatcher.setHashIterations(2);//散列的次数，相当于 md5(md5(""));
        return hashedCredentialsMatcher;
    }

    /**
     *  开启shiro aop注解支持.
     * @param securityManager
     * @return
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager){
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }

    /**
     * 配置shiro redisManager
     * 使用的是shiro-redis开源插件
     * @return
     */
    public RedisManager redisManager() {
        RedisManager redisManager = new RedisManager();
        redisManager.setHost(host);
        redisManager.setPassword(password);
        // 配置缓存过期时间
        redisManager.setTimeout(timeout);
        return redisManager;
    }

    /**
     * cacheManager 缓存 redis实现
     * 使用的是shiro-redis开源插件
     * @return
     */
    public RedisCacheManager cacheManager() {
        RedisCacheManager redisCacheManager = new RedisCacheManager();
        redisCacheManager.setRedisManager(redisManager());
        return redisCacheManager;
    }

    /**
     * RedisSessionDAO shiro sessionDao层的实现 通过redis
     */
    @Bean
    public RedisSessionDAO redisSessionDAO() {
        RedisSessionDAO redisSessionDAO = new RedisSessionDAO();
        redisSessionDAO.setRedisManager(redisManager());
        return redisSessionDAO;
    }

    /**
     * shiro session的管理
     */
    @Bean
    public DefaultWebSessionManager sessionManager() {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        sessionManager.setSessionDAO(redisSessionDAO());
        return sessionManager;
    }

}
