package com.example.springRest.Service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.springRest.Model.Admin;
import com.example.springRest.Model.Notification;
import com.example.springRest.Model.Post;
import com.example.springRest.Repository.NotificationRepository;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired @Lazy
    private SseService sseService;

    public void notifyLike(Post post, Admin actor) {
        Admin recipient = post.getAuthor();
        if (recipient.getPkAdminId().equals(actor.getPkAdminId())) return;

        Notification n = new Notification();
        n.setRecipient(recipient);
        n.setActor(actor);
        n.setPost(post);
        n.setType("LIKE");
        n.setMessage(actor.getName() + " liked your post \"" + truncate(post.getTitle(), 40) + "\"");
        n.setCreatedAt(LocalDateTime.now());
        notificationRepository.save(n);
        sseService.sendNotificationCount(recipient.getUserName(),
                notificationRepository.countByRecipientAndReadFalse(recipient));
    }

    public void notifyComment(Post post, Admin actor, String commentContent) {
        Admin recipient = post.getAuthor();
        if (recipient.getPkAdminId().equals(actor.getPkAdminId())) return;

        Notification n = new Notification();
        n.setRecipient(recipient);
        n.setActor(actor);
        n.setPost(post);
        n.setType("COMMENT");
        n.setMessage(actor.getName() + " commented on \"" + truncate(post.getTitle(), 30) + "\": " + truncate(commentContent, 40));
        n.setCreatedAt(LocalDateTime.now());
        notificationRepository.save(n);
        sseService.sendNotificationCount(recipient.getUserName(),
                notificationRepository.countByRecipientAndReadFalse(recipient));
    }

    public List<Notification> getNotifications(Admin user) {
        return notificationRepository.findByRecipientOrderByCreatedAtDesc(user);
    }

    public long getUnreadCount(Admin user) {
        return notificationRepository.countByRecipientAndReadFalse(user);
    }

    @Transactional
    public void markAllRead(Admin user) {
        List<Notification> unread = notificationRepository.findByRecipientAndReadFalseOrderByCreatedAtDesc(user);
        unread.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unread);
    }

    @Transactional
    public void markRead(Long id, Admin user) {
        notificationRepository.findById(id).ifPresent(n -> {
            if (n.getRecipient().getPkAdminId().equals(user.getPkAdminId())) {
                n.setRead(true);
                notificationRepository.save(n);
            }
        });
    }

    private static String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max - 1) + "…";
    }
}
