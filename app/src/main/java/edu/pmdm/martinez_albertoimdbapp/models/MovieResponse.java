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
        private RatingsSummary ratingsSummary;
        private PrimaryImage primaryImage;

        public TitleText getTitleText() {
            return titleText;
        }

        public ReleaseYear getReleaseYear() {
            return releaseYear;
        }

        public RatingsSummary getRatingsSummary() {
            return ratingsSummary;
        }

        public PrimaryImage getPrimaryImage() {
            return primaryImage;
        }
    }

    public static class TitleText {
        private String text;

        public String getText() {
            return text;
        }
    }

    public static class ReleaseYear {
        private String year;

        public String getYear() {
            return year;
        }
    }

    public static class RatingsSummary {
        private String aggregateRating;

        public String getAggregateRating() {
            return aggregateRating;
        }
    }

    public static class PrimaryImage {
        private String url;

        public String getUrl() {
            return url;
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
