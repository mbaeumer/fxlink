package se.mbaeumer.fxlink.models;

public class CategoryCount {
    private String category;
    private int count;

    public String getCategory() {
        return category;
    }

    public int getCount() {
        return count;
    }

    public CategoryCount(String category, int count) {
        this.category = category;
        this.count = count;
    }

    public void increaseCount(){
        count++;
    }
}
