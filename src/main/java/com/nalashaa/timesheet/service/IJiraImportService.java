/**
 * 
 */
package com.nalashaa.timesheet.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author somadutta
 *
 */
public interface IJiraImportService {

	String jiraImport(MultipartFile file , String client);
}
