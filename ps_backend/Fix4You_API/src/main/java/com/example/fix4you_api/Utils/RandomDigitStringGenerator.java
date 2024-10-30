package com.example.fix4you_api.Utils;

import java.util.Random;

public class RandomDigitStringGenerator {
    public static String generateRandomDigitString() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(5);

        for (int i = 0; i < 5; i++) {
            int digit = random.nextInt(10); // generates a digit from 0 to 9
            sb.append(digit);
        }

        return sb.toString();
    }
}
