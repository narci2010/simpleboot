package com.lhy.common.shiro.token.manager.impl;

import io.jsonwebtoken.Claims;

import org.apache.shiro.authc.AuthenticationException;

import com.lhy.common.shiro.token.StatelessToken;
import com.lhy.common.shiro.token.manager.AbstractTokenManager;
import com.lhy.common.shiro.util.JwtUtil;

/**
 * 简单的JwtTokenManager 服务端生成token发送给客户端 ，客户端自己保存token
 * 无法防范repear attack 但是需要客户端知道共享密钥,
 * @author Administrator
 *
 */
public class SimpleJwtTokenManagerImpl extends AbstractTokenManager{
	
	private JwtUtil jwtUtil;
	
	public JwtUtil getJwtUtil() {
		return jwtUtil;
	}

	public void setJwtUtil(JwtUtil jwtUtil) {
		this.jwtUtil = jwtUtil;
	}
	
	@Override
	public String createStringToken(String userCode) {
		try {
			// userCode仅表示该用户登陆过 
			return jwtUtil.createJWT(null, userCode, -1);
		} catch (Exception e) {
			logger.error("token创建失败",e);
		}
		return null;
	}
	
	@Override
	public boolean check(String authentication) {
		StatelessToken accessToken = super.getToken(authentication);
		if(accessToken == null){
			return false;
		}
		return this.checkToken(accessToken);
	}
	
	@Override
	public boolean checkToken(StatelessToken accessToken) {
		Claims parseJWT = parseClaims(accessToken);
		checkStatelessTokenUserCode(parseJWT, accessToken);
		return super.checkMemoryToken(accessToken);
	}
	
	private void checkStatelessTokenUserCode(Claims parseJWT,StatelessToken accessToken){
		String subject = parseJWT.getSubject();
		if(!accessToken.getUserCode().equals(subject)){
			throw new AuthenticationException("token:所属用户和签名不一致"+accessToken.getToken());
		}
	}
	
	private Claims parseClaims(StatelessToken accessToken){
		Claims parseJWT = null;
		try {
			parseJWT = jwtUtil.parseJWT(accessToken.getToken());
		} catch (Exception e) {
			throw new AuthenticationException("token:解析失败:"+accessToken.getToken(),e);
		}
		return  parseJWT;
	}
	

}
