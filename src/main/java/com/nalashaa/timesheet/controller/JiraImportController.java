
package com.nalashaa.timesheet.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.nalashaa.timesheet.constant.Endpoints;
import com.nalashaa.timesheet.repository.IPersonDAO;
import com.nalashaa.timesheet.repository.IProjectDAO;
import com.nalashaa.timesheet.repository.IProjectPersonDAO;
import com.nalashaa.timesheet.repository.ITaskDAO;
import com.nalashaa.timesheet.repository.ITaskPersonDAO;
import com.nalashaa.timesheet.repository.IWorkLogDAO;
import com.nalashaa.timesheet.repository.IWorkLogStatusDAO;
import com.nalashaa.timesheet.service.IJiraImportService;

/**
 * 
 * @author somdutt
 * 
 * This class is responsible for Jira Import REST APIs call .
 */
@RestController
@RequestMapping("/import")
public class JiraImportController {

    private static Logger logger = LogManager.getLogger(JiraImportController.class);

    @Autowired
    IProjectDAO projectrepo;
    
    @Autowired
    IPersonDAO personRepo;
    
    @Autowired
    ITaskDAO taskrepo;
    
    @Autowired
    ITaskPersonDAO taskPersonRepository;
    
    @Autowired
    IWorkLogStatusDAO worklogstatusrepo;
    
    @Autowired
    IWorkLogDAO worklogrepo;
    
    @Autowired
    ITaskDAO taskRepository;
    
    @Autowired
    IProjectPersonDAO projpersonrepo;
    
    @Autowired
    IJiraImportService jiraimportservice;
    
   /**
    * 
    * @param file
    * @param userId
    * @return
    */
    @PostMapping(value = "/jiraimport")
    public ResponseEntity jiraImport(@RequestParam(value="attachment") MultipartFile file, @RequestParam(value="userId") String userId) {
        String response = jiraimportservice.jiraImport(file, userId);
            return ResponseEntity.ok()
            		.contentType(MediaType.parseMediaType("application/text"))
            		.body(response);
        
    }
    
    
    /**
     * 
     * @return
     * @throws IOException
     */
    @GetMapping(value = Endpoints.DOWLOAD_JIRA_IMPORT_SAMPLE)
    public ResponseEntity<InputStreamResource> downloadTemplate() throws IOException{
    	ClassLoader classLoader = getClass().getClassLoader();
    	File fl = new File(classLoader.getResource("jira-template/JIRA_TEMPLATE.xlsx").getFile());
    	InputStreamResource resource = new InputStreamResource(new FileInputStream(fl));
    	return ResponseEntity.ok()
       // Content-Disposition
    			.header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fl.getName())
     // Content-Type
    			.contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
     // Contet-Length
    			.contentLength(fl.length()) //
    			.body(resource); 
    }
}
