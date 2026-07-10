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
 *             - DataEntry class has instance variables for event type, class name, initial delay, recurring delay, priority, and disabled status.
 *      - Creates instance variables for the plan file name, a list of DataEntry objects, a map for priorities, a list of active events, and a thread for handling failure events.
 *      - Creates a Greenhouse constructor that only takes the plan file name as an argument.
 *      - Overrides the run method to start the greenhouse simulation. So that the start method can be called by both run() and userComand restart() methods. 
 *      - Defines a readPlan method to read the plan file and populate the plan and priorities data structures. 
 *      - Posts a message indicating how many events were loaded and how many classes were disabled due to previous failures or an event unknown and ignored message.
 *      - Defines a resolvePriority method to determine the priority of an event based on its class name and the priorities map.
 *      - Defines a start method to initiate the greenhouse simulation, sets all hasFailed flags to false and clears all plans from prior runs (important when using the restart command), and then schedules events based on the plan and handling any errors that may happen. 
 *             - Also contains a try catch block to provide a user friendly message if the plan file cannot be read.
 *             - Checks for disabled events and skips scheduling them, and handles failed events by scheduling them on a separate thread.
 *             - Uses reflection to dynamically create instances of event classes and start their threads, allowing for flexibility in adding new events without modifying the greenhouse code. This allows the user to add classes and only need to compile them rather than the entire greenhouse program.
 *                        - Uses a try catch block to handle any exceptions that may occur during event creation, then marking the event as disabled, adding it to the permanent list of disabled entries, and stopping all threads before restarting the greenhouse if necessary.
 *     - Defines a scheduleFailure method to schedule failure events on a separate thread.
 *             - The failure thread sleeps for the specified initial delay, sets the hasFailed flag to true, and creates and rings a bell 5 times to indicate a failure and then prints a message identifying which event failed.
 *     - Defines a restart method to stop all active events, clear the list of active events, interrupt the failure thread if it is running, and then call the start method to restart the greenhouse.
 *     - Defines a userCommand method to listen for user commands in the console to restart or quit the greenhouse. It creates and uses a Scanner to read user input. The only valid commands are "restart" and "quit". It checks for these commands and calls the appropriate method to restart or quit the greenhouse. If the command is not recognized, it prints a message to the console indicating that the command is unknown and provides instructions for restart or quit commands.
 *     - Defines a main method to create a Greenhouse, start the greenhouse thread, and call the userCommand method to listen for user commands.
 * 
 * Particulars: 
 * - The program is designed to be handle various scenarios including various failures. 
 * - It provides clear feedback to the user about the state of the simulation and any problems.
 * - The program is designed in a way that allows for new event classes to be added easily by creating and compiling a new class and adding its events to the plan file.
 * 
 * Input: 
 * - Greenhouse_plan.txt file containing the events to be scheduled. The plan file must be placed in the same directory as Greenhouse.java and named greenhouse_plan.txt. 
 *      - The plan file should contain entries for priorities, events, tests, and failures in this format (Each entry should be on a new line):
 *          - priority= STRING className (or * for all), INTEGER priority (lower numbers indicate higher priority)
 *          - event= STRING className, LONG initialDelay, LONG recurringDelay (use 0 for non-recurring events)
 *          - test= STRING className, LONG initialDelay (tests are non-recurring)
 *          - failed= STRING className, LONG initialDelay (failed events are non-recurring)
 * 
 * Compiling and executing the program:
 * - To compile and run the entire program, enter the folder where the folder Greenhouse is located and run the following command in the terminal:
 *              1) javac Greenhouse\*.java
 *              2) cd Greenhouse
 *              3) java Greenhouse
 * 
 * Ensure that the plan file is present in the same directory as Greenhouse.java and named greenhouse_plan.txt. If there is no plan file present, the Greenhouse class will not have any events to schedule, and no door toggling will occur when executing the program.
 * 
 * Classes:
 * - Greenhouse: 
 *     -The main class that orchestrates the greenhouse simulation, reading the plan file, scheduling events, and managing the simulation. 
 * - DataEntry:
 *     - A class within Greenhouse that holds the data for each event in the plan file, including type, class name, initial delay, recurring delay, priority, and disabled status.
 * - Bell:
 *    - Represents a bell that can be rung. It is used to indicate failures.
 * - Door:
 *    - Represents a door and can be opened and closed.
 * - Event:
 *    - An abstract superclass for events in the greenhouse simulation. It provides a common interface and functionality for scheduling and executing events.
 * - Fan:
 *    - Represents a fan and can be turned on and off.
 * - Light:
 *    - Represents a light that can be turned on and off.
 * - Location:
 *    - Represents the location of trays in the greenhouse. Trays can be moved between their original and their alternate locations. Accounts for differnt microclimates in the greenhouse.
 * - Rotate:
 *    - Represents the rotation of trays in the greenhouse. Trays can be oriented North-South or East-West. Helps plants grow straight up. 
 * - Thermostat:
 *    - Represents a thermostat that can be turned on or off. 
 * - Water:
 *    - Represents a watering machine that can be turned on or off. 
 * - Window:
 *    - Represents a window that can be opened and closed.
 * 
 * - Instance Variables:
 *     - planFile (private final String): Holds the name of the plan file to be read.
 *     - plan (private final List<DataEntry>): Holds the list of DataEntry objects representing the events to be scheduled.
 *     - priorities (private final Map<String, Integer>): Holds class names and their priorities.
 *     - activeEvents (private final List<Event>): Holds the list of active events that have been scheduled and are currently running.
 *     - failureThread (private Thread): Holds the thread responsible for scheduling a failure event.
 *     - disabledClasses (private final static Set<String>): Holds the set of classes that can be disabled, preventing scheduling of events that have failed or are not needed.
 */

 /** Test Plan
 * Test Case 1:
 * Description: This test case checks to see if the program runs correctly with the provided plan file in the format instructed by the assignment instructions. 
 * 
 * Input: greenhouse_plans.txt
 *      Contents of greenhouse_plans.txt:
 *          priority=*,10
 *          priority=Light,5
 *          priority=Bell,1
 *          priority=Thermostat,2
 *          event=Thermostat,1000,*
 *          event=Light,1000,1000
 *          priority=Water,5
 *          event=Water,3000,5000
 *          test=Bell,1000
 *          failed=Thermostat,7000
 *          event=Water,8000,5000
 *          event=Fan,10000,2000
 * 
 * Expected Output (until the first failed attempt at turning on the fans):
 *      Plan Loaded 7 events. 0 classes disabled due to previous failures.
 *      If you wish to restart the greenhouse, type 'restart' and press enter.
 *      If you wish to quit the greenhouse, type 'quit' and press enter.
 *      Do not worry if restart/quit is typed across multiple lines, the greenhouse will restart or quit once the command is complete.
 *      Thermostat turned ON
 *      Light turned ON
 *      ALARM!!!
 *      Light turned OFF
 *      Watering machine ON
 *      Light turned ON
 *      Light turned OFF
 *      Light turned ON
 *      Light turned OFF
 *      ALARM!!!
 *      ALARM!!!
 *      ALARM!!!
 *      ALARM!!!
 *      Thermostat failed.
 *      Light turned ON
 *      Watering machine OFF
 *      Watering machine ON
 *      Light turned OFF
 *      Light turned ON
 *      Fan controls attempted. Fan controls are disabled because Thermostat has failed. Fan is OFF.
 * 
 * Actual Output (until the first failed attempt at turning on the fans):
 *      Plan Loaded 7 events. 0 classes disabled due to previous failures.
 *      If you wish to restart the greenhouse, type 'restart' and press enter.
 *      If you wish to quit the greenhouse, type 'quit' and press enter.
 *      Do not worry if restart/quit is typed across multiple lines, the greenhouse will restart or quit once the command is complete.
 *      Thermostat turned ON
 *      Light turned ON
 *      ALARM!!!
 *      Light turned OFF
 *      Watering machine ON
 *      Light turned ON
 *      Light turned OFF
 *      Light turned ON
 *      Light turned OFF
 *      ALARM!!!
 *      ALARM!!!
 *      ALARM!!!
 *      ALARM!!!
 *      Thermostat failed.
 *      Light turned ON
 *      Watering machine OFF
 *      Watering machine ON
 *      Light turned OFF
 *      Light turned ON
 *      Fan controls attempted. Fan controls are disabled because Thermostat has failed. Fan is OFF.
 * 
 * Explanation: The actual output should match the expected output and it does. Code executed successfully.
 * 
 * Test Case 2:
 * Description: This test case checks if when new input is added to the greenhouse_plan.txt file if the program handles it correctly. 
 * Note The input has: 
 *      -additional class names with no corresponding class files
 *      -additional class names with corresponding class files 
 *      -non-standard event types (not event, test, failed, or priority)
 *      -non-standard spacing
 * 
 * Input: greenhouse_plans.txt
 *     Contents of greenhouse_plans.txt:
 *          priority=*,10
 *          priority=Light,5
 *          priority=Bell,1
 *          priority=Thermostat,2
 *          event=Thermostat,1000,*
 *          event=Light,1000,1000
 *          priority=Water,5
 *          event=Water,3000,5000
 *          test=Bell,1000
 *          failed=Thermostat,7000
 *          event=Water,8000       ,5000
 *          event=Fan,10000,2000    
 *          event=Fertilizer,      6000,6000     
 *          event=Location,1500,7500
 *          priority=Location,                9     
 *          priority=Rotate,8
 *          event=Rotate, 8500, 8500
 *          priority=Fertilizer,4
 *          disaster=earthquake, 99999999999, 8888888888888
 *          priority=earthquake, 1
 * 
 * Expected Output (until the first failed attempt at turning on the fans):
 *      Unknown Event type detected. Event ignored: priortity
 *      Unknown Event type detected. Event ignored: disaster
 *      Plan Loaded 10 events. 0 classes disabled due to previous failures.
 *      Fertilizer encountered an error. Restarting greenhouse and skipping Fertilizer in future runs.
 *      If you wish to restart the greenhouse, type 'restart' and press enter.
 *      If you wish to quit the greenhouse, type 'quit' and press enter.
 *      Do not worry if restart/quit is typed across multiple lines, the greenhouse will restart or quit once the command is complete.
 *      Plan Loaded 10 events. 1 classes disabled due to previous failures.
 *      Fertilizer is disabled and will not be scheduled.
 *      Thermostat turned ON
 *      ALARM!!!
 *      Light turned ON
 *      Tray locations moved to ORIGINAL location
 *      Light turned OFF
 *      Watering machine ON
 *      Light turned ON
 *      Light turned OFF
 *      Light turned ON
 *      Light turned OFF
 *      ALARM!!!
 *      ALARM!!!
 *      ALARM!!!
 *      ALARM!!!
 *      ALARM!!!
 *      Thermostat failed.
 *      Light turned ON
 *      Watering machine ON
 *      Watering machine OFF
 *      Light turned OFF
 *      Rotated tray orientation to NORTH-SOUTH
 *      Tray locations moved to ALTERNATE location
 *      Light turned ON
 *      Fan controls attempted. Fan controls are disabled because Thermostat has failed. Fan is OFF.
 *      
 * Actual Output (until the first failed attempt at turning on the fans):
 *      Unknown Event type detected. Event ignored: priortity
 *      Unknown Event type detected. Event ignored: disaster
 *      Plan Loaded 10 events. 0 classes disabled due to previous failures.
 *      Fertilizer encountered an error. Restarting greenhouse and skipping Fertilizer in future runs.
 *      If you wish to restart the greenhouse, type 'restart' and press enter.
 *      If you wish to quit the greenhouse, type 'quit' and press enter.
 *      Unknown Event type detected. Event ignored: priortity
 *      Do not worry if restart/quit is typed across multiple lines, the greenhouse will restart or quit once the command is complete.
 *      Unknown Event type detected. Event ignored: disaster
 *      Plan Loaded 10 events. 1 classes disabled due to previous failures.
 *      Fertilizer is disabled and will not be scheduled.
 *      Thermostat turned ON
 *      ALARM!!!
 *      Light turned ON
 *      Tray locations moved to ORIGINAL location
 *      Light turned OFF
 *      Watering machine ON
 *      Light turned ON
 *      Light turned OFF
 *      Light turned ON
 *      Light turned OFF
 *      ALARM!!!
 *      ALARM!!!
 *      ALARM!!!
 *      ALARM!!!
 *      ALARM!!!
 *      Thermostat failed.
 *      Light turned ON
 *      Watering machine ON
 *      Watering machine OFF
 *      Light turned OFF
 *      Rotated tray orientation to NORTH-SOUTH
 *      Tray locations moved to ALTERNATE location
 *      Light turned ON
 *      Fan controls attempted. Fan controls are disabled because Thermostat has failed. Fan is OFF.
 * 
 * Explanation: 
 *      The difference between the expected and actual output is that the actual output has two additional lines indicating that the unknown event types were ignored in the second run. This is because the program is designed to check and ignore any unknown event types on each run rather than storing them in a list. This is less of a problem and more of an oversight. If the program was to expect large numbers of unknown event types a different handling method could be implemented in a future iteration.
 *      The rest of the output matches the expected output, indicating that the program handled the new input correctly and cleaned the data issues automatically. 
 * 
 * Case 3:
 * Description: This test case checks if the program handles the absence of the plan file correctly.
 * 
 * Input: No plan file present in the directory.
 * 
 * Expected Output: 
 *      greenhouse_plans.txt does not exist in the Greenhouse folder. Please ensure that the file is present in the correct location and run the greenhouse again.
 * 
 * Actual Output:
 *     greenhouse_plans.txt does not exist in the Greenhouse folder. Please ensure that the file is present in the correct location and run the greenhouse again.
 * 
 * Explanation: The actual output matches the expected output, indicating that the program correctly handles the absence of the plan file by providing a user-friendly message and exiting gracefully.
 * 
 * Case 4:
 * Description: This test case checks if the program handles user commands correctly.
 * 
 * Input: greenhouse_plans.txt
 *      Contents of greenhouse_plans.txt:
 *          priority=*,10
 *          priority=Light,5
 *          priority=Bell,1
 *          priority=Thermostat,2
 *          event=Thermostat,1000,*
 *          event=Light,1000,1000
 *          priority=Water,5
 *          event=Water,3000,5000
 *          test=Bell,1000
 *          failed=Thermostat,7000
 *          event=Water,8000,5000
 *          event=Fan,10000,2000
 *      User types "restart", "restart", and then "quit" in the console.
 *
 * Expected Output:
 *     When the user types "restart", the program should print "User requested Restart of Greenhouse." and restart the greenhouse simulation.
 *     When the user types "quit", the program should print "User requested Quit of Greenhouse." and exit the greenhouse simulation.
 *     When the user types "quit", the program should print "User requested Quit of Greenhouse." and exit the greenhouse simulation.
 * 
 * Actual Output(*** are beside the relevant lines of output to indicate the user command that was entered):
 *      Plan Loaded 7 events. 0 classes disabled due to previous failures.
 *      If you wish to restart the greenhouse, type 'restart' and press enter.
 *      If you wish to quit the greenhouse, type 'quit' and press enter.
 *      Do not worry if restart/quit is typed across multiple lines, the greenhouse will restart or quit once the command is complete.
 *      Thermostat turned ON
 *      Light turned ON
 *      ALARM!!!
 *      Light turned OFF
 * ***  restart
 *      Light turned OFF
 * ***  User requested Restart of Greenhouse.
 *      Stopping all processes.
 *      Restarting Greenhouse...
 *      Plan Loaded 7 events. 0 classes disabled due to previous failures.
 *      Thermostat turned ON
 *      Light turned ON
 *      ALARM!!!
 *      Light turned OFF
 *      Watering machine ON
 *      Light turned ON
 * ***  restart
 *      Light turned OFF
 * ***  User requested Restart of Greenhouse.
 *      Stopping all processes.
 *      Restarting Greenhouse...
 *      Plan Loaded 7 events. 0 classes disabled due to previous failures.
 *      ALARM!!!
 *      Thermostat turned ON
 *      Light turned ON
 *      Light turned OFF
 *      Light turned ON
 *      Watering machine ON
 *      Light turned OFF
 * ***  quit
 *      Light turned ON
 *
 * ***  User requested Quit of Greenhouse.
 * 
 * Explanation: The actual output matches the expected output, indicating that the program correctly handles user commands for restarting and quitting the greenhouse simulation.
 * 
 * Limitations: 
 * - The program relies on the presence of the plan file in the correct location. If the plan file is missing or incorrectly formatted, the program will not function as intended.
 * - Future iterations could include more error handling with user friendly messages for how to fix more problems with the plan file. Also, a GUI could be implemented to allow for easier user interaction with the greenhouse.
 */

 import java.io.*;  // Importing to use BufferedReader and FileReader for reading the plan file. Also importing IOException for handling file reading exceptions.
 import java.util.*; // Importing to use Set, HashSet, List, ArrayList, Map, HashMap, and Scanner for various data structures and user input handling.

