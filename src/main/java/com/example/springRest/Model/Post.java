package com.example.springRest.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Data
@Entity
public class Post {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long pkPostId;
private String title;

@Column(columnDefinition = "TEXT")
private String content;

private String status;

@Temporal(TemporalType.TIMESTAMP)
private java.util.Date createdDate;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "author_Id")
private Admin author;

private String coverImageUrl;
}
