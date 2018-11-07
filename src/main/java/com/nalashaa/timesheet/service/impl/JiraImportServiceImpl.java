/**
 * 
 */
package com.nalashaa.timesheet.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.nalashaa.timesheet.Exception.TimeSheetException;
import com.nalashaa.timesheet.entity.Person;
import com.nalashaa.timesheet.entity.Project;
import com.nalashaa.timesheet.entity.ProjectPerson;
import com.nalashaa.timesheet.entity.Task;
import com.nalashaa.timesheet.entity.TaskPerson;
import com.nalashaa.timesheet.entity.Worklog;
import com.nalashaa.timesheet.entity.WorklogStatus;
import com.nalashaa.timesheet.repository.IPersonDAO;
import com.nalashaa.timesheet.repository.IProjectDAO;
import com.nalashaa.timesheet.repository.IProjectPersonDAO;
import com.nalashaa.timesheet.repository.ITaskDAO;
import com.nalashaa.timesheet.repository.ITaskPersonDAO;
import com.nalashaa.timesheet.repository.IWorkLogDAO;
import com.nalashaa.timesheet.repository.IWorkLogStatusDAO;
import com.nalashaa.timesheet.service.IJiraImportService;
import com.nalashaa.timesheet.util.ApplicationConstants;

/**
 * @author somadutta
 *
 */
@Service
@Transactional
public class JiraImportServiceImpl implements IJiraImportService {

	/* (non-Javadoc)
	 * @see com.nalashaa.timesheet.service.IJiraImport#jiraImport(org.springframework.web.multipart.MultipartFile, java.lang.String)
	 */
	private static Logger logger = LogManager.getLogger(JiraImportServiceImpl.class);
	
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
    private Environment env;
    
