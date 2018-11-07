package com.nalashaa.timesheet.service.impl;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nalashaa.timesheet.Exception.TimeSheetException;
import com.nalashaa.timesheet.entity.Client;
import com.nalashaa.timesheet.entity.Country;
import com.nalashaa.timesheet.repository.IClientDAO;
import com.nalashaa.timesheet.repository.ICountryDAO;
import com.nalashaa.timesheet.repository.IPersonDAO;
import com.nalashaa.timesheet.service.IClientService;

/**
 * This class is used to provide the implementations of all client's serviecs.
 * @author siva
 *
 */
@Service
@Transactional
public class ClientServiceImpl implements IClientService {
    
    private static final Logger logger = LogManager.getLogger(ClientServiceImpl.class);

    @Autowired
    IClientDAO clientRepository;
    
    @Autowired
    IPersonDAO personRepository;
    
    @Autowired
	ICountryDAO countryDAO;
    
    
    /**
	 * This method is used to provide the implementation for createClient method of ClientService interface 
	 * to create client on the basis of provided client informations as an argument.
	 * 
	 * @param client
	 * 			It contains the client information which needs to be registered.
	 * @return
	 * 			It return client object after registering in the application.
	 */
    @Override
    public Client createClient(Client client) {
        logger.info("Entered : createClient service");
        Client clientDb=null;
        try {
        if (StringUtils.isNotEmpty(client.getClientId())) {
            clientDb = clientRepository.getClientByClientId(client.getClientId());
            if (clientDb != null) {
                throw new TimeSheetException("Client is already registered with this client Id");
            }
        } 
        client.setCreatedDate(new Date());
        client.setLastUpdatedTime(new Date());
        return clientRepository.save(client);
        } catch (Exception ex) {
            logger.error("Error in ClientServiceImpl/CreateClient");
            logger.error(ex.getMessage());
            throw ex;
        }
    }

	/**
     * This method is used to provide all active clients list.
     * 
     * @return
     * 			It returns list of all active clients.	
     */
	@Override
	public List<Client> getAllActiveClients() {
		 logger.info("Entered : getAllClients");
		List<Client>  list=clientRepository.getAllActiveClients();
		return list;
	}

	/**
	 * This method is used to provide the implementation for updateClient method of clientService interface.
	 * 
	 * @param client
	 * 			It represents client details which needs to be updated.
	 * @return
	 * 			It returns client details after update.
	 */
	@Override
	public void updateClient(Client client) {
		 logger.info("Entered : updateClient");
		 client.setLastUpdatedTime(new Timestamp(System.currentTimeMillis()));
		 clientRepository.save(client);
	}
	
	/**
	 * This method is used to return list of countries.
	 * @return
	 * 		List of countries object.
	 */
	@Override
	public List<Country> getAllCountries() {
		 logger.info("Entered : getAllCountries");
		return countryDAO.findAll();
	}

}
