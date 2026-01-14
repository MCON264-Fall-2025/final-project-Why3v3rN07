package edu.course.eventplanner;
import edu.course.eventplanner.model.*;
import edu.course.eventplanner.service.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


public class EventPlannerTests {

    @Test
    void addGuestIncreasesGuestCount() {
        GuestListManager manager = new GuestListManager();
        assertEquals(0, manager.getGuestCount());

        manager.addGuest(new Guest("Alice", "friend"));
        assertEquals(1, manager.getGuestCount());

        manager.addGuest(new Guest("Bob", "family"));
        assertEquals(2, manager.getGuestCount());
    }

    @Test
    void removeGuestDecreasesGuestCount() {
        GuestListManager manager = new GuestListManager();

        manager.addGuest(new Guest("Alice", "friend"));
        assertEquals(1, manager.getGuestCount());

        manager.removeGuest("Alice");
        assertEquals(0, manager.getGuestCount());
    }

    @Test
    void lookUpGuestReturnsCorrectGuest() {
        GuestListManager manager = new GuestListManager();

        Guest alice = new Guest("Alice", "friend");
        Guest bob = new Guest("Bob", "family");

        manager.addGuest(alice);
        manager.addGuest(bob);

        assertEquals(alice, manager.findGuest("Alice"));
        assertEquals(bob, manager.findGuest("Bob"));
    }

    @Test
    void selectsVenueWithinBudget() {
        VenueSelector selector = new VenueSelector(List.of(
                new Venue("Community Hall",1500,40,5,8),
                new Venue("Garden Hall",2500,64,8,8),
                new Venue("Warehouse Event Space",2500,90,15,6),
                new Venue("Grand Ballroom",5000,120,15,8),
                new Venue("Museum Atrium",10000,120,12,10)
        ));

        Venue venue1 = selector.selectVenue(7500, 120);
        assertEquals("Grand Ballroom", venue1.getName());

        Venue venue2 = selector.selectVenue(1000, 100);
        assertNull(venue2);

        Venue venue3 = selector.selectVenue(20000, 100);
        assertEquals("Grand Ballroom", venue3.getName());

        Venue venue4 = selector.selectVenue(3000, 60);
        assertEquals("Garden Hall", venue4.getName());
    }

    @Test
    void seatsGuestsByGroup() {
        SeatingPlanner planner = new SeatingPlanner(new Venue("Test Hall", 500, 25, 5, 5));
        List<Guest> guests = List.of(
                new Guest("Alicia", "family"),
                new Guest("Barbara", "family"),
                new Guest("Caroline", "family"),
                new Guest("Daria", "friends"),
                new Guest("Evelyn", "coworkers"),
                new Guest("Frankie", "friends"),
                new Guest("Grace", "coworkers"),
                new Guest("Harriet", "coworkers"),
                new Guest("Isabella", "coworkers"),
                new Guest("Janelle", "friends"),
                new Guest("Kaitlin", "coworkers"),
                new Guest("Lisa", "friends"),
                new Guest("Maryanne", "friends"),
                new Guest("Nancy", "friends"),
                new Guest("Olivia", "family"),
                new Guest("Patricia", "family"),
                new Guest("Queenie", "family"),
                new Guest("Roslyn", "friends"),
                new Guest("Shelia", "family")

        );
        Map<Integer, List<Guest>> seating = planner.generateSeating(guests);

        assertEquals(5, seating.size());
        assertEquals(5, seating.get(1).size());

        for (Guest guest : seating.get(0)) assertEquals("coworkers", guest.getGroupTag());
        for (Guest guest : seating.get(1)) assertEquals("family", guest.getGroupTag());
        for (Guest guest : seating.get(2)) assertEquals("friends", guest.getGroupTag());
        assertEquals(seating.get(2).get(0).getGroupTag(), seating.get(2).get(1).getGroupTag());
    }

    @Test
    void executesAndUndoesTasks() {
        TaskManager manager = new TaskManager();
        manager.addTask(new Task("Task 1"));
        manager.addTask(new Task("Task 2"));
        manager.addTask(new Task("Task 3"));

        assertEquals(3, manager.remainingTaskCount());

        manager.executeNextTask();
        assertEquals(2, manager.remainingTaskCount());

        manager.undoLastTask();
        assertEquals(3, manager.remainingTaskCount());
    }
}
