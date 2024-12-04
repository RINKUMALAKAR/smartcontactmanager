package com.smart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;
import com.smart.service.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HomeController {
	@Autowired
	private UserService service;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepository userRepo;
	
	@RequestMapping("/")
	public String home(Model model) {
		
		model.addAttribute("title" ,"Home : This is Home Page");
		return "home";
		
	}
	
	
	@RequestMapping("/about")
	public String about(Model model) {
		
		model.addAttribute("title" ,"About : This is About Page");
		return "about";
		
	}
	
	@RequestMapping("/signup")
	public String userSignup(Model model) {
		
		model.addAttribute("title" ,"Register : This is SignUp Page");
		model.addAttribute("user",new User());
		return "signup";
		
	}
	
	@GetMapping("/signin")
	public String userLogin(Model model) {
		
		model.addAttribute("title" ,"Login : This is Login Page");
		return "login";
	}
	
//	@PostMapping("/signin")
//	public String authenticateUser(@RequestParam("email") String email,
//	@RequestParam("password") String password ,Model model , HttpSession session) {
//		if(service.authenticate(email, password)) {
//			session.setAttribute("user", email);
//			return "user_dashboard";
//		}
//		else {
//			model.addAttribute("message", "Invalid Username or password");
//			return "login";
//		}
//		
//		
//	}
		
	
	
	@PostMapping("/do_register")
	public String registeredUser(@Valid  @ModelAttribute("user") User user ,BindingResult result, @RequestParam(value="agreement",defaultValue="false")boolean agreement,Model model,HttpSession session) {
		try {
			if(!agreement) {
				System.out.println("You Have Not Agreed Terms & Condition ");
				throw new Exception("You Have Not Agreed Terms & Conditions ");
			}
				
			if(result.hasErrors()) {
				System.out.println("Error...."+result.toString());
				model.addAttribute("user",user);
				return "signup";
			}
				user.setRole("ROLE_USER");
				user.setEnabled(true);
				user.setPassword(passwordEncoder.encode(user.getPassword()));
				System.out.println("User "+user);
				
				User res = this.userRepo.save(user);
				System.out.println(result);
				model.addAttribute("user",new User());
				if(!result.hasErrors()) {
				model.addAttribute("title" ,"Successfully Registered...");
				}
				
				
				session.setAttribute("message", new Message("Successfully Registered... !! ","alert-success"));
				return "signup";
				
		}
		catch(Exception e) {
			e.printStackTrace();
			model.addAttribute("title" ,"Opps! Registration Failed...");
			model.addAttribute("user",user);
			if(!agreement) {
				session.setAttribute("message", new Message("Something went wrong !! "+e.getMessage(),"alert-danger"));
			}
			else {
				session.setAttribute("message", new Message("Something went wrong !! ","alert-danger"));
			}
			
			return "signup";
		}
		
		
	}
	
	


}