public class Greenhouse implements Runnable {  // Implementing Runnable interface to allow the greenhouse to run on one thread and a separate thread to listen for user commands to run at the same time.

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
    private final static Set<String> disabledClasses = new HashSet<>(); // Set of classes that can be disabled. This is used to prevent scheduling of events that have failed or are not needed.

    public Greenhouse(String planFile) { // Constructor that takes the name of the plan file as an argument. This allows the user to specify a different plan file if desired in a future iteration.
        this.planFile = planFile;
    }

    @Override // Overriding the run method from the Runnable interface. This is the entry point for the thread when it is started.
    public void run() {
        start();
    }

    private void readPlan() {  // Method to read the plan file and create the greenhouse plan and priorities data structure.

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(planFile))) { // Using try catch block to handle simple errors in file reading to potentially let user be able to fix problems themselves.
            String line;  // Will hold each entry in the plan file as a separate entry that will be parsed and added to the plan data structure.

            while ((line = bufferedReader.readLine()) != null) {  // read each line of the plan file and skip empty lines.
                line = line.trim(); // Trim whitespace so that if there is or is not whitespace the data is still readable.
                if (line.isEmpty()) continue;

                String[] entryTypeSplit = line.split("="); // split the line to get the entry type. 

                String entryType = entryTypeSplit[0].trim().toLowerCase(); // get entry type and standardize to lower case in case user enters a different case.
                String restOfEntry = entryTypeSplit[1].trim(); // remove whitspace from the rest of the entry so that if user entry spaces do not affect readability.

                DataEntry entry = new DataEntry(); // Create a new DataEntry object to hold the data for this process.
                entry.type = entryType;

                switch (entryType) {  // Will sort the entries by type and apply the approriate logic to populate the DataEntry object and add it to the appropriate data structure.
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
            System.out.println("Plan Loaded " + plan.size() + " events. " + disabledClasses.size() + " classes disabled due to previous failures."); // Print a message indicating how many events were loaded and how many classes were disabled. This provides feedback to the user about the state of the greenhouse plan, to help the user identify any unexpected behaviors. 
        } catch (FileNotFoundException e) {
            System.out.println(planFile + " does not exist in the Greenhouse folder. Please ensure that the file is present in the correct location and run the greenhouse again."); // catch the FileNotFoundException and print a user friendly message to the console so that user can quickly fix the issue. 
            System.exit(1); // exit the program
        } catch (IOException e) {
            System.out.println("There was a problem reading " + planFile + "."); // catch other types of IOException and print a message to the console identifying the area of the issue.
            System.exit(1); // exit the program
        }
    }

    private int resolvePriority(String className) { // Method to resolve the priority for a given class name. It checks to see if a * priority is set and changes it to the default priority of 10.
        if (priorities.containsKey(className)) return priorities.get(className);
        if (priorities.containsKey("*")) return priorities.get("*");
        return 10;
    }

    public void start() { // Method to start the greenhouse. 
        Event.hasFailed = false;     // Reset failure flag in all classes so that everything starts cleanly after restart
        plan.clear();                     // Clear the plan so we can reload it fresh on restart

        readPlan(); // calls readPlan method to populate the plan and priorities data structure fresh on each run.

        for (DataEntry entry : plan) { // Goes through each entry in the plan to check if an entry was disabled on a prior run. If it was disabled it will not add it to the schedule.
            if (entry.disabled) {
                System.out.println(entry.className + " is disabled and will not be scheduled.");
                continue;
            }
            if (entry.type.equals("failed")) { // creates a separate thread to schedule failure events so that the greenhouse can continue to run other events while waiting for the failure event to occur. This allows the greenhouse to continue to run other events while waiting for the failure event to occur.
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
                    activeEvents.clear(); // Clear the list of active events so that it can be repopulated from scratch with the new events on restart. The disabled events list will not be cleared as they are stored in a separate object.

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
        activeEvents.clear();  // Clear the list of active events so that it can be repopulated from scratch with the new events on restart. The disabled events list will not be cleared as they are stored in a separate object. Remeber this is the process called for both an automatic restart and a users requested one.

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
            System.out.println("Do not worry if restart/quit is typed across multiple lines, the greenhouse will restart or quit once the command is complete."); // with how many messages this program generates it is unlikely that a user will be able to complete a command on a single line so this instruction is provided to reassure them.
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