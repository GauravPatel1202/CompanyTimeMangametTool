package com.nalashaa.timesheet.util;

import java.io.File;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class EmailTemplate {

	private static Logger logger = LogManager.getLogger(EmailTemplate.class);
	private String templateId;

	private String template;

	private Map<String, String> replacementParams;

	public void loadTemplate(String templateId){
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("email-templates/" + templateId).getFile());
		String content = ApplicationConstants.BLANK;
		try {
			content = new String(Files.readAllBytes(file.toPath()));
		} catch (Exception e) {
			logger.error("UNABLE TO READ TEMPLATE WITH ID : "+templateId +" DUE TO "+e.getMessage());
		}
		this.template = content;
	}

	public String getTemplate(Map<String, String> replacements) {
		String cTemplate = this.getTemplate();

		if (!isObjectEmpty(cTemplate)) {
			for (Map.Entry<String, String> entry : replacements.entrySet()) {
				cTemplate = cTemplate.replace("{{" + entry.getKey() + "}}", entry.getValue());
			}
		}
		return cTemplate;
	}

	/**
	 * Checks if is object empty.
	 *
	 * @param object the object
	 * @return true, if is object empty
	 */
	public static boolean isObjectEmpty(Object object) {
		if(object == null) return true;
		else if(object instanceof String) {
			if (((String)object).trim().length() == 0) {
				return true;
			}
		} else if(object instanceof Collection) {
			return isCollectionEmpty((Collection<?>)object);
		}
		return false;
	}
	
	/**
	 * Generate a CRON expression is a string comprising 6 or 7 fields separated by white space.
	 *
	 * @param seconds    mandatory = yes. allowed values = {@code  0-59    * / , -}
	 * @param minutes    mandatory = yes. allowed values = {@code  0-59    * / , -}
	 * @param hours      mandatory = yes. allowed values = {@code 0-23   * / , -}
	 * @param dayOfMonth mandatory = yes. allowed values = {@code 1-31  * / , - ? L W}
	 * @param month      mandatory = yes. allowed values = {@code 1-12 or JAN-DEC    * / , -}
	 * @param dayOfWeek  mandatory = yes. allowed values = {@code 0-6 or SUN-SAT * / , - ? L #}
	 * @param year       mandatory = no. allowed values = {@code 1970–2099    * / , -}
	 * @return a CRON Formatted String.
	 */
	public static String generateCronExpression(final String seconds, final String minutes, final String hours,
	                                             final String dayOfMonth,
	                                             final String month, final String dayOfWeek, final String year)
	{
	  return String.format("%1$s %2$s %3$s %4$s %5$s %6$s %7$s", seconds, minutes, hours, dayOfMonth, month, dayOfWeek, year);
	}
	
	/**
	 * Checks if is collection empty.
	 *
	 * @param collection the collection
	 * @return true, if is collection empty
	 */
	private static boolean isCollectionEmpty(Collection<?> collection) {
		if (collection == null || collection.isEmpty()) {
			return true;
		}
		return false;
	}
	
	/**
	 * @return the templateId
	 */
	public String getTemplateId() {
		return templateId;
	}

	/**
	 * @param templateId
	 *            the templateId to set
	 */
	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	/**
	 * @return the template
	 */
	public String getTemplate() {
		return template;
	}

	/**
	 * @param template
	 *            the template to set
	 */
	public void setTemplate(String template) {
		this.template = template;
	}

	/**
	 * @return the replacementParams
	 */
	public Map<String, String> getReplacementParams() {
		return replacementParams;
	}

	/**
	 * @param replacementParams
	 *            the replacementParams to set
	 */
	public void setReplacementParams(Map<String, String> replacementParams) {
		this.replacementParams = replacementParams;
	}

}
