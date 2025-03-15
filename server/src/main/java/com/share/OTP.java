package com.share;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Random;

import com.share.verification.Email;

public class OTP {
    private static Random random = new Random();
    private static String path = OTP.class.getResource("OTP.class").getPath();
    public static String directory_path = path.substring(0, path.lastIndexOf("/")).substring(1);
    
    public static Email EMAIL = new Email();

    public static String getTemplateText(String folder, String filename) {
        String filePath = merge(merge(directory_path, folder), filename);
        StringBuilder contentBuilder = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = br.readLine()) != null) {
                contentBuilder.append(line);
            }    
            br.close();
            return contentBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String merge(String dir, String str) {
        return Paths.get(dir, str).toString();
    }

    public static int generateRandomCode() {
        return random.nextInt(900000) + 100000;
    }
}
