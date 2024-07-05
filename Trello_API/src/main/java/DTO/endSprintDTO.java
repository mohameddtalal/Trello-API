package DTO;

import java.util.Date;

public class endSprintDTO {
	
	private String newSprintName;
	
	private String oldSprintName;
	
	private Date newEndDate;

	private String userEmail;
	
	public endSprintDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getNewSprintName() {
		return newSprintName;
	}

	public void setNewSprintName(String newSprintName) {
		this.newSprintName = newSprintName;
	}

	public String getOldSprintName() {
		return oldSprintName;
	}

	public void setOldSprintName(String oldSprintName) {
		this.oldSprintName = oldSprintName;
	}

	public Date getNewEndDate() {
		return newEndDate;
	}

	public void setNewEndDate(Date newEndDate) {
		this.newEndDate = newEndDate;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public endSprintDTO(String newSprintName, String oldSprintName, Date newEndDate, String userEmail) {
		super();
		this.newSprintName = newSprintName;
		this.oldSprintName = oldSprintName;
		this.newEndDate = newEndDate;
		this.userEmail = userEmail;
	}
	
	
	
	
}
