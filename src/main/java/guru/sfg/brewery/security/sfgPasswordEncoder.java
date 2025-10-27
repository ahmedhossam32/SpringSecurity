package guru.sfg.brewery.security;


import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.*;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;

import java.util.HashMap;
import java.util.Map;

public class sfgPasswordEncoder
{
    public static PasswordEncoder createDelegatingPasswordEncoder() {
        String encodingId = "bcrypt15";
        Map<String, PasswordEncoder> encoders = new HashMap();
        encoders.put("bcrypt", new BCryptPasswordEncoder());   // old users
        encoders.put(encodingId, new BCryptPasswordEncoder(15));
        encoders.put("ldap", new LdapShaPasswordEncoder());
        encoders.put("noop", NoOpPasswordEncoder.getInstance());
        encoders.put("SHA-256", new MessageDigestPasswordEncoder("SHA-256"));
        encoders.put("sha256", new StandardPasswordEncoder());
        return new DelegatingPasswordEncoder(encodingId, encoders);
    }

    private sfgPasswordEncoder() {
    }
}
