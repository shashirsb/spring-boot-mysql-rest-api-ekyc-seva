package com.oracle.ekyc.utils;

import java.util.Random;

public class Utils {

    public static void main(String[] args) {
        Utils utils = new Utils();
        String randomAlphabet = utils.getRandomAlphabets();
        String nanoTime = utils.getNanoTime();
        System.out.println("SEVA"+randomAlphabet+nanoTime);

    }

    public String getNanoTime() {
        return "" + System.nanoTime();

    }

    public String getRandomAlphabets() {
        Random r = new Random();
        StringBuilder sb = new StringBuilder();

        int count = 6;
        while (count > 0) {
            char c = (char) (r.nextInt(26) + 'A');
            sb.append(c);
            count--;
        }

        return sb.toString();
    }

    public static String randomizeString(String input) {
        char[] _chars = input.toCharArray();

        // Randomize the _chars by swapping with a random index
        for (int i = _chars.length - 1; i > 0; i--) {
            int j = (int) (Math.random() * (i + 1));
            char temp = _chars[i];
            _chars[i] = _chars[j];
            _chars[j] = temp;
        }

        return new String(_chars);
    }

}
