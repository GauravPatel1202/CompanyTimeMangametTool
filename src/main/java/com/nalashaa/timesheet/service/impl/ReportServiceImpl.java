package com.nalashaa.timesheet.service.impl;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.nalashaa.timesheet.entity.Person;
import com.nalashaa.timesheet.entity.Project;
import com.nalashaa.timesheet.entity.Report;
import com.nalashaa.timesheet.entity.Task;
import com.nalashaa.timesheet.entity.TaskPerson;
import com.nalashaa.timesheet.entity.Worklog;
import com.nalashaa.timesheet.entity.WorklogStatus;
import com.nalashaa.timesheet.repository.IPersonDAO;
import com.nalashaa.timesheet.repository.IProjectDAO;
import com.nalashaa.timesheet.repository.ITaskDAO;
import com.nalashaa.timesheet.repository.ITaskPersonDAO;
import com.nalashaa.timesheet.repository.IWorkLogDAO;
import com.nalashaa.timesheet.service.IReportService;

/**
 * This class is used to provide the implementations of all Report serviecs.
 * 
 * @author VijayGanesh
 */
@Service
@Transactional
public class ReportServiceImpl implements IReportService {

	// private static final Logger logger =
	// LogManager.getLogger(ReportServiceImpl.class);

	@Autowired
	ITaskDAO iTaskDAO;


	@Autowired
	IWorkLogDAO iWorkLogDAO;

	@Autowired
	ITaskPersonDAO iTaskPersonDAO;


	public List<Worklog> saveUploadedTimesheet(MultipartFile file, String projectId) {

		BufferedReader br = null;
		String cvsSplitBy = ",";
		List<Worklog> workLogList = new ArrayList<Worklog>();

		try {
			String line;
			InputStream is = file.getInputStream();
			br = new BufferedReader(new InputStreamReader(is));
			
			while ((line = br.readLine()) != null) {
				String[] workLogDetails = line.split(cvsSplitBy);
				Worklog worklog = new Worklog();
				String taskId = workLogDetails[0];
				String taskName = workLogDetails[1];
				String resourceName = workLogDetails[2];
				Long estimatedHours = Long.valueOf(workLogDetails[3]);
				Long hoursSpent = Long.valueOf(workLogDetails[4]);

						Task task = iTaskDAO.findByTaskId(taskId);
						if (task == null) {
							Task createTask = new Task();
							createTask.setTaskId(taskId);
							createTask.setTaskName(taskName);
							createTask.setHoursLeft(estimatedHours - hoursSpent);
							createTask.setEstimatedHours(Double.valueOf(estimatedHours));
							//createTask.setCreatedBy(String.valueOf(person.getId()));
							createTask.setCreatedDate(new Timestamp(System.currentTimeMillis()));
							createTask.setLastUpdatedTime(new Timestamp(System.currentTimeMillis()));
							task = iTaskDAO.save(createTask);
						}
						
						worklog.setHoursSpent(Float.valueOf(hoursSpent));
						worklog.setTask(task);
						//worklog.setPerson(person);
						//worklog.setProject(project);
						//worklog.setCreatedBy(String.valueOf(person.getId()));
						worklog.setCreatedDate(new Timestamp(System.currentTimeMillis()));
						worklog.setLastUpdatedTime(new Timestamp(System.currentTimeMillis()));
						WorklogStatus worklogStatus = new WorklogStatus();
						worklogStatus.setId(1);
						worklog.setStaus(worklogStatus);
						workLogList.add(worklog);
			}
			if (workLogList.size() > 0) {
				return iWorkLogDAO.save(workLogList);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	@Override
	public Report getReportDetails(String projectId, String month, String year) {
		
		
		return null;
	}

}
