package com.example.springRest.Response;

import java.util.Date;

import com.example.springRest.Model.Admin;

import lombok.Data;

@Data
public class PostReq {
	private Long pkPostId;
	private String title;
	private String content;
	private Admin author;
	private Date createdDate;
	private String status;
	private String coverImageUrl;
}
