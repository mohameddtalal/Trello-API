package DTO;

import javax.persistence.Column;

public class ListOfCardsDTO {
	
	private Long listid;
    private String name;
    
	public Long getListid() {
		return listid;
	}
	public void setListid(Long listid) {
		this.listid = listid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ListOfCardsDTO(Long listid, String name) {
		super();
		this.listid = listid;
		this.name = name;
	}
    
	
    
}
