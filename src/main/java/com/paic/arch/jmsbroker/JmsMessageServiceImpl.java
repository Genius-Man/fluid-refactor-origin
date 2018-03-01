package com.paic.arch.jmsbroker;

import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.region.DestinationStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.jms.*;
import java.lang.IllegalStateException;

/**
 * @author HuiYu.Xiao
 * @date 2018/3/1
 * @company Shenzhen H&T Intelligent Control Co.,Ltd
 */
public class JmsMessageServiceImpl implements JmsMessageService {

    private Logger logger = LoggerFactory.getLogger(JmsMessageServiceImpl.class);


    private JmsCommunication jmsCommunication;

    private Connection connection;

    private Session session;

    public JmsMessageServiceImpl(JmsCommunication jmsCommunication) {
        this.jmsCommunication = jmsCommunication;
    }

    /**
     * 绑定broker
     *
     * @param aBrokerUrl
     */
    @Override
    public void bindToBrokerAtUrl(String aBrokerUrl) {
        jmsCommunication.setBrokerUrl(aBrokerUrl);
    }

    /**
     * 获取url
     *
     * @return
     */
    @Override
    public String getBrokerUrl() {
        return jmsCommunication.getBrokerUrl();
    }

    /**
     * 消息发送
     *
     * @param aDestinationName
     * @param aMessage
     */
    @Override
    public void sendMessage(String aDestinationName, String aMessage) {
        init();
        try {
            //创建队列
            Queue queue = session.createQueue(aDestinationName);
            MessageProducer producer = session.createProducer(queue);
            //发送消息
            producer.send(session.createTextMessage(aMessage));
            producer.close();
        } catch (Exception e) {
            logger.warn("Failed to send message");
            throw new IllegalStateException(e.getMessage());
        } finally {
            close();
        }
    }

    /**
     * 消息发送
     *
     * @param destination
     * @param message
     * @param timeOut
     */
    @Override
    public void sendMessage(String destination, String message, long timeOut) {

    }

    /**
     * @param aDestinationName
     * @param aTime
     * @return
     */
    @Override
    public String getMessage(String aDestinationName, long aTime) {
        init();
        String msg;
        try {
            Queue queue = session.createQueue(aDestinationName);
            MessageConsumer consumer = session.createConsumer(queue);
            Message message = consumer.receive(aTime);
            consumer.close();
            if (message != null) {
                msg = ((TextMessage) message).getText();
            } else {
                throw new MyJMSException.NoMessageReceivedException(
                        String.format("No messages received from the broker within the %d timeout", aTime));
            }
        } catch (JMSException jmse) {
            logger.warn("Failed to receive message");
            throw new IllegalStateException(jmse);
        } finally {
            close();
        }
        return msg;
    }

    /**
     * 获取消息条数
     *
     * @param aDestinationName
     * @return
     */
    @Override
    public long getMessageCount(String aDestinationName) {
        long count = 0L;
        try {
            count = getDestinationStatisticsFor(aDestinationName).getMessages().getCount();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    private DestinationStatistics getDestinationStatisticsFor(String aDestinationName) {
        Broker regionBroker = jmsCommunication.getBrokerService().getRegionBroker();
        for (org.apache.activemq.broker.region.Destination destination : regionBroker.getDestinationMap().values()) {
            if (destination.getName().equals(aDestinationName)) {
                return destination.getDestinationStatistics();
            }
        }
        throw new IllegalStateException(String.format("Destination %s does not exist on broker at %s", aDestinationName, jmsCommunication.getBrokerUrl()));

    }

    /**
     * 启动broker
     */
    @Override
    public void startBroker() {
        try {
            jmsCommunication.setBrokerService(new BrokerService());
            jmsCommunication.getBrokerService().setPersistent(false);
            jmsCommunication.getBrokerService().addConnector(jmsCommunication.getBrokerUrl());
            jmsCommunication.getBrokerService().start();
        } catch (Exception e) {
            logger.warn("Failed to add Connector to broker");
            e.printStackTrace();
        }
    }

    /**
     * 停止broker
     */
    @Override
    public void stopBroker() {
        if (jmsCommunication.getBrokerService() != null) {
            try {
                jmsCommunication.getBrokerService().stop();
                jmsCommunication.getBrokerService().waitUntilStopped();
            } catch (Exception e) {
                logger.warn("annot stop the broker");
                throw new IllegalStateException(e.getMessage());
            }
        }
    }

    /**
     * 判断队列是否为空
     *
     * @param destinationName
     * @return
     */
    @Override
    public boolean isEmptyQueueAt(String destinationName) {
        long count = 0L;
        try {
            count = getEnqueuedMessageCountAt(destinationName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count == 0L;
    }

    public long getEnqueuedMessageCountAt(String aDestinationName) throws Exception {
        return getDestinationStatisticsFor(aDestinationName).getMessages().getCount();
    }

    /**
     * 初始化链接
     */
    private void init() {
        try {
            connection = jmsCommunication.getConnectionFactory().createConnection();
            connection.start();
        } catch (JMSException jmse) {
            logger.error("failed to create connection to {}", jmsCommunication.getBrokerUrl());
            throw new IllegalStateException(jmse);
        }

        try {
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        } catch (JMSException jmse) {
            logger.error("Failed to create session on connection {}", connection);
            throw new IllegalStateException(jmse);
        }
    }

    /**
     * 关闭连接
     */
    private void close() {
        if (session != null) {
            try {
                session.close();
            } catch (JMSException jmse) {
                logger.warn("Failed to close session");
                throw new IllegalStateException(jmse);
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (JMSException jmse) {
                logger.warn("Failed to close connection to broker");
                throw new IllegalStateException(jmse);
            }
        }
    }

}
