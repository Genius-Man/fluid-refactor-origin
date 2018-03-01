package com.paic.arch.jmsbroker;

/**
 * @author HuiYu.Xiao
 * @date 2018/3/1
 *
 */
public class MyJMSException {

     static class NoMessageReceivedException extends RuntimeException {
        public NoMessageReceivedException(String reason) {
            super(reason);
        }
    }
}
