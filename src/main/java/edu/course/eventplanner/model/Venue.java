package edu.course.eventplanner.model;

import java.text.DecimalFormat;

public class Venue {
    private final String name;
    private final double cost;
    private final int capacity;
    private final int tables;
    private final int seatsPerTable;
    public Venue(String name, double cost, int capacity, int tables, int seatsPerTable) {
        this.name = name;
        this.cost = cost;
        this.capacity = capacity;
        this.tables = tables;
        this.seatsPerTable = seatsPerTable;
    }
    public String getName() { return name; }
    public double getCost() { return cost; }
    public int getCapacity() { return capacity; }
    public int getTables() { return tables; }
    public int getSeatsPerTable() { return seatsPerTable; }

    DecimalFormat d = new DecimalFormat("$0,000.00");
    public String toString() {
        return
        "Venue: " + name + "\n" +
        "Cost: " + d.format(cost) + "\n" +
        "Capacity:  " + capacity + "\n" +
        "Tables: " + tables + "\n" +
        "Seats per table: " + seatsPerTable + "\n";
    }


}
