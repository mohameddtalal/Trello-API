package DTO;

import java.util.Date;

public class sprintDTO {
	private String name;
	private Date startDate;
	private Date endDate; 
	private String userEmail;
	
	public sprintDTO(String name, Date startDate, Date endDate,String userEmail) {
		super();
		this.name = name;
		this.startDate = startDate;
		this.endDate = endDate;
	
		this.userEmail = userEmail;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	
	public String getUserEmail() {
		return userEmail;
	}
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	public sprintDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
}
