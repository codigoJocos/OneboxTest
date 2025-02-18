package model;

import java.util.concurrent.ScheduledFuture;

public class Product {
    private int id;
    private String name;
    private String description;
    private int amount;
    private ScheduledFuture countDown;

    public Product() {}

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public int getAmount() {
        return amount;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public ScheduledFuture getCountDown() {
        return countDown;
    }

    public void setCountDown(ScheduledFuture countDown) {
        this.countDown = countDown;
    }

    @Override
    public String toString() {
        return "Product {" +
                " id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +                
                ", amount=" + amount +
                " }";
    }
}
