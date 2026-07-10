/** Student Name: Jonathan Matters 
 * Student ID: 3718743 
 * Date: 2024-07-24 
 * Assignment #3 
 * Program Name: Greenhouse.java, 
 * Description: This program runs a greenhouse simulation. It reads a plan file for a list of events to be scheduled and executed. 
 * */

/** 
 * Purpose: 
 * - The purpose of this program is to simulate a greenhouse environment by scheduling and executing various events based on the plan file. The program allows for dynamic scheduling of events, handling of failures, and user interaction for restarting or quitting the simulation.
 * - This class is designed to be the main entry point for the greenhouse simulation. It is the orchestrator that reads the plan file, schedules events, and manages the lifecycle of the simulation.
 * 
 * Code behaviours: 
 * - The class: 
 *      - imports necessary libraries for file reading, data structures, and user input handling.
 *      - implements the Runnable interface, allowing it to run on a separate thread.
 *      - defines a DataEntry class to hold event data while reading the plan file.
 *      - 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * Particulars: 
 * Compiling and executing the program:
 * including example input data if required. 
 * 
 * Classes:
 *   with descriptions. 
 * Instance Variables: 
 *   with descriptions.
 */

/**
 *The third comment block is the test plan. At its core, a test plan simply tells another person how the program actually worked during testing. In cases where there is no input, there is still often output, so you can still show exactly how the program should function. In programs with input, you can also describe various test cases, including those where incorrect data (or no data) is entered and what output should be expected. Finally, the test plan is a place to discuss limitations of your program and things that could be done to improve it. 
 */

 import java.io.*;  // Importing to use BufferedReader and FileReader for reading the plan file. Also importing IOException for handling file reading exceptions.
 import java.util.*; // Importquiting to use Set, HashSet, List, ArrayList, Map, HashMap, and Scanner for various data structures and user input handling.

public class Greenhouse implements Runnable {  // Implementing Runnable interface to allow the greenhouse to run on one thread and a separeate thread to listen for user commands to run at the same time.
    private final static Set<String> disabledClasses = new HashSet<>(); // Set of classes that can be disabled. This is used to prevent scheduling of events that have failed or are not needed.

    private static class DataEntry { // Creating a class to hold the data for each event in the plan file.
        String type;  // Will hold the type of the event (event, test, failed, priority).
        String className;  // will hold the class name of the event to be scheduled (Bell, Thermostat, etc.).
        long initialDelay;  // Will hold the initial delay before the event is scheduled.
        long recurringDelay;  // Will hold the recurring delay for the event. If 0, the event is non-recurring.
        int priority;  // Will hold the priority of the event. Lower numbers indicate higher priority.
        boolean disabled; // Flag to indicate if the event is disabled. If true, the event will not be scheduled.
    }

    private final String planFile;  // Will hold the name of the plan file to be read. Passed in through the constructor.
    private final List<DataEntry> plan = new ArrayList<>();  // Will hold the list of DataEntry objects that represent the events to be scheduled. Populated by reading the plan file.
    private final Map<String, Integer> priorities = new HashMap<>();  // Will hold class names and their priorities. Populated by reading the plan file.
    private final List<Event> activeEvents = new ArrayList<>();  // Will hold the list of active events that have been scheduled and are currently running. Used to stop all events on restart.
    private Thread failureThread; // Will hold the thread that is responsible for scheduling a failure event. Used to stop the failure thread on restart.

    public Greenhouse(String planFile) { // Constructor that takes the name of the plan file as an argument. This allows the user to specify a different plan file if desired in a future iteration.
        this.planFile = planFile;
    }

    @Override // Overriding the run method from the Runnable interface. This is the entry point for the thread when it is started.
    public void run() {
        start();
    }

