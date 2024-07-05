package Service;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.ws.rs.ApplicationPath;

import Entity.Board;
import Entity.Card;
import Entity.Comment;
import Entity.User;
import Exception.BoardNotFoundException;
import Exception.CardNotFoundException;
import Exception.InvalidInputException;
import Exception.ListNotFoundException;
import Exception.SameListException;
import Exception.UserNotFoundException;
import Exception.conflictException;
import Messaging.JMS;
import Entity.ListOfCards;
import Entity.Sprint;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.security.authorization.AuthorizationException;

import DTO.AssignCardDTO;
import DTO.BoardDTO;
import DTO.CardDTO;
import DTO.CommentDTO;
import DTO.ListOfCardsDTO;
import DTO.addCommentDTO;
import DTO.addDescriptionDTO;
import DTO.getCardDTO;
import DTO.returnCardDTO;

import javax.persistence.NoResultException;
import javax.persistence.Persistence;

@Stateless
@Path("")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CardService {
	
	@PersistenceContext(unitName = "hello")
    private EntityManager entityManager ;
	
	@EJB
    private BoardService boardService;
	
	@EJB
    private UserService userService;
	
	@EJB
    private ListOfCardsService listService;
	
	@EJB
	private JMS jms;
	
	private boolean isCardNameExists(String title) {      
        TypedQuery<Card> query = entityManager.createQuery("SELECT c FROM Card c WHERE c.title = :title", Card.class);
        query.setParameter("title", title);        
        return !query.getResultList().isEmpty();
    }
	
	@POST
    @Path("createCard")
    public Response createCard(CardDTO  newCard) {
	    try { 
	    	Card card = new Card();
	    	card.setTitle(newCard.getCardTitle());
	    	card.setStatus(Entity.Card.STATUS.TO_DO);
			Board board = boardService.getBoardByName(newCard.getBoardName());
		 	User user = userService.getUserByEmail(newCard.getUserEmail());
		 	ListOfCards list = listService.getListByName(newCard.getListName());
	    	if (board==null) {
		        throw new BoardNotFoundException("Board isn't found");
	    	}
	    	if (user==null || (!board.getCollaborators().contains(user) && !board.getOwner().equals(user))) {
		        throw new UserNotFoundException("User isn't found");
	    	}
	    	if (list==null) {
		        throw new ListNotFoundException("List isn't found");
	    	}
	    	if(isCardNameExists(card.getTitle())) {
		        throw new InvalidInputException("Card with same namefound");
	    	}
	    	card.setList(list);
	    	card.setAssignee(user);
	    	entityManager.persist(card);
	        jms.send("Card '" + card.getTitle()  + "' Created");
	       return Response.status(Response.Status.CREATED)
	       .entity("card '"+ card.getTitle() +"' is created in list '" + card.getList().getName() +  "' with id: " + card.getCardId() ).build();
	    	
	   } catch (BoardNotFoundException | UserNotFoundException | ListNotFoundException | InvalidInputException e){  
		   return Response.status(Response.Status.NOT_FOUND)
                   .entity(e.getMessage()).build();
	   } catch (Exception e) {
	        e.printStackTrace(); 
	        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
	                       .entity(e).build();
	    }
	}
	
	 public Card getCardByName(String title) {
		  Query query = entityManager.createQuery("SELECT c FROM Card c WHERE c.title = :title");
	        query.setParameter("title", title);
	        return (Card) query.getSingleResult();
	}
	
	 
	@PUT
	@Path("moveCard")
	public Response moveCardToList(CardDTO  dto) {
	  try {
		User user = userService.getUserByEmail(dto.getUserEmail());
	    Card card = getCardByName(dto.getCardTitle());
	    if (card == null || user == null) {
	      throw new CardNotFoundException("Card or User not found");
	    }
	    ListOfCards newList = listService.getListByName(dto.getListName());
	    if (newList == null) {
	      throw new ListNotFoundException("List not found");
	    }
	    if (!card.getList().getBoard().equals(newList.getBoard())) {
	      throw new InvalidInputException("Card can only be moved within the same board");
	    }
	    if(card.getList().getId().equals(newList.getId())) {
	    	throw new SameListException("That's arleady the same card");
	    } 
	    if(!card.getList().getBoard().getCollaborators().contains(user)) {
	    	throw new conflictException("User isn't authorized to move the card");
	    }
	    
	    card.setList(newList);
        jms.send("Card '" + card.getTitle()  + "' deleted");
	    entityManager.merge(card);
	    return Response.status(Response.Status.OK).entity("Card " + card.getTitle() + " successfully moved to " + newList.getName()).build();
	  } catch (CardNotFoundException | ListNotFoundException | InvalidInputException | SameListException e) {
	    return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
	  } catch(conflictException e) {
		    return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
	  } catch (Exception e) {
	    e.printStackTrace();
	    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("An error occurred while moving the card").build();
	  }
	}
	
	
	@PUT
	@Path("assignCard")
	public Response assignCardToUser(AssignCardDTO dto) {
	  try {
	    Card card = getCardByName(dto.getCardTitle());
	    if (card == null) {
	      throw new CardNotFoundException("Card not found");
	    }
	    User assignedTo = userService.getUserByEmail(dto.getAssignedToEmail());
	    User assignee = userService.getUserByEmail(dto.getAssigneEmail());
	    if (assignee == null || assignedTo == null) {
	      throw new UserNotFoundException("User not found");
	    } 
	    if(!card.getList().getBoard().getOwner().equals(assignee)) {
	    	throw new NotAuthorizedException("User not authorized");	   
	    } 
	    if(!card.getList().getBoard().getCollaborators().contains(assignedTo) && !card.getList().getBoard().getOwner().equals(assignedTo)) {
	    	throw new InvalidInputException("User is not part of the board");	   
	    }
	    
	    card.addUser(assignedTo);
        jms.send("Card '" + card.getTitle()  + "' is assigned to '" + assignedTo.getEmail() + "' by: " + assignee.getEmail() );
	    entityManager.merge(card); 
	    return Response.status(Response.Status.OK)
	        .entity("Card " + card.getTitle() + " assigned to " + assignedTo.getEmail())
	        .build();
	  } catch (CardNotFoundException | UserNotFoundException | InvalidInputException e) {
	    return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
	  } catch (NotAuthorizedException e) {
		    return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
	  } catch (Exception e) {
	    e.printStackTrace();
	    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e).build();
	  }
	}
	
	@PUT
	@Path("addDescription")
	public Response addDescription(addDescriptionDTO dto) { 
	  try {
	    Card card = getCardByName(dto.getTitle());
	    User user = userService.getUserByEmail(dto.getUserEmail());
	    if (card == null) {
	      throw new CardNotFoundException("Card not found");
	    }
	    if (user == null) {
		      throw new UserNotFoundException("User not found");
		} 
	    if(!card.getAssignedTOs().contains(user) && !card.getList().getBoard().getOwner().equals(user)) {
	    	throw new NotAuthorizedException("User not authorized");	   
	    } 
	    card.setTitle(dto.getTitle());
	    card.setDescription(dto.getDescription());
	 
        jms.send("Card '" + card.getTitle()  + "' description is '" + dto.getDescription() + "' by: " + user.getEmail() );
	    entityManager.merge(card); 
	    return Response.status(Response.Status.OK)
	        .entity("Card " + card.getTitle() + " description is " + dto.getDescription()).build();
	  } catch (CardNotFoundException | UserNotFoundException | InvalidInputException e) {
	    return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
	  } catch (NotAuthorizedException e) {
		    return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
	  } catch (Exception e) {
	    e.printStackTrace();
	    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e).build();
	  }
	}
	
	
	public Comment addCommentHelper(String newComment) {
		Comment comment = new Comment();
		comment.comment = newComment;
		entityManager.persist(comment);
		return comment;
	}
	
	@PUT
	@Path("addComments")
	public Response addComments(addCommentDTO dto) {
	  try {
		Card card = getCardByName(dto.getTitle());
		Comment comment = addCommentHelper(dto.getComment());
	    User user = userService.getUserByEmail(dto.getUserEmail());
	    if (card == null) {
	      throw new CardNotFoundException("Card not found");
	    }
	    if (user == null) {
	      throw new UserNotFoundException("User not found");
	    } 
	    if(!card.getAssignedTOs().contains(user) && !card.getList().getBoard().getOwner().equals(user)) {
	    	throw new NotAuthorizedException("User not authorized");	   
	    } 
	    comment.setUserEmail(user.getEmail());
	    entityManager.merge(comment);
	   card.addComment(comment);
       jms.send("Card '" + card.getTitle() + "' added comment is '" + dto.getComment() + "' by: " + user.getEmail());
	   entityManager.merge(card); 
	    return Response.status(Response.Status.OK)
	        .entity("Card " + card.getTitle() + " has comment '" + dto.getComment()  + "' by: " + user.getEmail() )
	        .build();
	  } catch (CardNotFoundException | UserNotFoundException | InvalidInputException e) {
	    return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
	  } catch (NotAuthorizedException e) {
		    return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
	  } catch (Exception e) {
	    e.printStackTrace();
	    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e).build();
	  }
	}
	
	@GET
    @Path("getAllTheCards")
    public Response getAllTheCards() {
        Query query = entityManager.createQuery("SELECT NEW DTO.getCardDTO(c.cardId, c.title, c.creationDate, c.status) FROM Card c");
        List<getCardDTO> cards = query.getResultList();
        return Response.status(Response.Status.OK).entity(cards).build();
    } 
	
