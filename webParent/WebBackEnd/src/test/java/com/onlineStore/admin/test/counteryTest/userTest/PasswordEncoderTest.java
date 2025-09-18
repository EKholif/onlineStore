package com.onlineStore.admin.test.counteryTest.userTest;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class PasswordEncoderTest {

     @Test
    public void testEncodePassword(){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
         String rawPassword = "";
          String encoded = passwordEncoder.encode(rawPassword);
         System.out.println(encoded);
        String encodedPassword = "$2a$10$qTv3DC4oPceCrz3bN96I1.rJYKTpWvssBJGsQAQ2n9acqvjiuRl0q";




        boolean matches = passwordEncoder.matches(rawPassword, encodedPassword);
        assertThat(matches).isTrue();

    }



}
