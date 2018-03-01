package com.paic.arch.jmsbroker;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;


/**
 * @author HuiYu.Xiao
 * @date 2018/3/1
 *
 */
public class JmsCommunication {

    private String brokerUrl;
    private BrokerService brokerService;
    private ActiveMQConnectionFactory connectionFactory;

    public JmsCommunication(String brokerUrl) {
        super();
        this.brokerUrl = brokerUrl;
        connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
    }

    public String getBrokerUrl() {
        return brokerUrl;
    }

    public void setBrokerUrl(String brokerUrl) {
        this.brokerUrl = brokerUrl;
    }

    public BrokerService getBrokerService() {
        return brokerService;
    }

    public void setBrokerService(BrokerService brokerService) {
        this.brokerService = brokerService;
    }

    public ActiveMQConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    public void setConnectionFactory(ActiveMQConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }
}
