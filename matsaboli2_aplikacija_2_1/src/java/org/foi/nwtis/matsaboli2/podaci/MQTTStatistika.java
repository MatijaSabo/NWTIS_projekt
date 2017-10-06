/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.matsaboli2.podaci;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Matija
 */
public class MQTTStatistika implements Serializable {

    private int JMSNumber;
    private Date startTime;
    private Date endTime;
    private int messagesNumber;
    private List<String> messagesData = new ArrayList<>();

    public MQTTStatistika() {
    }

    public MQTTStatistika(int JMSNumber, Date startTime, Date endTime, int messagesNumber) {
        this.JMSNumber = JMSNumber;
        this.startTime = startTime;
        this.endTime = endTime;
        this.messagesNumber = messagesNumber;
    }

    public int getJMSNumber() {
        return JMSNumber;
    }

    public void setJMSNumber(int JMSNumber) {
        this.JMSNumber = JMSNumber;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public int getMessagesNumber() {
        return messagesNumber;
    }

    public void setMessagesNumber(int messagesNumber) {
        this.messagesNumber = messagesNumber;
    }

    public List<String> getMessagesData() {
        return messagesData;
    }

    public void setMessagesData(List<String> messagesData) {
        this.messagesData = messagesData;
    }
}
