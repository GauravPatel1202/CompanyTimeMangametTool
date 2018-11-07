/**
 * 
 */
package com.nalashaa.timesheet.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nalashaa.timesheet.entity.Project;
import com.nalashaa.timesheet.entity.Task;
import com.nalashaa.timesheet.repository.IClientDAO;
import com.nalashaa.timesheet.repository.IPersonDAO;
import com.nalashaa.timesheet.repository.IProjectDAO;
import com.nalashaa.timesheet.repository.IProjectPersonDAO;
import com.nalashaa.timesheet.repository.ITaskDAO;
import com.nalashaa.timesheet.repository.ITaskPersonDAO;
import com.nalashaa.timesheet.repository.IWorkLogDAO;
import com.nalashaa.timesheet.service.IPurgeOldDataService;

/**
 * @author somadutta
 *
 */
@Service
@Transactional
public class PurgeOldDataServiceImpl implements IPurgeOldDataService {

	/* (non-Javadoc)
	 * @see com.nalashaa.timesheet.service.IPurgeOldDataService#purgeOldData()
	 */
	@Autowired
	ITaskDAO taskRepo;
	
	@Autowired
	ITaskPersonDAO taskPersonRepo;
	
	@Autowired
	IProjectDAO projectRepo;
	
	@Autowired
	IProjectPersonDAO properRepo;
	
	@Autowired
	IWorkLogDAO wlRepo;
	
	@Autowired
	IClientDAO clientRepo;
	
	@Autowired
	IPersonDAO personRepo;
	
	@Override
	public void purgeOldData() {
		// TODO Auto-generated method stub
		List<Long> taskids= taskRepo.findOldTasksToBePurged();
		if(!taskids.isEmpty()) {
			/*List<Long> taskids = new ArrayList<Long>();*/
			wlRepo.purgeOldEntries(taskids);
			taskPersonRepo.purgeOldTaskAllocations(taskids);
			taskRepo.deleteOldTasks(taskids);
		}
		List<Long> projectids = projectRepo.findOldProjectsToBePurged();
		if(!projectids.isEmpty()) {
//			List<Long> projectids = new ArrayList<Long>();
			properRepo.purgeOldProjectPersonAllocation(projectids);
		}
		projectRepo.purgeOldProjects();
		clientRepo.purgeInactiveClients();
		personRepo.purgeInactiveEmployees();
	}

}
