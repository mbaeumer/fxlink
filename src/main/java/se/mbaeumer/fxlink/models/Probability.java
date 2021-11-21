package se.mbaeumer.fxlink.models;

public class Probability {
    private String categoryName;
    private double probability;

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public double getProbability() {
        return probability;
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }

    public Probability(String categoryName, double probability) {
        this.categoryName = categoryName;
        this.probability = probability;
    }

    @Override
    public String toString(){
        return this.categoryName + " (" + String.format("%8.5f",this.probability) + ")";
    }
}
