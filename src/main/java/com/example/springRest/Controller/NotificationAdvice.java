package com.example.springRest.Controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.example.springRest.Model.Admin;
import com.example.springRest.Service.AdminService;
import com.example.springRest.Service.NotificationService;

@ControllerAdvice
public class NotificationAdvice {

    @Autowired private NotificationService notificationService;
    @Autowired private AdminService        adminService;

    @ModelAttribute("unreadNotificationCount")
    public long unreadCount(Principal principal) {
        if (principal == null) return 0;
        Admin user = adminService.findByUsername(principal.getName());
        if (user == null) return 0;
        return notificationService.getUnreadCount(user);
    }
}
