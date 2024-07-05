package DTO;

import java.util.Date;

import Entity.Card.STATUS;

public class getCardDTO {
	
	private Long cardId;
	private String title;
	private Date creationDate ;
	private Entity.Card.STATUS status;
	
	public Long getId() {
		return cardId;
	}
	public void setId(Long cardId) {
		this.cardId = cardId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	public Entity.Card.STATUS getStatus() {
		return status;
	}
	public void setStatus(Entity.Card.STATUS status) {
		this.status = status;
	}
	
	public getCardDTO(Long cardId, String title, Date creationDate, STATUS status) {
		super();
		this.cardId = cardId;
		this.title = title;
		this.creationDate = creationDate;
		this.status = status;
	}
}
