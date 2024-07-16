package dmit2015.fileio;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileReaderDemo02 {

    public static void main(String[] args) {
        var app = new FileReaderDemo02();
        app.run();
    }

    private void run() {
        Path csvPath = Path.of("/home/user2015/Downloads/Property_Assessment_Data__Current_Calendar_Year__20240715.csv");
// Use a `BufferedReader` when you need to read one line at a time from a file.
        System.out.println("I am reading one line at a time and printing each line using a BufferedReader.");

        try (var reader = new BufferedReader(Files.newBufferedReader(csvPath, StandardCharsets.UTF_8))) {
            // If you are reading a file from a web component (Servlet, Faces) that has a Virtual File System (VFS)
            // then you will need the code below and place your csv file in src/main/resources folder. The path must begin with a forward slash /.
            // var reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/data/csv/electricity-exports-and-imports-data-dictionary.csv")));
            String line;
            String streetNumber = "136 AVENUE NW";
            String houseNumber = "13104";
            boolean foundHouse = false;
            while ( (line = reader.readLine()) != null && !foundHouse) {
                String[] tokens = line.trim().split(",");
                // houseNumber is in index 2
                // streetNumber is index 3
                // assessedValue is in index 8
                // Value should be 346500
                if (tokens[2].equalsIgnoreCase(houseNumber) && tokens[3].equalsIgnoreCase(streetNumber)) {
                    foundHouse = true;
                    System.out.printf("The property assessment value for %s %s is %s\n",
                            tokens[2],
                            tokens[3],
                            tokens[8]);
                }
            }
        } catch(IOException ex) {
            System.out.println(ex.getMessage());
        }


    }
}
