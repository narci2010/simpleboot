package com.lhy.config;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.Filter;

import net.sf.ehcache.CacheManager;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.authc.AnonymousFilter;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.lhy.auth.service.MyAuthService;
import com.lhy.common.shiro.filter.StatelessAuthcFilter;
import com.lhy.common.shiro.realm.StatelessRealm;
import com.lhy.common.shiro.service.PrincipalService;
import com.lhy.common.shiro.subject.StatelessDefaultSubjectFactory;
import com.lhy.common.shiro.token.helper.EhCacheUserTokenHelper;
import com.lhy.common.shiro.token.manager.TokenManager;
import com.lhy.common.shiro.token.manager.impl.DefaultTokenManagerImpl;

/**
 * shiro配置
 * @author luanhy
 *
 */
@Configuration
public class ShiroConfig {
     
     private static final Logger logger = LoggerFactory.getLogger(ShiroConfig.class);
     
     /**
      * token管理类
      * @param cacheManager
      * @param bootProperties
      * @return
      */
     @Bean
     public TokenManager tokenManager(CacheManager cacheManager,BootProperties bootProperties){
          logger.info("ShiroConfig.getTokenManager()");
          //默认的token管理实现类 32位uuid
          DefaultTokenManagerImpl tokenManager = new DefaultTokenManagerImpl();
          //token失效时间
          tokenManager.setExpirateTime(bootProperties.getExpirateTime());
         
          //用户token委托给ehcache管理
          EhCacheUserTokenHelper ehCacheUserTokenHelper = new EhCacheUserTokenHelper();
          ehCacheUserTokenHelper.setCacheManager(cacheManager);
          tokenManager.setUserTokenOperHelper(ehCacheUserTokenHelper);
          
//          RaFilterJwtTokenManagerImpl tokenManager1 = new RaFilterJwtTokenManagerImpl();
//          JwtUtil jwtUtil = new JwtUtil();
//          jwtUtil.setProfiles(bootProperties.getKey());
//          tokenManager1.setJwtUtil(jwtUtil);
//          tokenManager1.setExpirateTime(bootProperties.getExpirateTime());
//          EhCacheLoginFlagHelper ehCacheLoginFlagHelper = new EhCacheLoginFlagHelper();
//          ehCacheLoginFlagHelper.setCacheManager(cacheManager);
//          tokenManager1.setLoginFlagOperHelper(ehCacheLoginFlagHelper);
//          tokenManager1.setUserTokenOperHelper(ehCacheUserTokenHelper);

          return tokenManager;
     }
     
     /**
      * 无状态域
      * @param tokenManager
      * @param principalService 登陆账号服务需要实现PrincipalService接口
      * @param authorizationService 授权服务 需要实现authorizationService接口
      * @return
      */
     @Bean
     public StatelessRealm statelessRealm(TokenManager tokenManager,@Qualifier("userService") PrincipalService principalService,MyAuthService authorizationService){
          logger.info("ShiroConfig.getStatelessRealm()");
          StatelessRealm realm = new StatelessRealm();
          realm.setTokenManager(tokenManager);
          realm.setPrincipalService(principalService);
          realm.setAuthorizationService(authorizationService);
          return realm;
     }
     
     /**
      * 会话管理类 禁用session
      * @return
      */
     @Bean
     public DefaultSessionManager defaultSessionManager(){
          logger.info("ShiroConfig.getDefaultSessionManager()");
          DefaultSessionManager manager = new DefaultSessionManager();
          manager.setSessionValidationSchedulerEnabled(false);
          return manager;
     }
     
     /**
      * 安全管理类
      * @param statelessRealm
      * @return
      */
     @Bean
     public DefaultWebSecurityManager defaultWebSecurityManager(StatelessRealm statelessRealm){
          logger.info("ShiroConfig.getDefaultWebSecurityManager()");
          DefaultWebSecurityManager manager = new DefaultWebSecurityManager();
          
          //禁用sessionStorage
          DefaultSubjectDAO de = (DefaultSubjectDAO)manager.getSubjectDAO();
          DefaultSessionStorageEvaluator defaultSessionStorageEvaluator =(DefaultSessionStorageEvaluator)de.getSessionStorageEvaluator();
          defaultSessionStorageEvaluator.setSessionStorageEnabled(false);
          
          manager.setRealm(statelessRealm);
          
          //无状态主题工程，禁止创建session
          StatelessDefaultSubjectFactory statelessDefaultSubjectFactory = new StatelessDefaultSubjectFactory();
          manager.setSubjectFactory(statelessDefaultSubjectFactory);
          
          manager.setSessionManager(defaultSessionManager());
          //设置了SecurityManager采用使用SecurityUtils的静态方法 获取用户等
          SecurityUtils.setSecurityManager(manager);
          return manager;
     }
     
