package com.eklavya.spotfit.view.spaceManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AuditLogManager {

    public static class LogEntry {
        public String timestamp;
        public String managerName;
        public String action;
        public String details;
        public String category;

        public LogEntry(String timestamp, String managerName, String action, String details, String category) {
            this.timestamp = timestamp;
            this.managerName = managerName;
            this.action = action;
            this.details = details;
            this.category = category;
        }
    }

    private static final List<LogEntry> logs = new ArrayList<>();

    static {
        // Initial sample logs
        addLog("Rahul Sharma", "Checked In Customer", "Arjun Mehta (SPF-2841) checked in via OTP", "CHECK_IN");
        addLog("Rahul Sharma", "Blocked Resource", "Studio A frozen 2:00 PM - 4:00 PM (Reason: Maintenance)", "RESOURCE");
        addLog("Rahul Sharma", "Updated Space Info", "Modified customer directions & rules", "SPACE");
        addLog("Rahul Sharma", "Rescheduled Booking", "SPF-2852 moved to 1:00 PM - 3:00 PM", "BOOKING");
    }

    public static void addLog(String managerName, String action, String details, String category) {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy — hh:mm a"));
        logs.add(0, new LogEntry(time, managerName, action, details, category));
    }

    public static List<LogEntry> getLogs() {
        return Collections.unmodifiableList(logs);
    }
}

