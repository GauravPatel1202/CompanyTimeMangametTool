
package com.nalashaa.timesheet.controller;

import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nalashaa.timesheet.constant.Endpoints;
import com.nalashaa.timesheet.entity.Client;
import com.nalashaa.timesheet.entity.Country;
import com.nalashaa.timesheet.service.IClientService;
import com.nalashaa.timesheet.util.GenericResponseDataBlock;
import com.nalashaa.timesheet.util.GenericResponseGenerator;

/**
 * 
 * @author ashwaniKannojia
 * 
 * This class is responsible for client's REST APIs call .
 */
@RestController
@RequestMapping("/client")
public class ClientController {

    private static Logger logger = LogManager.getLogger(ClientController.class);

    @Autowired
    IClientService clientService;
    
   /**
    * This method is used to create client.
    * @param client
    * 		It represents client information.
    * @return
    * 		It will return client object as response after registering.
    */
    @PostMapping(value = Endpoints.DEFAULT)
    public ResponseEntity<Client> createClient(@RequestBody Client client) {
        logger.info("Entered : createClient");
        return new ResponseEntity<Client>(clientService.createClient(client), HttpStatus.OK);
    }

    /**
     * This method is used to get all Clients details.
     * @return
     * 		It is returning all clients list as response.
     */
    @GetMapping(value = Endpoints.GET_ALL_ACTIVE_CLIENTS)
    public ResponseEntity<List<Client>> getAllActiveClients() {
        logger.info("Entered : getAllClients ");
        return new ResponseEntity<List<Client>>(clientService.getAllActiveClients(), HttpStatus.OK);
    }
    
    /**
     * This method is used to update the client informations.
     * @param client
     * 			It is object of client with updated informations.
     * @return
     * 			Return as response of GenericResponseDataBlock object. 
     */
    @PutMapping(value = Endpoints.DEFAULT)
    public ResponseEntity<GenericResponseDataBlock> updateClient(@RequestBody Client client) {
        logger.info("Entered : Update Client");
        try {
        	clientService.updateClient(client);
        	logger.info("Updated client successfully");
        	return GenericResponseGenerator.getGenericResponse("Success", true, 200,HttpStatus.OK);
        }catch(Exception ex) {
        	logger.error("EXCEPTION : Failure to update client details. Reason :"+ex.getMessage());
        	return GenericResponseGenerator.getGenericResponse(ex.getMessage(), false, 500,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    
    /**
     * This method is used to update the client informations.
     * @param client
     * 			It is object of client with updated informations.
     * @return
     * 			Return as response of GenericResponseDataBlock object. 
     */
    @GetMapping(value = Endpoints.ALL_COUNTRIES)
    public ResponseEntity<List<Country>> getCountries() {
        logger.info("Entered : Get All Countries");
        return new ResponseEntity<List<Country>>(clientService.getAllCountries(),HttpStatus.OK);
    }
    
}
