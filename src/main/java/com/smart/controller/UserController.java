package com.smart.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;

import jakarta.persistence.criteria.Path;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserRepository userRepo;
	@ModelAttribute
	public void addCommonData(Model model , Principal principal) {
		String user1 =principal.getName();
		System.out.println(user1);
		User user = userRepo.getUserByUsername(user1);
		System.out.println("User "+user );
		model.addAttribute("user", user);
		
	}
	
	@RequestMapping("/index")
	public String userDashboard(Model model, Principal principal) {
		model.addAttribute("title", "User Dashboard : Smart Contact Manager");
		return "normal/user_dashboard";
	}
	
	//Open add form Handler
	
	@GetMapping("/add-contact")
	public String addFormHandler(Model model) {
		model.addAttribute("title","Add Contact");
		model.addAttribute("contact",new Contact());
		
		return "normal/add_contact_form";
	}
	
	
	//process  to add contact on database
	@PostMapping("/process-contact")
	public String processAddContact ( @Valid
		    @ModelAttribute Contact contact,
		    @RequestParam("img") MultipartFile file,
		    Principal principal,
		    HttpSession session
		)  {
		   try {
			// Get the currently logged-in user
	   	       String name = principal.getName();
	   	       
			    User user = this.userRepo.getUserByUsername(name);
			   
			    //processing and uploading file... 
			    if(!file.isEmpty()) {
			    	 contact.setImg(file.getOriginalFilename());
			    	 String fileName = file.getOriginalFilename();
			    	 String path = "C:\\Users\\SIC\\Documents\\workspace-spring-tool-suite-4-4.24.0.RELEASE\\smartcontactmanager\\src\\main\\resources\\static\\uploaded_files";
			    	 String filePath= path+File.separator+fileName;
			    	 File f = new File(path);
			    	 if(!f.exists()) {
			    		 f.mkdir();
			    	 }
			    	 Files.copy(file.getInputStream(), Paths.get(filePath));
			    	 System.out.println("Image is uploaded");
			    	 
			    	 
			    	
			    	
			    	
			    	
			    	
			    	 
//			    	//upload the file to folder & update the name in contact
//			    	contact.setImage(file.getOriginalFilename());
//			    	File saveFile = new ClassPathResource("static/img").getFile();
//			    	
//			    	Path path =   Paths.get(saveFile.getAbsolutePath()+ File.separator+file.getOriginalFilename());
//			    	Files.copy(file.getInputStream(), path,StandardCopyOption.REPLACE_EXISTING);
			    	
			    }
			     
			    contact.setUser(user);
			    
			    user.getContactList().add(contact);
			    this.userRepo.save(user);
			    System.out.println("saved to database");
			    System.out.println("data : "+ contact);
			  //message success
			    session.setAttribute("message", new Message("Your Contact added Successfully","success"));
		 
		   }
		   catch(Exception e ) {
			   
			   System.out.println("Error : "+e.getMessage());
			   e.printStackTrace();
			 //message error
			    session.setAttribute("message", new Message("Opps ! Something went wrong. Try Again","danger"));
		   }
		    	 

		    return "normal/add_contact_form";
		}
	
	

}


//package com.smart.controller;
//
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.security.Principal;
//import java.util.UUID;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.multipart.MultipartFile;
//
//import com.smart.dao.UserRepository;
//import com.smart.entities.Contact;
//import com.smart.entities.User;
//
//import jakarta.validation.Valid;
//
//@Controller
//@RequestMapping("/user")
//public class UserController {
//
//    @Autowired
//    private UserRepository userRepo;
//
//    private static final String UPLOAD_DIR = "src/main/resources/static/img";
//
//    @ModelAttribute
//    public void addCommonData(Model model, Principal principal) {
//        String username = principal.getName();
//        User user = userRepo.getUserByUsername(username);
//        model.addAttribute("user", user);
//    }
//
//    @RequestMapping("/index")
//    public String userDashboard(Model model) {
//        model.addAttribute("title", "User Dashboard : Smart Contact Manager");
//        return "normal/user_dashboard";
//    }
//
//    @GetMapping("/add-contact")
//    public String addFormHandler(Model model) {
//        model.addAttribute("title", "Add Contact");
//        model.addAttribute("contact", new Contact());
//        return "normal/add_contact_form";
//    }
//
//    @PostMapping("/process-contact")
//    public String processAddContact(
//    		@Valid @ModelAttribute("contact")  Contact contact,
//            @RequestParam("image") MultipartFile image,
//            Principal principal,
//            Model model) {
//
//        try {
//            if (!image.isEmpty()) {
//                // Generate unique file name and store it
//                String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
//                Path uploadPath = Paths.get(UPLOAD_DIR);
//
//                if (!Files.exists(uploadPath)) {
//                    Files.createDirectories(uploadPath);
//                }
//
//                Path filePath = uploadPath.resolve(fileName);
//                image.transferTo(filePath.toFile());
//                contact.setImage(fileName);
//            } else {
//                contact.setImage("default.png");
//            }
//
//            // Link contact with the logged-in user
//            String username = principal.getName();
//            User user = userRepo.getUserByUsername(username);
//            contact.setUser(user);
//            user.getContactList().add(contact);
//
//            // Save user (cascades and saves contact as well)
//            userRepo.save(user);
//
//            model.addAttribute("message", "Contact added successfully!");
//        } catch (IOException e) {
//            e.printStackTrace();
//            model.addAttribute("error", "File upload failed");
//            return "normal/add_contact_form?error";
//        }
//
//        return "normal/add_contact_form";  // Redirect to add contact form or success page
//    }
//}