     /**
      * Shiro生命周期处理
      * @return
      */
     @Bean
     public LifecycleBeanPostProcessor lifecycleBeanPostProcessor(){
          logger.info("ShiroConfig.getLifecycleBeanPostProcessor()");
          return new LifecycleBeanPostProcessor();
     }
     
     
     /**
      * 身份验证过滤器
      * @param manager
      * @param tokenManager
      * @return
      */
     @Bean
     public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager manager,TokenManager tokenManager){
          logger.info("ShiroConfig.getShiroFilterFactoryBean()");
          ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean();
          bean.setSecurityManager(manager);
          Map<String,Filter> filters = new HashMap<String,Filter>();
          //无需增加 shiro默认会添加该filter
          //filters.put("anon", anonymousFilter()); 
          
          //无状态授权过滤器 
         //特别注意！不能声明为bean 否则shiro无法管理该filter生命周期，该过滤器会执行其他过滤器拦截过的路径
          StatelessAuthcFilter statelessAuthcFilter = statelessAuthcFilter(tokenManager);
          filters.put("statelessAuthc", statelessAuthcFilter);
          bean.setFilters(filters);
          //注意是LinkedHashMap 保证有序
          Map<String, String> filterChainDefinitionMap = new LinkedHashMap<String, String>();
          
          //1， 相同url规则，后面定义的会覆盖前面定义的(执行的时候只执行最后一个)。
          //2， 两个url规则都可以匹配同一个url，只执行第一个
          filterChainDefinitionMap.put("/html/**", "anon");
          filterChainDefinitionMap.put("/resource/**", "anon");
          filterChainDefinitionMap.put("/login", "anon");
          filterChainDefinitionMap.put("/login/**", "anon");
          filterChainDefinitionMap.put("/favicon.ico", "anon");
          filterChainDefinitionMap.put("/**", "statelessAuthc");
          
          bean.setFilterChainDefinitionMap(filterChainDefinitionMap);
          
          //字符串方式创建过滤链 \n换行 
//        String s = "/resource/**=anon\n/html/**=anon\n/login/**=anon\n/login=anon\n/**=statelessAuthc";
//        bean.setFilterChainDefinitions(s);
          return bean;
     }
     
   /**
    *   
    * @Function: ShiroConfig::anonymousFilter
    * @Description: 该过滤器无需增加 shiro默认会添加该filter
    * @return
    * @version: v1.0.0
    * @author: hyluan
    * @date: 2017年5月8日 下午5:39:10 
    *
    * Modification History:
    * Date         Author          Version            Description
    *-------------------------------------------------------------
    */
   public AnonymousFilter anonymousFilter(){
        logger.info("ShiroConfig.anonymousFilter()");
        return new AnonymousFilter();
   }
   
   /**
    * 
    * @Function: ShiroConfig::statelessAuthcFilter
    * @Description:  无状态授权过滤器 注意不能声明为bean 否则shiro无法管理该filter生命周期，<br>
    * 				  该过滤器会执行其他过滤器拦截过的路径
    * @param tokenManager
    * @return
    * @version: v1.0.0
    * @author: hyluan
    * @date: 2017年5月8日 下午5:38:55 
    *
    * Modification History:
    * Date         Author          Version            Description
    *-------------------------------------------------------------
    */
   public StatelessAuthcFilter statelessAuthcFilter(TokenManager tokenManager){
       logger.info("ShiroConfig.statelessAuthcFilter()");
       StatelessAuthcFilter statelessAuthcFilter = new StatelessAuthcFilter();
       statelessAuthcFilter.setTokenManager(tokenManager);
       return statelessAuthcFilter;
  }
   
   
     
     
}
