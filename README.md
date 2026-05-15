
  # SpringBoot Feeds Application

  A full-stack social feeds platform built with **Spring Boot 3** where users can publish posts,
  interact with others through likes and comments, and manage their public profile — all behind
  a role-based authentication system.

  ---

  ## Features

  ### Authentication & Security
  - User registration and login with **Spring Security** (session-based)
  - Passwords hashed with **BCrypt**
  - Role-based access control: `USER` and `ADMIN` roles
  - Forgot password / reset password via **email token** (Gmail SMTP)

     ---

  ## Tech Stack

  | Layer | Technology |
  | Backend | Java 17, Spring Boot 3.3.2 |
  | Security | Spring Security 6, BCrypt |
  | Persistence | Spring Data JPA, H2 (in-memory, MySQL mode) |
  | Templating | Thymeleaf + Thymeleaf Security Extras |
  | File Storage | Cloudinary |
  | Email | Spring Mail (Gmail SMTP) |
  | Frontend | Bootstrap 5.3, Google Fonts (Inter) |
  | Build | Maven, Lombok |

  ---
  
  ## Getting Started

  ### Prerequisites
  - Java 17+
  - Maven 3.8+
  - A free [Cloudinary] account
  - A Gmail account with an app password enabled
