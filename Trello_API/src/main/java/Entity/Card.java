package Entity;

import javax.persistence.*;
import java.io.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="Card")
public class Card implements Serializable{
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cardId;

	@OneToMany
//	 @JoinColumn(name = "id") 
    private List<Comment> comments;
    
    @Column(nullable = false)
    private String title;

    
	@Column
    private String description;
	
	
    @ManyToOne
	@JoinColumn(name="userId")
	private User assignee;
	
	@Enumerated(EnumType.STRING)
    @Column(nullable = false)
	private STATUS status; 
	
	@ManyToOne
	@JoinColumn(name="listid")
	private ListOfCards list;
	
	@ManyToMany
	private List<User> assignedTOs;
	
    
	public Card() {
		super();
	}
	

	public Long getCardId() {
		return cardId;
	}


	public void setCardId(Long cardId) {
		this.cardId = cardId;
	}


	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}
	
	public void addComment(Comment comment) {
		comments.add(comment);
	}

	
	public void removeComment(Comment comment) {
		comments.remove(comment);
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public User getAssignee() {
		return assignee;
	}

	public void setAssignee(User assignee) {
		this.assignee = assignee;
	}


	public STATUS getStatus() {
		return status;
	}


	public void setStatus(STATUS status) {
		this.status = status;
	}

	public ListOfCards getList() {
		return list;
	}

	public void setList(ListOfCards list) {
		this.list = list;
	}

	public List<User> getAssignedTOs() {
		return assignedTOs;
	}

	public void setAssignedTOs(List<User> assignedTOs) {
		this.assignedTOs = assignedTOs;
	}

	public void addUser(User user) {
		assignedTOs.add(user);
	}
	
	public void removeUser(User user) {
		assignedTOs.remove(user);
	}
	
	
	public enum STATUS {
        TO_DO,
        IN_PROGRESS,
        DONE
	}
	
}
