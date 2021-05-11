package se.mbaeumer.fxlink.models;

public class Suggestion {
    private String category;
    private int count;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Suggestion(String category, int count) {
        this.category = category;
        this.count = count;
    }

    public void increaseCount(){
        count++;
    }

    @Override
    public String toString() {
        return "Suggestion{" +
                "category='" + category + '\'' +
                ", count=" + count +
                '}';
    }
}
