package com.nalashaa.timesheet.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.nalashaa.timesheet.entity.Report;
import com.nalashaa.timesheet.entity.Worklog;
import com.nalashaa.timesheet.service.IReportService;
import com.nalashaa.timesheet.util.GenericResponseDataBlock;
import com.nalashaa.timesheet.util.GenericResponseGenerator;

@RestController
@RequestMapping("/report")
public class ReportController {

	@Autowired
	IReportService reportService;

	@PostMapping("/import")
	public ResponseEntity<GenericResponseDataBlock> handleFileUpload(@RequestParam("file") MultipartFile file, String projectId) {

		List<Worklog> worklog = reportService.saveUploadedTimesheet(file, projectId);
		if (worklog != null) {
			return GenericResponseGenerator.getGenericResponse("Success", true, 200, HttpStatus.OK);
		}
		return GenericResponseGenerator.getGenericResponse("Failure", false, 500, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@GetMapping("")
	public ResponseEntity<Report> getReportDetails(String projectId, String month, String year) {

		return new ResponseEntity<Report>(reportService.getReportDetails(projectId, month, year), HttpStatus.OK);
	}
	
}