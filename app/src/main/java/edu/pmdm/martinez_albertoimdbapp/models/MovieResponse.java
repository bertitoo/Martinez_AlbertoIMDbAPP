package edu.pmdm.martinez_albertoimdbapp.models;

public class MovieResponse {
    private Data data;

    public Data getData() {
        return data;
    }

    public static class Data {
        private Title title;
        private Description description;

        public Title getTitle() {
            return title;
        }

        public Description getDescription() {
            return description;
        }
    }

    public static class Title {
        private TitleText titleText;
        private ReleaseYear releaseYear;
        private PrimaryImage primaryImage;
        private RatingsSummary ratingsSummary;

        public TitleText getTitleText() {
            return titleText;
        }

        public ReleaseYear getReleaseYear() {
            return releaseYear;
        }

        public PrimaryImage getPrimaryImage() {
            return primaryImage;
        }

        public RatingsSummary getRatingsSummary() {
            return ratingsSummary;
        }
    }

    public static class TitleText {
        private String text;

        public String getText() {
            return text;
        }
    }

    public static class ReleaseYear {
        private int year;

        public int getYear() {
            return year;
        }
    }

    public static class PrimaryImage {
        private String url;

        public String getUrl() {
            return url;
        }
    }

    public static class RatingsSummary {
        private double aggregateRating;

        public double getAggregateRating() {
            return aggregateRating;
        }
    }

    public static class Description {
        private Value value;

        public Value getValue() {
            return value;
        }
    }

    public static class Value {
        private String plainText;

        public String getPlainText() {
            return plainText;
        }
    }
}