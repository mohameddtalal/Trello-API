package Entity;

import javax.persistence.*;
import java.io.*;

@Entity
@Table(name = "User")
public class User implements Serializable {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Column(nullable = false)
    private String name;
    
    public User() {
		super();
	}

	public User(String name, String email, String password, UserRole role) {
        this.name = name;
        this.email = email;
        this.password = hashPassword(password); // Hash password before saving
        this.role = role;
    }
    
    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = hashPassword(password); // Hash password before saving
    }

    private String hashPassword(String password) {
      
        return password;
    }
     

    public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public UserRole getRole() {
		return role;
	}

	public void setRole(UserRole role) {
		this.role = role;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getId() {
		return userId;
	}
	
	 public enum UserRole {
	        TEAM_LEADER,
	        COLLABORATOR
	 }
}
