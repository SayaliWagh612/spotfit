package com.eklavya.spotfit.controller.owner;

import com.eklavya.spotfit.controller.admin.MockDataStore;
import com.eklavya.spotfit.model.*;
import java.util.List;
import java.util.stream.Collectors;

public class OwnerComplaintController {

    private final MockDataStore store = MockDataStore.getInstance();

    public List<Complaint> getComplaints(String spaceIdScope, String statusFilter) {
        List<Complaint> list = store.getComplaintsForScope(spaceIdScope);
        return list.stream()
                .filter(c -> statusFilter == null || statusFilter.equals(c.getStatus()))
                .collect(Collectors.toList());
    }

    public boolean resolveComplaint(String complaintId, String ownerResolutionNotes) {
        List<Complaint> list = store.getComplaintsForScope("ALL");
        for (Complaint c : list) {
            if (c.getComplaintId().equals(complaintId)) {
                c.setStatus("RESOLVED");
                c.setOwnerNotes(ownerResolutionNotes);
                c.setResolvedAt("Just now");
                store.addActivityLog(c.getSpaceId(), "Business Owner", store.getCurrentOwner().getName(), "RESOLVE_COMPLAINT", "Resolved complaint " + complaintId + ": " + ownerResolutionNotes);
                return true;
            }
        }
        return false;
    }

    public boolean escalateToAdmin(String complaintId, String reason) {
        List<Complaint> list = store.getComplaintsForScope("ALL");
        for (Complaint c : list) {
            if (c.getComplaintId().equals(complaintId)) {
                c.setStatus("ESCALATED");
                c.setOwnerNotes("Escalated to SpotFit Admin: " + reason);
                store.addActivityLog(c.getSpaceId(), "Business Owner", store.getCurrentOwner().getName(), "ESCALATE_ADMIN", "Escalated complaint " + complaintId + " to Admin");
                return true;
            }
        }
        return false;
    }
}

