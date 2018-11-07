
package com.nalashaa.timesheet.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.nalashaa.timesheet.entity.Report;
import com.nalashaa.timesheet.entity.Worklog;

/**
 * @author VijayGanesh
 * 
 *         This class is used to provide interface for report operations.
 */
public interface IReportService {

	Report getReportDetails(String projectId, String month, String year);

	public List<Worklog> saveUploadedTimesheet(MultipartFile file, String projectId);

}
