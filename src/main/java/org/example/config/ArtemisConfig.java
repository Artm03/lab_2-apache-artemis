package org.example.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;

import jakarta.jms.ConnectionFactory;
@Slf4j
@Configuration
@EnableJms
public class ArtemisConfig {
    @Value("${spring.activemq.broker-url}")
    private String brokerUrl;

    @Value("${spring.activemq.user}")
    private String user;

    @Value("${spring.activemq.password}")
    private String password;

    @Bean
    public ConnectionFactory connectionFactory() {
        try {
            log.info("Broker URL: {}, User: {}", brokerUrl, user);
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
            connectionFactory.setBrokerURL(brokerUrl);
            connectionFactory.setUser(user);
            connectionFactory.setPassword(password);
            connectionFactory.setPreAcknowledge(true);

            connectionFactory.setReconnectAttempts(-1); // Infinite retries
            connectionFactory.setRetryInterval(1000);
            connectionFactory.setRetryIntervalMultiplier(1.5);
            connectionFactory.setMaxRetryInterval(60000);

            return connectionFactory;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create ConnectionFactory", e);
        }
    }
    @Bean
    public JmsTemplate jmsTemplate() {
        JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory());
        jmsTemplate.setPubSubDomain(false);
        log.info("JmsTemplate created successfully with active connection factory.");
        return jmsTemplate;
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(ConnectionFactory connectionFactory) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setConcurrency("1-1");
        factory.setPubSubDomain(true); // установить true, если используете топики
        return factory;
    }

}