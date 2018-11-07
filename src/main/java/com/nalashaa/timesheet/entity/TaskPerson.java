/**
 * 
 */

package com.nalashaa.timesheet.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * @author siva
 *
 */

@Entity
@Table(name = "TASKPERSON",uniqueConstraints=
@UniqueConstraint(columnNames = {"TASK_ID", "PERSON_ID"}))
public class TaskPerson implements Serializable {

    @Id
    @Column(name = "ID", unique = true, nullable = false, length = 20)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    
    @ManyToOne
    @JoinColumn(name = "TASK_ID", referencedColumnName = "ID", updatable = true, insertable = true, nullable = false)
    private Task task;
    
    @ManyToOne
    @JoinColumn(name = "PERSON_ID", referencedColumnName = "ID", updatable = true, insertable = true, nullable = false)
    private Person person;

      
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
     * @return the Task
     */
    public Task getTask() {
        return task;
    }

    
    /**
     * @param Task the Task to set
     */
    public void setTask(Task task) {
        this.task = task;
    }

    
    /**
     * @return the person
     */
    public Person getPerson() {
        return person;
    }

    
    /**
     * @param person the person to set
     */
    public void setPerson(Person person) {
        this.person = person;
    }

}
