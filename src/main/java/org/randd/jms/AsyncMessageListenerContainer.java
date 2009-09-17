package org.randd.jms;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Install;
import static org.jboss.seam.annotations.Install.BUILT_IN;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.log.Log;
import org.jboss.seam.util.Naming;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.Topic;
import javax.naming.NamingException;

/**
 * Simple asynchronous message listener container that uses <code>MessageConsumer.setMessageListener()</code> to create
 * MessageConsumers for the specified listener. <p/>
 *
 * <p> A new connection is created and started for each container instance. There
 * is no connection pooling, so using this class is not recommended if there are a lot of listeners. </p>
 * 
 * <p> Either <code>connectionFactory</code> or <code>connectionFactoryJndiName</code> need to be set. If both are set,
 * <code>connectionFactory</code> takes precedence. </p>
 *
 * <p> Either <code>destination</code> or
 * <code>destinationJndiName</code> need to be set. If both are set, <code>destination</code> takes precedence. </p>
 *
 * <p> If <code>outjectedSessionName</code> is set, the created JMS session is outjected to application scope.
 * The listener class may use this session to commit JMS transactions or reply to messages. The session is managed by this
 * container, and the listener <b>must not</b> close it. </p>
 *
 * <p> This class does no special processing of <code>acknoledgeMode</code> (default is
 * <code>AUTO_ACKNOLEDGE</code>) and <code>sessionTransacted</code> (default is <code>false</code>). They are only used for creating
 * the underlying session. The listener is responsible for acknowledging messages and committing transactions as appropriate. </p>
 *
 * <p> The <code>exceptionListener</code>, if set, is called in case of a connection problem or when the listener's
 * <code>onMessage</code> method throws an exception. </p>
 */

@Scope(ScopeType.APPLICATION)
@BypassInterceptors
@Name("org.randd.jms.asyncMessageListenerContainer")
@Install(precedence = BUILT_IN, classDependencies = "javax.jms.ExceptionListener")
public class AsyncMessageListenerContainer implements ExceptionListener
{
    @Logger
    private Log log;

    private ConnectionFactory connectionFactory;

    private String connectionFactoryJndiName = "UIL2ConnectionFactory";

    private Destination destination;

    private String destinationJndiName;

    private MessageListener listener;

    private ExceptionListener exceptionListener;

    private int acknowledgeMode = Session.AUTO_ACKNOWLEDGE;

    private boolean sessionTransacted = false;

    private boolean subscriptionDurable = false;

    private String durableSubscriptionName;

    private String outjectedSessionName;

    private Connection connection;

    private Session session;

    private MessageConsumer consumer;

    public AsyncMessageListenerContainer()
    {
    }

    @Create
    public void create() throws JMSException, NamingException
    {
        checkConfiguration();

        if (connectionFactory == null)
        {
            connectionFactory = lookupConnectionFactory();
        }
        connection = connectionFactory.createConnection();
        connection.setExceptionListener(this);
        session = createSession(connection);

        if (destination == null)
        {
            destination = lookupDestination();
        }

        createConsumer();
        log.debug(String.format("created consumer for %s (%s)", destination, destinationJndiName));
        connection.start();
        log.debug("started connection");
    }

    private void checkConfiguration()
    {
        if (connectionFactory == null && connectionFactoryJndiName == null)
        {
            throw new IllegalStateException("Either connectionFactory or connectionFactoryJndiName must be set.");
        }

        if (destination == null && destinationJndiName == null)
        {
            throw new IllegalStateException("Either destination or destinationJndiName must be set.");
        }

        if (listener == null)
        {
            throw new IllegalStateException("listener is required.");
        }

        if (subscriptionDurable)
        {
            if (durableSubscriptionName == null)
            {
                throw new IllegalStateException("durableSubscriptionName is required.");
            }
        }
    }

    private Destination lookupDestination() throws NamingException
    {
        return (Destination) Naming.getInitialContext().lookup(destinationJndiName);
    }

    private ConnectionFactory lookupConnectionFactory() throws NamingException
    {
        return (ConnectionFactory) Naming.getInitialContext().lookup(connectionFactoryJndiName);
    }

    @Destroy
    public void destroy() throws JMSException
    {
        closeAll();
    }

    private void closeAll()
    {
        closeConsumer(consumer);
        closeSession(session);
        closeConnection(connection);
    }

    private void closeConsumer(MessageConsumer consumer)
    {
        if (consumer == null)
        {
            return;
        }

        try
        {
            consumer.close();
        }
        catch (JMSException ex)
        {
            log.warn("Could not close JMS MessageConsumer.", ex);
        }
        catch (Throwable ex)
        {
            log.warn("Unexpected exception on closing JMS MessageConsumer.", ex);
        }
        consumer = null;
    }

