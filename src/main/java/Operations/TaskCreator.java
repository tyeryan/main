package Operations;

import CustomExceptions.RoomShareException;
import Enums.ExceptionType;
import Enums.Priority;
import Enums.RecurrenceScheduleType;
import Enums.TimeUnit;
import Model_Classes.*;

import javafx.util.Pair;
import java.util.ArrayList;
import java.util.Timer;
import java.util.Date;

public class TaskCreator {
    private Parser parser;
    Timer timer;

    /**
     * Constructor for a TaskCreator
     */
    public TaskCreator() {
        parser = new Parser();
        timer = new Timer();
    }

    /**
     * Extract the task type from the user's input
     * @param input user's input
     * @return the task type
     * @throws RoomShareException when the task type is invalid
     */
    public String extractType(String input) throws RoomShareException {
        String[] typeArray = input.split("#");
        String type;
        if (typeArray.length != 1)
            type = typeArray[1].toLowerCase();
        else
            throw new RoomShareException(ExceptionType.emptyTaskType);

        return type;
    }

    /**
     * Extract the description of a task from user's input
     * @param input user's input
     * @return the description of the task
     * @throws RoomShareException when there's no description detected
     */
    public String extractDescription(String input) throws RoomShareException {
        String[] descriptionArray = input.split("\\(");
        String description;
        if (descriptionArray.length != 1) {
            String[] descriptionArray2 = descriptionArray[1].trim().split("\\)");
            description = descriptionArray2[0].trim();
        } else
            throw new RoomShareException(ExceptionType.emptyDescription);

        return description;
    }

    /**
     * Extract the priority of a task from user's input
     * @param input user's input
     * @return the priority of the task
     */
    public Priority extractPriority(String input) {
        String[] priorityArray = input.split("\\*");
        Priority priority;
        if (priorityArray.length != 1) {
            String inputPriority = priorityArray[1].trim();
            try {
                priority = Priority.valueOf(inputPriority);
            } catch (IllegalArgumentException e) {
                System.out.println("There seems to some mistake in your priority entry, will be setting priority as low");
                priority = Priority.low;
            }
        } else {
            priority = Priority.low;
        }

        return priority;
    }

    /**
     * Extract the date and time of a task from user's input
     * @param input user's input
     * @return the date and time of the task
     * @throws RoomShareException when there is no date and time detected or the format of date and time is invalid
     */
    public ArrayList<Date> extractDate(String input) throws RoomShareException {
        String[] dateArray = input.split("&");
        ArrayList<Date> dates = new ArrayList<>();
        if (dateArray.length != 1) {
            if (dateArray.length <= 3) {
                String dateInput = dateArray[1].trim();
                try {
                    dates.add(parser.formatDate(dateInput));
                } catch (RoomShareException e) {
                    System.out.println("Wrong date format, date is set default to current date");
                    dates.add(new Date());
                }
            } else {
                String fromInput = dateArray[1].trim();
                String toInput = dateArray[2].trim();
                try {
                    dates.add(parser.formatDate(fromInput));
                } catch (RoomShareException e) {
                    System.out.println("Wrong date format, starting date is set default to current date");
                    dates.add(new Date());
                }
                try {
                    dates.add(parser.formatDate(toInput));
                } catch (RoomShareException e) {
                    System.out.println("Wrong date format, ending date is set default to current date");
                    dates.add(new Date());
                }
            }
        } else
            throw new RoomShareException(ExceptionType.emptyDate);

        return dates;
    }

    /**
     * Extract the assignee of a task from user's input
     * @param input user's input
     * @return the name of the assignee
     */
    public String extractAssignee(String input) {
        String[] assigneeArray = input.split("@");
        String assignee;
        if (assigneeArray.length != 1) {
            assignee = assigneeArray[1].trim();
        } else {
            assignee = "everyone";
        }
        return assignee;
    }

    /**
     * Extract the recurrence schedule of task from user's input
     * @param input user's input
     * @return the recurrence schedule of the task
     */
    public RecurrenceScheduleType extractRecurrence(String input) {
        String[] recurrenceArray = input.split("%");
        RecurrenceScheduleType recurrence;
        if (recurrenceArray.length != 1) {
            String inputRecurrence = recurrenceArray[1].trim();
            try {
                recurrence = RecurrenceScheduleType.valueOf(inputRecurrence);
            } catch (IllegalArgumentException e) {
                System.out.println("There seems to some mistake in your recurrence entry, will be setting recurrence as none");
                recurrence = RecurrenceScheduleType.none;
            }
        } else {
            recurrence = RecurrenceScheduleType.none;
        }

        return  recurrence;
    }

