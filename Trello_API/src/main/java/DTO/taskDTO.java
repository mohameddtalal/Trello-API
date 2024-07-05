package DTO;


import Entity.Task.TaskStatus;

public class taskDTO {

		private String name;
	   
	    private String status;
		
		private int storyPoints; 
		
		private String userEmail;
		
		private String sprintName;
		
		

		public String getSprintName() {
			return sprintName;
		}

		public void setSprintName(String sprintName) {
			this.sprintName = sprintName;
		}

		public taskDTO(String name, String status, Integer storyPoints, String userEmail, String sprintName) {
			super();
			this.name = name;
			this.status = status;
			this.storyPoints = storyPoints;
			this.userEmail = userEmail;
			this.sprintName = sprintName;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public int getStoryPoints() {
			return storyPoints;
		}

		public void setStoryPoints(int storyPoints) {
			this.storyPoints = storyPoints;
		}

		public String getUserEmail() {
			return userEmail;
		}

		public void setUserEmail(String userEmail) {
			this.userEmail = userEmail;
		}

	

		public taskDTO() {
			super();
			// TODO Auto-generated constructor stub
		}
		
		
		
		
}
