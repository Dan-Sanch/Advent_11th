package utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileReaderTools {
    private static final String INPUT_FILE_NAME = "resources\\rucksacks.txt";

    public static String[] readFileAsArray(String fileName) {
        List<String> lineList = new ArrayList<>();
        try {
            BufferedReader reader = getBufferedReader(fileName);
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                lineList.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return lineList.toArray(new String[0]);
    }

    public static BufferedReader getBufferedReader(String fileName) throws FileNotFoundException {
        BufferedReader reader;
        FileReader fileReader = new FileReader(fileName);
        reader = new BufferedReader(fileReader);
        return reader;
    }
}
