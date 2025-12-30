package edu.course.eventplanner.model;

import java.util.Objects;

public class Guest {
    private final String name;
    private final String groupTag;
    public Guest(String name, String groupTag) {
        this.name = name;
        this.groupTag = groupTag;
    }
    public String getName() {
        return name;
    }
    public String getGroupTag() {
        return groupTag;
    }
    @Override
    public int hashCode() {
        return Objects.hash(groupTag);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Guest)) return false;
        Guest guest = (Guest) o;
        return Objects.equals(groupTag, guest.groupTag);
    }
}
