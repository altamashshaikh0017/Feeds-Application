package com.example.springRest.Controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.springRest.Entity.User;
import com.example.springRest.Response.AdminReq;

@Controller
public class UserController {
	List<User> ulist = new ArrayList<>();
	
	private static final Logger logger = LogManager.getLogger(UserController.class);
	
//	  @GetMapping("/register")
//	    public String registerPage() {
//		  logger.info("Inside UserController::registerPage()");
//	        return "register";
//	    }
//
//	    @PostMapping("/register")
//	    public String register(@RequestParam String username,
//	                           @RequestParam String password,
//	                           @RequestParam String email,
//	                           @RequestParam String phoneNo,
//	                           @RequestParam String role) {
//	        User user = new User();
//	        user.setUsername(username);
//	        user.setPassword(password); // Ideally, password should be encoded
//	        user.setEmail(email);
//	        user.setPhoneNo(phoneNo);
//	        user.setRole(role);
//	        ulist.add(user);
//	        System.out.println(ulist);
////	        userRepository.save(user);
//	        logger.info("Inside UserController::register()");
//	        return "redirect:/login";
//	    }
	
	@GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("admin", new AdminReq());
        return "register";
    }
	    
	    @GetMapping("/login")
	    public String loginPage(Model model , @RequestParam(value = "error", required = false) String error) {
	        if(error!=null) {
	        	model.addAttribute("error" , "Invalid Username or Password");
	        }
	        return "login";
	    }
}
