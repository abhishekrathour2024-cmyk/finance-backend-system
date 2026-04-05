package com.zorvyn.finance_backend.util;

import com.zorvyn.finance_backend.entity.enums.Role;
import com.zorvyn.finance_backend.exception.AccessDeniedException;

public class RoleValidator {

    public static void adminOnly(Role role) {
        if (role != Role.ADMIN) {
            throw new AccessDeniedException("Access Denied: ADMIN only");
        }
    }

    public static void analystOrAdmin(Role role) {
        if (role != Role.ADMIN && role != Role.ANALYST) {
            throw new AccessDeniedException("Access Denied: ANALYST or ADMIN only");
        }
    }

    public static void dashboardAccess(Role role) {
        if (role != Role.ADMIN && role != Role.ANALYST && role != Role.VIEWER) {
            throw new AccessDeniedException("Access Denied");
        }
    }
}
