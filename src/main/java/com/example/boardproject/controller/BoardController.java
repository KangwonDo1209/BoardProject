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
    public String save(@ModelAttribute BoardDTO boardDTO){ // ModelAttribute를 통해 데이터를 DTO로 변환해서 처리
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
    public String findById(@PathVariable Long id, Model model){
        // DB에서 id에 해당하는 게시글의 데이터를 찾아서 detail.html에 보여준다.
        // 이 과정에서 게시글의 조회수를 증가시킨다.
        boardService.updateHits(id); // 조회수 +1
        BoardDTO boardDTO = boardService.findById(id); // 게시글 불러오기
        model.addAttribute("board", boardDTO); // model에 게시글 데이터 저장
        return "detail";
    }

    @GetMapping("/update/{id}")
    public String updateForm(@PathVariable Long id, Model model){ // 경로상의 값을 가져오기 위해 PathVariable 사용
        BoardDTO boardDTO = boardService.findById(id);
        model.addAttribute("boardUpdate", boardDTO);
        return "update";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute BoardDTO boardDTO, Model model){ // ModelAttribute를 통해 데이터를 DTO로 변환해서 처리
        BoardDTO board = boardService.update(boardDTO); // 게시글 수정 후, 게시글 정보를 가져옴
        model.addAttribute("board", board); // 가져온 게시글 정보를 model에 저장
        return "detail"; // 해당 게시글을 다시 반환하지만, update 페이지에서 detail을 보여주는 문제 발생 (새로고침 시 불편함)
        //return "redirect:/board/" + boardDTO.getId(); // 이 방법을 사용하면, 수정마다 조회수가 1 늘어나는 문제점 발생
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id){
        boardService.delete(id);
        return "redirect:/board/";
    }
}
