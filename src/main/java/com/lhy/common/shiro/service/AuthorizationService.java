package com.lhy.common.shiro.service;

import java.util.List;

/**  
 * Copyright: Copyright (c) 2017 wisedu
 * 
 * @ClassName: AuthorizationService.java
 * @Description: 查询角色
 *
 * @version: v1.0.0
 * @author: hyluan
 * @date: 2017年5月9日 下午4:10:17 
 *
 * Modification History:
 * Date         Author          Version            Description
 *---------------------------------------------------------*
 * 2017年5月9日     hyluan           v1.0.0               修改原因
 */
public interface AuthorizationService {
	
	List<String> selectRoles(String principal);
}

