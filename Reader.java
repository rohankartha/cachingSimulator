package org.example;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Reader {
    public String fileName;
    public String line;
    List<String> lines = new ArrayList<>();
    BufferedReader bufferedReader;

    public Reader (String fileName) throws FileNotFoundException {
        this.fileName = fileName;
        bufferedReader = new BufferedReader(new FileReader(fileName));
    }

    public void read() throws IOException {

        while ((line = bufferedReader.readLine()) != null){
            lines.add(line);
        }
    }








}
