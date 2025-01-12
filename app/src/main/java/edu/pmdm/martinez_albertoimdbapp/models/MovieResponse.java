package edu.pmdm.martinez_albertoimdbapp.models;

public class MovieResponse {
    private Data data;

    public Data getData() {
        return data;
    }

    public static class Data {
        private Title title;

        public Title getTitle() {
            return title;
        }
    }

    public static class Title {
        private TitleText titleText;
        private ReleaseYear releaseYear;
        private PrimaryImage primaryImage;
        private RatingsSummary ratingsSummary;
        private Plot plot; // Campo adicional para el plot

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

        public Plot getPlot() {
            return plot;
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

    // Clase para mapear el campo "plot"
    public static class Plot {
        private PlotText plotText;

        public PlotText getPlotText() {
            return plotText;
        }
    }

    // Clase para mapear "plotText"
    public static class PlotText {
        private String plainText;

        public String getPlainText() {
            return plainText;
        }
    }
}