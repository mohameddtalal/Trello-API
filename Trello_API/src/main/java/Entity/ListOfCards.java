package Entity;

import javax.persistence.*;
import java.io.*;
import java.util.List;

@Entity
@Table(name="ListOfCards")
public class ListOfCards implements Serializable {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long listid;

    @Column(nullable = false)
    private String name;
    
    @ManyToOne
	@JoinColumn(name="boardid")
	private Board board;
        
    public ListOfCards() {
    	super();
    }
    
	public Long getId() {
		return listid;
	}

	public Board getBoard() {
		return board;
	}

	public void setBoard(Board board) {
		this.board = board;
	}

	public ListOfCards(String name2, Board board2) {
	}

	public void setId(Long id) {
		this.listid = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
        
}
