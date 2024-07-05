package DTO;

public class CommentDTO {
	private String comment;
	private String userEmail;
	public CommentDTO(String userEmail, String comment) {
		super();
		this.userEmail = userEmail;
		this.comment = comment;
	}
	public String getUserEmail() {
		return userEmail;
	}
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public CommentDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	
}
