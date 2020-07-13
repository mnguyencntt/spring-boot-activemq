package com.anz.activemq.jms.expirer;

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
public class Expirer {
  private static final Logger LOGGER = LoggerFactory.getLogger(Expirer.class);

  @Value("${activemq.expired-queue}")
  private String expiredQueue;

  @Autowired
  private NoteRepository noteRepository;

  @JmsListener(destination = "${activemq.expired-queue}", concurrency = "10-10")
  public void receive(String message) {
    try {
      LOGGER.info("ExpirerQueue: '{}', Expired-Message: '{}'", expiredQueue, message);

      // Main logic update EXPIRED to DB
      MessageInfo messageInfo = JsonUtils.toObject(message, MessageInfo.class);
      Note note = noteRepository.findById(messageInfo.getNoteId()).get();
      note.setStatus(QueueStatus.EXPIRED.name());
      noteRepository.save(note);
    } catch (Exception e) {
      // TODO: handle exception
    }
  }

  public boolean check() {
    return true;
  }
}
