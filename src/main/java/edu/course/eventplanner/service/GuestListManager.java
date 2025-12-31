package edu.course.eventplanner.service;

import edu.course.eventplanner.model.Guest;
import java.util.*;

public class GuestListManager {
    private final LinkedList<Guest> guests = new LinkedList<>();
    private final Map<String, Guest> guestByName = new HashMap<>();

    public void addGuest(Guest guest) {
        guests.add(guest);
        guestByName.put(guest.getName(), guest);
    }
    public boolean removeGuest(String guestName) {
        return guests.remove(guestByName.remove(guestName));
        //hashmap.remove returns null if not found -- what does linkedlist.remove(null) return?
    }
    public Guest findGuest(String guestName) {
        return guestByName.get(guestName);
    }
    public int getGuestCount() {
        return guests.size();
    }
    public List<Guest> getAllGuests() {
        return guests;
    }
}
