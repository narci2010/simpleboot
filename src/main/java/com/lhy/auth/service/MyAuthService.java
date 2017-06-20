package com.lhy.auth.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.lhy.common.mapper.WxUserMapper;
import com.lhy.common.model.WxUser;
import com.lhy.common.shiro.service.AuthorizationService;
import com.lhy.common.shiro.service.PrincipalService;

/**  
 * Copyright: Copyright (c) 2017 wisedu
 * 
 * @ClassName: MyAuthService.java
 * @Description: 具体权限服务
 *
 * @version: v1.0.0
 * @author: hyluan
 * @date: 2017年5月9日 下午4:12:16 
 *
 * Modification History:
 * Date         Author          Version            Description
 *---------------------------------------------------------*
 * 2017年5月9日     hyluan           v1.0.0               修改原因
 */
@Service
@CacheConfig(cacheNames="role")
public class MyAuthService implements AuthorizationService {
	
		private final Logger logger = LoggerFactory.getLogger(this.getClass());

		@Autowired
		private WxUserMapper userMapper;

		@Override
		@Cacheable
		public List<String> selectRoles(String principal) {
			List<String> roles = new ArrayList<String>();
			//从数据库获取权限并设置
			logger.info("add roles");
			if("admin".equals(principal)){
				roles.add("admin");
				roles.add("vistor");
			}
			return roles;
			
		}

	}
