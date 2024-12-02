package com.example.boardproject.service;

import com.example.boardproject.dto.BoardDTO;
import com.example.boardproject.entity.BoardEntity;
import com.example.boardproject.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        // Repository에서 게시글 Entity리스트를 받아 각각을 DTO리스트로 변환시켜 반환
        List<BoardEntity> boardEntityList = boardRepository.findAll();
        List<BoardDTO> boardDTOList = new ArrayList<>();
        for(BoardEntity boardEntity: boardEntityList) {
            boardDTOList.add(BoardDTO.toBoardDTO(boardEntity));
        }
        return boardDTOList;
    }
    @Transactional // 자동적 트랜잭션 관리를 통해 update, delete 등의 작업이 안전하게 수행되게 함
    public void updateHits(Long id) {
        boardRepository.updateHits(id); // id에 해당하는 게시글 조회수 증가
    }

    public BoardDTO findById(Long id) { // id에 해당하는 게시글 탐색 및 반환
        // Optinal을 통해 데이터 존재 여부를 안전하게 처리 가능
        Optional<BoardEntity> optionalBoardEntity = boardRepository.findById(id);
        if(optionalBoardEntity.isPresent()){ // 존재하면 DTO로 변환 후 반환
            BoardEntity boardEntity = optionalBoardEntity.get();
            BoardDTO boardDTO = BoardDTO.toBoardDTO(boardEntity);
            return boardDTO;
        }
        else{ // 존재하지 않으면 null 반환
            return null;
        }
    }

    public BoardDTO update(BoardDTO boardDTO) {
        // id가 존재하면 update, 존재하지 않으면 insert로 간주함 (여기선 update)
        BoardEntity boardEntity = BoardEntity.toUpdateEntity(boardDTO);
        boardRepository.save(boardEntity); // Repository에 데이터 저장 여기서 hits 초기화
        return findById(boardDTO.getId());
    }

    public void delete(Long id) {
        // 게시글 삭제
        boardRepository.deleteById(id);
    }
}
