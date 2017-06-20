package com.lhy.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.lhy.common.mapper.WxUserMapper;
import com.lhy.common.model.WxUser;
import com.lhy.common.shiro.service.PrincipalService;

@Service
@CacheConfig(cacheNames="wxUser")
public class UserService implements PrincipalService<WxUser>{
	
	@Autowired
	private WxUserMapper userMapper;
	
	@Cacheable
	public WxUser getUserByUserCode(String userCode){
		WxUser user = new WxUser();
		user.setUserCode(userCode);
		return userMapper.selectOne(user);
	}

	@Override
	public WxUser select(String principal) {
		return this.getUserByUserCode(principal);
	}
}
