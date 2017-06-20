package com.lhy.common.shiro.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.util.Base64Utils;

public class JwtUtil {
	
    private String profiles;
	
	public String getProfiles() {
		return profiles;
	}

	public void setProfiles(String profiles) {
		this.profiles = profiles;
	}

	/**
	 * 由字符串生成加密key
	 * @return
	 */
	public SecretKey generalKey(){
		String stringKey = profiles;
		byte[] encodedKey = Base64Utils.decodeFromString(stringKey);
	    SecretKey key = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
	    return key;
	}

	/**
	 * 创建jwt
	 * @param id
	 * @param subject
	 * @param ttlMillis
	 * @return
	 * @throws Exception
	 */
	public String createJWT(String id, String subject, long ttlMillis) throws Exception {
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
		String key = Base64Utils.encodeToString(profiles.getBytes());
		long nowMillis = System.currentTimeMillis();
		JwtBuilder builder = Jwts
				.builder()
				.setHeaderParam("typ", "JWT")
				.setHeaderParam("alg", "HS256")
				.setIssuer("lhy")
				.setIssuedAt(new Date(nowMillis))
				.setSubject(subject)
				.signWith(signatureAlgorithm,key);
		if (ttlMillis >= 0) {
		    long expMillis = nowMillis + ttlMillis;
		    Date exp = new Date(expMillis);
		    builder.setExpiration(exp);
		}
		return builder.compact();
	}
	
	/**
	 * 解密jwt
	 * @param jwt
	 * @return
	 * @throws Exception
	 */
	public Claims parseJWT(String jwt) throws Exception{
		String key = Base64Utils.encodeToString(profiles.getBytes());
		Claims claims = Jwts.parser().setSigningKey(key)      
		   .parseClaimsJws(jwt).getBody();
		return claims;
	}
	
	public static void main(String[] args) {
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
		String stringKey = "hmackey";
		JwtBuilder builder = Jwts.builder()
				.setHeaderParam("typ","JWT")
				.setHeaderParam("alg", "HS256")
			.setIssuer("joe")
		    .signWith(signatureAlgorithm, Base64Utils.encodeToString(stringKey.getBytes()));
		System.out.println(builder.compact());
	}
	
}
