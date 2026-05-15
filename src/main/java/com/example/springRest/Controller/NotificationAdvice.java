package com.example.springRest.Controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.example.springRest.Model.Admin;
import com.example.springRest.Service.AdminService;
import com.example.springRest.Service.NotificationService;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class NotificationAdvice {

    @Autowired private NotificationService notificationService;
    @Autowired private AdminService        adminService;

    @ModelAttribute("unreadNotificationCount")
    public long unreadCount(Principal principal, HttpServletRequest request) {
        if (principal == null) return 0;
        // SSE stream requests are long-lived — skipping the DB query here prevents
        // OSIV from acquiring a HikariCP connection that would never be released.
        if ("/notifications/stream".equals(request.getRequestURI())) return 0;
        Admin user = adminService.findByUsername(principal.getName());
        if (user == null) return 0;
        return notificationService.getUnreadCount(user);
    }
}
