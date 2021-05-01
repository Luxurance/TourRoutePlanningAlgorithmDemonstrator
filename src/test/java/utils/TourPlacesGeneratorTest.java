package utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import trp.TourPlace;

import java.io.*;
import java.util.List;

public class TourPlacesGeneratorTest {
    private static final String testFilePath = "test.csv";
    private static TourPlacesGenerator generator;
    private static File testFile;

    @BeforeAll
    static void init() throws IOException {
        testFile = new File(testFilePath);
        if (!testFile.exists()) testFile.createNewFile();

        FileWriter writer = new FileWriter(testFile);
        /* skipped line */
        writer.write(", , , , \n");
        /* test data */
        writer.write(",A,1,2,3\n");
        writer.write(",B,4,5,6\n");
        writer.write(",C,7,8,9\n");
        writer.close();

    }

    @Test
    void testGetTourPlacesSize() throws IOException {
        generator = new TourPlacesGenerator(testFilePath);
        List<TourPlace> places = generator.getTourPlaces();
        assertEquals(3, places.size());
        generator.close();
    }

    @Test
    void testGetTourPlacesProperties() throws IOException {
        generator = new TourPlacesGenerator(testFilePath);
        List<TourPlace> places = generator.getTourPlaces();
        assertEquals(4, places.get(1).getPopularity());
        assertEquals(5, places.get(1).getAttractionsCount());
        assertEquals(6, places.get(1).getQuarantinePeriod());
        generator.close();
    }

    @AfterAll
    static void end() {
        if (testFile.exists()) testFile.delete();
    }
}
