package com.lhy.api;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.lhy.common.model.WxUser;
import com.lhy.common.shiro.service.PrincipalService;
import com.lhy.common.shiro.token.StatelessToken;
import com.lhy.common.shiro.token.manager.TokenManager;
import com.lhy.common.shiro.util.RequestUtil;


@RestController
@RequestMapping("/login")
public class LoginController{
	
	@Autowired
	private PrincipalService<WxUser> principalService;
	
	@Autowired
	private TokenManager tokenManager;
	
	protected static final Logger logger = LoggerFactory.getLogger(LoginController.class);
	
	@RequestMapping(value = "",method = RequestMethod.POST)
	public StatelessToken login(String userCode, String password) {
		logger.info("userCode:"+userCode);
		WxUser usr = principalService.select(userCode);
		if (usr == null) {
			return new StatelessToken(userCode, "valid user");
		}
		if(!password.equals(usr.getPwd())){
			return new StatelessToken(userCode, "valid user password");
		}
		StatelessToken createToken = tokenManager.createToken(userCode);
		return createToken;
	}
	
	@RequestMapping(value = "/logout",method = RequestMethod.GET)
	public String logout(HttpServletRequest request) {
		String authorization = RequestUtil.newInstance().getRequestHeader(request,"authorization");
		StatelessToken token = tokenManager.getToken(authorization);
		if(token!= null){
			tokenManager.deleteToken(token.getUserCode());
		}
		SecurityUtils.getSubject().logout();
		logger.info("用户登出");
		return "logout success";
	}
	
	
}