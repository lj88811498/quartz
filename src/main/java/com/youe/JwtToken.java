package com.youe;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Monkey
 * @Date: Created in 14:39  2018/9/25.
 * @Description:
 */
public class JwtToken {
    public static String createToken() throws Exception{

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("alg", "HS256");
        map.put("typ", "JWT");
        String token = JWT.create()
                .withHeader(map)//header
                .withClaim("name", "monkey1")//payload
                .withClaim("age", "18")
                .sign(Algorithm.HMAC256("secret123"));//加密
        return token;
    }

    public static void verifyToken(String token,String key) throws Exception{
        //解密
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(key))
                .build();
        DecodedJWT jwt = verifier.verify(token);

        Map<String, Claim> claims = jwt.getClaims();
        System.out.println(claims.get("name").asString());
    }

    public static void main(String[] args) throws Exception{
        String token = createToken();
        System.out.println(token);
        verifyToken(token, "secret123");
    }
}
