/**
 * 
 */
package com.nalashaa.timesheet.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
/**
 * 
 * @author ashwanikannojia
 * 
 * This class is responsible for creating Email object using required field {from, to, cc, subject, message}
 *
 */
public class Email {
	
	private String from;

	private List<String> to;

	private List<String> cc;

	private String subject;

	private String message;
	
	private boolean isForgetPassword;

	public Email() {
		this.to = new ArrayList<String>();
		this.cc = new ArrayList<String>();
	}
	/**
	 * This is constructor, to set the provided arguments in the Email object.
	 * @param from : It represent the person name from where it came.
	 * @param toList : It represent the list of sender's mail ids.
	 * @param subject : Topic about the mail.
	 * @param message : It represent the information, which is needs to be send.
	 */
	public Email(String from, String toList, String subject, String message,boolean isForgetPassword) {
		this();
		this.from = from;
		this.subject = subject;
		this.message = message;
		this.isForgetPassword = isForgetPassword;
		this.to.addAll(Arrays.asList(splitByComma(toList)));
	}

	/**
	 * This is constructor, to set the provided arguments in the Email object.
	 * @param from		: It represent the person name from where it came.
	 * @param toList	: It represent the list of sender's mail ids.
	 * @param ccList	: It represent the list of sender's mail ids which will use as a carbon copy. 
	 * @param subject	: Topic about the mail.
	 * @param message	: It represent the information, which is needs to be send.
	 */
	public Email(String from, String toList, String ccList, String subject, String message) {
		this();
		this.from = from;
		this.subject = subject;
		this.message = message;
		this.to.addAll(Arrays.asList(splitByComma(toList)));
		this.cc.addAll(Arrays.asList(splitByComma(ccList)));
	}
	
	/**
	 * 
	 * @param from
	 * @param toList
	 * @param subject
	 * @param message
	 */
	public Email(String from, String toList, String subject, String message)
	{
		this();
		this.from = from;
		this.subject = subject;
		this.message = message;
		this.to.addAll(Arrays.asList(splitByComma(toList)));
	}
	
	public Email(String from, List<String> toList, String subject, String message)
	{
		this();
		this.from = from;
		this.subject = subject;
		this.message = message;
		this.to.addAll(toList);
	}

	/**
	 * @return the from
	 */
	public String getFrom() {
		return from;
	}

	/**
	 * @param from
	 *            the from to set
	 */
	public void setFrom(String from) {
		this.from = from;
	}

	/**
	 * @return the to
	 */
	public List<String> getTo() {
		return to;
	}

	/**
	 * @param to
	 *            the to to set
	 */
	public void setTo(List<String> to) {
		this.to = to;
	}

	/**
	 * @return the cc
	 */
	public List<String> getCc() {
		return cc;
	}

	/**
	 * @param cc
	 *            the cc to set
	 */
	public void setCc(List<String> cc) {
		this.cc = cc;
	}

	/**
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * @param subject
	 *            the subject to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message
	 *            the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the isForgetPassword
	 */
	public boolean isForgetPassword() {
		return isForgetPassword;
	}
	
	/**
	 * @param isForgetPassword the isForgetPassword to set
	 */
	public void setForgetPassword(boolean isForgetPassword) {
		this.isForgetPassword = isForgetPassword;
	}
	
	/**
	 * 
	 * @param toMultiple
	 * @return
	 */
	private String[] splitByComma(String toMultiple) {
		String[] toSplit = toMultiple.split(",");
		return toSplit;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getToAsList() {
		return concatenate(this.to, ",");
	}
	
	/**
	 * This method is used to concatenate the list of items.
	 * @param listOfItems
	 * @param separator
	 * @return
	 */
	public static String concatenate(List<String> listOfItems, String separator) {
		StringBuilder sb = new StringBuilder();
		Iterator<String> stit = listOfItems.iterator();

		while (stit.hasNext()) {
			sb.append(stit.next());
			if (stit.hasNext()) {
				sb.append(separator);
			}
		}

		return sb.toString();
	}
}
