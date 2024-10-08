package com.onlineStore.admin.test.userTest;

import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class PasswordEncoderTest {

     @Test
    public void testEncodePassword(){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String rawPassword = "1";
        String encodedPassword = "'$2a$10$1pu/o2S4kwAQ3aFWvIAW9eEnbVgOq0/SebsHLe7BsyZYJ9mSOdRpG'";




        boolean matches = passwordEncoder.matches(rawPassword, encodedPassword);
        assertThat(matches).isTrue();

    }



}
