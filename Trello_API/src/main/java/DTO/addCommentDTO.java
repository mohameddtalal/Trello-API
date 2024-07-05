package DTO;

public class addCommentDTO {
	private String comment;
	private String title;
	private String userEmail;
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUserEmail() {
		return userEmail;
	}
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	public addCommentDTO(String comment, String title, String userEmail) {
		
		this.comment = comment;
		this.title = title;
		this.userEmail = userEmail;
	}
	public addCommentDTO() {
		super();
	}
	
	public addCommentDTO(String title) {
		this.title = title;
	}
	
	
}
