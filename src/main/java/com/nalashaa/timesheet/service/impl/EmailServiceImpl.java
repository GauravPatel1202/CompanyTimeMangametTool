package com.nalashaa.timesheet.service.impl;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.nalashaa.timesheet.model.Email;
import com.nalashaa.timesheet.service.IEmailService;
import com.nalashaa.timesheet.util.EmailTemplate;

/**
 * 
 * @author ashwanikannojia
 * 
 * This class is used to provide email services.
 *
 */
@Component
public class EmailServiceImpl implements IEmailService {

	private static final Logger logger = LogManager.getLogger(EmailServiceImpl.class);

	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	EmailTemplate emailTemplate;

	@Autowired
    private Environment env;
	
	
	private void send(Email eParams) throws MessagingException {
			sendPlainTextMail(eParams);
	}
	
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
	 * @throws MessagingException 
	 */
	@Override
	public void sendMail(String emailId, boolean isPassword,String tempPassword, String templateName, String subject, String empName, String empId, String date) throws MessagingException{
		String from = env.getRequiredProperty("from");
		
		String to = emailId;
		Map<String, String> replacements = new HashMap<String, String>();
		if(isPassword) {
			emailTemplate.loadTemplate(env.getRequiredProperty(templateName));
			replacements.put("defaultpassword", tempPassword);
		}else if("timesheet.manager.rejection".equals(templateName)){
			emailTemplate.loadTemplate(env.getRequiredProperty("timesheet.manager.rejection"));
		}
		replacements.put("empId", empId);
		replacements.put("user", empName);
		replacements.put("dates", date);
		//replacements.put("today", String.valueOf(new Date()));
		String message = emailTemplate.getTemplate(replacements);
		Email email = new Email(from, to, subject, message, isPassword);
		send(email);
		logger.info("SUCCESSFULLY SENT EMAIL TO :"+emailId);
	}
	
	/**
	 * This method is used to set all email parameteres for template.
	 * @param eParams
	 * 			It is used to pass email object.
	 * @throws MessagingException 
	 */
	private void sendPlainTextMail(Email eParams) throws MessagingException {
		
		MimeMessage mime = mailSender.createMimeMessage();
  	  	MimeMessageHelper helper = new MimeMessageHelper(mime, true);
		eParams.getTo().toArray(new String[eParams.getTo().size()]);
		helper.setTo(eParams.getTo().toArray(new String[eParams.getTo().size()]));
		helper.setReplyTo(eParams.getFrom());
		helper.setFrom(eParams.getFrom());
		helper.setSubject(eParams.getSubject());
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("email-templates/Logo.png").getFile());
		String htmlText = "<pre style='font-family:serif;font-size:12pt;color:rgb(31,73,125);'>"+eParams.getMessage()+
				"</pre><br/><img src=\"cid:logo\"><pre style='color:rgb(127,127,127);font-size:11pt;font-family:calibri;'>This message was sent by Nalashaa Healthcare Solutions – Orion\r\n" + 
				"<span style='font-size:10pt;'>Login to Orion-Time tracker: http://nalashaa.orion.com:8080/#/login" + 
				"<br/><i>If you happen to encounter any issue with regards to accessing the Orion application or  any bugs kindly email it to orion_helpdesk@nalashaa.com</i></pre> ";
		helper.setText(htmlText,true);
		helper.addInline("logo", file);
		if(eParams.getCc() != null)
			if (eParams.getCc().size() > 0) {
				helper.setCc(eParams.getCc().toArray(new String[eParams.getCc().size()]));
			}
		mailSender.send(mime);
	}

}
