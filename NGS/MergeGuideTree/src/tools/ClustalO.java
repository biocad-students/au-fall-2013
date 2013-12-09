package tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Author: Oleg Yasnev (oyasnev@gmail.com)
 * Date: 09.12.13
 */
public class ClustalO {
    protected String path;

    public ClustalO(String path) {
        this.path = path;
    }

    public void run(String inputFile, String outputFile) {
        try {
            Process process = new ProcessBuilder(path, "-i", inputFile, "-o", outputFile, "--force").start();
            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