    /**
     * Extract the duration of a task from user's input
     * @param input user's input
     * @return the amount of time and unit of the duration as a Pair<Integer,TimeUnit>
     */
    public Pair<Integer, TimeUnit> extractDuration(String input) {
        String[] durationArray = input.split("\\^");
        int duration;
        TimeUnit unit;
        if (durationArray.length != 1) {
            try {
                String[] inputDuration = durationArray[1].split(" ");
                duration = Integer.parseInt(inputDuration[0].trim());
                unit = TimeUnit.valueOf(inputDuration[1].trim());
            } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
                System.out.println("There's a problem with the duration you've specified, default to no duration");
                duration = 0;
                unit = TimeUnit.unDefined;
            }
        } else {
            duration = 0;
            unit = TimeUnit.unDefined;
        }

        return new Pair<>(duration,unit);
    }

    /**
     * Extract the reminder flag of a task from user's input
     * @param input user's input
     * @return the reminder flag of the task
     */
    public boolean extractReminder(String input) {
        String[] reminderArray = input.split("!");
        if (reminderArray.length != 1) {
            if(reminderArray[1].contains("R"))
                return true;
            else
                return false;
        } else {
            return false;
        }
    }

    /**
     * Create a new task based on the description the user key in
     * @param input the description of the task
     * @return a new Task object created based on the description
     * @throws RoomShareException when there are some formatting errors
     */
    public Task create(String input) throws RoomShareException {
        // extract the Task Type
        String type = this.extractType(input);

        // extract the priority
        Priority priority = this.extractPriority(input);

        // extract the description
        String description = this.extractDescription(input);

        // extract date
        ArrayList<Date> dates = this.extractDate(input);
        Date date = new Date();
        Date from = new Date();
        Date to = new Date();
        if (dates.size() == 1) {
            date = dates.get(0);
        } else {
            from = dates.get(0);
            to = dates.get(1);
        }

        // extract the assignee
        String assignee = this.extractAssignee(input);

        // extract recurrence schedule
        RecurrenceScheduleType recurrence = this.extractRecurrence(input);

        //extract duration
        Pair<Integer, TimeUnit> durationAndUnit = this.extractDuration(input);
        int duration = durationAndUnit.getKey();
        TimeUnit unit = durationAndUnit.getValue();

        //extract reminder
        boolean remind = this.extractReminder(input);

        if (type.contains("assignment")) {
            Assignment assignment = new Assignment(description, date);
            assignment.setPriority(priority);
            assignment.setAssignee(assignee);
            assignment.setRecurrenceSchedule(recurrence);
            return assignment;
        } else if (type.contains("leave")) {
            String user;
            String[] leaveUserArray = input.split("@");
            if (leaveUserArray.length != 1) {
                user = leaveUserArray[1].trim();
            } else
                throw new RoomShareException(ExceptionType.emptyUser);
            Leave leave = new Leave(description, user, from, to);
            leave.setPriority(priority);
            leave.setRecurrenceSchedule(recurrence);
            return leave;
        } else if (type.contains("meeting")) {
            if (remind) {
                if (unit.equals(TimeUnit.unDefined)) {
                    // duration was not specified or not correctly input
                    Meeting meeting = new Meeting(description, date);
                    meeting.setPriority(priority);
                    meeting.setAssignee(assignee);
                    meeting.setRecurrenceSchedule(recurrence);
                    TaskReminder taskReminder = new TaskReminder(description, duration);
                    taskReminder.start();
                    return meeting;
                } else {
                    Meeting meeting = new Meeting(description, date, duration, unit);
                    meeting.setPriority(priority);
                    meeting.setAssignee(assignee);
                    meeting.setRecurrenceSchedule(recurrence);
                    TaskReminder taskReminder = new TaskReminder(description, duration);
                    taskReminder.start();
                    return meeting;
                }
            } else {
                if (unit.equals(TimeUnit.unDefined)) {
                    // duration was not specified or not correctly input
                    Meeting meeting = new Meeting(description, date);
                    meeting.setPriority(priority);
                    meeting.setAssignee(assignee);
                    meeting.setRecurrenceSchedule(recurrence);
                    return meeting;
                } else {
                    Meeting meeting = new Meeting(description, date, duration, unit);
                    meeting.setPriority(priority);
                    meeting.setAssignee(assignee);
                    meeting.setRecurrenceSchedule(recurrence);
                    return meeting;
                }
            }
        } else {
            throw new RoomShareException(ExceptionType.wrongTaskType);
        }
    }

    public void updateTask(String input, Task oldTask) {

    }
}