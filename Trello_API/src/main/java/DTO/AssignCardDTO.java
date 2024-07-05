package DTO;

public class AssignCardDTO {
	
	private String assigneEmail;
	private String assignedToEmail;
	private String cardTitle;
	
	public String getAssigneEmail() {
		return assigneEmail;
	}
	public void setAssigneEmail(String assigneEmail) {
		this.assigneEmail = assigneEmail;
	}
	public String getAssignedToEmail() {
		return assignedToEmail;
	}
	public void setAssignedToEmail(String assignedToEmail) {
		this.assignedToEmail = assignedToEmail;
	}
	public String getCardTitle() {
		return cardTitle;
	}
	public void setCardTitle(String cardTitle) {
		this.cardTitle = cardTitle;
	}
	public AssignCardDTO(String assigneEmail, String assignedToEmail, String cardTitle) {
		super();
		this.assigneEmail = assigneEmail;
		this.assignedToEmail = assignedToEmail;
		this.cardTitle = cardTitle;
	}
	public AssignCardDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	

}