    private void closeSession(Session session)
    {
        if (session == null)
        {
            return;
        }

        try
        {
            session.close();
        }
        catch (JMSException ex)
        {
            log.warn("Could not close JMS Session.", ex);
        }
        catch (Throwable ex)
        {
            log.warn("Unexpected exception on closing JMS Session.", ex);
        }
        session = null;
    }

    private void closeConnection(Connection conn)
    {
        if (conn == null)
        {
            return;
        }

        try
        {
            try
            {
                conn.stop();
            }
            finally
            {
                conn.close();
            }
        }
        catch (javax.jms.IllegalStateException ex)
        {
            log.warn("Connection is already closed.", ex);
        }
        catch (JMSException ex)
        {
            log.warn("Could not close JMS Connection.", ex);
        }
        catch (Throwable ex)
        {
            log.warn("Unexpected exception on closing JMS Connection.", ex);
        }
        connection = null;
    }

    protected MessageConsumer createConsumer(Session session, Destination destination) throws JMSException
    {
        if (isSubscriptionDurable() && destination instanceof Topic)
        {
            return session.createDurableSubscriber((Topic) destination, getDurableSubscriptionName());
        }

        return session.createConsumer(destination);
    }

    protected void executeListener(Message message)
    {
        try
        {
            log.debug("invoking message listener...");
            listener.onMessage(message);
            log.debug("done");
        }
        catch (Throwable e)
        {
            log.debug("caught exception while calling listener", e);
            handleException(e);
        }
    }

    protected void handleException(Throwable ex)
    {
        if (ex instanceof JMSException)
        {
            if (exceptionListener != null)
            {
                exceptionListener.onException((JMSException) ex);
            }
        }
        else
        {
            log.warn("Exception handling message", ex);
        }
    }

    protected Connection createConnection() throws JMSException
    {
        return connectionFactory.createConnection();
    }

    protected Session createSession(Connection connection) throws JMSException
    {
        Session session = connection.createSession(sessionTransacted, acknowledgeMode);
        if (outjectedSessionName != null)
        {
            Contexts.getApplicationContext().set(outjectedSessionName, session);
        }

        return session;
    }

    public void onException(JMSException ex)
    {
        if (exceptionListener != null)
        {
            exceptionListener.onException(ex);
        }

        try
        {
            log.warn("trying to recover connection...");
            initListener();
        }
        catch (JMSException ex1)
        {
            log.error("failed to recover connection", ex1);
        }
    }

    protected void initListener() throws JMSException
    {
        closeConnection(connection);
        closeConsumer(consumer);
        closeSession(session);

        connection = createConnection();
        connection.setExceptionListener(this);
        session = createSession(connection);
        createConsumer();

        connection.start();
    }

    private void createConsumer() throws JMSException
    {
        MessageConsumer consumer = createConsumer(session, destination);
        consumer.setMessageListener(new MessageListener()
        {
            public void onMessage(final Message message)
            {
                executeListener(message);
            }
        });
    }

    public ConnectionFactory getConnectionFactory()
    {
        return connectionFactory;
    }

    public void setConnectionFactory(ConnectionFactory connectionFactory)
    {
        this.connectionFactory = connectionFactory;
    }

    public int getAcknowledgeMode()
    {
        return acknowledgeMode;
    }

    public void setAcknowledgeMode(int acknowledgeMode)
    {
        this.acknowledgeMode = acknowledgeMode;
    }

    public boolean isSessionTransacted()
    {
        return sessionTransacted;
    }

    public void setSessionTransacted(boolean sessionTransacted)
    {
        this.sessionTransacted = sessionTransacted;
    }

    public Destination getDestination()
    {
        return destination;
    }

    public void setDestination(Destination destination)
    {
        this.destination = destination;
    }

    public boolean isSubscriptionDurable()
    {
        return subscriptionDurable;
    }

    public void setSubscriptionDurable(boolean subscriptionDurable)
    {
        this.subscriptionDurable = subscriptionDurable;
    }

    public String getDurableSubscriptionName()
    {
        return durableSubscriptionName;
    }

    public void setDurableSubscriptionName(String durableSubscriptionName)
    {
        this.durableSubscriptionName = durableSubscriptionName;
    }

    public MessageListener getListener()
    {
        return listener;
    }

    public void setListener(MessageListener listener)
    {
        this.listener = listener;
    }

    public ExceptionListener getExceptionListener()
    {
        return exceptionListener;
    }

    public void setExceptionListener(ExceptionListener exceptionListener)
    {
        this.exceptionListener = exceptionListener;
    }

    public String getDestinationJndiName()
    {
        return destinationJndiName;
    }

    public void setDestinationJndiName(String destinationJndiName)
    {
        this.destinationJndiName = destinationJndiName;
    }

    public String getOutjectedSessionName()
    {
        return outjectedSessionName;
    }

    public void setOutjectedSessionName(String outjectedSessionName)
    {
        this.outjectedSessionName = outjectedSessionName;
    }
}
