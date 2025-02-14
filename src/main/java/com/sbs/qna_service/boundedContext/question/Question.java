package com.sbs.qna_service.boundedContext.question;

import com.sbs.qna_service.boundedContext.answer.Answer;
import com.sbs.qna_service.boundedContext.user.SiteUser;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity // 스프링부트가 Question를 Entity로 본다.
public class Question {
  @Id // PRIMARY KEY
  @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO_INCREMENT
  private Integer id; // INT id

  @Column(length = 200) // VARCHAR(200)
  private String subject;

  @Column(columnDefinition = "TEXT") // TEXT
  private String content;

  private LocalDateTime createDate; // DATETIME
  private LocalDateTime modifyDate;

  @ManyToOne
  private SiteUser author;

  @ManyToMany
  // Set 자료형은 중복된 값을 무시한다.
  private Set<SiteUser> voters  = new LinkedHashSet<>();

  // @OneToMany 자바세상에서의 편의를 위해 필드 생성
  // 이 녀석은 실에 DB 테이블에 칼럼이 생성되지 않는다.
  // DB는 리스트나 배열을 만들 수 없다.
  // answerList : 만들어도 되고 만들지 않아도 된다.
  // 다만 만들면 해당 객체(질문객체)에서 관련된 답변을 찾을 때 편하다.
  // CascadeType.REMOVE : 질문이 삭제되면 답변도 같이 삭제된다.
  @OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE) // fetch = FetchType.EAGER
  private List<Answer> answerList = new ArrayList<>();

  // 외부에서 answerList 필드에 접근하는 것을 차단
  public void addAnswer(Answer a) {
    a.setQuestion(this); // Question 객체에 Answer 추가
    answerList.add(a); // Answer 객체에 Question 설정
  }

  public void addVoter(SiteUser voter) {
    voters.add(voter);
  }
}