    private void readPlan() throws IOException {  // Method to read the plan file and create the greenhouse plan and priorities data structure.

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(planFile))) { // Using try-with-resources to automatically close the BufferedReader when done.
            String line;  // Will hold each entry in the plan file as a seperate entry that will be parsed and added to the plan data structure.

            while ((line = bufferedReader.readLine()) != null) {  // read each line of the plan file and skip empty lines.
                line = line.trim(); // Trim whitespace so that if there is or is not whitespace the data is still readable.
                if (line.isEmpty()) continue;

                String[] entryTypeSplit = line.split("="); // split the line to get the entry type. 

                String entryType = entryTypeSplit[0].trim().toLowerCase(); // get entry type and standardize to lower case in case user enters a different case.
                String restOfEntry = entryTypeSplit[1].trim(); // remove whitspace from the rest of the entry so that if user entry spaces do not affect readability.

                DataEntry entry = new DataEntry(); // Create a new DataEntry object to hold the data for this process.
                entry.type = entryType;

                switch (entryType) {  // Will sort the entries by type and apply the approriate logic to populate the DataEntry obeject and add it to the appropriate data structure.
                    case "priority" -> { // priority entries will only have class name and priority, so this splits it and adds it to the priorities map. So, that the thread can access them for scheduling priorities.
                        String[] priorityRestofEntry = restOfEntry.split(",");
                        priorities.put(priorityRestofEntry[0].trim(),
                                Integer.valueOf(priorityRestofEntry[1].trim()));
                    }
                    case "event" -> { // Will sort the event entries by class name, initial delay, and recurring delay. It will also check if the event is disabled and add it to the plan list.
                        String[] eventRestofEntry = restOfEntry.split(",");
                        entry.className = eventRestofEntry[0].trim();
                        entry.initialDelay = Long.parseLong(eventRestofEntry[1].trim());
                        if (eventRestofEntry[2].trim().equals("*")) { /// Use '*' to indicate the event is non recurring
                            entry.recurringDelay = 0L; // changes the recurring delay to 0 so that the event is non-recurring.
                        } else {
                            entry.recurringDelay = Long.parseLong(eventRestofEntry[2].trim()); // otherwise, takes the  recurring delay as entered.
                        }
                        entry.priority = resolvePriority(entry.className); // resolve the priority for this event based on the class name and the priorities map. So that the thread will give appropriate priority to the event when scheduling it.
                        entry.disabled = disabledClasses.contains(entry.className); // check if the class is disabled and set the disabled flag accordingly. So that the thread will not schedule disabled events. So that events that failed in prior runs are not scheduled.
                        plan.add(entry); // Add the entry to the plan list so that it can be scheduled when the greenhouse is started.
                    }
                    case "test" -> { // will sort the test entries by class name and initial delay (as tests only run once). It will also check if the event is disabled and add it to the plan list.
                        String[] testRestofEntry = restOfEntry.split(",");
                        entry.className = testRestofEntry[0].trim();
                        entry.initialDelay = Long.parseLong(testRestofEntry[1].trim());
                        entry.recurringDelay = 0; // tests are non-recurring, so set the recurring delay to 0.
                        entry.priority = resolvePriority(entry.className); // resolve the priority for this event based on the class name and the priorities map. So that the thread will give appropriate priority to the event when scheduling it.
                        entry.disabled = disabledClasses.contains(entry.className); // check if the class is disabled and set the disabled flag accordingly. So that the thread will not schedule disabled events. So that events that failed in prior runs are not scheduled.
                        plan.add(entry); // Add the entry to the plan list so that it can be scheduled when the greenhouse is started.
                    }
                    case "failed" -> { // 
                        String[] failedRestofEntry = restOfEntry.split(","); // will sort the failed entries by class name and initial delay (as failed events only run once). It will also check if the event is disabled and add it to the plan list.
                        entry.className = failedRestofEntry[0].trim();
                        entry.initialDelay = Long.parseLong(failedRestofEntry[1].trim());
                        entry.recurringDelay = 0; // failed events are non-recurring, so set the recurring delay to 0.
                        entry.priority = resolvePriority(entry.className); // resolve the priority for this event based on the class name and the priorities map. So that the thread will give appropriate priority to the event when scheduling it.
                        entry.disabled = disabledClasses.contains(entry.className); // check if the class is disabled and set the disabled flag accordingly. So that the thread will not schedule disabled events. So that events that failed in prior runs are not scheduled.
                        plan.add(entry); // Add the entry to the plan list so that it can be scheduled when the greenhouse is started.
                    }
                    default -> System.out.println("Unknown Event type detected. Event ignored: " + entryType); // If the entry type is not recognized, print a message to the console and ignore the line. This allows the user to add comments or other non-event lines to the plan file without causing errors.
                }
            }
        }
        System.out.println("Plan Loaded " + plan.size() + " events. " + disabledClasses.size() + " classes disabled due to previous failures."); // Print a message indicating how many events were loaded and how many classes were disabled. This provides feedback to the user about the state of the greenhouse plan, to help the user identify any unexpected behaviors. 
    }

    private int resolvePriority(String className) { // Method to resolve the priority for a given class name. It checks to see if a * priority is set and changes it to the default priority of 10.
        if (priorities.containsKey(className)) return priorities.get(className);
        if (priorities.containsKey("*")) return priorities.get("*");
        return 10;
    }

    public void start() { // Method to start the greenhouse. 
        Event.hasFailed = false;     // Reset failure flag in all classes so that everything starts cleanly after restart
        plan.clear();                     // Clear the plan so we can reload it fresh on restart

        try {  // calls readPlan method to populate the plan and priorities data structure fresh on each run.
            readPlan();
        } catch (IOException e) { // If there is an error reading the plan throws an error so that user can fix the plan file without having to call a technician.
            throw new RuntimeException("Failed to read greenhouse plan: " + planFile, e);
        }

        for (DataEntry entry : plan) { // Goes through each entry in the plan to check if an entry was disabled on a prior run. If it was diasbled it will not add it to the schedule.
            if (entry.disabled) {
                System.out.println(entry.className + " is disabled and will not be scheduled.");
                continue;
            }
            if (entry.type.equals("failed")) { // creates a seperate thread to schedule failure events so that the greenhouse can continue to run other events while waiting for the failure event to occur. This allows the greenhouse to continue to run other events while waiting for the failure event to occur.
                scheduleFailure(entry);
                continue;
            }
            if (entry.type.equals("event") || entry.type.equals("test")) { // creates a new thread for each event or test entry in the plan. It uses reflection to create an instance of the event class and starts the thread. If there is an error creating the event, it marks the event as disabled and restarts the greenhouse.
                try {
                    Class<?> clazz = Class.forName(entry.className); // Load the class dynamically, so that the greenhouse can schedule any event class without having to hardcode it. This allows the user to add new events to the plan file without having to modify the greenhouse code or recompile.
                    Event event = (Event) clazz.getDeclaredConstructor(long.class, long.class, int.class) 
                                               .newInstance(entry.initialDelay, entry.recurringDelay, entry.priority); // Create a new instance of the event class using reflection, so that the greenhouse can schedule any event class without having to hardcode it. This allows the user to add new events to the plan file without having to modify the greenhouse code or recompile.
                    activeEvents.add(event); // Add the event to the list of active events so that it can be stopped on restart. This allows the greenhouse to stop all events on restart without having to keep track of them individually.
                    event.start(); // Start the event thread so that it can run concurrently with other events. This allows the greenhouse to run multiple events concurrently without having to wait for one event to finish before starting the next.
                } catch (Exception e) {
                    entry.disabled = true;               // Mark the event as disabled if there is an error creating the event, so that it will not be scheduled in future runs.
                    disabledClasses.add(entry.className); // This creates a list of disabled classes so that the greenhouse can skip scheduling them in future runs. 
                    System.out.println(entry.className + " encountered an error. Restarting greenhouse and skipping " + entry.className + " in future runs."); // provides a print out so that the user can see which event failed and why the greenhouse is restarting. This allows the user to understand what is happening and take appropriate action.

                    for (Event event : activeEvents) { // Stop all active events before restarting the greenhouse as required by the instructions. This allows the greenhouse to stop all events on restart without having to keep track of them individually.
                        event.stopEvent();
                    }
                    activeEvents.clear(); // Clear the list of active events so that it can be repopulated from scratch with the new events on restart. The disabled events list will not be cleared as they are stored in a seperate object.

                    if (failureThread != null && failureThread.isAlive()) { // Stop the failure thread if it is running, so that it does not interfere with the restart process or continue into the next cycle.
                        failureThread.interrupt();
                    }
                    start(); // Restart the greenhouse so that the restart happens automatically without user intervention upon an error reading the entries as required by the assignment.
                    return; // Return to exit the current run method and prevent further execution of the current thread. This ensures that the greenhouse restarts cleanly without any lingering threads or events from the previous run.
                }
            }
        }
    }

