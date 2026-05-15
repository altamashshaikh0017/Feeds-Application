package com.example.springRest.Controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.springRest.Model.Admin;
import com.example.springRest.Model.Notification;
import com.example.springRest.Service.AdminService;
import com.example.springRest.Service.NotificationService;
import com.example.springRest.Service.SseService;

@Controller
public class NotificationController {

    @Autowired private NotificationService notificationService;
    @Autowired private AdminService        adminService;
    @Autowired private SseService          sseService;

    @GetMapping(value = "/notifications/stream", produces = "text/event-stream")
    @ResponseBody
    public SseEmitter stream(Principal principal) {
        return sseService.subscribe(principal.getName());
    }

    @GetMapping("/notifications")
    public String viewNotifications(Model model, Principal principal) {
        Admin user = adminService.findByUsername(principal.getName());
        List<Notification> notifications = notificationService.getNotifications(user);
        notificationService.markAllRead(user);
        model.addAttribute("notifications", notifications);
        model.addAttribute("loggedInUser",  user);
        return "notifications";
    }

    @PostMapping("/notifications/markAllRead")
    public String markAllRead(Principal principal) {
        Admin user = adminService.findByUsername(principal.getName());
        notificationService.markAllRead(user);
        return "redirect:/notifications";
    }

    @PostMapping("/notifications/markRead/{id}")
    public String markRead(@PathVariable Long id, Principal principal) {
        Admin user = adminService.findByUsername(principal.getName());
        notificationService.markRead(id, user);
        return "redirect:/notifications";
    }
}
