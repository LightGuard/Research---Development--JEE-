package org.randd.jms;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueConnectionFactory;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Date;

@Name("hornetSendReceive")
@Scope(ScopeType.SESSION)
public class TestHornetSendReceive
{
   private Queue testQueue;
   private ConnectionFactory cf;
   private Connection conn;
   private boolean messageSent;
   private String messageSentText;
   private boolean messageReceived;
   private String messageReceivedText;

   @Logger
   private Log log;

   @Create
   public void init()
   {
       InitialContext ic = null;
       try
       {
           ic = new InitialContext();
           this.cf = (QueueConnectionFactory) ic.lookup("java:comp/env/jms/connectionFactory");
           this.testQueue = (Queue) ic.lookup("java:comp/env/jms/queues/testQueue");

           this.conn = this.cf.createConnection();
           this.conn.start();
       }
       catch (Exception e)
       {
           this.log.error("Error retrieving from JNDI", e);
       }
       finally
       {
           try
           {
               ic.close();
           }
           catch (NamingException e)
           {
               // Eat exception
           }
       }
   }

   public void sendMessage()
   {
       try
       {
           Session s = this.conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
           MessageProducer mp = s.createProducer(this.testQueue);

           TextMessage txtMsg = s.createTextMessage("Hello from Hornet at: " + new Date());
           mp.send(txtMsg);
           this.messageSent = true;
           this.messageSentText = txtMsg.getText();
           s.close();
       }
       catch (JMSException e)
       {
           this.log.error("Error sending message", e);
           this.messageSent = false;
       }
   }

   public void receiveMessage()
   {
       try
       {
           Session s = this.conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
           MessageConsumer mc = s.createConsumer(this.testQueue);

           TextMessage receivedMsg = (TextMessage) mc.receive(1000);
           this.messageReceivedText = receivedMsg.getText();
           this.messageReceived = true;
           s.close();
       }
       catch (JMSException e)
       {
           this.log.error("Error receiving the message", e);
           this.messageReceived = false;
       }
   }

   @Destroy
   public void cleanup()
   {
       try
       {
           this.conn.stop();
       }
       catch (JMSException e)
       {
           this.log.error("Could not stop JMS connection", e);
       }
   }

    public boolean isMessageSent()
    {
        return messageSent;
    }

    public void setMessageSent(boolean messageSent)
    {
        this.messageSent = messageSent;
    }

    public boolean isMessageReceived()
    {
        return messageReceived;
    }

    public void setMessageReceived(boolean messageReceived)
    {
        this.messageReceived = messageReceived;
    }

    public String getMessageSentText()
    {
        return messageSentText;
    }

    public void setMessageSentText(String messageSentText)
    {
        this.messageSentText = messageSentText;
    }

    public String getMessageReceivedText()
    {
        return messageReceivedText;
    }

    public void setMessageReceivedText(String messageReceivedText)
    {
        this.messageReceivedText = messageReceivedText;
    }
}
