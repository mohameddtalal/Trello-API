package Service;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.JMSConsumer;
import javax.jms.TextMessage;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.Consumes;
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

import org.jboss.security.annotation.Authentication;

import DTO.inviteCollaboratorDTO;
import DTO.userUpdateDTO;
import Entity.User;
import Exception.InvalidInputException;
import Exception.LoginFailedException;
import Messaging.JMS;
import Exception.EmailAlreadyExistsException;

@Stateless
@Path("")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserService {
	
	@PersistenceContext(unitName="hello")
	private EntityManager entityManager;
	
	@Inject
	private JMS jms;
	
	@POST
	@Path("register")
    public Response registerUser(User user) {
		try {
        validateEmail(user.getEmail());
        validatePassword(user.getPassword());
        
        if (existsByEmail(user.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");    
        }
        jms.send("User '" + user.getName()  + "' Created");
        entityManager.persist(user);
        return Response.status(Response.Status.CREATED).entity("User registered successfully!").build();
		} catch ( EmailAlreadyExistsException e) {
	        return Response.status(Response.Status.BAD_REQUEST)
	                       .entity(e.getMessage()).build();
	    } catch (Exception e) {
	        e.printStackTrace();
	        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
	                       .entity(e).build();
	    }
    }
	
	 private boolean existsByEmail(String email) {
	        Query query = entityManager.createQuery("SELECT COUNT(u) FROM User u WHERE u.email = :email");
	        query.setParameter("email", email);
	        return (Long) query.getSingleResult() > 0;
	    }

	    private void validateEmail(String email) throws InvalidInputException {
	        String emailRegex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@[\\w-]+(?:\\.[\\w-]+)*$";
	        if (!email.matches(emailRegex)) {
	            throw new InvalidInputException("Invalid email format");
	        }
	    }
	    
	    private void validatePassword(String password) throws InvalidInputException {
	        int minimumLength = 8; 
	        if (password == null || password.length() < minimumLength) {
	            throw new InvalidInputException("Password must be at least " + minimumLength + " characters long");
	        }
	    }
	
	@GET
	@Path("login")
	public Response loginUser(@QueryParam("email") String email, @QueryParam("password") String password) throws LoginFailedException {
	    try {
			Query query = entityManager.createQuery("SELECT u FROM User u WHERE u.email = :email AND u.password = :password");
	    	query.setParameter("email", email);
	    	query.setParameter("password", password);
	        User user = getUserByEmail(email);
	        
	      if (user.equals(null) || !verifyPassword(password, user)) {
	            throw new LoginFailedException("Invalid email or password");
	        }
	        jms.send("User '" + user.getName()  + "' is logged in");
	        return Response.status(Response.Status.OK).entity(user).build();
	        
	    } catch (LoginFailedException e) {
	        return Response.status(Response.Status.UNAUTHORIZED)
	                       .entity(e.getMessage()).build();
	        
	    } catch (Exception e) {
	        e.printStackTrace(); 
	        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
	                       .entity("An error occurred during login.").build();
	    }
	}
	
	
	@GET
	@Path("getUser")
	public Response getUser(inviteCollaboratorDTO email) {
	        User user = getUserByEmail(email.getInvitedEmail());
	         return Response.status(Response.Status.OK).entity(user).build();
	}
	
	
	 public User getUserByEmail(String email) {
	        Query query = entityManager.createQuery("SELECT u FROM User u WHERE u.email = :email");
	        query.setParameter("email", email);
	        return (User) query.getSingleResult();        
	 }
	 
	 private boolean verifyPassword(String password, User user) {
	        if (user.getPassword().equals(password)) return true;
	        else return false;
	 }
	 
	 @PUT
	 @Path("update")
	 public Response updateUser(userUpdateDTO dto) {		
	     try {
	    	 User existingUser = entityManager.find(User.class, dto.getUserId());
	         if (existingUser == null) {
	             throw new NotFoundException("User not found with ID: " + dto.getUserId());
	         }

	         // Update user fields if they are not null in the request
	         if (dto.getName() != null) {
	             existingUser.setName(dto.getName());
	         }
	         if (dto.getEmail() != null) {
	             if (!dto.getEmail().equals(existingUser.getEmail()) && existsByEmail(dto.getEmail())) {
	                 throw new EmailAlreadyExistsException("Email already exists");
	             }
	             existingUser.setEmail(dto.getEmail());
	         }
	         if (dto.getPassword() != null) {
	             existingUser.setPassword(dto.getPassword());
	         }
		        jms.send("User '" + existingUser.getName()  + "' updated");
	         entityManager.merge(existingUser);
	         return Response.status(Response.Status.OK).entity("User updated successfully!").build();
	     } catch (NotFoundException e) {
	         return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
	     } catch (EmailAlreadyExistsException e) {
	         return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
	     } catch (Exception e) {
	         e.printStackTrace();
	         return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("An error occurred during update.").build();
	     }
	 }

	public User getUserByID(Long id) {
		  Query query = entityManager.createQuery("SELECT u FROM User u WHERE u.id = :id");
	        query.setParameter("id", id);
	        return (User) query.getSingleResult();
	}
	
//	@GET
//  	@Path("consume")
//  	public Response getMessage2() {
//  		try {
//  	        JMSConsumer consumer = jms.getContext().createConsumer(jms.getQueue());
//  	      TextMessage message = (TextMessage) consumer.receive();
//  	      String myMsg = message.getText();
//  	    consumer.close();
//		return Response.status(Response.Status.OK).entity(myMsg).build();
//
//  		}
//  		catch (Exception e) {
//			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e).build();
//
//  		}
//  	}
	
//	 public User getCurrentUser() {
//	        // Your implementation to retrieve the currently authenticated user
//	        // This could involve accessing security contexts, session attributes, or custom authentication mechanisms
//	        
//	        Authentication authentication ;
//	        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof UserDetails) {
//	            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//	            // Assuming your User entity has a username field
//	            String username = userDetails.getUsername();
//	            // Retrieve the user from the database or wherever user details are stored
//	            return userRepository.findByUsername(username);
//	        } else {
//	            return null; // No authenticated user
//	        }
//	    }
	
	//
//	private void authenticateUser(String authorization) {
//		
//	}

   
}