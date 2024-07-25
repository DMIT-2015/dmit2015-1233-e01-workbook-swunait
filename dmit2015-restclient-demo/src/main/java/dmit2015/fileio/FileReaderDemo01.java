package dmit2015.fileio;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FileReaderDemo01 {

    public static void main(String[] args) {
        var app = new FileReaderDemo01();
        app.run();
    }

    public void run() {
        Path csvPath = Path.of("/home/user2015/Downloads/E-Scooter_Locations_20240715.csv");
        // Use the `Files.readAllLines()` method to read all lines from a file as a List.
        System.out.println("I am reading all the lines from the csv file into a list of String.");
        List<String> allLines = null;
        try {
            allLines = Files.readAllLines(csvPath);
            System.out.println("I am printing each line read from the list.");
            allLines.forEach(System.out::println);
//            allLines.forEach(item -> System.out.println(item));
            System.out.println("\n\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
