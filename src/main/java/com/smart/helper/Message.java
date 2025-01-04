package com.smart.helper;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;



@Component("message")
public class Message {
   private String content;
   private String type;
   
public Message(String content, String type) {
	super();
	this.content = content;
	this.type = type;
}
public Message() {
	super();
	// TODO Auto-generated constructor stub
}

public boolean removeMessageFromSession(String attributeName) {
    try {
        // Retrieve the current request attributes
        ServletRequestAttributes attributes = 
            (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes != null) {
            // Get the session, if available
            HttpSession session = attributes.getRequest().getSession(false);

            if (session != null) {
                // Check if the attribute exists before removal
                if (session.getAttribute(attributeName) != null) {
                    session.removeAttribute(attributeName);
                    System.out.println("Removed attribute '" + attributeName + "' from session.");
                    return true; // Successful removal
                } else {
                    System.out.println("Attribute '" + attributeName + "' not found in session.");
                }
            } else {
                System.out.println("No session available to remove the attribute.");
            }
        } else {
            System.out.println("No request context available.");
        }
    } catch (Exception e) {
        // Print stack trace for debugging
        e.printStackTrace();
    }
    return false; // Attribute not removed
}

public String getContent() {
	return content;
}
public void setContent(String content) {
	this.content = content;
}
public String getType() {
	return type;
}
public void setType(String type) {
	this.type = type;
}

}
