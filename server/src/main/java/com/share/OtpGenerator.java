package com.share;

import java.util.Random;

public class OtpGenerator {
    private static Random random = new Random();
    
    public static int generateCode() {
        return random.nextInt(900000) + 100000;
    }

}
