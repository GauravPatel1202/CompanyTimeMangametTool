package com.nalashaa.timesheet.service;

/**
 * 
 * @author ashwanikannojia
 *
 *This is interface which is responsible for email services.
 */
public interface IEmailService {
	
	/**
	 * This method is used to send mail to user for two cases :
	 *  1. User has not filled his/her timesheet in required time.
	 *  2. To send the mail who has forgotten his/her password.
	 * @param emailId
	 * 			It is email id of that particular user.
	 * @param isForgetPassword
	 * 			It is a flag to identify the objective of mail. 
	 * @param tempPasword
	 * 			That field is used to store temporary password for that user.
	 * @throws javax.mail.MessagingException 
	 */
	public void sendMail(String emailId, boolean isPassword, String tempPassword, String templateName, String subject,
			String empName, String empId, String date) throws javax.mail.MessagingException;


}
