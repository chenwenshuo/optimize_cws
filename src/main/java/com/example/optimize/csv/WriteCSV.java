package com.example.optimize.csv;

import org.apache.commons.csv.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class WriteCSV {
    public static void main(String[] args) {
        try {
            FileWriter writer = new FileWriter("output.csv");
            CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("Name", "Age"));

                //csvPrinter.printRecord(person.getName(), person.getAge());
            csvPrinter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}