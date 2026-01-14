package edu.course.eventplanner.util;

import edu.course.eventplanner.model.*;
import java.util.*;

public class Generators {
    public static List<Venue> generateVenues() {
        return List.of(
            new Venue("Community Hall",1500,40,5,8),
            new Venue("Garden Hall",2500,64,8,8),
            new Venue("Warehouse Event Space",2500,90,15,6),
            new Venue("Grand Ballroom",5000,120,15,8),
            new Venue("Museum Atrium",10000,120,12,10)
        );
    }
    public static List<Guest> generateGuests(int n) {
        List<Guest> guests = new ArrayList<>();
        String[] groups = {"family","friend","neighbor","coworker"};
        for(int i=1;i<=n;i++){
            guests.add(new Guest("Guest"+i, groups[i%groups.length]));
        }
        return guests;
    }
}
