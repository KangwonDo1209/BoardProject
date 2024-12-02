package com.example.boardproject.service;

import com.example.boardproject.dto.BoardDTO;
import com.example.boardproject.entity.BoardEntity;
import com.example.boardproject.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

// DTO -> Entity
// Entity -> DTO
// Controller로 부터 넘겨 받을 때는 DTO로, Repository로 넘겨 줄 때는 Entity로
// DB 데이터 조회 시, Repository로 부터 Entity로 받아오고, Controller로 넘겨 줄 때는 DTO로
// Entity는 DB와 직접적인 연관이 있기 때문에, View단에 노출을 시키지 않는 것이 좋다.
// Entity는 Service 클래스 까지만 오게 코드를 짜는것이 좋다.
@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    public void save(BoardDTO boardDTO) {
        // Controller단에서 DTO를 받아서 Entity로 변경해준 뒤, Repository에 저장
        BoardEntity boardEntity = BoardEntity.toSaveEntity(boardDTO);
        boardRepository.save(boardEntity);
    }

    public List<BoardDTO> findAll() {
        // Repository에서 게시글 Entity리스트를 받아
        List<BoardEntity> boardEntityList = boardRepository.findAll();
        List<BoardDTO> boardDTOList = new ArrayList<>();
        for(BoardEntity boardEntity: boardEntityList) {
            boardDTOList.add(BoardDTO.toBoardDTO(boardEntity));
        }
        return boardDTOList;
    }
}
