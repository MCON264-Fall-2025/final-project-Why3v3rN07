package edu.course.eventplanner;
import edu.course.eventplanner.model.*;
import edu.course.eventplanner.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class EventPlannerTests {

    @Nested
    class GuestListTests {

        @Test
        void getGuestCountReturnsCorrectCount() {
            GuestListManager manager = new GuestListManager();
            assertEquals(0, manager.getGuestCount());

            manager.addGuest(new Guest("Alice", "friend"));
            assertEquals(1, manager.getGuestCount());

            manager.addGuest(new Guest("Bob", "family"));
            assertEquals(2, manager.getGuestCount());

            manager.removeGuest("Bob");
            assertEquals(1, manager.getGuestCount());

            manager.removeGuest("Alice");
            assertEquals(0, manager.getGuestCount());
        }

        @Test
        void addGuestAddsGuest() {
            GuestListManager manager = new GuestListManager();
            assertEquals(0, manager.getGuestCount());

            manager.addGuest(new Guest("Alice", "friend"));
            assertEquals(1, manager.getGuestCount());

            manager.addGuest(new Guest("Bob", "family"));
            assertEquals(2, manager.getGuestCount());
        }

        @Test
        void removeGuestRemovesGuest() {
            GuestListManager manager = new GuestListManager();

            assertFalse(manager.removeGuest("Alice"));

            manager.addGuest(new Guest("Alice", "friend"));
            manager.addGuest(new Guest("Bob", "family"));
            assertEquals(2, manager.getGuestCount());

            assertFalse(manager.removeGuest("Carol"));

            assertTrue(manager.removeGuest("Alice"));
            assertEquals(1, manager.getGuestCount());
            assertNull(manager.findGuest("Alice"));

            assertNotNull(manager.findGuest("Bob"));

            assertFalse(manager.removeGuest("Alice"));
        }

        @Test
        void findGuestReturnsCorrectGuest() {
            GuestListManager manager = new GuestListManager();

            Guest alice = new Guest("Alice", "friend");
            Guest bob = new Guest("Bob", "family");
            manager.addGuest(alice);
            manager.addGuest(bob);

            assertEquals(alice, manager.findGuest("Alice"));
            assertEquals(bob, manager.findGuest("Bob"));
            assertNull(manager.findGuest("Carol"));
        }

        @Test
        void getAllGuestsReturnsAllGuests() {
            GuestListManager manager = new GuestListManager();
            List<Guest> guests = List.of(
                    new Guest("Alice", "friend"),
                    new Guest("Bob", "family"),
                    new Guest("Carol", "family")
            );
            for (Guest guest : guests) manager.addGuest(guest);
            List<Guest> allGuests = manager.getAllGuests();
            assertEquals(allGuests, guests);
            assertEquals(guests.size(), allGuests.size());
            assertTrue(allGuests.containsAll(guests));
        }

    }

    @Nested
    class VenueSelectionTests {
        VenueSelector selector;

        @BeforeEach
        void setUp() {
            selector = new VenueSelector(List.of(
                    new Venue("Warehouse Event Space", 2500, 90, 15, 6),
                    new Venue("Museum Atrium", 10000, 120, 12, 10),
                    new Venue("Community Hall", 1500, 40, 5, 8),
                    new Venue("Garden Hall", 2500, 64, 8, 8),
                    new Venue("Grand Ballroom", 5000, 120, 15, 8)
            ));
        }

        @Test
        void getOptionsSortsVenuesCorrectly() {
            ArrayList<Venue> sorted_venues = selector.getOptionsList(Integer.MAX_VALUE, 1);
            assertEquals(5, sorted_venues.size());

            for (int i = 0; i < sorted_venues.size()-1; i++) {
                assertTrue(sorted_venues.get(i).getCost() <= sorted_venues.get(i+1).getCost());
                assertTrue(sorted_venues.get(i).getCapacity() <= sorted_venues.get(i+1).getCapacity());
            }

            assertEquals("Community Hall", sorted_venues.getFirst().getName());
            assertEquals("Museum Atrium", sorted_venues.getLast().getName());
        }

        @Test
        void getOptionsReturnsAllValidVenues() {
            SortedSet<Venue> list1 = selector.getOptions(Integer.MAX_VALUE, 1);
            assertEquals(5, list1.size());
            assertEquals("Community Hall", list1.getFirst().getName());

            SortedSet<Venue> list2= selector.getOptions(5000, 100);
            assertEquals(1, list2.size());
            assertEquals("Grand Ballroom", list2.getFirst().getName());

            SortedSet<Venue> list3 = selector.getOptions(5000, 50);
            assertEquals(3, list3.size());
            assertEquals("Garden Hall", list3.getFirst().getName());
        }

        @Test
        void getOptionsReturnsEmptyWhenNoValidVenues() {
            SortedSet<Venue> list4 = selector.getOptions(10000, 500);
            assertEquals(0, list4.size());

            SortedSet<Venue> list5 = selector.getOptions(10, 10);
            assertEquals(0, list5.size());
        }

        @Test
        void getOptionsReturnsEmptySetWhenNoValidVenues() {
            SortedSet<Venue> list = selector.getOptions(0, 1000);
            assertEquals(0, list.size());
        }

        @Test
        void getFittingReturnsAllWithEnoughCapacity() {
            List<Venue> fittingVenues1 = selector.getFitting(100);
            assertEquals(2, fittingVenues1.size());
            assertEquals("Grand Ballroom", fittingVenues1.getFirst().getName());

            List<Venue> fittingVenues2 = selector.getFitting(20);
            assertEquals(5, fittingVenues2.size());
            assertEquals("Community Hall", fittingVenues2.getFirst().getName());
        }

        @Test
        void getFittingReturnsEmptyListWhenNoValidVenues() {
            List<Venue> fittingVenues = selector.getFitting(10000);
            assertEquals(0, fittingVenues.size());
        }

        @Test
        void selectsVenueWithinBudget() {
            Venue venue1 = selector.selectVenue(7500, 120);
            assertEquals("Grand Ballroom", venue1.getName());

            Venue venue3 = selector.selectVenue(20000, 100);
            assertEquals("Grand Ballroom", venue3.getName());

            Venue venue4 = selector.selectVenue(3000, 60);
            assertEquals("Garden Hall", venue4.getName());
        }

        @Test
        void selectVenueReturnsNullWhenNoValidVenues() {
            Venue venue1 = selector.selectVenue(100000, 10000);
            assertNull(venue1);

            Venue venue2 = selector.selectVenue(10, 10);
            assertNull(venue2);
        }

        @Test
        void selectVenueSelectsMinNeededCapacity() {
            Venue venue1 = selector.selectVenue(10000, 100);
            assertEquals("Grand Ballroom", venue1.getName());

            Venue venue2 = selector.selectVenue(10000, 12);
            assertEquals("Community Hall", venue2.getName());

            Venue venue3 = selector.selectVenue(10000, 50);
            assertEquals("Garden Hall", venue3.getName());
        }

    }

    @Nested
    class SeatingPlannerTests {
        SeatingPlanner planner;
        List<Guest> guests;
        Venue venue;
        @BeforeEach
        void setUp() {
            venue = new Venue("Test Hall", 500, 30, 6, 5);
            planner = new SeatingPlanner(venue);
            guests = List.of(
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
        }

        @Test
        void usesCorrectNumberOfTables() {
            Map<Integer, List<Guest>> seating = planner.generateSeating(guests);
            assertEquals(6, seating.size());

            SeatingPlanner planner2 = new SeatingPlanner(new Venue("Test Hall 2", 500, 20, 10, 2));
            Map<Integer, List<Guest>> seating2 = planner2.generateSeating(guests);
            assertEquals(10, seating2.size());
        }

        @Test
        void putsCorrectNumberOfGuestsPerTable() {
            Map<Integer, List<Guest>> seating = planner.generateSeating(guests);
            assertEquals(5, seating.get(0).size());

            for (int i = 0; i < venue.getCapacity()/guests.size(); i++) assertEquals(5, seating.get(i).size());
        }

        @Test
        void seatsGuestsByGroup() {
            Map<Integer, List<Guest>> seating = planner.generateSeating(guests);

            for (Guest guest : seating.get(0)) assertEquals("coworkers", guest.getGroupTag());
            for (Guest guest : seating.get(1)) assertEquals("family", guest.getGroupTag());
            for (Guest guest : seating.get(2)) assertEquals("friends", guest.getGroupTag());
            assertEquals(seating.get(2).get(0).getGroupTag(), seating.get(2).get(1).getGroupTag());
        }

        @Test
        void returnsEmptyChartIfNoGuests() {
            Map<Integer, List<Guest>> seating = planner.generateSeating(new ArrayList<>());
            assertTrue(seating.isEmpty());
        }

    }

    @Nested
    class TaskManagerTests {

        @Test
        void addTask() {
            TaskManager manager = new TaskManager();
            assertEquals(0, manager.remainingTaskCount());
            assertEquals(0, manager.completedTaskCount());

            manager.addTask(new Task("Task 1"));
            assertEquals(1, manager.remainingTaskCount());
            assertEquals(0, manager.completedTaskCount());

            manager.addTask(new Task("Task 2"));
            assertEquals(2, manager.remainingTaskCount());
            assertEquals(0, manager.completedTaskCount());
        }

        @Test
        void executeTasks() {
            TaskManager manager = new TaskManager();

            assertEquals(0, manager.remainingTaskCount());
            assertEquals(0, manager.completedTaskCount());
            assertNull(manager.executeNextTask());

            manager.addTask(new Task("Task 1"));
            manager.addTask(new Task("Task 2"));
            manager.addTask(new Task("Task 3"));

            assertEquals(3, manager.remainingTaskCount());
            assertEquals(0, manager.completedTaskCount());

            assertEquals("Task 1", manager.executeNextTask().getDescription());
            assertEquals(2, manager.remainingTaskCount());
            assertEquals(1, manager.completedTaskCount());

            manager.executeNextTask();

            assertEquals("Task 3", manager.executeNextTask().getDescription());
            assertEquals(0, manager.remainingTaskCount());
            assertEquals(3, manager.completedTaskCount());

            assertNull(manager.executeNextTask());
        }

        @Test
        void undoTasks() {
            TaskManager manager = new TaskManager();

            assertNull(manager.undoLastTask());
            assertEquals(0, manager.remainingTaskCount());
            assertEquals(0, manager.completedTaskCount());

            manager.addTask(new Task("Task 1"));
            manager.addTask(new Task("Task 2"));
            manager.addTask(new Task("Task 3"));

            assertEquals(3, manager.remainingTaskCount());
            assertEquals(0, manager.completedTaskCount());

            manager.executeNextTask();
            manager.executeNextTask();
            assertEquals(1, manager.remainingTaskCount());
            assertEquals(2, manager.completedTaskCount());

            assertEquals("Task 2", manager.undoLastTask().getDescription());
            assertEquals(2, manager.remainingTaskCount());
            assertEquals(1, manager.completedTaskCount());
            assertEquals("Task 1", manager.undoLastTask().getDescription());
            assertEquals(3, manager.remainingTaskCount());
            assertEquals(0, manager.completedTaskCount());

            assertNull(manager.undoLastTask());
            assertEquals(3, manager.remainingTaskCount());
            assertEquals(0, manager.completedTaskCount());

            manager.executeNextTask();
            assertEquals("Task 3", manager.undoLastTask().getDescription());
        }
    }
    /*
    @Nested
    class InterfaceTests {
        //am I meant to mock Main?
    }
     */
}
