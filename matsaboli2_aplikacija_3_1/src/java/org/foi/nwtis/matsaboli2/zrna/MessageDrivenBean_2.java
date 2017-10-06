/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.matsaboli2.zrna;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import org.foi.nwtis.matsaboli2.podaci.MQTTStatistika;

/**
 *
 * @author Matija
 */
@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "jms/NWTiS_matsaboli2_2")
    ,
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
})
public class MessageDrivenBean_2 implements MessageListener {
    
    public MessageDrivenBean_2() {
    }
    
    @Override
    public void onMessage(Message message) {
        
        try {
            MQTTStatistika statistika = new MQTTStatistika();
            statistika = (MQTTStatistika) message.getBody(Object.class);
            SingletonSessionBean.MqttList.add(statistika);
        } catch (JMSException ex) {
            Logger.getLogger(MessageDrivenBean_2.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        System.out.println("Dodana nova Mqtt statistika");
    }
    
}
