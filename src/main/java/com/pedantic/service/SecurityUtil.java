package com.pedantic.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.apache.shiro.authc.credential.PasswordService;
import org.apache.shiro.codec.CodecSupport;
import org.apache.shiro.codec.Hex;
import org.apache.shiro.crypto.AesCipherService;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha512Hash;
import org.apache.shiro.util.ByteSource;

import javax.crypto.spec.SecretKeySpec;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.sound.midi.Soundbank;
import javax.xml.registry.infomodel.User;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RequestScoped
public class SecurityUtil {
	
    private final PasswordService passwordService = new DefaultPasswordService();

    @Inject
    private QueryService queryService;


    public String encryptText(String plainText) {
        return passwordService.encryptPassword(plainText);
    }

//    public boolean passwordsMatch(String plainTextPassword, String encryptedPassword) {
//        return passwordService.passwordsMatch(plainTextPassword, encryptedPassword);
//    }

    public Key generateKey(String keyString) {
         return new SecretKeySpec(keyString.getBytes(), 0, keyString.getBytes().length, "DES");
    }

    public boolean authenticateUser(String email, String password) {
        return queryService.authenticateUser(email, password);

    }

    public Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public boolean passwordsMatch(String dbStoredHashedPassword, String saltText, String clearTextPassword) {
        ByteSource salt = ByteSource.Util.bytes(Hex.decode(saltText));
        String hashedPassword = hashAndSaltPassword(clearTextPassword, salt);
        return hashedPassword.equals(dbStoredHashedPassword);
    }

    public Map<String, String> hashPassword(String clearTextPassword) {
        ByteSource salt = getSalt();

        Map<String, String> credMap = new HashMap<>();
        credMap.put("hashedPassword", hashAndSaltPassword(clearTextPassword, salt));
        credMap.put("salt", salt.toHex());
        return credMap;


    }

    private String hashAndSaltPassword(String clearTextPassword, ByteSource salt) {
        return new Sha512Hash(clearTextPassword, salt, 2000000).toHex();
    }

    private ByteSource getSalt() {
        return new SecureRandomNumberGenerator().nextBytes();
    }


}