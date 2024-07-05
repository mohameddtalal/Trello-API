package DTO;

public class inviteCollaboratorDTO {
	
    private String invitedEmail;
    private String inviterEmail;
    private String boardName;

	public String getInvitedEmail() {
		return invitedEmail;
	}

	public void setInvitedEmail(String invitedEmail) {
		this.invitedEmail = invitedEmail;
	}

	public String getInviterEmail() {
		return inviterEmail;
	}

	public void setInviterEmail(String inviterEmail) {
		this.inviterEmail = inviterEmail;
	}

	public String getBoardName() {
		return boardName;
	}

	public void setBoardName(String boardName) {
		this.boardName = boardName;
	}

	
}
