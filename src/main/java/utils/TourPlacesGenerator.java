package utils;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import trp.TourPlace;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TourPlacesGenerator {

    private FileReader fileReader;

    private CSVReader csvReader;

    public TourPlacesGenerator(String filename) throws FileNotFoundException {
        this.fileReader = new FileReader(filename);
        this.csvReader = new CSVReaderBuilder(fileReader)
                .withSkipLines(1)
                .build();
    }

    public List<TourPlace> getTourPlaces() throws IOException {
        List<String[]> placeData = csvReader.readAll();
        List<TourPlace> tourPlaces = new ArrayList<>();
        long placeCount = 0;

        for (String[] placeDatum : placeData) {
            TourPlace tourPlace = new TourPlace(
                    placeCount,
                    placeDatum[1],
                    Double.parseDouble(placeDatum[2]),
                    Integer.parseInt(placeDatum[3]),
                    Integer.parseInt(placeDatum[4])
            );
            tourPlaces.add(tourPlace);
            ++placeCount;
        }

        return tourPlaces;
    }

    public void close() throws IOException {
        csvReader.close();
        fileReader.close();
    }
}