//	@GET
//    @Path("getAllTheCardsByEntity")
//    public Response getAllTheCards(@QueryParam("title") String title) {
//		Query query = entityManager.createQuery("SELECT c FROM Card c WHERE c.title = :title");
//		query.setParameter("title", title);
//        Card card = (Card) query.getSingleResult();
//        return Response.status(Response.Status.OK).entity(card).build();
//    } 
	
 	@GET
    @Path("getAllCards")
    public Response getcards() {
        Query query = entityManager.createQuery("SELECT NEW DTO.returnCardDTO(c.title, c.description) FROM Card c");
        List<returnCardDTO> cards = query.getResultList();
        return Response.status(Response.Status.OK).entity(cards).build();
    } 
 	
	
//	@GET
//    @Path("/getCard/{id}")
//    public Response getCardById(@PathParam("id") Long id) {
//		Card card = entityManager.find(Card.class, id);
//        if (card == null) {
//        	
//            return Response.status(Response.Status.NOT_FOUND).entity("card not found").build();
//        }      
//        return Response.ok().entity(card).build();
//    }
////
//	@GET
////	@Path("getAllComments")
//	 public List<Comment> getAllCommentsForCard(addCommentDTO dto) {
//	        try {
//	        	String title = dto.getTitle();
//	            // Query to retrieve all comments for the given card title
//	            TypedQuery<Comment> query = entityManager.createQuery(
//	                    "SELECT NEW Entity.Comment(c.comment, c.userEmail) FROM Comment c JOIN c.card Card WHERE card.title = :title",
//	                    Comment.class);
//	            query.setParameter("title", title);
//	            return query.getResultList();
//	        } catch (Exception e) {
//	            // Handle exception
//	            e.printStackTrace();
//	            return null;
//	        }
////	    }
//	@GET
//	@Path("getAllComments")
//	 public Respone getAllCommentsForCard2(addCommentDTO dto) {
//	        try {
//	        	String title = dto.getTitle();
//
//	            TypedQuery<CommentDTO> query = entityManager.createQuery(
//	                    "SELECT NEW com.example.CommentDTO(c.comment, c.userEmail) FROM Comment c JOIN c.card card WHERE card.title = :title",
//	                    CommentDTO.class);
//	            query.setParameter("title", title);
//	            return query.getResultList();
//	        } catch (Exception e) {
//	            // Handle exception
//	            e.printStackTrace();
//	            return null;
//	        }
//	    }
//	
//	
//	@GET
//    @Path("/getAllComments/{id}")
//    public Response getBoardsForTeamLeader(@PathParam("id") ) {
//		String title = dto.getTitle();
//        Query query = entityManager.createQuery("SELECT NEW DTO.CommentDTO(c.comment, c.userEmail) FROM Comment c JOIN c.card Card WHERE Card.title = :title");
//        query.setParameter("title", title);
//        List<CommentDTO> comments = query.getResultList();
//        return Response.status(Response.Status.OK).entity(comments).build();
//    }
//	
	
	public Card getcardByID(Long cardId) {
		  Query query = entityManager.createQuery("SELECT c FROM Card c WHERE c.cardId = :cardId");
	        query.setParameter("cardId", cardId);
	        return (Card) query.getSingleResult();
	}
	
	
	@GET
    @Path("/getCard/{id}")
    public Response getCardById2(@PathParam("id") Long cardId) {
		Card card = getcardByID(cardId);
		if (card == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("card not found").build();
        }  
		List<CommentDTO> dtos = new ArrayList<>();
		List<Comment> comments = card.getComments();

		for(Comment c : comments) {
			CommentDTO dto = new CommentDTO();
			dto.setComment(c.comment);
			dto.setUserEmail(c.userEmail);
			dtos.add(dto);
		}

            
        return Response.ok().entity(dtos).build();
    }
	
	
	

}