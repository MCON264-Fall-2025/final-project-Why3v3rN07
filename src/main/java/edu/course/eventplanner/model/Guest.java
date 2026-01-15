package edu.course.eventplanner.model;

public class Guest implements Comparable<Guest>{
    @Override
    public int compareTo(Guest other) {
        return this.groupTag.compareTo(other.groupTag);
    }
    private final String name;
    private final String groupTag;
    public Guest(String name, String groupTag) {
        this.name = name;
        this.groupTag = groupTag;
    }
    public String getName() { return name; }
    public String getGroupTag() { return groupTag; }
}
