package com.example.boardproject.controller;

import com.example.boardproject.dto.BoardDTO;
import com.example.boardproject.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board") // 이 클래스의 모든 Mapping은 /board 하위 매핑으로 처리 됨
public class BoardController {
    private final BoardService boardService;

    @GetMapping("/save")
    public String saveForm(){
        return "save";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute BoardDTO boardDTO){
        System.out.println("boardDTO = " + boardDTO);
        boardService.save(boardDTO); // DTO를 Entity로 변경 후, Repository에 저장
        return "index";
    }

    @GetMapping("/")
    public String findAll(Model model){
        // DB에서 전체 게시글 데이터를 가져와서 list.html에 보여준다.
        List<BoardDTO> boardDTOList = boardService.findAll(); // 모든 게시글 리스트 불러오기
        model.addAttribute("boardList",boardDTOList);
        return "list";
    }

}
