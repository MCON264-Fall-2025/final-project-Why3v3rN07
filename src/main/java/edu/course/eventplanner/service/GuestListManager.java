package edu.course.eventplanner.service;

import edu.course.eventplanner.model.Guest;
import java.util.*;

public class GuestListManager<E> {
    private final LinkedList<E> guests = new LinkedList<>();
    private final Map<String, E> guestByName = new HashMap<>(); // Used for Fast Lookup
    private int size;

    public void addGuest(E guest) {
        int i;
        for(i = 0; i < guests.size(); i++)
            if(guests.get(i).equals(guest))
                break;

        if (i == 0) {
            guests.addFirst(guest);
            size++;
        } else if (i == size) {
            guests.addLast(guest);
            size++;
        } else {
            guests.add(i, guest);
            size++;
        }

        // Store in map so we can find it fast later
        String name;
        if (guest instanceof Guest) {
            name = ((Guest) guest).getName();
        } else {
            name = guest.toString();
        }
        guestByName.put(name, guest);
    }

    public boolean removeGuest(String guestName) {

        // Use the map to find the object first
        E toRemove = guestByName.get(guestName);
        if (toRemove == null) {
            return false; // Not in the map, so not in the list
        }

        int j;
        for(j = 0; j < guests.size(); j++) {
            if(guests.get(j).equals(toRemove)) {
                break;
            }
        }

        if (j >= guests.size()) {
            return false;
        }

        if(j == 0) {
            guests.removeFirst();
            size--;
        } else {
            guests.remove(j);
            size--;
        }

        // Remove from map to keep it in sync
        guestByName.remove(guestName);
        return true;
    }

    public E findGuest(String guestName) {
        // This is O(1) instead of looping through the whole list
        return guestByName.get(guestName);
    }

    public int size() {
        return size;
    }

    public int getGuestCount() {
        return guests.size();
    }

    public List<E> getAllGuests() {
        return guests;
    }
}