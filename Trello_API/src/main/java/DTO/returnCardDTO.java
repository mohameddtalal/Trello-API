package DTO;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import Entity.User;

public class returnCardDTO {
    private String title;
	
private String description;
    
    
//	private List<String> comments;


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}

//
//	public User getAssignee() {
//		return assignee;
//	}
//
//
//	public void setAssignee(User assignee) {
//		this.assignee = assignee;
//	}



	

	public returnCardDTO(String description, String title) {
	super();
	this.description = description;
	this.title = title;
}

//
//	public List<String> getComments() {
//		return comments;
//	}
//
//
//	public void setComments(List<String> comments) {
//		this.comments = comments;
//	}


	public returnCardDTO() {
		super();
		
	}
	
	
}
