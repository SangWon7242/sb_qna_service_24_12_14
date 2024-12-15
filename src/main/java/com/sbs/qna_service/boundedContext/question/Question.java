package com.sbs.qna_service.boundedContext.question;

import com.sbs.qna_service.boundedContext.answer.Answer;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
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

  // @OneToMany 자바세상에서의 편의를 위해 필드 생성
  // 이 녀석은 실에 DB 테이블에 칼럼이 생성되지 않는다.
  // DB는 리스트나 배열을 만들 수 없다.
  // answerList : 만들어도 되고 만들지 않아도 된다.
  // 다만 만들면 해당 객체(질문객체)에서 관련된 답변을 찾을 때 편하다.
  // CascadeType.REMOVE : 질문이 삭제되면 답변도 같이 삭제된다.
  @OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE)
  private List<Answer> answerList;
}
