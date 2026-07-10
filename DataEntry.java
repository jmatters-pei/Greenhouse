protected static class DataEntry { // Creating a class to hold the data for each event in the plan file.
     String type;  // Will hold the type of the event (event, test, failed, priority).
     String className;  // will hold the class name of the event to be scheduled (Bell, Thermostat, etc.).
     long initialDelay;  // Will hold the initial delay before the event is scheduled.
     long recurringDelay;  // Will hold the recurring delay for the event. If 0, the event is non-recurring.
     int priority;  // Will hold the priority of the event. Lower numbers indicate higher priority.
     boolean disabled; // Flag to indicate if the event is disabled. If true, the event will not be scheduled.
    }
}
