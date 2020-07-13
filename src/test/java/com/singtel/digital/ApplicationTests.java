package com.singtel.digital;

import static org.junit.Assert.assertEquals;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import com.anz.activemq.enumeration.QueueStatus;
import com.anz.activemq.jms.expirer.Expirer;
import com.anz.activemq.jms.reciever.Receiver;
import com.anz.activemq.jms.sender.Sender;
import com.anz.activemq.model.Note;
import com.anz.activemq.repository.NoteRepository;

@Ignore
@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {
  @Autowired
  private NoteRepository noteRepository;

  @Autowired
  private Sender sender;

  @Autowired
  private Receiver receiver;

  @Autowired
  private Expirer expirer;

  private Predicate<Note> predicateReceived = p -> p.getStatus().equalsIgnoreCase(QueueStatus.RECEIVED.name());
  private Predicate<Note> predicateExpired = p -> p.getStatus().equalsIgnoreCase(QueueStatus.EXPIRED.name());

  /*
   * 1. Send 10 messages to QUEUE1, Send 10 messages to QUEUE2
   * 
   * 2. 1 listener QUEUE1, 0 listener QUEUE2 -> will be expired
   * 
   * 3. Result: 10 messages to QUEUE1 status "RECEIVED", 10 messages QUEUE2 status "EXPIRED"
   */
  @Before
  public void init() {
    sender.check();
    receiver.check();
    expirer.check();
    noteRepository.deleteAll();
  }

  @Test
  public void testSendMessageToQueue1() throws Exception {
    multipleThreadSenderToQueue();
  }

  private void multipleThreadSenderToQueue() throws InterruptedException {
    final int numerMessages = 10;
    final UUID uuid = UUID.randomUUID();
    ExecutorService workerThreadPool = Executors.newFixedThreadPool(numerMessages);
    for (int i = 0; i < numerMessages; i++) {
      workerThreadPool.submit(() -> {
        sender.send(uuid + " Hello JMS ActiveMQ!");
      });
    }
    workerThreadPool.awaitTermination(20, TimeUnit.SECONDS);
    workerThreadPool.shutdown();
    workerThreadPool.shutdownNow();
    Thread.sleep(20000l);
    System.out.println(String.format("All is Shutdown: %s, All is Terminated: %s", workerThreadPool.isShutdown(), workerThreadPool.isTerminated()));

    List<Note> listAll = noteRepository.findAll();
    List<Note> listReceived = listAll.stream().filter(predicateReceived).collect(Collectors.toList());
    List<Note> listExpired = listAll.stream().filter(predicateExpired).collect(Collectors.toList());
    assertEquals(20, listAll.size());
    assertEquals(numerMessages, listReceived.size());
    assertEquals(numerMessages, listExpired.size());

    Thread.sleep(2000l);
  }
}
