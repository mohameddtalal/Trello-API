package DTO;

public class BoardDTO {	
	
	private Long boardid;
	private String name ;
	
	public Long getId() {
		return boardid;
	}
	public void setId(Long boardid) {
		this.boardid = boardid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public BoardDTO(Long boardid, String name) {
		super();
		this.boardid = boardid;
		this.name = name;
	}
	
	

}
