package edu.course.eventplanner.service;

import edu.course.eventplanner.model.Venue;
import java.util.*;

public class VenueSelector {
    private final List<Venue> venues;

    Comparator<Venue> priceComparator = Comparator.comparingDouble(Venue::getCost);
    Comparator<Venue> capacityComparator = Comparator.comparingInt(Venue::getCapacity);
    Comparator<Venue> comp = new Comparator<Venue>() {
        @Override
        public int compare(Venue v1, Venue v2) {
            return priceComparator.thenComparing(capacityComparator).compare(v1, v2);
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

        for (Venue venue : venues) {
            if (venue.getCapacity() >= guestCount && venue.getCost() <= budget) validOptions.add(venue);
        }
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












    /*
    private final TreeSet<Venue> venues = new TreeSet<>();
    public VenueSelector(List<Venue> venues) {
        this.venues.addAll(venues); //venues comparable by capacity
    }

    public Venue selectVenue(double budget, int guestCount) {
        Venue smallest = null;
        for (Venue venue : venues) {
            if (venue.getCapacity() < guestCount) break;
            smallest = venue;
        }
        SortedSet<Venue> enoughSeats = venues.subSet(venues.getFirst(), smallest); //venues with enough seats

        SortedSet<Venue> affordable = new TreeSet<>();
        for (Venue venue : enoughSeats) {
            if (venue.getCost() <= budget) affordable.add(venue); //enough seats and withing budget
        }
        if (affordable.isEmpty()) return enoughSeats.last(); //if none affordable,
        return affordable.first(); //or last? which way is it sorted?
    }
    public static List<Venue> getOptions() { return venues; }*/
}

/*TODO
opinjons:
treeset is wrong, need to just make 2 sorted lists (use comparator, not comparable) and return best that's in both, or smallest cap fit if none, or cheaper if tied
 */
