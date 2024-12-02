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
    public String findAll(Model model){ // Model에 DTO 데이터를 저장하여 list.html에 전달
        // DB에서 전체 게시글 데이터를 가져와서 list.html에 보여준다.
        List<BoardDTO> boardDTOList = boardService.findAll(); // 모든 게시글 리스트 불러오기
        model.addAttribute("boardList",boardDTOList); // model에 DTO 데이터를 저장
        return "list";
    }

    @GetMapping("/{id}")
    public String findById(@PathVariable Long id, Model model){ // 경로상의 값을 가져오기 위해 PathVariable 사용
        // DB에서 id에 해당하는 게시글의 데이터를 찾아서 detail.html에 보여준다.
        // 이 과정에서 게시글의 조회수를 증가시킨다.
        boardService.updateHits(id); // 조회수 +1
        BoardDTO boardDTO = boardService.findById(id); // 게시글 불러오기
        model.addAttribute("board", boardDTO); // model에 게시글 데이터 저장
        return "detail";
    }
}
