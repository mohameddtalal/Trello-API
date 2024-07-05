package Entity;

import javax.persistence.*;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "Sprint")
public class Sprint {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long sprintId;
	
	private String name;
	private Date startDate;
	private Date endDate; 
	
	private int totalCompletedStoryPoints; 
	private int totalUncompletedStoryPoints;
	
//	@OneToMany (fetch = FetchType.EAGER)
//	public List<Task> tasks;
	@ManyToOne
	private User owner;
	
//	private List<User> user;
	
	public Sprint() {
		super();
	}

	
	public void addStoryPoints(int sp) {
	    if (this.totalUncompletedStoryPoints == 0) {
	        this.totalUncompletedStoryPoints = 0; // Or another default value
	    }
	    this.totalUncompletedStoryPoints += sp;
	}
	public User getOwner() {
		return owner;
	}


	public void setOwner(User owner) {
		this.owner = owner;
	}


	public Long getSprintId() {
		return sprintId;
	}

	public void setSprintId(Long sprintId) {
		this.sprintId = sprintId;
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

	public Integer getTotalCompletedStoryPoints() {
		return totalCompletedStoryPoints;
	}

	public void setTotalCompletedStoryPoints(int totalCompletedStoryPoints) {
		this.totalCompletedStoryPoints = totalCompletedStoryPoints;
	}

	public Integer getTotalUncompletedStoryPoints() {
		return totalUncompletedStoryPoints;
	}

	public void setTotalUncompletedStoryPoints(int totalUncompletedStoryPoints) {
		this.totalUncompletedStoryPoints = totalUncompletedStoryPoints;
	}

//	public List<User> getUser() {
//		return user;
//	}
//
//	public void setUser(List<User> user) {
//		this.user = user;
//	}	

}