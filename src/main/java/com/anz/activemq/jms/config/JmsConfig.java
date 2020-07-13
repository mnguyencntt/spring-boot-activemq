package com.anz.activemq.jms.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;

@Configuration
@EnableJms
public class JmsConfig {
  @Value("${spring.activemq.broker-url}")
  private String brokerUrl;

  @Value("${activemq.receive-timeout}")
  private long receiveTimeout;

  @Value("${activemq.time-to-live}")
  private long timeToLive;

  @Value("${activemq.qos-enabled}")
  private boolean explicitQosEnabled;

  @Bean
  public ActiveMQConnectionFactory activeMQConnectionFactory() {
    ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();
    activeMQConnectionFactory.setBrokerURL(brokerUrl);
    activeMQConnectionFactory.setPassword("MinhNguyen");
    return activeMQConnectionFactory;
  }

  @Bean
  public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
    DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
    factory.setConnectionFactory(activeMQConnectionFactory());
    return factory;
  }

  @Bean
  public CachingConnectionFactory cachingConnectionFactory() {
    return new CachingConnectionFactory(activeMQConnectionFactory());
  }

  @Bean
  public JmsTemplate jmsTemplate() {
    JmsTemplate jmsTemplate = new JmsTemplate(cachingConnectionFactory());
    jmsTemplate.setExplicitQosEnabled(explicitQosEnabled);
    jmsTemplate.setReceiveTimeout(receiveTimeout);
    if (explicitQosEnabled) {
      jmsTemplate.setTimeToLive(timeToLive);
    }
    return jmsTemplate;
  }
}
