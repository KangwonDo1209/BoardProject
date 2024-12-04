package com.example.boardproject.controller;

import com.example.boardproject.dto.BoardDTO;
import com.example.boardproject.dto.CommentDTO;
import com.example.boardproject.service.BoardService;
import com.example.boardproject.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board") // 이 클래스의 모든 Mapping은 /board 하위 매핑으로 처리 됨
public class BoardController {
    private final BoardService boardService;
    private final CommentService commentService;

    @GetMapping("/save")
    public String saveForm(){
        return "save";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute BoardDTO boardDTO) throws IOException { // ModelAttribute를 통해 데이터를 DTO로 변환해서 처리
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
    public String findById(@PathVariable Long id, Model model,
                           @PageableDefault(page=1) Pageable pageable){
        // DB에서 id에 해당하는 게시글의 데이터를 찾아서 detail.html에 보여준다.
        // 이 과정에서 게시글의 조회수를 증가시킨다.
        boardService.updateHits(id); // 조회수 +1
        BoardDTO boardDTO = boardService.findById(id); // 게시글 불러오기
        /* 댓글 목록 가져오기 */
        List<CommentDTO> commentDTOList = commentService.findAll(id);
        model.addAttribute("commentList", commentDTOList);

        model.addAttribute("board", boardDTO); // model에 게시글 데이터 저장
        model.addAttribute("page", pageable.getPageNumber()); // page 데이터를 저장하여 목록으로 돌아올 때 사용
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
        // return "detail"; // 해당 게시글을 다시 반환하지만, update 페이지에서 detail을 보여주는 문제 발생 (새로고침 시 불편함)
        return "redirect:/board/" + boardDTO.getId(); // 이 방법을 사용하면, 수정마다 조회수가 1 늘어나는 문제점 발생
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id){
        boardService.delete(id);
        return "redirect:/board/";
    }

    // /board/paging?page=1
    @GetMapping("/paging")
    public String paging(@PageableDefault(page = 1) Pageable pageable, Model model){ // page를 입력으로 받으며, 입력이 없을 시 1페이지로 제공
        // 입력받은 페이지에 해당하는 게시글 기본 정보를 boardList에 저장
        Page<BoardDTO> boardList = boardService.paging(pageable); // BoardDTO가 담긴 Page객체를 서비스에 호출(pageable을 파라미터로)
        // 페이지 목록 정보(1,2,3... 페이지)
        int blockLimit = 3;
        int startPage = (((int)(Math.ceil((double)pageable.getPageNumber() / blockLimit))) - 1) * blockLimit + 1; // 1 4 7 10 ~~
        int endPage = Math.min((startPage + blockLimit - 1), boardList.getTotalPages());
        //int endPage = ((startPage + blockLimit - 1) < boardList.getTotalPages()) ? startPage + blockLimit - 1 : boardList.getTotalPages();
        // paging에 전달되는 정보 : 현재 페이지에 보여줄 boardDTO들, 페이지 목록 시작/끝 지점
        model.addAttribute("boardList", boardList);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        return "paging";
    }
}
