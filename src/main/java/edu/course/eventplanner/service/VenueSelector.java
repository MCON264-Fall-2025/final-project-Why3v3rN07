package edu.course.eventplanner.service;

import edu.course.eventplanner.model.Venue;
import java.util.*;

public class VenueSelector {
    private final List<Venue> venues;

    Comparator<Venue> priceComparator = Comparator.comparingDouble(Venue::getCost);
    Comparator<Venue> capacityComparator = Comparator.comparingInt(Venue::getCapacity);
    Comparator<Venue> nameComparator = Comparator.comparing(Venue::getName); //tie-breaker
    Comparator<Venue> comp = new Comparator<Venue>() {
        @Override
        public int compare(Venue v1, Venue v2) {
            return priceComparator.thenComparing(capacityComparator).thenComparing(nameComparator).compare(v1, v2);
        }
    };

    public VenueSelector(List<Venue> venues) {
        this.venues = venues;
    }

    public Venue selectVenue(double budget, int guestCount) {
        SortedSet<Venue> options = getOptions(budget, guestCount);
        if (options.isEmpty()) return null;
        else return options.first();
    }

    public SortedSet<Venue> getOptions(double budget, int guestCount) {
        SortedSet<Venue> validOptions = new TreeSet<>(comp);
        for (Venue venue : venues)
            if (venue.getCapacity() >= guestCount && venue.getCost() <= budget) validOptions.add(venue);
        return validOptions;
    }

    public ArrayList<Venue> getOptionsList(double budget, int guestCount) {
        return new ArrayList<>(getOptions(budget, guestCount));
    }

    public ArrayList<Venue> getFitting(int guestCount) {
        SortedSet<Venue> fittingOptions = new TreeSet<>(comp);
        for (Venue venue : venues) {
            if (venue.getCapacity() >= guestCount) fittingOptions.add(venue);
        }
        return new ArrayList<>(fittingOptions);
    }
}