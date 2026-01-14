package edu.course.eventplanner.service;

import edu.course.eventplanner.model.*;
import java.util.*;

public class SeatingPlanner {
    private final Venue venue;
    private final Map<String, Queue<Guest>> groups = new TreeMap<>();
    private final Map<Integer, List<Guest>> tables = new HashMap<>();

    public SeatingPlanner(Venue venue) { this.venue = venue; }

    public Map<Integer, List<Guest>> generateSeating(List<Guest> guests) {
        int seatsPerTable = venue.getSeatsPerTable();
        for (Guest guest : guests) { //sort guests
            groups.putIfAbsent(guest.getGroupTag(), new LinkedList<>());
            groups.get(guest.getGroupTag()).add(guest);
        }

        for (int i = 0; i < venue.getTables(); i++) {  //create tables
            tables.put(i, new ArrayList<>(seatsPerTable));
        }

        int tableNum = 0;
        for (String group : groups.keySet()) { //for groups that can fill tables, fill tables
            while (groups.get(group).size() >= seatsPerTable) {
                for (int seat = 0; seat < seatsPerTable; seat++) {
                    tables.get(tableNum).add(groups.get(group).poll());
                }
                tableNum++;
            }
        }

        Queue<Guest> stragglers = new LinkedList<>(); //gather stragglers
        for (String group : groups.keySet()) {
            stragglers.addAll(groups.get(group));
        }

        for (;tableNum < venue.getTables(); tableNum++){ //put them at the remaining tables
            for (int seat = 0; seat < seatsPerTable; seat++) {
                if (stragglers.isEmpty()) break;
                tables.get(tableNum).add(stragglers.poll());
            }
        } //not the best way, but they can get up and move if they don't like it

        return tables;
    }
}