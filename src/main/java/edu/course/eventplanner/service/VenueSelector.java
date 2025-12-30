package edu.course.eventplanner.service;

import edu.course.eventplanner.model.Venue;
import java.util.*;

public class VenueSelector {
    private final List<Venue> venues;
    private List<Venue> validVenues= new ArrayList<>();
    public VenueSelector(List<Venue> venues) {
        this.venues = venues;
    }
    public Venue selectVenue(double budget, int guestCount) {
        //add the valid venues to the validVenues list
        for(Venue v:venues){
            if(v.getCost() <= budget && v.getCapacity()>= guestCount )
                validVenues.add(v);
        }
        // Bubble Sort Algorithm
        for (int i = 0; i < validVenues.size() - 1; i++) {
            for (int j = 0; j < validVenues.size() - i - 1; j++) {

                Venue left = validVenues.get(j);
                Venue right = validVenues.get(j + 1);
                boolean shouldSwap = false;

                // Rule 1: Is the right one cheaper?
                if (right.getCost() < left.getCost()) {
                    shouldSwap = true;
                }
                // Rule 2: If costs are tied, is the right one a smaller capacity?
                else if (right.getCost() == left.getCost()) {
                    if (right.getCapacity() < left.getCapacity()) {
                        shouldSwap = true;
                    }
                }

                // The Swap
                if (shouldSwap) {
                    validVenues.set(j, right);
                    validVenues.set(j + 1, left);
                }
            }
        }
        if (validVenues.isEmpty()) {
            return null;
        }
        return validVenues.get(0); // The winner!
    }
}
