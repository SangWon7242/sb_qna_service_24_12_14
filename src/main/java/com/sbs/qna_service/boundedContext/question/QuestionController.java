package com.sbs.qna_service.boundedContext.question;

import com.sbs.qna_service.boundedContext.answer.AnswerForm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/question")
@Controller
@RequiredArgsConstructor
public class QuestionController {
  private final QuestionService questionService;

  @GetMapping("/list")
  public String list(Model model) {
    List<Question> questionList = questionService.findAll();

    model.addAttribute("questionList", questionList);

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

  @PostMapping("/create")
  // @Valid QuestionForm questionForm
  // questionForm 값을 바인딩 할 때 유효성 체크를 해라!
  public String questionCreate(@Valid QuestionForm questionForm, BindingResult bindingResult) {
    if (bindingResult.hasErrors()) { // hasErrors : 에러가 존재한다면 true, 존재하지 않으면 false
      // question_form.html 실행
      // 다시 작성하라는 의미로 응답에 폼을 실어서 보냄
      return "question_form";
    }
    
    questionService.create(questionForm.getSubject(), questionForm.getContent());
    return "redirect:/question/list"; // 질문 저장후 질문목록으로 이동
  }
}
