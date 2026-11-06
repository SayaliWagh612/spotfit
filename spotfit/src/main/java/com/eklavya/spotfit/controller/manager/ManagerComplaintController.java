package com.eklavya.spotfit.controller.manager;

import com.eklavya.spotfit.controller.admin.MockDataStore;
import com.eklavya.spotfit.model.*;
import java.util.List;

public class ManagerComplaintController {

    private final MockDataStore store = MockDataStore.getInstance();
    private String assignedSpaceId() { Manager m = new ManagerProfileController().getLoggedInManager(); return m == null ? "space-1" : m.getSpaceId(); }

    public List<Complaint> getComplaints() {
        return store.getComplaintsForScope(assignedSpaceId());
    }

    public boolean acknowledgeComplaint(String complaintId, String managerNotes) {
        List<Complaint> list = store.getComplaintsForScope(assignedSpaceId());
        for (Complaint c : list) {
            if (c.getComplaintId().equals(complaintId)) {
                c.setStatus("IN_PROGRESS");
                c.setManagerNotes(managerNotes);
                store.addActivityLog(assignedSpaceId(), "Space Manager", new ManagerProfileController().getLoggedInManager().getName(), "ACKNOWLEDGE_COMPLAINT", "Acknowledged complaint " + complaintId + ": " + managerNotes);
                return true;
            }
        }
        return false;
    }

    public boolean resolveOperationalComplaint(String complaintId, String resolutionNotes) {
        List<Complaint> list = store.getComplaintsForScope(assignedSpaceId());
        for (Complaint c : list) {
            if (c.getComplaintId().equals(complaintId)) {
                if (c.getType() == null || !c.getType().startsWith("OPERATIONAL_")) {
                    return false; // Financial complaints must be escalated to Owner
                }
                c.setStatus("RESOLVED");
                c.setManagerNotes(resolutionNotes);
                c.setResolvedAt("Just now");
                store.addActivityLog(assignedSpaceId(), "Space Manager", new ManagerProfileController().getLoggedInManager().getName(), "RESOLVE_OPERATIONAL_COMPLAINT", "Resolved operational complaint " + complaintId);
                return true;
            }
        }
        return false;
    }

    public boolean escalateToOwner(String complaintId, String reason) {
        List<Complaint> list = store.getComplaintsForScope(assignedSpaceId());
        for (Complaint c : list) {
            if (c.getComplaintId().equals(complaintId)) {
                c.setStatus("ESCALATED");
                c.setEscalatedToOwner(true);
                c.setManagerNotes("Escalated to Business Owner: " + reason);
                store.addActivityLog(assignedSpaceId(), "Space Manager", new ManagerProfileController().getLoggedInManager().getName(), "ESCALATE_COMPLAINT_OWNER", "Escalated complaint " + complaintId + " to Business Owner");
                return true;
            }
        }
        return false;
    }
}

