/** Student Name: Jonathan Matters 
 * Student ID: 3718743 
 * Date: 2024-07-24 
 * Assignment #3 
 * Program Name: Greenhouse.java, 
 * Description: This program runs a greenhouse simulation. It reads a plan file for a list of events to be scheduled and executed. 
 * */

/**
 * Purpose: 
 * - This class represents an abstract super class for events in the greenhouse simulation. It is meant to standardize all the classes as much as possible, and to provide a common interface for all events in the simulation. It provides the basic structure and functionality for scheduling and executing events, including initial delay, recurring delay, and priority.
 * 
 * Code behaviours: 
 * - The Event class extends the Thread class and provides a framework for scheduling and executing events in the greenhouse simulation. It defines an abstract action method that must be implemented by subclasses to specify the behavior of the event when it is triggered.
 * - It establishes the common variables for al levents, including initialDelay, recurringDelay, isRunning, hasFailed, eventPriority and threadPriority. 
 * - It provides a constructor to initialize the event with an initial delay, recurring delay, and priority. The constructor calculates the thread priority based on the event priority and sets it accordingly. So that it will be usable by the thread.
 * - It provides a second constructor to initialize the event with an initial delay and priority, with a default recurring delay of 0 (indicating that the event does not recur).
 * - It provides an abstract action method that that must be implemented by subclasses to define the specific behavior of the method when it is called.
 * - It overrides the run method from the Thread class to implement the scheduling and execution of events. The run method sleeps for the initial delay, then repeatedly calls the action method after each recurring interval indicated by the recurringDelay.
 * - It provides a stopEvent method to stop the event from running, and a setThreadPriority method to set the priority of the thread that executes the event.
 * 
 * Particulars: 
 * - The Event class is abstract and cannot be instantiated directly. It is meant to be used to create subclasses that implement the action method to define specific behaviors for different types of events in the greenhouse simulation.
 * 
 * Compiling and executing the program:
 * - Event is not a standalone program and cannot be executed on its own. It is part of the Greenhouse simulation and is used in conjunction with other classes to create a greenhouse simulation. 
 * - To compile and execute the program, you would typically compile the entire Greenhouse simulation project.
 * - To compile and run the entire program, enter the folder where the folder Greenhouse is located and run the following command in the terminal:
 *              1) javac Greenhouse\*.java
 *              2) cd Greenhouse
 *              3) java Greenhouse
 * 
 * Ensure that the plan file is present in the same directory as Greenhouse.java and named greenhouse_plan.txt. If there is no plan file present, the Greenhouse class will not have any events to schedule, and no door toggling will occur when executing the program.
 * 
 * Classes:
 * - Event:
 *   - An abstract superclass for events in the greenhouse simulation. It provides a common interface and functionality for scheduling and executing events.
 *  
 * Instance Variables: 
 * - InitialDelay (protected long):
 *         - The initial delay before the event is triggered, in milliseconds. Is used to determine when the door should be toggled after the simulation starts.
 * - RecurringDelay (protected long):
 *         - The delay between recurring triggers of the event, in milliseconds. Is used to determine how often the door should be toggled after the initial trigger.
 * - isRunning (protected volatile boolean):
 *         - A flag indicating whether the event is currently running.
 * - hasFailed (public static volatile boolean):
 *         - A flag indicating whether the event has failed.
 * - eventPriority (protected int):
 *        - The priority of the event, used to determine the thread priority.
 * - threadPriority (protected int):
 *       - The priority of the thread that executes the event. Calculated based on the event priority.
 */

public abstract class Event extends Thread { //creates an abstract class called Event that extends the Thread class, so that it can be executed in a separate thread.
    protected long initialDelay; // Will hold the initial delay before the event is triggered, in milliseconds, before the action method is called in the concrete subclass.
    protected long recurringDelay; // Will hold the delay between recurring triggers of the event, in milliseconds, before the action method is called in the concrete subclass.
    protected volatile boolean isRunning = true; // Will hold a flag indicating whether the event is currently running. It is marked as volatile to ensure that changes to this variable are visible across threads.
    public static volatile boolean hasFailed = false; // Will hold a flag indicating whether the event has failed. It is marked as volatile to ensure that changes to this variable are visible across threads.
    protected int eventPriority; // Will hold the priority of the event, used to determine the thread priority.
    protected int threadPriority; // Will hold the priority of the thread that executes the event. Calculated based on the event priority.

    public Event(long initialDelay, long recurringDelay, int eventPriority) { // Constructor to initialize the event with an initial delay, recurring delay, and priority. The constructor calculates the thread priority based on the event priority and sets it accordingly. This will be the constructor used by most concrete subclasses.
        this.initialDelay = initialDelay;
        this.recurringDelay = recurringDelay;
        this.eventPriority = eventPriority;
        
        int threadPriorityCalculated;  // Will hold the calculated thread priority based on the event priority. This variable is only used within the constructor to calculate the thread priority based on the event priority.

        if (eventPriority ==0)
            threadPriorityCalculated = 1;
        else threadPriorityCalculated = Thread.MAX_PRIORITY-(eventPriority-1);
        setThreadPriority(threadPriorityCalculated);
        this.setPriority(this.threadPriority);
    }

    public Event(long initialDelay, int eventPriority) { // Constructor to initialize the event with an initial delay and priority, with a default recurring delay of 0 (indicating that the event does not recur). This will be the constructor used by most concrete subclasses. Will be used by bell and any other one off events.
        this.initialDelay = initialDelay;
        this.recurringDelay = 0;
        this.eventPriority = eventPriority;
        this.threadPriority = Thread.MAX_PRIORITY-(eventPriority-1);

        this.setPriority(this.threadPriority);
    }

    public abstract void action() throws Exception; // Abstract method that must be implemented by all subclasses to define the specific behavior of the event when it is triggered. This standardizes the method name for all subclasses, so that the Greenhouse class can call the action method on any event without knowing the specific subclass type.

    @Override
    public void run() {  // Overrides the run method from the Thread class to implement the scheduling and execution of events. This simplifies and standardizes the scheduling and execution of events for all subclasses, so that the Greenhouse class can start any event without knowing the specific subclass type.
        try {
            Thread.sleep(initialDelay);

            while (isRunning) {
                action();

                if (recurringDelay == 0) {
                    break;
                } else {
                    Thread.sleep(recurringDelay);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            System.out.println("Event error: " + e.getMessage());
        }
    }

    public void stopEvent() { // Method to stop the event from running. This allows the Greenhouse class to stop any event without knowing the specific subclass type. It sets the isRunning flag to false and interrupts the thread to stop its execution. Is also useful to be able to call this standard language across all methods, so that all processes can be stopped more easily.
        isRunning = false;
        this.interrupt();
    }
    public void setThreadPriority(int priority) { // Method to set the priority of the thread that executes the event. This allows the Greenhouse class to set the priority of any event without knowing the specific subclass type. It sets the threadPriority variable to the specified priority value.
        this.threadPriority = priority;
    }
}
