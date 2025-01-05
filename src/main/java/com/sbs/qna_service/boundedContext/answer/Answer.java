package com.sbs.qna_service.boundedContext.answer;

import com.sbs.qna_service.boundedContext.question.Question;
import com.sbs.qna_service.boundedContext.user.SiteUser;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Answer {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(columnDefinition = "TEXT")
  private String content;

  private LocalDateTime createDate;
  private LocalDateTime modifyDate;

  @ManyToOne
  private SiteUser author;

  @ManyToMany
  private Set<SiteUser> voters  = new LinkedHashSet<>();

  @ManyToOne
  @ToString.Exclude // ToString 대상에서 제외
  private Question question; // question_id 생성

  public void addVoter(SiteUser voter) {
    voters.add(voter);
  }
}
