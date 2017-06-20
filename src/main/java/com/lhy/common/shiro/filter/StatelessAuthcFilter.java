package com.lhy.common.shiro.filter;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.lhy.common.shiro.token.StatelessToken;
import com.lhy.common.shiro.token.manager.TokenManager;
import com.lhy.common.shiro.util.RequestUtil;

/**
 * 无状态授权过滤器 
 * @author luanhy
 *
 */
public class StatelessAuthcFilter extends AccessControlFilter {
	
	
	private final Logger logger = Logger.getLogger(StatelessAuthcFilter.class);
	
	@Autowired
	private TokenManager tokenManager;
	
	public TokenManager getTokenManager() {
		return tokenManager;
	}

	public void setTokenManager(TokenManager tokenManager) {
		this.tokenManager = tokenManager;
	}

	@Override
	protected boolean isAccessAllowed(ServletRequest request,
			ServletResponse response, Object mappedValue) throws Exception {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		logger.info("拦截到的url:" + httpRequest.getRequestURL().toString());
		// 前段token授权信息放在请求头中传入
		String authorization = RequestUtil.newInstance().getRequestHeader(
				(HttpServletRequest) request, "authorization");
		if (StringUtils.isEmpty(authorization)) {
			onLoginFail(response, "请求头不包含认证信息authorization");
			return false;
		}
		// 获取无状态Token
		StatelessToken accessToken = tokenManager.getToken(authorization);
		try {
			// 委托给Realm进行登录
			getSubject(request, response).login(accessToken);
		} catch (Exception e) {
			logger.error("auth error:" + e.getMessage(), e);
			onLoginFail(response, "auth error:" + e.getMessage()); // 6、登录失败
			return false;
		}
		// 通过isPermitted 才能调用doGetAuthorizationInfo方法获取权限信息
		getSubject(request, response).isPermitted(httpRequest.getRequestURI());
		return true;
	}

	@Override
	protected boolean onAccessDenied(ServletRequest request,
			ServletResponse response) throws Exception {
		return false;
	}
	
	 //登录失败时默认返回401状态码  
	  private void onLoginFail(ServletResponse response,String errorMsg) throws IOException {  
	    HttpServletResponse httpResponse = (HttpServletResponse) response;  
	    httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  
	    httpResponse.setContentType("text/html");
	    httpResponse.setCharacterEncoding("utf-8");
	    httpResponse.getWriter().write(errorMsg);  
	    httpResponse.getWriter().close();
	  }  

}
