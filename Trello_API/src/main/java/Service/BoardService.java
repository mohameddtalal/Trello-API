package Service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import DTO.BoardDTO;
import DTO.CreateBoardDTO;
import DTO.inviteCollaboratorDTO;

import javax.ws.rs.ProcessingException;
import Entity.Board;
import Entity.User;
import Exception.BoardAlreadyExistsException;
import Exception.BoardNotFoundException;
import Exception.InvalidInputException;
import Exception.UserNotFoundException;
import Exception.conflictException;
import Messaging.JMS;

import java.lang.Long;

@Stateful
@Path("")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class BoardService {
	 
	@PersistenceContext(unitName="hello")
	private EntityManager entityManager;
	
	@EJB
	private UserService userService;
	
	@EJB
	private JMS jms;
	
	
	 public Board getBoardByName(String name) {
		  Query query = entityManager.createQuery("SELECT b FROM Board b WHERE b.name = :name");
	        query.setParameter("name", name);
	        return (Board) query.getSingleResult();
	}
	
	@POST
	@Path("createBoard")
	public Response createBoard(CreateBoardDTO dto) {
		User user = userService.getUserByEmail(dto.getUserEmail());
		Board board = new Board();
		board.setOwner(user);
		board.setName(dto.getBaordName());
		try {
			if (existsByName(board.getName())) {
				throw new BoardAlreadyExistsException("Board with name '" + board.getName() + "' already exists");
			}
	        if (board.getOwner() == null || !board.getOwner().getRole().equals(Entity.User.UserRole.TEAM_LEADER)) {
	            return Response.status(Response.Status.FORBIDDEN)
	                    .entity("User does not have permission to create boards").build();
	        }
	        if (user == null ){
				throw new UserNotFoundException("User doesn't exists");
	        }
	     
	       entityManager.persist(board);
	        jms.send("Board '" + board.getName()  + "' Created");
	       return Response.status(Response.Status.CREATED).entity(board).build();
		} catch (UserNotFoundException e) {
	        return Response.status(Response.Status.NOT_FOUND)
                    .entity("Invalid ID format. Please provide a valid long value.").build(); 
	        } 
		catch (BoardAlreadyExistsException e) {
		       return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		} catch (Exception e) {
	        e.printStackTrace();
	        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
	                       .entity("An error occurred during creating board.").build();
	    }
	}
	
	 @DELETE
	 @Path("remove")
	 public Response deleteBoard(CreateBoardDTO dto)  {
			User user = userService.getUserByEmail(dto.getUserEmail());
		 try {
			 Board board = getBoardByName(dto.getBaordName());
		 	 if (board.equals(null) || !entityManager.contains(board)) {
			        throw new BoardNotFoundException("Board not found with name " + dto.getBaordName());
			 }
		 	if (!board.getOwner().equals(user)) {
		        throw new InvalidInputException(user.getEmail() + "Doesn't have authorization");

	        }
		    entityManager.remove(board);
	        jms.send("Board '" + board.getName()  + "' removed");
		    return Response.status(Response.Status.ACCEPTED).entity("Board Deleted").build();
		 } catch (InvalidInputException e) {
	            return Response.status(Response.Status.UNAUTHORIZED).entity("User does not have permission to delete boards").build();
		 }
		 catch (BoardNotFoundException e) {
			    return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
		 } catch (Exception e) {
		        e.printStackTrace();
		        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
		                       .entity("An error occurred during creating board.").build();
		 }
		 	
	}
	 
	 
	 	@GET
	    @Path("getAll")
	    public Response getBoardsForTeamLeader() {
	        Query query = entityManager.createQuery("SELECT NEW DTO.BoardDTO(b.id, b.name) FROM Board b");
	        List<BoardDTO> boards = query.getResultList();
	        return Response.status(Response.Status.OK).entity(boards).build();
	    } 
	
	
	 private boolean existsByName(String name) {
	        Query query = entityManager.createQuery("SELECT COUNT(b) FROM Board b WHERE b.name = :name");
	        query.setParameter("name", name);
	        return (Long) query.getSingleResult() > 0;
	 }
	 
	 @GET
	 @Path("viewBoards")
	 public Response getBoardsForTeamLeader(CreateBoardDTO dto) {
		 try {
			 User user = userService.getUserByEmail(dto.getUserEmail());
			    if (user==null) {
			        throw new UserNotFoundException("User not found");
			    }
			    Query query = entityManager.createQuery("SELECT b FROM Board b WHERE b.owner = :user");   
			    query.setParameter("user", user);
			    List<Board> boards = query.getResultList();
			    if (boards.isEmpty()) {
			        throw new BoardNotFoundException("No boards found for user with EMAIL: " + dto.getUserEmail());
			    }
			    List<String> names = new ArrayList<>();
			    for (Board board : boards) {
			    	names.add(board.getName());
			    }
			    return Response.status(Response.Status.OK).entity(names).build();	
		 } catch(BoardNotFoundException | UserNotFoundException e) {
			    return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();	
		 } catch (Exception e) {
		        e.printStackTrace();
		        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
		                       .entity("An error occurred during creating board.").build();
		    }
		 	
	}
	 

	 @PUT
	 @Path("inviteCollaborator")
	 public Response addCollaborator(inviteCollaboratorDTO dto) {
		 User inviterUser = userService.getUserByEmail(dto.getInviterEmail());
			User invitedUser =  userService.getUserByEmail(dto.getInvitedEmail());
			Board board = getBoardByName(dto.getBoardName());	
		 try {	
				if (!existsByName(board.getName())) {
					throw new BoardAlreadyExistsException("Board with name '" + board.getName() + "' doesn't exist");
				}
				if (invitedUser == null || inviterUser == null) {
					throw new UserNotFoundException("User doesn't exist");
		        }
				if (!board.getOwner().getEmail().equals(inviterUser.getEmail())) {
					throw new InvalidInputException("Inviter user is not a authorized");
				}
				if(board.getCollaborators().contains(invitedUser)) {
					throw new conflictException("User arleady added");
				}
		        board.addCollaborator(invitedUser);
		        jms.send("Board '" + board.getName()  + "' has user '" + invitedUser.getEmail() + "' invited as collabortator by :" + inviterUser.getEmail());
		        entityManager.merge(board);
		       return Response.status(Response.Status.OK).entity("User added").build();
			} catch (BoardAlreadyExistsException e) {
			       return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
			} catch (UserNotFoundException e) {
			       return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
			} catch (InvalidInputException e) {
				return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
			} catch (Exception e) {
		        e.printStackTrace();
		        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e).build();
		    }
		
		}
	 
	
	 
	 public Board getBoardByID(Long id) {
		  Query query = entityManager.createQuery("SELECT b FROM Board b WHERE b.id = :id");
	        query.setParameter("id", id);
	        return (Board) query.getSingleResult();
	}

	 
}
