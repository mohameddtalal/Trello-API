package Entity;

import java.util.List;
import javax.persistence.*;
import java.io.*;

@Entity
@Table(name="Board")
public class Board implements Serializable {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(insertable = false, updatable = false)
    private Long boardid;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name="userId")
    private User owner;
  
    @ManyToMany 
    private List<User> collaborators;

    public Board() {
    	super();
    }
    
	public Board(String name, User owner) {
		this.name = name;
		this.owner = owner;
	}
	public List<User> getCollaborators() {
		return collaborators;
	}
	public void setCollaborators(List<User> collaborators) {
		this.collaborators = collaborators;
	}
	
	public void addCollaborator (User user) {
		collaborators.add(user);
	}

	public void removeCollaborator (User user) {
		collaborators.remove(user);
	}

	public Long getId() {
		return boardid;
	}

	public void setId(Long id) {
		this.boardid = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}
    
}
