/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.matsaboli2.podaci;

import java.util.Date;
import java.io.Serializable;

/**
 *
 * @author Matija
 */
public class EmailStatistika implements Serializable {
    private int JMSNumber;
    private Date startTime;
    private Date endTime;
    private int messagesNumber;
    private int NWTiSMessagesNumber;

    public EmailStatistika() {
    }

    public EmailStatistika(int JMSNumber, Date startTime, Date endTime, int messagesNumber, int NWTiSMessagesNumber) {
        this.JMSNumber = JMSNumber;
        this.startTime = startTime;
        this.endTime = endTime;
        this.messagesNumber = messagesNumber;
        this.NWTiSMessagesNumber = NWTiSMessagesNumber;
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

    public int getNWTiSMessagesNumber() {
        return NWTiSMessagesNumber;
    }

    public void setNWTiSMessagesNumber(int NWTiSMessagesNumber) {
        this.NWTiSMessagesNumber = NWTiSMessagesNumber;
    }
}
