package com.paic.arch.jmsbroker;

/**
 * @author HuiYu.Xiao
 * @date 2018/3/1
 *
 */
public interface JmsMessageService {

    /**
     * 绑定broker
     */
    public void bindToBrokerAtUrl(String aBrokerUrl);

    /**
     * 获取url
     *
     * @return
     */
    public String getBrokerUrl();

    /**
     * 消息发送
     *
     * @param aDestinationName
     * @param aMessage
     */
    public void sendMessage(String aDestinationName, String aMessage);

    /**
     * 消息发送
     *
     * @param destination
     * @param message
     * @param timeOut
     */
    public void sendMessage(String destination, String message, long timeOut);

    /**
     * 获取消息
     *
     * @param aDestinationName
     * @param aTime
     * @return
     */
    public String getMessage(String aDestinationName, long aTime);

    /**
     * 获取消息条数
     *
     * @param aDestinationName
     * @return
     */
    public long getMessageCount(String aDestinationName);

    /**
     * 启动broker
     */
    public void startBroker();

    /**
     * 停止broker
     */
    public void stopBroker();

    /**
     * 判断队列是否为空
     *
     * @param destinationName
     * @return
     */
    public boolean isEmptyQueueAt(String destinationName);

}
