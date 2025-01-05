package com.sbs.qna_service.boundedContext.question;

import com.sbs.qna_service.boundedContext.answer.AnswerForm;
import com.sbs.qna_service.boundedContext.user.SiteUser;
import com.sbs.qna_service.boundedContext.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RequestMapping("/question")
@Controller
@RequiredArgsConstructor
public class QuestionController {
  private final QuestionService questionService;
  private final UserService userService;

  @GetMapping("/list")
  public String list(Model model,  @RequestParam(defaultValue = "0") int page) {
    Page<Question> paging = questionService.getList(page);
    model.addAttribute("paging", paging);

    return "question_list";
  }

  @GetMapping("/detail/{id}")
  public String detail(Model model, @PathVariable("id") Integer id, AnswerForm answerForm) {
    Question question = questionService.getQuestion(id);

    model.addAttribute("question", question);

    return "question_detail";
  }

  @GetMapping("/create")
  public String questionCreate(Model model) {
    model.addAttribute("questionForm", new QuestionForm());
    return "question_form";
  }

  @PreAuthorize("isAuthenticated()")
  @PostMapping("/create")
  // @Valid QuestionForm questionForm
  // questionForm 값을 바인딩 할 때 유효성 체크를 해라!
  public String questionCreate(
      @Valid QuestionForm questionForm,
      BindingResult bindingResult,
      Principal principal, // 현재 인증된 사용자의 정보를 나타냄
      Authentication authentication
  ) {
    if (bindingResult.hasErrors()) { // hasErrors : 에러가 존재한다면 true, 존재하지 않으면 false
      // question_form.html 실행
      // 다시 작성하라는 의미로 응답에 폼을 실어서 보냄
      return "question_form";
    }

    SiteUser siteUser = userService.getUser(principal.getName());
    questionService.create(questionForm.getSubject(), questionForm.getContent(), siteUser);

    /*
    System.out.println("username : " + principal.getName()); // 로그인한 사용자의 이름
    System.out.println("사용자 권한 : " + authentication.getAuthorities().toString()); // 로그인한 사용자의 권한
    */

    return "redirect:/question/list"; // 질문 저장후 질문목록으로 이동
  }
}
