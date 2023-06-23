package com.oracle.ekyc;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class EkycFileReader {

    public InputStream getFileAsIOStream(final String fileName) throws FileNotFoundException {
        InputStream ioStream = new FileInputStream(fileName);

        return ioStream;
    }

    public void printFileContent(InputStream is) throws IOException {
        try (InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
            is.close();
        }
    }
    
}
