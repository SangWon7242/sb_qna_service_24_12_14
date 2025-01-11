package com.sbs.qna_service.boundedContext.answer;

import com.sbs.qna_service.boundedContext.question.Question;
import com.sbs.qna_service.boundedContext.question.QuestionService;
import com.sbs.qna_service.boundedContext.user.SiteUser;
import com.sbs.qna_service.boundedContext.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@RequestMapping("/answer")
@RequiredArgsConstructor
@Controller
public class AnswerController {
  private final QuestionService questionService;
  private final AnswerService answerService;
  private final UserService userService;

  @PreAuthorize("isAuthenticated()")
  @PostMapping("/create/{id}")
  public String createAnswer (
      Model model,
      @PathVariable("id") Integer id,
      @Valid AnswerForm answerForm,
      BindingResult bindingResult,
      Principal principal
  ) {
    Question question = questionService.getQuestion(id);

    // principal.getName() : 글 작성자를 가져온다.
    SiteUser siteUser = userService.getUser(principal.getName());

    if (bindingResult.hasErrors()) {
      model.addAttribute("question", question);
      return "question_detail";
    }

    // TODO: 답변을 저장한다.
    Answer answer = answerService.create(question, answerForm.getContent(), siteUser);

    // return "%d번 질문에 대한 답변이 생성되었습니다.(답변 번호 : %d)".formatted(id, answer.getId());

    return "redirect:/question/detail/%s#answer_%d".formatted(id, answer.getId());
  }

  @PreAuthorize("isAuthenticated()")
  @GetMapping("/modify/{id}")
  public String answerModify(AnswerForm answerForm, @PathVariable("id") Integer id, Principal principal) {
    Answer answer = answerService.getAnswer(id);

    if (!answer.getAuthor().getUsername().equals(principal.getName())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
    }
    answerForm.setContent(answer.getContent());
    return "answer_form";
  }

  @PreAuthorize("isAuthenticated()")
  @PostMapping("/modify/{id}")
  public String answerModify(@Valid AnswerForm answerForm, BindingResult bindingResult,
                             @PathVariable("id") Integer id, Principal principal) {
    if (bindingResult.hasErrors()) {
      return "answer_form";
    }

    Answer answer = answerService.getAnswer(id);

    if (!answer.getAuthor().getUsername().equals(principal.getName())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
    }

    answerService.modify(answer, answerForm.getContent());
    return String.format("redirect:/question/detail/%s#answer_%d", answer.getQuestion().getId(), answer.getId());
  }

  @PreAuthorize("isAuthenticated()")
  @GetMapping("/delete/{id}")
  public String answerDelete(Principal principal, @PathVariable("id") Integer id) {
    Answer answer = answerService.getAnswer(id);

    if (!answer.getAuthor().getUsername().equals(principal.getName())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
    }

    answerService.delete(answer);
    return String.format("redirect:/question/detail/%s", answer.getQuestion().getId());
  }


  @PreAuthorize("isAuthenticated()")
  @GetMapping("/vote/{id}")
  public String answerVote(Principal principal, @PathVariable("id") Integer id) {
    Answer answer = answerService.getAnswer(id);
    SiteUser siteUser = userService.getUser(principal.getName());

    answerService.vote(answer, siteUser);
    return String.format("redirect:/question/detail/%s#answer_%d", answer.getQuestion().getId(), answer.getId());
  }
}
