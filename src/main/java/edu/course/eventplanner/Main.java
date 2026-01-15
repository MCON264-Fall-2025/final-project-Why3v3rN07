package edu.course.eventplanner;

import edu.course.eventplanner.model.*;
import edu.course.eventplanner.service.*;
import edu.course.eventplanner.util.Generators;

import java.util.*;

public class Main {
    static GuestListManager guestListManager = new GuestListManager();
    static TaskManager taskManager = new TaskManager();
    static VenueSelector venueSelector = null;
    static Venue chosenVenue = null;
    static Map<Integer, List<Guest>> seating = null;

    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);
        boolean done = false;
        System.out.println("Event Planner Mini");
        while (!done) {
            System.out.print("""
                    \n  ~~~~ Menu ~~~~
                    0. Load sample guests
                    1. Load sample venues
                    2. Add guest
                    3. Remove guest
                    4. Select venue
                    5. Generate seating chart
                    6. Add preparation task
                    7. Execute next task
                    8. Undo last task
                    9. Print event summary
                    10. Exit
                    
                    Enter your choice:\s""");
            switch (input.nextLine()) {
                case "0": loadSampleGuests(input); break;
                case "1": loadSampleVenues(); break;
                case "2": addGuest(input); break;
                case "3": removeGuest(input); break;
                case "4": selectVenue(input); break;
                case "5": generateSeatingChart(); break;
                case "6": addPreparationTask(input); break;
                case "7": executeNextTask(); break;
                case "8": undoLastTask(); break;
                case "9": printEventSummary(); break;
                case "10": done = true; System.out.println("Goodbye."); break;
                default: invalid();
            }
        }
    }

    static void loadSampleGuests(Scanner input) {
        System.out.print("Number of guests to load: ");
        if (input.hasNextInt()) {
            int numGuests = input.nextInt();
            input.nextLine();
            for (Guest g : Generators.generateGuests(numGuests))
                guestListManager.addGuest(g);
            System.out.println(numGuests + " guests loaded.");
        } else invalid();
    }

    static void loadSampleVenues() {
        venueSelector = new VenueSelector(Generators.generateVenues());
        System.out.println("Venues loaded.");
    }

    static void addGuest(Scanner input) {
        System.out.print("Guest name: ");
        String name = input.nextLine();
        System.out.print("Group tag: ");
        String groupTag = input.nextLine();
        guestListManager.addGuest(new Guest(name, groupTag));
        System.out.println("Guest added.");
    }

    static void removeGuest(Scanner input) {
        System.out.print("Guest name: ");
        String name = input.nextLine();
        if (guestListManager.findGuest(name) != null) {
            guestListManager.removeGuest(name);
            System.out.println("Guest removed.");
        } else System.out.println("Guest not found.");
    }

    static void selectVenue(Scanner input) {
        if (guestListManager.getGuestCount() == 0) {
            System.out.println("No guests loaded; please upload guestlist first.");
            return;
        }
        if (venueSelector == null) {
            System.out.println("No venues loaded; please load venues first.");
            return;
        }
        if (chosenVenue != null) {
            System.out.println("You previously selected this venue: \n" + chosenVenue);
            System.out.print("Would you like to change your selection? (y/n) ");
            String reply = input.nextLine().toLowerCase();
            if (reply.equals("y")) System.out.println("Starting Venue Selector...");
            else if (reply.equals("n")) {
                System.out.println("Continuing with previous selection.");
                return;
            }
            else {
                invalid();
                return;
            }
        }
        System.out.print("What's your budget? $");
        double budget;
        if (input.hasNextDouble()) {
            budget = input.nextDouble();
            input.nextLine();
            Venue best = venueSelector.selectVenue(budget, guestListManager.getGuestCount());
            if (best == null) offerFittingVenues(input);
            else {
                System.out.println("\nBest Option: \n" + best);
                System.out.println("""
                        Is this acceptable?
                        1. Yes, set this as my venue
                        2. No, view all valid options""");
                switch (input.nextLine()) {
                    case "1":
                        chosenVenue = best;
                        System.out.println("Venue set.");
                        break;
                    case "2":
                        List<Venue> validVenues = venueSelector.getOptionsList(budget, guestListManager.getGuestCount());
                        if (validVenues.isEmpty()) {
                            offerFittingVenues(input);
                            break;
                        }
                        for (int i = 0; i < validVenues.size(); i++)
                            System.out.println(i + ")\n" + validVenues.get(i).toString());
                        System.out.print("Which venue would you like to use? (Enter the number) ");
                        if (input.hasNextInt()) {
                            int choice = input.nextInt();
                            input.nextLine();
                            if (choice >= 0 && choice < validVenues.size()){
                                chosenVenue = validVenues.get(choice);
                                System.out.println("Venue set.");
                            } else invalid();
                            break;
                        } 
                        invalid();
                        break;
                    default: invalid();
                }
            }
        } else invalid();
    }

    private static void offerFittingVenues(Scanner input) {
        List<Venue> fittingVenues = venueSelector.getFitting(guestListManager.getGuestCount());
        if (fittingVenues.isEmpty()) {
            System.out.println("Sorry, no venues fitting " + guestListManager.getGuestCount() + " guests found.");
        }
        else {
            System.out.println("Sorry, no venues found that fit your budget. \nAvailable venues with high enough capacity: \n");
            for (int i = 0; i < fittingVenues.size(); i++)
                System.out.println(i + ")\n" + fittingVenues.get(i).toString());
            System.out.print("Which venue would you like to use? (Enter the number) ");
            try {
                chosenVenue = fittingVenues.get(input.nextInt());
                input.nextLine();
                System.out.println("Venue set.");
            } catch (IndexOutOfBoundsException e) {
                invalid();
            }
        }
    }

    static void generateSeatingChart() {
        if (guestListManager.getGuestCount() == 0) System.out.println("No guests loaded; please upload guestlist first.");
        else if (chosenVenue == null) System.out.println("No venue selected; please select a venue first.");
        else {
            SeatingPlanner planner = new SeatingPlanner(chosenVenue);
            seating = planner.generateSeating(guestListManager.getAllGuests());
            printSeating();
        }
    }

    private static void printSeating() {
        if (seating == null) System.out.println("Seating chart not generated.");
        else {
            System.out.println("---Seating Chart---\n");
            for (Integer table : seating.keySet()) {
                System.out.println("\nTable " + table + ":");
                for (Guest guest : seating.get(table))
                    System.out.println(guest.getName() + " (" + guest.getGroupTag() + ")");
            }
            System.out.println("\n");
        }
    }

    static void addPreparationTask(Scanner input) {
        System.out.print("Task description: ");
        taskManager.addTask(new Task(input.nextLine()));
        System.out.println("Task added.");
        System.out.println(taskManager.remainingTaskCount() + " task(s) left to do; " + taskManager.completedTaskCount() + " completed.");
    }

    static void executeNextTask() {
        Task completedTask = taskManager.executeNextTask();
        if (completedTask == null) System.out.println( "No tasks to execute.");
        else System.out.println("Task '" + completedTask.getDescription() + "' completed.");
        System.out.println(taskManager.remainingTaskCount() + " tasks left to do; " + taskManager.completedTaskCount() + " completed.");
    }

    static void undoLastTask() {
        Task undoneTask = taskManager.undoLastTask();
        if (undoneTask == null) System.out.println("No tasks to undo.");
        else System.out.println("Task '"+undoneTask.getDescription()+"' undone.");
        System.out.println(taskManager.remainingTaskCount() + " tasks left to do; " + taskManager.completedTaskCount() + " completed.");
    }

    static void printEventSummary() {
        System.out.println("\n---Event Summary---\n");

        if (chosenVenue != null) System.out.println(chosenVenue);
        else System.out.println("No venue selected.");

        System.out.println("Guests: " + guestListManager.getGuestCount() + "\n");

        printSeating();

        System.out.println("Task Status:");
        System.out.println(taskManager.remainingTaskCount() + " upcoming task(s)");
        System.out.println(taskManager.completedTaskCount() + " task(s) completed");
    }

    private static void invalid() {
        System.out.println("Invalid entry; returning to menu.");
    }
}
