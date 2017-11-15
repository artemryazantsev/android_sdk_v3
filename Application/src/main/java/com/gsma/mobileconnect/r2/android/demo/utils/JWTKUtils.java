package com.gsma.mobileconnect.r2.android.demo.utils;





import io.jsonwebtoken.CompressionCodecs;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;


public class JWTKUtils {

    private final static String KEY = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAKFulDnYtlfDy5dYyy8GnZpWtfu/cwsFjxxGX28M2Q6xy2PJxGbnAr7DfCZHwFPmX+B8NBUKwrouH3DIdOY8r/1HK5SmNkSrwJWhDp5o4f+K96/ywaSwNsV5aiTOfY0HpgyWMyXcTsiYOrbrjrb8f++N/KPxcS9aUjbnFa1JCqKrAgMBAAECgYAy5SAKVM64QNyRLMdyXg2WOTxK0IM+uFP0QCOaaVyNi3RqU5R+NNritdMQZpK2YO1dYwswjyHxfNUgw7JHXF7HZVE3YzDDLIdLJpXSxTkoZZw4euXgujS7/8rdN1nwg2kVwcFFYdCrHaTnpThVmRlhnbrYWnfE7LfZNDqpzk9NwQJBAM9LqAbWWEDTusGJetvN7ebuNOeRZq4xgIyR4euMCMNZZbdhHeelKEbAIKjtaW4UcTqG0YVRrC0jsrrmbezvyHsCQQDHXFW7vuZhVGOPWGtZlj3pqRxlVTfi4QnkqCBdlsyub60OEBDJ5F4aUZwoBFAXRDvA9NeVxsI8KfUxN2ylKq+RAkEAimrZYt9jhL4cmTjkybrjHW139ByZ9LznOnX65h7WhyK9kcqOfmxAzaVi6YnF+ZQ62zrE0wHrNyT8JmLRigG+2wJAZxIS9TU2cN2rd6IUJkYqDlMzbiSH91G3xrMxwJFFk24Df8DInUjnah4FaR8JtW2+ov0zNKmFJIvgy6hHEIGoMQJBAJPHlOHVQbnce7P4mKbHv8n68yM5bCtpj/c4ktf4OR+0211OCy0cdFJMJhuUHGPgpOuEscXw6HTNwJ2GUvXx1ME=";
    public static String encrypt(final String value) {
        return Jwts.builder()
                .setSubject(value)
                .compressWith(CompressionCodecs.DEFLATE)
                .signWith(SignatureAlgorithm.HS256, KEY)
                .compact();
    }

    public static String decrypt( String encryptValue) {
        return Jwts.parser().setSigningKey(KEY).parseClaimsJws(encryptValue).getBody().getSubject();
    }

    public static boolean isValueValid(String value) {
        try {
            decrypt(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
