package com.anz.activemq.jms.reciever;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;
import com.anz.activemq.domain.MessageInfo;
import com.anz.activemq.enumeration.QueueStatus;
import com.anz.activemq.model.Note;
import com.anz.activemq.repository.NoteRepository;
import com.anz.activemq.util.JsonUtils;

@Service
public class Receiver {
  private static final Logger LOGGER = LoggerFactory.getLogger(Receiver.class);

  @Value("${activemq.provisioning-queue1}")
  private String provisioningQueue1;

  @Autowired
  private NoteRepository noteRepository;

  @JmsListener(destination = "${activemq.anz-queue1}", concurrency = "10-10")
  public void receive(String message) {
    LOGGER.info("ReceiverQueue: '{}', ReceivedMessage: '{}'", provisioningQueue1, message);

    // Main logic update RECEIVED to DB
    MessageInfo messageInfo = JsonUtils.toObject(message, MessageInfo.class);
    Note note = noteRepository.findById(messageInfo.getNoteId()).get();
    note.setStatus(QueueStatus.RECEIVED.name());
    noteRepository.save(note);
  }

  public boolean check() {
    return true;
  }
}
