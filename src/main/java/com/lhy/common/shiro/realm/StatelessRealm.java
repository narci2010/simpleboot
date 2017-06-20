package com.lhy.common.shiro.realm;

import java.util.List;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import com.lhy.common.shiro.service.AuthorizationService;
import com.lhy.common.shiro.service.PrincipalService;
import com.lhy.common.shiro.token.StatelessToken;
import com.lhy.common.shiro.token.manager.TokenManager;

public class StatelessRealm extends AuthorizingRealm {
	
	private TokenManager tokenManager;
	
	@SuppressWarnings("rawtypes")
	private PrincipalService principalService;
	
	@Override
	public boolean supports(AuthenticationToken token) {
		return token instanceof StatelessToken;
	}
	
	private AuthorizationService authorizationService;

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		 //根据用户名查找角色，请根据需求实现  
		String userCode = (String) principals.getPrimaryPrincipal(); 
		SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
		List<String> selectRoles = authorizationService.selectRoles(userCode);
		authorizationInfo.addRoles(selectRoles);
		return authorizationInfo;
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(
			AuthenticationToken token) throws AuthenticationException {
		StatelessToken statelessToken = (StatelessToken)token;
		
		String userCode = (String)statelessToken.getPrincipal();
		
		checkUserExists(userCode);
		
		String credentials = (String)statelessToken.getCredentials();
		boolean checkToken = tokenManager.checkToken(statelessToken);
		if (checkToken) {
			return new SimpleAuthenticationInfo(userCode, credentials, super.getName());
		}else{
			throw new AuthenticationException("token认证失败");
		}
	}
	
	private void checkUserExists(String userCode) throws AuthenticationException {
		Object principal = principalService.select(userCode);
		if(principal == null){
			throw new UnknownAccountException("userCode "+userCode+" wasn't in the system");
		}
	}
	
	public TokenManager getTokenManager() {
		return tokenManager;
	}

	public void setTokenManager(TokenManager tokenManager) {
		this.tokenManager = tokenManager;
	}
	
	@SuppressWarnings("rawtypes")
	public PrincipalService getPrincipalService() {
		return principalService;
	}

	@SuppressWarnings("rawtypes")
	public void setPrincipalService(PrincipalService principalService) {
		this.principalService = principalService;
	}

	public AuthorizationService getAuthorizationService() {
		return authorizationService;
	}

	public void setAuthorizationService(AuthorizationService authorizationService) {
		this.authorizationService = authorizationService;
	}

}
