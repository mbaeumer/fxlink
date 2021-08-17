package se.mbaeumer.fxlink.models;

public class CategoryCount {
    private String category;
    private int count;

    public void setCategory(String category) {
        this.category = category;
    }

    public void setCount(int count) {
        this.count = count;
    }

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
