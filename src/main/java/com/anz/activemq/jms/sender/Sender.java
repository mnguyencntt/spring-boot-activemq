package com.anz.activemq.jms.sender;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;
import com.anz.activemq.domain.MessageInfo;
import com.anz.activemq.enumeration.QueueStatus;
import com.anz.activemq.model.Note;
import com.anz.activemq.repository.NoteRepository;
import com.anz.activemq.util.JsonUtils;

@Service
public class Sender {
  private static final Logger LOGGER = LoggerFactory.getLogger(Sender.class);

  @Autowired
  private JmsTemplate jmsTemplate;

  @Autowired
  private NoteRepository noteRepository;

  @Value("${activemq.anz-queue1}")
  private String provisioningQueue1;

  @Value("${activemq.anz-queue2}")
  private String provisioningQueue2;

  @PostConstruct
  public void init() {
    LOGGER.info("JmsTemplate ReceiveTimeout: {}, TimeToLive: {}", jmsTemplate.getReceiveTimeout(), jmsTemplate.getTimeToLive());
  }

  public void send(String message) {
    extracted(message, provisioningQueue1);
    extracted(message, provisioningQueue2);
  }

  private void extracted(String message, String provisioningQueue) {
    Thread currentThread = Thread.currentThread();
    String sendingMessage = String.format("Send-ThreadId: %s, Send-ThreadName: %s, Send-Message: %s", currentThread.getId(), currentThread.getName(), message);

    // Main logic update NEW to DB
    Note note1 = new Note(provisioningQueue, message, QueueStatus.NEW.name());
    note1 = noteRepository.save(note1);

    // Main logic Send message to QUEUE
    final Long noteId = note1.getId();
    MessageInfo messageInfo = new MessageInfo(noteId, sendingMessage);
    LOGGER.info("SenderQueue: '{}', SendingMessage:'{}'", provisioningQueue, JsonUtils.toJson(messageInfo));
    // jmsTemplate.convertAndSend(provisioningQueue, noteId);
    jmsTemplate.send(provisioningQueue, new MessageCreator() {
      @Override
      public Message createMessage(Session session) throws JMSException {
        TextMessage textMessage = session.createTextMessage();
        textMessage.setText(JsonUtils.toJson(messageInfo));
        return textMessage;
      }
    });
  }

  public boolean check() {
    return true;
  }
}
