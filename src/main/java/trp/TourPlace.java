package trp;

public class TourPlace {
    /* Used in distance cost matrix */
    private long placeId;

    private String placeName;

    private double popularity;

    private int attractionsCount;

    private int quarantinePeriod;

//    private boolean visaRequired;

    public TourPlace(long placeId, String placeName, double popularity, int attractionsCount, int quarantinePeriod) {
        this.placeId = placeId;
        this.placeName = placeName;
        this.popularity = popularity;
        this.attractionsCount = attractionsCount;
        this.quarantinePeriod = quarantinePeriod;
    }

    public long getPlaceId() {
        return placeId;
    }

    public void setPlaceId(long placeId) {
        this.placeId = placeId;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public int getAttractionsCount() {
        return attractionsCount;
    }

    public void setAttractionsCount(int attractionsCount) {
        this.attractionsCount = attractionsCount;
    }

    public int getQuarantinePeriod() {
        return quarantinePeriod;
    }

    public void setQuarantinePeriod(int quarantinePeriod) {
        this.quarantinePeriod = quarantinePeriod;
    }
}
