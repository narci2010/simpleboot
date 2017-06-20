package com.lhy.common.shiro.token.manager.impl;

import java.util.UUID;

import com.lhy.common.shiro.token.StatelessToken;
import com.lhy.common.shiro.token.manager.AbstractTokenManager;

/**
 * 默认token管理实现类
 * @author luanhy
 *
 */
public class DefaultTokenManagerImpl extends AbstractTokenManager{
	
	@Override
	public String createStringToken(String userCode) {
		//创建简易的32为uuid
		return UUID.randomUUID().toString().replace("-", "");
	}

	@Override
	public boolean checkToken(StatelessToken model) {
		return super.checkMemoryToken(model);
	}


}
