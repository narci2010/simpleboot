package com.lhy.common.shiro.util;

import java.util.List;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.lhy.common.shiro.service.AuthorizationService;

@Component
public class AuthUtil {
	
	@Autowired
	@Qualifier("myAuthService")
	private AuthorizationService authorizationService;
	
	public List<String> getRoles(){
		String userCode = getUserCode();
		return authorizationService.selectRoles(userCode);
	}
	
	public String getUserCode(){
		Subject subject = SecurityUtils.getSubject();
		return subject.getPrincipal().toString();
	}
 	
}
