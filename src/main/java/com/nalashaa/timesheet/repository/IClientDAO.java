
package com.nalashaa.timesheet.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.nalashaa.timesheet.entity.Client;

/**
 * 
 * Interface for CRUD operations on a repository for a Client type.
 * 
 * @author ashwanikannojia
 */
@Repository
public interface IClientDAO extends JpaRepository<Client, Long> {

	/**
	 * A constant holding the query to get all active clients. QUERY: {@value #GET_ALL_ACTIVE_CLIENTS}.
	 */
	String GET_ALL_ACTIVE_CLIENTS = "SELECT client FROM Client client WHERE client.status = 1 ORDER BY client.id desc";
	
	/**
	 * A constant holding the query to delete/update the client based on provided id. QUERY: {@value #DELETE_CLIENT}.
	 */
	String DELETE_CLIENT = "UPDATE Client client SET client.status = 0 WHERE client.id = ?1";
	
	/**
	 * A constant holding the query to get the instance of client based on provided client id. QUERY: {@value #GET_CLIENT_BY_CLIENT_ID}
	 */
	String GET_CLIENT_BY_CLIENT_ID = "SELECT client FROM Client client WHERE client.clientId =?1";
	
	
	/**
     * This method is used to return all instances of active clients.
     * 
     * @return It returns all active instances of client
     */
	@Query(GET_ALL_ACTIVE_CLIENTS)
    List<Client> getAllActiveClients();

    /**
	 * This method is used to remove the client details based on provided client id.
	 * Basically its not hard delete, its just for making active(1) to inactive(0).
	 * 
	 * @param clientId represents id of that instance which needs to be removed from the repository
	 */
    @Modifying
    @Query(DELETE_CLIENT)
    void deleteClient(long clientId);

    /**
     * Retrieves an entity by its id.
     * 
     * @param clientId must not be {@literal null}.
     * @return the entity with the given id or {@literal null} if none found
     */
    @Query(GET_CLIENT_BY_CLIENT_ID)
	Client getClientByClientId(String clientId);
    
    @Modifying
    @Query("DELETE FROM Client client WHERE client.status = false")
    void purgeInactiveClients();
    
}