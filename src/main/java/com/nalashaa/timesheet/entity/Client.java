/**
 * 
 */

package com.nalashaa.timesheet.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonInclude;


@Entity
@Table(name = "CLIENT")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Client extends PersistentEntity {

	private static final long serialVersionUID = -31855326171689986L;

	@Id
    @Column(name = "ID", unique = true, nullable = false, length = 20)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "CLIENT_ID", updatable = true, length = 30, unique = true, nullable = false)
    private String clientId;
    
    @Column(name = "NAME", updatable = true, length = 200)
    private String name;

    @Column(name = "TELEPHONE_NUMBER", updatable = true, nullable = false, length = 20)
    private long telephoneNumber;

    @Column(name = "STATUS", updatable = true)
    private boolean status;
    
    @Column(name = "EMAIL_ID", updatable = true, nullable = false, length = 150)
    private String emailId;
    
    @Column(name = "ADDRESS_LINE_1", updatable = true, length =255)
    private String addressLine1;
    
    @Column(name = "ADDRESS_LINE_2", updatable = true, length =255)
    private String addressLine2;
    
    @Column(name = "CITY", updatable = true, length =50)
    private String city;
    
    @Column(name = "STATE", updatable = true, length =50)
    private String state;
    
    @Column(name = "COUNTRY", updatable = true, length =50)
    private String country;
    
    @Column(name = "POSTAL_CODE", updatable = true,  length =20)
    private Long postalCode;

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the clientId
	 */
	public String getClientId() {
		return clientId;
	}

	/**
	 * @param clientId the clientId to set
	 */
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the telephoneNumber
	 */
	public long getTelephoneNumber() {
		return telephoneNumber;
	}

	/**
	 * @param telephoneNumber the telephoneNumber to set
	 */
	public void setTelephoneNumber(long telephoneNumber) {
		this.telephoneNumber = telephoneNumber;
	}

	/**
	 * @return the status
	 */
	public boolean isStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(boolean status) {
		this.status = status;
	}

	/**
	 * @return the emailId
	 */
	public String getEmailId() {
		return emailId;
	}

	/**
	 * @param emailId the emailId to set
	 */
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	/**
	 * @return the addressLine1
	 */
	public String getAddressLine1() {
		return addressLine1;
	}

	/**
	 * @param addressLine1 the addressLine1 to set
	 */
	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}

	/**
	 * @return the addressLine2
	 */
	public String getAddressLine2() {
		return addressLine2;
	}

	/**
	 * @param addressLine2 the addressLine2 to set
	 */
	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}

	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * @param city the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * @param country the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * @return the postalCode
	 */
	public Long getPostalCode() {
		return postalCode;
	}

	/**
	 * @param postalCode the postalCode to set
	 */
	public void setPostalCode(Long postalCode) {
		this.postalCode = postalCode;
	}

    
}
