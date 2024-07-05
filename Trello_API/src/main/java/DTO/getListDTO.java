package DTO;

public class getListDTO {
	
	private String listName;
	
	private String boardName;

	public String getListName() {
		return listName;
	}

	public void setListName(String listName) {
		this.listName = listName;
	}

	public String getBoardName() {
		return boardName;
	}

	public void setBoardName(String boardName) {
		this.boardName = boardName;
	}

	public getListDTO(String listName, String boardName) {
		super();
		this.listName = listName;
		this.boardName = boardName;
	}

	
}
