package edu.course.eventplanner.service;

import edu.course.eventplanner.model.*;
import java.util.*;

public class SeatingPlanner {
    private final Venue venue;

    public SeatingPlanner(Venue venue) {
        this.venue = venue;
    }

    public Map<Integer, List<Guest>> generateSeating(List<Guest> guests) {
        // 1. Group guests by their tag: Map<String, Queue<Guest>>
        Map<String, Queue<Guest>> groupedGuests = new LinkedHashMap<>();

        for (Guest guest : guests) {
            String tag = guest.getGroupTag();
            // If the group doesn't exist in map, create a new Queue for it
            groupedGuests.putIfAbsent(tag, new LinkedList<>());
            groupedGuests.get(tag).add(guest);
        }

        // 2. Prepare the seating plan
        Map<Integer, List<Guest>> seatingPlan = new TreeMap<>();
        int currentTableId = 1;
        int tableCapacity = venue.getSeatsPerTable(); // Assuming a standard capacity for this example
        int maxTables = venue.getTables();

        // 3. Process each group from the Map
        for (String tag : groupedGuests.keySet()) {
            Queue<Guest> groupQueue = groupedGuests.get(tag);

            while (!groupQueue.isEmpty()) {
                // Get or create the current table list
                seatingPlan.putIfAbsent(currentTableId, new ArrayList<>());
                List<Guest> currentTable = seatingPlan.get(currentTableId);

                // If table is full and more tables are available, move to the next table
                if (currentTable.size() >= tableCapacity) {
                    if (currentTableId < maxTables) {
                        currentTableId++;
                        // Use continue to jump to the top of the while loop
                        // so the NEW currentTableId is used to create the next list
                        continue;
                    }
                    // if table is full and there are no more tables left, exit the loop.
                    break;
                }

                // Add guest from the group queue to the table
                currentTable.add(groupQueue.poll());
            }
        }

        return seatingPlan;
    }
}