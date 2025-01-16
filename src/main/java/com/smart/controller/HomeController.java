package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
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
import org.springframework.web.multipart.MultipartFile;
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
	
	@PostMapping("/signin")
	public String authenticateUser(@RequestParam("email") String email,
	@RequestParam("password") String password ,Model model , HttpSession session) {
		if(service.authenticate(email, password)) {
			session.setAttribute("user", email);
			return "user_dashboard";
		}
		else {
			model.addAttribute("message", "Invalid Username or password");
			return "login";
		}
		
		
	}
		
	
	
//	@PostMapping("/do_register")
//	public String registeredUser(@Valid  @ModelAttribute("user") User user 
//			,BindingResult result, @RequestParam(value="agreement",defaultValue="false")boolean agreement,
//			Model model,HttpSession session , Principal principal,
//			@RequestParam("profileImage")MultipartFile file ) {
//		try {
//			
//			
//			
////			String name = principal.getName();
//	   	       
//		    User u = this.userRepo.getUserByUsername(name);
//		   
//		    //processing and uploading file... 
//		    if(!file.isEmpty()) {
//		    	 u.setImageUrl(file.getOriginalFilename());
//		    	 String fileName = file.getOriginalFilename();
//		    	 //String path = "C:\\Users\\SIC\\Documents\\workspace-spring-tool-suite-4-4.24.0.RELEASE\\smartcontactmanager\\src\\main\\resources\\static\\uploaded_files";
//		    	 String path = new ClassPathResource("static/img").getFile().getAbsolutePath();
//		    	 String filePath= path+File.separator+fileName;
//		    	 File f = new File(path);
//		    	 if(!f.exists()) {
//		    		 f.mkdir();
//		    	 }
//		    	 Files.copy(file.getInputStream(), Paths.get(filePath),StandardCopyOption.REPLACE_EXISTING);
//		    	 System.out.println("Image is uploaded "+filePath);
//		    
//		    	
//		    	 
////		    	//upload the file to folder & update the name in contact
////		    	contact.setImage(file.getOriginalFilename());
////		    	File saveFile = new ClassPathResource("static/img").getFile();
////		    	
////		    	Path path =   Paths.get(saveFile.getAbsolutePath()+ File.separator+file.getOriginalFilename());
////		    	Files.copy(file.getInputStream(), path,StandardCopyOption.REPLACE_EXISTING);
//		    	
//		    }else {
//	            // If no file is uploaded, set a default image
//	            u.setImageUrl("contact.jpg");
//	        }
//			
//			
//			if(!agreement) {
//				System.out.println("You Have Not Agreed Terms & Condition ");
//				throw new Exception("You Have Not Agreed Terms & Conditions ");
//			}
//				
//			if(result.hasErrors()) {
//				System.out.println("Error...."+result.toString());
//				model.addAttribute("user",user);
//				return "signup";
//			}
//				user.setRole("ROLE_USER");
//				user.setEnabled(true);
//				user.setPassword(passwordEncoder.encode(user.getPassword()));
//				System.out.println("User "+user);
//				
//				User res = this.userRepo.save(user);
//				System.out.println(result);
//				model.addAttribute("user",new User());
//				if(!result.hasErrors()) {
//				model.addAttribute("title" ,"Successfully Registered...");
//				}
//				
//				
//				session.setAttribute("message", new Message("Successfully Registered... !! ","alert-success"));
//				return "signup";
//				
//		}
//		catch(Exception e) {
//			e.printStackTrace();
//			model.addAttribute("title" ,"Opps! Registration Failed...");
//			model.addAttribute("user",user);
//			if(!agreement) {
//				session.setAttribute("message", new Message("Something went wrong !! "+e.getMessage(),"alert-danger"));
//			}
//			else {
//				session.setAttribute("message", new Message("Something went wrong !! ","alert-danger"));
//			}
//			
//			return "signup";
//		}
	
//		
	
	@PostMapping("/do_register")
	public String registeredUser(@Valid @ModelAttribute("user") User user, 
	                             BindingResult result, 
	                             @RequestParam(value = "agreement", defaultValue = "false") boolean agreement,
	                             Model model, 
	                             HttpSession session, 
	                             Principal principal,  // principal is null if user is not logged in
	                             @RequestParam("profileImage") MultipartFile file) {
	    try {
	        // If user is logged in (principal is not null), redirect to home page (or any other relevant page)
	        if (principal != null) {
	            session.setAttribute("message", new Message("You are already logged in.", "alert-info"));
	            return "redirect:/home";  // Redirect to home/dashboard page if already logged in
	        }

	        // Now we know the user is not logged in, proceed with registration
	        // Handle file upload (if any)
	        if (!file.isEmpty()) {
	            user.setImageUrl(file.getOriginalFilename());
	            String fileName = file.getOriginalFilename();
	            String path = new ClassPathResource("static/img").getFile().getAbsolutePath();
	            String filePath = path + File.separator + fileName;
	            File f = new File(path);
	            if (!f.exists()) {
	                f.mkdir();
	            }
	            Files.copy(file.getInputStream(), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
	            System.out.println("Image is uploaded " + filePath);
	        } else {
	            user.setImageUrl("contact.jpg");  // Use a default image if no image is uploaded
	        }

	        // Check if the user has agreed to the terms and conditions
	        if (!agreement) {
	            throw new Exception("You Have Not Agreed To Terms & Conditions");
	        }

	        // Check for validation errors
	        if (result.hasErrors()) {
	            model.addAttribute("user", user);
	            return "signup";  // Return to signup page if there are validation errors
	        }

	        // Set user role, enabled status, and encode password
	        user.setRole("ROLE_USER");
	        user.setEnabled(true);
	        user.setPassword(passwordEncoder.encode(user.getPassword()));

	        // Save the user to the database
	        User res = this.userRepo.save(user);
	        model.addAttribute("user", new User());  // Clear the form after successful registration
	        model.addAttribute("title", "Successfully Registered...");
	        session.setAttribute("message", new Message("Successfully Registered... !!", "alert-success"));
	        return "signup";  // Show success message on the signup page

	    } catch (Exception e) {
	        e.printStackTrace();
	        model.addAttribute("title", "Oops! Registration Failed...");
	        model.addAttribute("user", user);  // Retain user details in case of failure
	        session.setAttribute("message", new Message("Something went wrong !! " + e.getMessage(), "alert-danger"));
	        return "signup";  // Return to signup page in case of error
	    }
	}

	
	
	
	
		
	}
	
	




