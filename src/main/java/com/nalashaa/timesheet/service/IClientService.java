
package com.nalashaa.timesheet.service;

import java.util.List;

import com.nalashaa.timesheet.entity.Client;
import com.nalashaa.timesheet.entity.Country;

/**
 * 
 * @author ashwanikannojia
 * 
 * This class is used to provide interface for client operations.
 *
 */
public interface IClientService {

	/**
	 * This method is used to create client on the basis of provided client informations as an argument.
	 * 
	 * @param client
	 * 			It contains the client information which needs to be registered.
	 * @return
	 * 			It return client object after registering in the application.
	 */
    Client createClient(Client client);
    
    /**
     * This method is used to provide all clients list.
     * 
     * @return
     * 			It returns list of all clients.	
     */
    List<Client> getAllActiveClients();
    
	/**
	 * This method is used to update the client details.
	 * 
	 * @param client
	 * 			It represents client details which needs to be updated.
	 */
	void updateClient(Client client);
	
	/**
	 * This method is used to return list of all countries.
	 * @return
	 * 		List of countries object.
	 */
	public List<Country> getAllCountries() ;

}