    /**
     * 
     * This method imports data from jira/version1 or other worklog related tools into the orion application.
     */
	@Override
	public String jiraImport(MultipartFile file, String userId) {
		// TODO Auto-generated method stub
		
		
		logger.info("Entered : jiraImport");
        InputStream inputStream = null;
		try {
			inputStream = file.getInputStream();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        Workbook workbook = null;
		try {
			workbook = WorkbookFactory.create(inputStream);
			
		} catch (EncryptedDocumentException | InvalidFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		CellStyle cellstyle= workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setColor(IndexedColors.RED.getIndex());
		cellstyle.setFillBackgroundColor(IndexedColors.YELLOW.getIndex());
		cellstyle.setFont(font);
		Map<String,Float> hrsSpent = new HashMap<String,Float>();
		Map<String , String> finalComments = new HashMap<String,String>();
        workbook.forEach(sheet -> 
        	{	
        		sheet.forEach(row -> 
        			{
        			try {
						if(row.getRowNum() == 0)
							return;
						String projectid = getCellValue(row.getCell(0), 0);
						/*if(row.getCell(0).getCellTypeEnum() == CellType.NUMERIC) {
							projectid = String.valueOf((int) row.getCell(0).getNumericCellValue());
						}
						else {
							projectid = row.getCell(0).getStringCellValue();
						}*/
						Project project = projectrepo.findByProjectId(projectid);
						Person person = personRepo.findByEmpIdAndStatusTrue(String.valueOf((int) row.getCell(1).getNumericCellValue()));
						DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
						DateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy");
						Date date = null;
						if(row.getCell(7).getCellTypeEnum() == CellType.STRING)
							try {
								date = dateFormat2.parse(row.getCell(7).getStringCellValue());
							} catch (ParseException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						else
							date = row.getCell(7).getDateCellValue();
						try {	
							date = dateFormat.parse(dateFormat.format(date));
						} catch (ParseException e) {
							e.printStackTrace();
						}
						
						ProjectPerson proper = projpersonrepo.findByProjectAndPerson(project, person);
						if(project != null && person!= null && (date.after(person.getDateOfJoining()) || date.equals(person.getDateOfJoining())) && proper != null) {
							Worklog wl = null;
							Task task  = null;
							String taskid = getCellValue(row.getCell(3),3);
							float daylimhrs = (float) row.getCell(6).getNumericCellValue();
							List<Worklog> wrkls = worklogrepo.findByPersonAndDate(person, date);
							for(Worklog wrkl : wrkls )
								daylimhrs = daylimhrs + wrkl.getHoursSpent();
							if((float) row.getCell(6).getNumericCellValue() > 24 || daylimhrs > 24)
							{
								throw new TimeSheetException("Total number of hrs exceed 24 for the day");
							}
							/*if(row.getCell(3).getCellTypeEnum() == CellType.NUMERIC)
								taskid = String.valueOf((int) row.getCell(3).getNumericCellValue());
							else {
								taskid = row.getCell(3).getStringCellValue();
							}*/	
							task = taskrepo.findByTaskIdAndProject(taskid,project);
							/*Task oldTask = taskrepo.findByTaskIdAndProject(taskid,project);*/
							TaskPerson taskPers = new TaskPerson();
							Task validTask = new Task();
							String comments = getCellValue(row.getCell(8), 8);
							/*if(row.getCell(8) != null) {
								if(row.getCell(8).getCellTypeEnum() == CellType.NUMERIC)
									comments = String.valueOf((int) row.getCell(8).getNumericCellValue());
								else if(row.getCell(8).getCellTypeEnum() == CellType.BLANK)
									comments = ApplicationConstants.BLANK;
								else 
									comments= row.getCell(8).getStringCellValue();
							}
							else {
								comments = ApplicationConstants.BLANK;
							}*/
							if(task != null) {
								Worklog wrklog = worklogrepo.findByPersonAndProjectAndTaskAndDate(person,project,task,date);
								if(wrklog != null) 
									wl = wrklog;
								else
									wl = new Worklog();
								task.getAssignedUsers().add(person);
								task.setTaskName(getCellValue(row.getCell(4), 4));
								task.setEstimatedHours(row.getCell(5).getNumericCellValue());
//								task.setJiraImport(true);
								taskrepo.save(task);
								wl.setTask(task);
								List<Person> taskPersonmapped = taskPersonRepository.getPersonsByTask(task); 
								boolean mapped = false;
								for (Person user : taskPersonmapped) {
									if(person.getId() == user.getId()){
										mapped = true;
										break;
									}
								}
								if(!mapped){
									taskPers.setTask(task);
									taskPers.setPerson(person);
									taskPersonRepository.save(taskPers);
								}
								validTask = task;
						
							}
							else {
								wl = new Worklog();
								Task newTask = new Task();
								newTask.setTaskId(taskid);
								newTask.setTaskName(getCellValue(row.getCell(4), 4));
								newTask.setCreatedBy(userId);
								newTask.setCreatedDate(new Timestamp(System.currentTimeMillis()));
								newTask.setLastUpdatedBy(userId);
								newTask.setLastUpdatedTime(new Timestamp(System.currentTimeMillis()));
								// Once per cell
								newTask.setEstimatedHours(row.getCell(5).getNumericCellValue());
								newTask.setProject(project);
								newTask.setStatus(true);
								newTask.getAssignedUsers().add(person);
								newTask.setActive(true);
								newTask.setJiraImport(true);
								Task nwtask = taskRepository.save(newTask);
								wl.setTask(nwtask);
								taskPers.setTask(nwtask);
								taskPers.setPerson(person);
								taskPersonRepository.save(taskPers);
								validTask = nwtask;
							}
							Calendar cal = Calendar.getInstance();
							cal.setTime(date);
							Date firstDay = DateUtils.addDays(date, 0);
							while(cal.get(Calendar.DAY_OF_WEEK) != 2) {
								firstDay = DateUtils.addDays(firstDay, -1);
								cal.setTime(firstDay);
							}
							Date lastDay = DateUtils.addDays(firstDay, 4);
							List<Worklog> weekEntries = worklogrepo.findByPersonAndDateBetweenOrderByIndexAsc(person, firstDay, lastDay);
							List<Long> taskLst = new ArrayList<Long>();
							for(Worklog workLog : weekEntries) {
								if(!taskLst.contains(workLog.getTask().getId())) {
									taskLst.add(workLog.getTask().getId());
								}
							}
							int index = 1;
							if(taskLst.size() == 0) {
								index = 1;
							}else if(taskLst.contains(validTask.getId())) {
								index = taskLst.indexOf(validTask.getId()) + 1;
							}else {
								index = taskLst.size() + 1;
							}
							wl.setPerson(person);
							wl.setProject(project);
							wl.setDate(date);
							/*StringBuilder uNiqueKey = new StringBuilder("");
							uNiqueKey.append(person.getEmpId()).append(validTask.getTaskId()).append(dateFormat.format(date));
							String uniqueKey = uNiqueKey.toString();*/
							String uniqueKey = person.getEmpId()+project.getProjectId()+validTask.getTaskId()+dateFormat.format(date);
							if(hrsSpent.containsKey(uniqueKey))
								{	
									String newComments = getCellValue(row.getCell(8), 8);
									/*if(row.getCell(8) != null) {
										if(row.getCell(8).getCellTypeEnum() == CellType.NUMERIC)
											newComments = String.valueOf((int) row.getCell(8).getNumericCellValue());
										else if(row.getCell(8).getCellTypeEnum() == CellType.BLANK)
											newComments = ApplicationConstants.BLANK;
										else 
											newComments= row.getCell(8).getStringCellValue();
									}
									else {
										newComments = ApplicationConstants.BLANK;
									}*/
									Float newhrsSpent = hrsSpent.get(uniqueKey) + (float) row.getCell(6).getNumericCellValue();
									if(newhrsSpent > 24)
										throw new TimeSheetException("Total number of hrs exceed 24 for the day");
									hrsSpent.put(uniqueKey, newhrsSpent);
									String oldComments = finalComments.get(uniqueKey) + "\n";
									finalComments.put(uniqueKey, oldComments+ newComments);
								}
								else {
									finalComments.put(uniqueKey, comments);
									hrsSpent.put(uniqueKey,(float) row.getCell(6).getNumericCellValue() );
							}
							wl.setComments(finalComments.get(uniqueKey));
							wl.setHoursSpent(hrsSpent.get(uniqueKey));
							wl.setCreatedBy(userId);
							wl.setCreatedDate(new Timestamp(System.currentTimeMillis()));
							wl.setLastUpdatedBy(userId);
			                wl.setLastUpdatedTime(new Timestamp(System.currentTimeMillis()));
							WorklogStatus wls = worklogstatusrepo.findOne((long) 4);
							wl.setStaus(wls);
							wl.setIndex(index);
							worklogrepo.save(wl);
						}
						else if (person == null) {
							row.setRowStyle(cellstyle);
	        				row.createCell(9).setCellValue(env.getRequiredProperty("jiraImport.exceptionmssg.employeeNotRegistered"));
	        				row.getCell(9).setCellStyle(cellstyle);
						}
						else if(project == null) {
							row.setRowStyle(cellstyle);
	        				row.createCell(9).setCellValue(env.getRequiredProperty("jiraImport.exceptionmssg.projectNotSetup"));
	        				row.getCell(9).setCellStyle(cellstyle);
						}
						else if(proper == null) {
							row.setRowStyle(cellstyle);
	        				row.createCell(9).setCellValue(env.getRequiredProperty("jiraImport.exceptionmssg.EmployeeNotAssignedToProject"));
	        				row.getCell(9).setCellStyle(cellstyle);
						}
						else if(date.before(person.getDateOfJoining())){
							row.setRowStyle(cellstyle);
	        				row.createCell(9).setCellValue(env.getRequiredProperty("jiraImport.exceptionmssg.JoiningDateAfterProvidedDate"));
	        				row.getCell(9).setCellStyle(cellstyle);
						}
							
						row.forEach(cell ->{
							printCellValue(cell);
						});
						System.out.println();
					}
        			catch (NullPointerException e) {
        				
        				row.setRowStyle(cellstyle);
        				row.createCell(9).setCellValue(env.getRequiredProperty("jiraImport.exceptionmssg.NullValueinRow"));
        				row.getCell(9).setCellStyle(cellstyle);
						e.printStackTrace();
					}
        			catch(TimeSheetException te) {
        				row.setRowStyle(cellstyle);
        				if(te.getMessage().contains("NullCellAt"))
        					row.createCell(9).setCellValue(env.getRequiredProperty("jiraImport.exceptionmssg.NullValueinRow"));
        				row.createCell(9).setCellValue(env.getRequiredProperty("jiraImport.exceptionmssg.hrsSpentExceed24"));
        				row.getCell(9).setCellStyle(cellstyle);
        			}
        			catch(IllegalStateException ise) {
        				row.setRowStyle(cellstyle);
        				row.createCell(9).setCellValue(env.getRequiredProperty("jiraImport.exceptionmssg.incorrectDataType"));
        				row.getCell(9).setCellStyle(cellstyle);
        				ise.printStackTrace();
        			}
        			catch(DataIntegrityViolationException dive) {
        				row.setRowStyle(cellstyle);
        				row.createCell(9).setCellValue(env.getRequiredProperty("jiraImport.exceptionmssg.DBimcompatibleData"));
        				row.getCell(9).setCellStyle(cellstyle);
        				dive.printStackTrace();        			
        			}
        		});
        	});
        byte[] out = null;
//        ByteArrayOutputStream ous = new ByteArrayOutputStream();
//        workbook.write(ous);
//        out = ous.toByteArray();
        	String response = "";
            try {
            ByteArrayOutputStream ous = new ByteArrayOutputStream();
            workbook.write(ous);
            out = ous.toByteArray();
            //String content = new String(out);
            //System.out.println(content);
            StringBuilder sb = new StringBuilder();
            sb.append("data:application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;base64,");
            sb.append(StringUtils.newStringUtf8(Base64.encodeBase64(out, false)));
            response = sb.toString();
            //System.out.println(response);
            }
            catch(Exception e){
            	e.printStackTrace();
            }
//            return new ResponseEntity(out, HttpStatus.OK);
		return response;
	}
	
	private String getCellValue(Cell cell,int cellNum) {
		String value=ApplicationConstants.BLANK;
		if(cell!=null) {
            switch(cell.getCellTypeEnum()){
                case BOOLEAN:
                    value=String.valueOf(cell.getBooleanCellValue());
                    break;
                case NUMERIC:
                    value=BigDecimal.valueOf(
                        cell.getNumericCellValue()).toPlainString();
                    break;
                case STRING:
                    value=String.valueOf(cell.getStringCellValue());
                    break;
                case FORMULA:
                    value=String.valueOf(cell.getCellFormula());
                    break;
                case BLANK:
                	if(cellNum != 8)
                		throw new TimeSheetException("NullCellAt " + cell.getColumnIndex() );
                    value=ApplicationConstants.BLANK;
                    break;
                default:
                    ;
            }
		}
		else if(cellNum != 8)
			throw new TimeSheetException("NullCellAt " + cellNum );
		return value;
	}
	
	/**
	 * 
	 * @param cell
	 */
	private static void printCellValue(Cell cell) {
        switch (cell.getCellTypeEnum()) {
            case BOOLEAN:
                System.out.print(cell.getBooleanCellValue());
                break;
            case STRING:
                System.out.print(cell.getRichStringCellValue().getString());
                break;
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    System.out.print(cell.getDateCellValue());
                } else {
                    System.out.print(cell.getNumericCellValue());
                }
                break;
            case FORMULA:
                System.out.print(cell.getCellFormula());
                break;
            case BLANK:
                System.out.print("");
                break;
            default:
                System.out.print("");
        }
        System.out.print("\t");
    }

}