private void scheduleFailure(DataEntry entry) { // Method to schedule a failure event.
    failureThread = new Thread(() -> { //creates a new failure thread
        try {
            Thread.sleep(entry.initialDelay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }
        Event.hasFailed = true; //Sets the hasFailed flag to true so that other events can check this flag and stop or take appropriate action if necessary.

            Bell bell = new Bell(0L, entry.priority); // creates and rings the bell 5 times to indicate a failure.
            for (int i = 0; i < 5; i++) {
                bell.action(); 
            }
            System.out.println(entry.className + " failed."); // provides the user with a print out of what failed so that they can take appropriate action. This allows the user to understand what is happening and take appropriate action.

        });
    failureThread.start(); // starts the failure thread so that it can run concurrently with other events.
    }

    public void restart() { // Method to restart the greenhouse. It stops all active events, clears the list of active events, interrupts the failure thread if it is running, and then calls the start method to restart the greenhouse. This allows the greenhouse to restart cleanly without any lingering threads or events from the previous run.
        System.out.println("Stopping all processes.");  //notify user that all process will stop so that they are aware of what is happening so they can identify expected behaviors.
        for (Event event : activeEvents) { // cycles through the list of active events and stops each one. This allows the greenhouse to stop all events on restart without having to keep track of them individually.
            event.stopEvent(); 
        }
        activeEvents.clear();  // Clear the list of active events so that it can be repopulated from scratch with the new events on restart. The disabled events list will not be cleared as they are stored in a seperate object. Remeber this is the process called for both an automatic restart and a user requented one.

        if (failureThread != null && failureThread.isAlive()) { // Stop the failure thread if it is running, so that it does not interfere with the restart process or continue into the next cycle.
            failureThread.interrupt();
        }

        System.out.println("Restarting Greenhouse..."); // notify user that the greenhouse is restarting so that they are aware of what is happening so they can identify expected behaviors.
        start(); // Restart the greenhouse
    }

    public void userCommand() { // Method to listen for user commands to restart or quit the greenhouse. It uses a Scanner to read user input from the console and checks for the commands "restart" or "quit". If the command is recognized, it calls the appropriate method to restart or quit the greenhouse. If the command is not recognized, it prints a message to the console indicating that the command is unknown and provides instructions for valid commands. This allows the user to control the greenhouse without having to modify the code or recompile.
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("If you wish to restart the greenhouse, type 'restart' and press enter.");
            System.out.println("If you wish to quit the greenhouse, type 'quit' and press enter.");
            System.out.println("Do not worry if restart/quit is typed across multiple lines, the greenhouse will restart or quit once the command is complete."); // with how many messages this program generates it is unlikely that a user will be able to complete a command on a single line so this instruction is provided to reasure them.
            while (true) {  // this loop will continue to listen for user commands until the program is terminated. So that there is a simple way for the user to restart or quit the greenhouse.
                if (scanner.hasNextLine()) {
                    String cmd = scanner.nextLine().trim().toLowerCase();
                    if (cmd.equals("restart")) {
                        System.out.println("User requested Restart of Greenhouse.");
                        restart();
                    } else if (cmd.equals("quit")) {
                        System.out.println("User requested Quit of Greenhouse.");
                        System.exit(0);
                    } else {
                        System.out.println("Unknown command. If you wish to restart the greenhouse, type 'restart' and press enter.");
                        System.out.println("If you wish to quit the greenhouse, type 'quit' and press enter.");
                        System.out.println("Do not worry if restart/quit is typed across multiple lines, the greenhouse will restart or quit once the command is complete.");
                    }
                }
            }
        }
    }

    public static void main(String[] args) { // Main method to start the greenhouse. It creates a new instance of the Greenhouse class, passing in the name of the plan file as an argument. It then creates a new thread for the greenhouse and starts it. Finally, it calls the userCommand method to listen for user commands to restart or quit the greenhouse. This allows the greenhouse to run on one thread while listening for user commands on another thread, allowing for concurrent execution of both tasks.
        Greenhouse greenhouse = new Greenhouse("greenhouse_plans.txt"); // Create a new instance of the Greenhouse class, passing in the name of the plan file as an argument. This allows the user to specify a different plan file if desired in a future iteration.
        Thread greenhouseThread = new Thread(greenhouse);  // Create a new thread for the greenhouse so that it can run concurrently with the user command thread. This allows the greenhouse to run on one thread while listening for user commands on another thread, allowing for concurrent execution of both tasks.
        greenhouseThread.start();  // Start the greenhouse thread so that it can run concurrently with the user command thread.
        greenhouse.userCommand(); // Start listening for user commands
    }
}