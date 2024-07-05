package DTO;

public class addDescriptionDTO {
	private String title;  
	private String description;
	private String userEmail;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getUserEmail() {
		return userEmail;
	}
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	public addDescriptionDTO(String description, String userEmail, String title) {
		
		this.description = description;
		this.userEmail = userEmail;
		this.title = title;
	}
	public addDescriptionDTO() {
		super();
	}
	
	

}
