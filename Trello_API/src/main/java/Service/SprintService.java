package Service;

import java.util.Arrays;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.ws.rs.ApplicationPath;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import DTO.endSprintDTO;
import DTO.sprintDTO;
import DTO.taskDTO;
import Entity.Sprint;
import Entity.Task;
import Entity.Task.TaskStatus;
import Entity.User;
import Exception.InvalidInputException;
import Exception.UserNotFoundException;
import Exception.conflictException;
import Messaging.JMS;

import javax.persistence.NoResultException;
import javax.persistence.Persistence;



import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.Queue;
import javax.jms.TextMessage;
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
import Messaging.JMS;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.Long;


@Stateless
@Path("")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SprintService {
	
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
	
	
		
	private boolean isTaskNameExists(String taskName) {
	       
        TypedQuery<Task> query = entityManager.createQuery("SELECT t FROM Task t WHERE t.name = :name", Task.class);
        query.setParameter("name", taskName);
                
        return !query.getResultList().isEmpty();
    }
	
	private boolean isSprintNameExists(String name) {
	       
        TypedQuery<Sprint> query = entityManager.createQuery("SELECT s FROM Sprint s WHERE s.name = :name", Sprint.class);
        query.setParameter("name", name);
                
        return !query.getResultList().isEmpty();
    }
	
 	@GET
    @Path("/getTask/{id}")
    public Response getTaskById(@PathParam("id") Long id) {
  		Task task = entityManager.find(Task.class, id);
        if (task == null) {
        	
            return Response.status(Response.Status.NOT_FOUND).entity("task not found").build();
        }      
        return Response.ok().entity(task).build();
    }
 	
	@GET
    @Path("/getSprint/{id}")
    public Response getSprintById(@PathParam("id") Long id) {
		Sprint sprint = entityManager.find(Sprint.class, id);
        if (sprint == null) {
        	
            return Response.status(Response.Status.NOT_FOUND).entity("sprint not found").build();
        }      
        return Response.ok().entity(sprint).build();
    }
	
	@POST
    @Path("createSprint")
	  public Response createSprint(sprintDTO sprintDTO) {
		 try { 
			 Sprint sprint = new Sprint();
			 sprint.setName(sprintDTO.getName());
			 sprint.setStartDate(sprintDTO.getStartDate());
			 sprint.setEndDate(sprintDTO.getEndDate());
			 User user = userService.getUserByEmail(sprintDTO.getUserEmail());
			 if (sprint == null || sprint.getName() == null || user == null || isSprintNameExists(sprint.getName())){
					throw new InvalidInputException("Error creating the sprint, please provide the information");
			    }
				sprint.setOwner(user);
				jms.send("Sprint '" + sprint.getName()  + "' is added by: " + user.getEmail());
				entityManager.persist(sprint);												
				return Response.status(Response.Status.CREATED).entity("sprint created").build();
		 } catch (InvalidInputException e) {
			    return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		 } catch (Exception e) {
			    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e).build();
		 }
	  }
	
	 public Sprint getSprintByName(String name) {
	        Query query = entityManager.createQuery("SELECT s FROM Sprint s WHERE s.name = :name");
	        query.setParameter("name", name);
	        return (Sprint) query.getSingleResult();        
	 }
	 
	 private Task.TaskStatus mapStatusFromString(String statusString) {
		    switch (statusString) {
		        case "TO_DO":
		            return Task.TaskStatus.TO_DO;
		        case "IN_PROGRESS":
		            return Task.TaskStatus.IN_PROGRESS;
		        case "DONE":
		            return Task.TaskStatus.DONE;
		        default:
		            throw new IllegalArgumentException("Invalid task status: " + statusString);
		    }
		}
	  	
	  	@POST
	  	@Path("createTask")
	  	public Response createTask(taskDTO taskDTO) {
	  		try {
	  			Sprint sprint = getSprintByName(taskDTO.getSprintName());
		  		User user = userService.getUserByEmail(taskDTO.getUserEmail());
		    if (!isValidStoryPoints(taskDTO.getStoryPoints())) {
		      throw new IllegalArgumentException("Story points must be part of the Fibonacci sequence (3, 5, 8, 13, 21)");
		    }if (user == null || sprint == null || taskDTO.getName()==null) {
			      throw new InvalidInputException("User not found or Sprint not found and task name mustr be provided");
			}
		    if (isTaskNameExists(taskDTO.getName())){
		    	throw new conflictException("Task name already exists. Please choose a different name.");
		    }
		    
		    Task task = new Task();
		    task.setUser(user);
		    task.setSprint(sprint);
		    task.setName(taskDTO.getName());
		    Task.TaskStatus status = mapStatusFromString(taskDTO.getStatus());
		    task.setStatus(status);		    
		    task.setStoryPoints(taskDTO.getStoryPoints());
			jms.send("Task '" + task.getName()  + "' is added by: " + user.getEmail()  + ", with SP " + task.getStoryPoints());
			sprint.addStoryPoints(task.getStoryPoints());
			entityManager.merge(sprint);
		    entityManager.persist(task);												
			
			return Response.status(Response.Status.CREATED).entity("task created with status: " + task.getStatus()).build();
	  			
	  		} catch (InvalidInputException | IllegalArgumentException e) {
				return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();

	  		}catch (conflictException e) {
		        return Response.status(Response.Status.CONFLICT).entity(e.getMessage()).build();
	  		}
	  		catch (Exception e) {
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e).build();
	  		} 
	  }
	  	
		  private boolean isValidStoryPoints(Integer storyPoints) {
		    return Arrays.asList(3, 5, 8, 13, 21).contains(storyPoints);
		  }

	  	public List<Task> findAllTasks() {
	  	  return entityManager.createQuery("SELECT t FROM Task t", Task.class).getResultList();
	  	}


	  	 private boolean existsByName(String name) {
		        Query query = entityManager.createQuery("SELECT COUNT(s) FROM Sprint s WHERE s.name = :name");
		        query.setParameter("name", name);
		        return (Long) query.getSingleResult() > 0;
		 }
	  	 
	  	@PUT
	  	@Path("endSprint")
	  	public Response endSprint(endSprintDTO dto) {
	  		try {
	  		int allFinishedPoints=0;
		    User user = userService.getUserByEmail(dto.getUserEmail());
		  	Sprint newSprint = new Sprint();
		    Sprint oldSprint = getSprintByName(dto.getOldSprintName());
		    if(user == null || newSprint == null || dto.getNewSprintName()== null ) {
		    	throw new NotFoundException("Please enter a correct user email and a sprint names");
		    }
		    if(existsByName(dto.getNewSprintName())) {
		    	throw new InvalidInputException("Arleady sprint with the same name");
		    }
		    newSprint.setName(dto.getNewSprintName());
		    newSprint.setStartDate(oldSprint.getEndDate());
		    newSprint.setEndDate(dto.getNewEndDate());
		    newSprint.setOwner(user);
		    List<Task> allTasks = findAllTasks();
		    List<Task> unfinishedTasks = new ArrayList<>();
		    for (Task t : allTasks) {
		        Task.TaskStatus status = t.getStatus();
		    	if(status != null && status.equals(Task.TaskStatus.DONE) && t.getSprint().getSprintId().equals(oldSprint.getSprintId())) {
		    		allFinishedPoints += t.getStoryPoints();
		    	}
		    	if(status != null && !status.equals(Task.TaskStatus.DONE) && t.getSprint().getSprintId().equals(oldSprint.getSprintId()) ) { 
		    		unfinishedTasks.add(t);
		    	}
		    }
		    
		    oldSprint.setTotalCompletedStoryPoints(allFinishedPoints);
		    oldSprint.setTotalUncompletedStoryPoints(oldSprint.getTotalUncompletedStoryPoints()-allFinishedPoints);
		    for(Task t : unfinishedTasks) {
		    	t.setSprint(newSprint);
		    	newSprint.addStoryPoints(t.getStoryPoints());
		    	entityManager.merge(t);
		    }
		    entityManager.merge(oldSprint);
		    entityManager.persist(newSprint);
			jms.send("Sprint '" + oldSprint.getName()  + "' is deleted, and Sprint '"  +  newSprint.getName() + "' is added");

			return Response.status(Response.Status.CREATED).entity("Sprint deleted").build();
	  		} catch (NotFoundException e) {
				return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
	  		} catch (InvalidInputException e) {
				return Response.status(Response.Status.FORBIDDEN).entity(e.getMessage()).build();

	  		} catch (Exception e) {
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e).build();
	  		}
	  	}
	  		  	
	  	@GET
	  	@Path("getSprintReport")
	  	public Response generateReport(sprintDTO dto) {
	  		String name = dto.getName();
	  		Sprint sprint = getSprintByName(name);
			return Response.status(Response.Status.ACCEPTED).
			entity("Total completed SP: " + sprint.getTotalCompletedStoryPoints() + ", Total uncompleted SP: " + sprint.getTotalUncompletedStoryPoints() ).build();
	  	}	
	  	
//	  	@GET
//	  	@Path("consume")
//	  	public Response getMessage() {
//	  		try {
//	  	        JMSConsumer consumer = Context.createConsumer(queue);
//	  	      TextMessage message = (TextMessage) consumer.receive();
//	  	      String myMsg = message.getText();
//	  	    consumer.close();
//			return Response.status(Response.Status.OK).entity(myMsg).build();
//
//	  		}
//	  		catch (Exception e) {
//				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e).build();
//
//	  		}
//	  	}
	  	
//	 	@GET
//	  	@Path("consume")
//	  	public Response getMessage2() {
//	  		try {
//	  	        JMSConsumer consumer = jms.getContext().createConsumer(jms.getQueue());
//	  	      TextMessage message = (TextMessage) consumer.receive();
//	  	      String myMsg = message.getText();
//	  	    consumer.close();
//			return Response.status(Response.Status.OK).entity(myMsg).build();
//
//	  		}
//	  		catch (Exception e) {
//				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e).build();
//
//	  		}
//	  	}

}
	  	
	  	