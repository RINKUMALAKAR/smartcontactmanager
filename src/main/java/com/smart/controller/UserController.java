package com.smart.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
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
	@Autowired
	private ContactRepository contactRepo;
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
	public String processAddContact ( 
		    @ModelAttribute Contact contact,
		    @RequestParam("profileImage") MultipartFile file,
		    Principal principal,
		    HttpSession session
		)  {
		   try {
			// Get the currently logged-in user
	   	       String name = principal.getName();
	   	       
			    User user = this.userRepo.getUserByUsername(name);
			   
			    //processing and uploading file... 
			    if(!file.isEmpty()) {
			    	 contact.setImage(file.getOriginalFilename());
			    	 String fileName = file.getOriginalFilename();
			    	 //String path = "C:\\Users\\SIC\\Documents\\workspace-spring-tool-suite-4-4.24.0.RELEASE\\smartcontactmanager\\src\\main\\resources\\static\\uploaded_files";
			    	 String path = new ClassPathResource("static/img").getFile().getAbsolutePath();
			    	 String filePath= path+File.separator+fileName;
			    	 File f = new File(path);
			    	 if(!f.exists()) {
			    		 f.mkdir();
			    	 }
			    	 Files.copy(file.getInputStream(), Paths.get(filePath),StandardCopyOption.REPLACE_EXISTING);
			    	 System.out.println("Image is uploaded "+filePath);
			    
			    	
			    	 
//			    	//upload the file to folder & update the name in contact
//			    	contact.setImage(file.getOriginalFilename());
//			    	File saveFile = new ClassPathResource("static/img").getFile();
//			    	
//			    	Path path =   Paths.get(saveFile.getAbsolutePath()+ File.separator+file.getOriginalFilename());
//			    	Files.copy(file.getInputStream(), path,StandardCopyOption.REPLACE_EXISTING);
			    	
			    }else {
		            // If no file is uploaded, set a default image
		            contact.setImage("contact.jpg");
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
	
	
	//Show contacts Handler
	//per page =5[n]
	//current page = 0[page]
	@GetMapping("/show-contacts/{page}")
	public  String showContacts(@PathVariable("page") Integer page,Model m , Principal principal) {
	String username = principal.getName();
	User user = this.userRepo.getUserByUsername(username);
//		List<Contact> list = user.getContactList();
	Pageable pageable= PageRequest.of(page,5);
	
	Page<Contact> list = this.contactRepo.findContactByUser(user.getId() , pageable);
	m.addAttribute("contacts",list);
	m.addAttribute("currentPage", page);
	m.addAttribute("totalPages", list.getTotalPages());
	m.addAttribute("title","Show Contacts ");
	
		return "normal/show_contacts";
	}
	
	
	//Showing contact details
	@RequestMapping("/contact/{cId}")
	public String showContactDetails(@PathVariable("cId") Integer cid ,Model m,Principal prin) {
		System.out.println("cId "+cid);
		Optional<Contact> contact =this.contactRepo.findById(cid);
		Contact con = contact.get();
		
		String userName = prin.getName();
		User user  = this.userRepo.getUserByUsername(userName);
		if(user.getId() == con.getUser().getId()) 
			m.addAttribute("details", con);
		m.addAttribute("title", con.getName());
		
		
		
		return "normal/contact_details";
	}
	
	
	@GetMapping("/delete/{cId}")
	public String deleteContact(@PathVariable("cId") Integer cId, 
			Principal prin, HttpSession session,Model m) {
		Contact contact = this.contactRepo.findById(cId).get();
		String username = prin.getName();
		User user  =this.userRepo.getUserByUsername(username);
		if(user.getId() == contact.getUser().getId()) {
//			contact.setUser(null);
//			this.contactRepo.delete(contact);
			user.getContactList().remove(contact);
			this.userRepo.save(user);
		}
		
	    

	    return "redirect:/user/show-contacts/0";
	}
	
	//update COntact Hander
	@PostMapping("/update-contact/{cId}")
	public String updateContact(@PathVariable("cId")Integer id , Model m ) {
		m.addAttribute("title","Update Contact");
		Contact contact = this.contactRepo.findById(id).get();
		m.addAttribute("contact",contact);
		
		return "normal/update_contact";
	}
	
	
	//process-update hander
	@PostMapping("/process-update")
	public String processUpdateHandler(@ModelAttribute Contact contact ,
	@RequestParam("profileImage") MultipartFile file ,Model m,
	HttpSession session ,Principal prin) {
		System.out.println("COntact name " +contact.getName());
		System.out.println("contact id :" + contact.getcId()); 
		Contact oldContact = this.contactRepo.findById(contact.getcId()).get();
		try {
			if(!file.isEmpty()) {
				//image file
				//rewrite the
				//delete old photo and insert new photo
				
				//for delete photo
				File deletePhoto = new ClassPathResource("static/img").getFile();
				File delFile = new File(deletePhoto , oldContact.getImage());
				delFile.delete();
				
				
				//for insert new photo
		    	String fileName = file.getOriginalFilename();
				String path = new ClassPathResource("static/img").getFile().getAbsolutePath();
		    	 String filePath= path+File.separator+fileName;
		    	 File f = new File(path);
		    	 if(!f.exists()) {
		    		 f.mkdir();
		    	 }
		    	 Files.copy(file.getInputStream(), Paths.get(filePath),StandardCopyOption.REPLACE_EXISTING);
		    	 System.out.println("Image is uploaded "+filePath);
		    	 contact.setImage(file.getOriginalFilename());
		    	 
				
				
				
			}
			else {
				//if file is empty
				contact.setImage(oldContact.getImage());
			}
			User user = this.userRepo.getUserByUsername(prin.getName());
			contact.setUser(user);
			this.contactRepo.save(contact);
			session.setAttribute("message", new Message("Your contact is updated","success"));
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		
		return "redirect:/user/contact/"+contact.getcId();
		
	}
	
	//remove attribute
	@PostMapping("/remove-message")
	@ResponseBody
	public void removeMessage(HttpSession session) {
	    session.removeAttribute("message");
	}
	
	
	//User Profile handler
	@GetMapping("/profile")
	public String profileHander(Model m , @ModelAttribute User user) {
		
		m.addAttribute("title","User Profile");
		return "normal/user_profile";
	}
	
	
	//User settings Hander
		@RequestMapping("/settings")
		public String userSettings( 
				Model m ,@ModelAttribute User user) {
			m.addAttribute("title","Settings");
//			User user = this.userRepo.findById(id).get();
//			m.addAttribute("user",user);
			
			return "normal/user_settings";
		}
		
	// user settings process Handler
		
		//process-update hander
		@PostMapping("/process-settings-update")
		public String settingsUpdateHandler(@ModelAttribute User user ,
		@RequestParam("profileImage") MultipartFile file ,Model m,
		@RequestParam("password") String newPassword ,
		HttpSession session ,Principal prin) {
//			System.out.println("COntact name " +contact.getName());
//			System.out.println("contact id :" + contact.getcId()); 
//			Contact oldContact = this.contactRepo.findById(contact.getcId()).get();
			User oldUser = this.userRepo.findById(user.getId()).get();
			try {
				
				if(newPassword != null && !newPassword.isEmpty()) {
					BCryptPasswordEncoder encoder = new  BCryptPasswordEncoder();
					String hashPassword = encoder.encode(newPassword);
					user.setPassword(hashPassword);
				}
				
				
				if(!file.isEmpty()) {
					//image file
					//rewrite the
					//delete old photo and insert new photo
					
					//for delete photo
					File deletePhoto = new ClassPathResource("static/img").getFile();
					File delFile = new File(deletePhoto , oldUser.getImageUrl());
					delFile.delete();
					
					
					//for insert new photo
			    	String fileName = file.getOriginalFilename();
					String path = new ClassPathResource("static/img").getFile().getAbsolutePath();
			    	 String filePath= path+File.separator+fileName;
			    	 File f = new File(path);
			    	 if(!f.exists()) {
			    		 f.mkdir();
			    	 }
			    	 Files.copy(file.getInputStream(), Paths.get(filePath),StandardCopyOption.REPLACE_EXISTING);
			    	 System.out.println("Image is uploaded "+filePath);
			    	 user.setImageUrl(file.getOriginalFilename());
			    	 
					
					
					
				}
				else {
					//if file is empty
					user.setImageUrl(oldUser.getImageUrl());
				}
//				User user = this.userRepo.getUserByUsername(prin.getName());
//				contact.setUser(user);
//				user.setName(user.getName());
//				user.setAbout(user.getAbout());
//				user.setEmail(user.getEmail());
			
				this.userRepo.save(user);
				session.setAttribute("message", new Message("Your Settings is updated","success"));
				
			}
			catch(Exception e){
				e.printStackTrace();
				session.setAttribute("message", new Message("Something Went Wrong !! ","danger"));
			}
			
			
			return "redirect:/user/settings";
			
		}	


	

}


