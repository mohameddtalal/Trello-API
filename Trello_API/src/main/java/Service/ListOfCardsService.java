package Service;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.security.authorization.AuthorizationException;

import DTO.BoardDTO;
import DTO.CreateBoardDTO;
import DTO.CreateListDTO;
import DTO.ListOfCardsDTO;
import DTO.getListDTO;
import Entity.Board;
import Entity.ListOfCards;
import Entity.User;
import Exception.BoardNotFoundException;
import Exception.InvalidInputException;
import Exception.ListNotFoundException;
import Exception.UserNotFoundException;
import Messaging.JMS;

@Stateless
@Path("")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ListOfCardsService {
	
	@PersistenceContext(unitName="hello")
	private EntityManager entityManager;
	
	@EJB
    private BoardService boardService;
	
	@EJB
    private UserService userService;
	
	@EJB
	private JMS jms;
	
	
	@Path("createList")
	@POST	
	public Response createList(CreateListDTO  newList) {
	 	Board board = boardService.getBoardByName(newList.getBoardName());
	 	User user = userService.getUserByEmail(newList.getUserEmail());
	 	ListOfCards list = new ListOfCards();
	    try { 
	    	if (board==null) {
		        throw new BoardNotFoundException("Board isn't found");
	    	}
	    	if (user==null) {
		        throw new UserNotFoundException("User isn't found");
	    	}
	    	if (!board.getOwner().equals(user)) {
	        throw new AuthorizationException("User is not authorized to create lists on this board");
	    	} 
	    	list.setBoard(board);
	    	if (existsByName(newList.getListName())) {
	    		ListOfCards existingList = getListByName(newList.getListName());
	    		if(existingList.getBoard().getName().equals(list.getBoard().getName()) ) {
	    				throw new InvalidInputException("List with same name found");		
	    	}	    	
	    	
	    }
	    	list.setName(newList.getListName());
			entityManager.persist(list);
	        jms.send("List '" + list.getName()  + "' Created");
		    return Response.status(Response.Status.CREATED).entity("List '"+ list.getName() +"' is created in board '" + list.getBoard().getName() + "' with id: " + list.getId()).build();	    	
	   } catch (AuthorizationException e){
		   return Response.status(Response.Status.UNAUTHORIZED)
                   .entity(e.getMessage()).build();
	   } catch (BoardNotFoundException | UserNotFoundException e){  
		   return Response.status(Response.Status.NOT_FOUND)
                   .entity(e.getMessage()).build();
	   } catch (InvalidInputException e) { 
		   return Response.status(Response.Status.FORBIDDEN)
                   .entity(e.getMessage()).build();
	   }
	    catch (Exception e) {
	        e.printStackTrace(); 
	        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
	                       .entity(e).build();
	    }
	}
	
	 private boolean existsByName(String name) {
	        Query query = entityManager.createQuery("SELECT COUNT(l) FROM ListOfCards l WHERE l.name = :name");
	        query.setParameter("name", name);
	        return (Long) query.getSingleResult() > 0;
	    }
	 
	 public ListOfCards getListByName(String name) {
		  Query query = entityManager.createQuery("SELECT l FROM ListOfCards l WHERE l.name = :name");
	        query.setParameter("name", name);
	        return (ListOfCards) query.getSingleResult();
	}
	
	
	 public List<ListOfCards> getListsByBoard(Board board) {
		  Query query = entityManager.createQuery("SELECT l FROM ListOfCards l WHERE l.board = :board");
	        query.setParameter("board", board);
	        List<ListOfCards> resultList = query.getResultList();
	        return resultList;
	}
	
		
	@DELETE
    @Path("deleteList")
    public Response deleteList(CreateListDTO deleteList) {    
		Board board = boardService.getBoardByName(deleteList.getBoardName());
	 	User user = userService.getUserByEmail(deleteList.getUserEmail());
	 	ListOfCards list= getListByName(deleteList.getListName());
	    try { 
	    	if (board==null) {
		        throw new BoardNotFoundException("Board isn't found");
	    	}
	    	if (list==null) {
		        throw new ListNotFoundException("List isn't found");
	    	}
	    	if (user==null) {
		        throw new UserNotFoundException("User isn't found");
	    	}
	    	if (!board.getOwner().equals(user)) {
	        throw new AuthorizationException("User is not authorized to delete lists on this board");
	    	} 
//	    	list.setBoard(null);
//	    	list.setName(null);
	    	entityManager.remove(list);
	        jms.send("List '" + list.getName()  + "' removed");
	       return Response.status(Response.Status.CREATED).entity("List '"+ list.getName() +"' is deleted from board :" + list.getBoard().getName()).build();
	    	
	   } catch (AuthorizationException e){
		   return Response.status(Response.Status.UNAUTHORIZED)
                   .entity(e.getMessage()).build();
	   } catch (BoardNotFoundException | UserNotFoundException | ListNotFoundException e){  
		   return Response.status(Response.Status.NOT_FOUND)
                   .entity(e.getMessage()).build();
	   } catch (Exception e) {
	        e.printStackTrace(); 
	        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
	                       .entity(e).build();
	    }   
    }
	
//	@GET
//    @Path("getList")
//    public Response getList(getListDTO getList)  {
//		ListOfCards list = getListByName(getList.getListName());
//		Board board = boardService.getBoardByName(getList.getBoardName());
//		try  {
//			if (board==null) {
//				throw new BoardNotFoundException("Board isn't found");
//			}
//			if (list==null) {
//				throw new ListNotFoundException("List isn't found");
//			}
//	        return Response.ok().entity(list).build();
//
//        }  catch (BoardNotFoundException | ListNotFoundException e){  
//        		return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
//        } catch (Exception e) {	
//        	return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
//        }
//    }
	
	@GET
    @Path("getAllTheLists")
    public Response getAllTheLists() {
        Query query = entityManager.createQuery("SELECT NEW DTO.ListOfCardsDTO(l.listid, l.name) FROM ListOfCards l");
        List<ListOfCardsDTO> lists = query.getResultList();
        return Response.status(Response.Status.OK).entity(lists).build();
    } 

//	
//	  	@GET
//	    @Path("getAllLists")
//	    public Response getBoardsForTeamLeader(String name) {
//	        Query query = entityManager.createQuery("SELECT NEW DTO.ListOfCardsDTO(l.listid, l.name) FROM ListOfCards l WHERE l.board.name = :name");
//	        query.setParameter("name", name);
//	        List<ListOfCardsDTO> lists = query.getResultList();
//	        return Response.status(Response.Status.OK).entity(lists).build();
//	    }
//	  	
//	  	@GET
//	    @Path("getAllListsByBoard")
//	  	 public Response getBoardsForTeamLeader2(String boardName) {
//			  Query query = entityManager.createQuery("SELECT l FROM ListOfCards l WHERE l.board.name = :boardName");
//		        query.setParameter("boardName", boardName);
//		        List<ListOfCards> lists = query.getResultList();
//		        return Response.status(Response.Status.OK).entity(lists).build();
//		}
	
}


