package com.example.springRest.Response;

import java.time.LocalDate;

import lombok.Data;

@Data
public class ProfileUpdateReq {
	private String name;
	private String bio;
	private String profilePictureUrl;
	private String website;
	private String location;
	private String gender;
	private LocalDate dateOfBirth;
	private String contactNumber;
}
