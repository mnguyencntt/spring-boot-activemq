package com.anz.activemq.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notes")
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = {"createdAt", "updatedAt"}, allowGetters = true)
@Data
@NoArgsConstructor
public class Note implements Serializable {
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String queueName;
  private String message;
  private String status;
  private String historical;

  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(nullable = false)
  private LocalDateTime updatedAt;

  public Note(String queueName, String message, String status) {
    this.queueName = queueName;
    this.message = message;
    this.status = status;
  }

  @PrePersist
  public void prePersist() {
    historical = status;
    createdAt = LocalDateTime.now(ZoneId.of("Singapore"));
    updatedAt = createdAt;
  }

  @PreUpdate
  public void preUpdate() {
    historical += "-" + status;
    updatedAt = LocalDateTime.now(ZoneId.of("Singapore"));
  }
}
