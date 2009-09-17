/*
 * $Id$
 */
package org.randd.jms;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

@Name("testListener")
@Scope(ScopeType.APPLICATION)
@AutoCreate
public class TestListener implements MessageListener
{
    @Logger
    private Log log;

    @Create
    public void init()
    {
        this.log.info("Starting up testListener");
    }
    
    public void onMessage(Message message)
    {
        try
        {
            this.log.info("Received message: " + ((TextMessage) message).getText());
        }
        catch (JMSException e)
        {
            this.log.error("Error with the message", e);
        }
    }
}
