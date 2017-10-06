/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.matsaboli2.dretve;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.mail.AuthenticationFailedException;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.FolderClosedException;
import javax.mail.FolderNotFoundException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.ReadOnlyFolderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.StoreClosedException;
import javax.mail.internet.MimeMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.foi.nwtis.matsaboli2.podaci.EmailStatistika;
import org.foi.nwtis.matsaboli2.zrna.SingletonSessionBean;

/**
 *
 * @author Matija
 */
public class ProvjeraEmailPoruka extends Thread {

    private EmailStatistika statistika = new EmailStatistika();
    private long pocetak_obrade = 0;
    private long kraj_obrade = 0;
    private int counter = 0;
    private int email_counter = 0;
    private int nwtis_counter = 0;

    private boolean flag = true;

    @Override
    public void interrupt() {
        System.out.println("--- Provjera email poruka interupt ---");
        flag = false;
        super.interrupt();
    }

    @Override
    public void run() {
        try {
            sleep(5000);
        } catch (InterruptedException ex) {
            Logger.getLogger(ProvjeraEmailPoruka.class.getName()).log(Level.SEVERE, null, ex);
        }

        while (flag) {
            
            long pocetak = System.currentTimeMillis();
            
            obradiPoruke();
            counter++;
            
            statistika.setStartTime(new Date(this.pocetak_obrade));
            statistika.setEndTime(new Date(this.kraj_obrade));
            statistika.setJMSNumber(counter);
            statistika.setMessagesNumber(email_counter);
            statistika.setNWTiSMessagesNumber(nwtis_counter);
            
            try {
                sendJMSMessageToNWTiS_matsaboli2_1();
                System.out.println("Poslana JMS poruka Email");
            } catch (JMSException | NamingException ex) {
                Logger.getLogger(ProvjeraEmailPoruka.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            email_counter = 0;
            nwtis_counter = 0;
            
            long kraj = System.currentTimeMillis();

            try {
                sleep((SingletonSessionBean.intervalDretve * 1000) - (kraj - pocetak));
            } catch (InterruptedException ex) {
                Logger.getLogger(ProvjeraEmailPoruka.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void obradiPoruke() {
        try {
            
            pocetak_obrade = System.currentTimeMillis();
            
            java.util.Properties properties = System.getProperties();
            properties.put("mail.smtp.host", SingletonSessionBean.emailServer);
            Session session = Session.getInstance(properties, null);

            Store store = session.getStore("imap");
            store.connect(SingletonSessionBean.emailServer, SingletonSessionBean.emailUser, SingletonSessionBean.emailPass);

            Folder folder = store.getFolder("INBOX");
            folder.open(Folder.READ_WRITE);

            Message[] messages = folder.getMessages();

            for (Message message1 : messages) {
                email_counter++;
                
                MimeMessage message = (MimeMessage) message1;
                if (message.getSubject().equals(SingletonSessionBean.emailSubject)
                        && (message.getContent() instanceof String)) {
                    nwtis_counter++;
                    premjestiPoruku(SingletonSessionBean.emailNwtisFolder, store, message, folder);
                }
            }

            folder.close(true);
            store.close();
            
            kraj_obrade = System.currentTimeMillis();

        } catch (AuthenticationFailedException e) {
        } catch (FolderClosedException | FolderNotFoundException | NoSuchProviderException | ReadOnlyFolderException | StoreClosedException | IOException e) {
        } catch (MessagingException e) {
        }
    }

    private void premjestiPoruku(String folderName, Store store, MimeMessage message, Folder folder) throws MessagingException {
        Folder newFolder = store.getFolder(folderName);

        if (!newFolder.exists()) {
            newFolder.create(Folder.HOLDS_MESSAGES);
        }

        newFolder.open(Folder.READ_WRITE);

        Message[] poruka = new Message[1];
        poruka[0] = message;

        folder.copyMessages(poruka, newFolder);

        folder.setFlags(poruka, new Flags(Flags.Flag.DELETED), true);
        folder.expunge();
    }

    @Override
    public synchronized void start() {
        super.start();
    }

    private void sendJMSMessageToNWTiS_matsaboli2_1() throws JMSException, NamingException {
        Context c = new InitialContext();
        ConnectionFactory cf = (ConnectionFactory) c.lookup("jms/NWTiS_QF_matsaboli2");
        Connection conn = null;
        javax.jms.Session s = null;

        try {
            conn = cf.createConnection();
            s = conn.createSession(false, s.AUTO_ACKNOWLEDGE);
            
            ObjectMessage object = s.createObjectMessage();
            object.setObject((Serializable) ((Object) statistika));

            Destination destination = (Destination) c.lookup("jms/NWTiS_matsaboli2_1");
            MessageProducer mp = s.createProducer(destination);
            mp.send(object);
        } finally {
            if (s != null) {
                try {
                    s.close();
                } catch (JMSException e) {
                    Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Cannot close session", e);
                }
            }
            if (conn != null) {
                conn.close();
            }
        }
    }

}
