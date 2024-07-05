package Entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Task")
public class Task {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long taskId;
	
    @Column(nullable = false)
	private String name;
	
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;
	
	private int storyPoints; 
	
	@ManyToOne
	private Sprint sprint;
	
	@ManyToOne
	@JoinColumn(name = "userId")
	User user;
	
	public Task() {
		super();
	}
	

	public User getUser() {
		return user;
	}



	public void setUser(User user) {
		this.user = user;
	}



	public Long getTaskId() {
		return taskId;
	}



	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}



	public TaskStatus getStatus() {
		return status;
	}



	public void setStatus(TaskStatus status) {
		this.status = status;
	}



	public int getStoryPoints() {
		return storyPoints;
	}



	public void setStoryPoints(int storyPoints) {
		this.storyPoints = storyPoints;
	}



	public Sprint getSprint() {
		return sprint;
	}


	public void setSprint(Sprint sprint) {
		this.sprint = sprint;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	
	 public enum TaskStatus {
		 TO_DO,
		 IN_PROGRESS,
		 DONE
	 }

}