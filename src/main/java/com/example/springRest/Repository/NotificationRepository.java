package com.example.springRest.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.springRest.Model.Admin;
import com.example.springRest.Model.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByRecipientOrderByCreatedAtDesc(Admin recipient);

    List<Notification> findByRecipientAndReadFalseOrderByCreatedAtDesc(Admin recipient);

    long countByRecipientAndReadFalse(Admin recipient);
}
