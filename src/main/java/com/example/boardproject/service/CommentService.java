package com.example.boardproject.service;

import com.example.boardproject.dto.CommentDTO;
import com.example.boardproject.entity.BoardEntity;
import com.example.boardproject.entity.CommentEntity;
import com.example.boardproject.repository.BoardRepository;
import com.example.boardproject.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;

    public Long save(CommentDTO commentDTO) {
        // CommentDTO를 입력받아 CommentEntity로 변환하고, 부모인 BoardEntity의 정보를 불러와 CommentRepository에 저장
        /* 부모 Entity(BoardEntity) 조회 */
        Optional<BoardEntity> optionalBoardEntity = boardRepository.findById(commentDTO.getBoardId());
        if (optionalBoardEntity.isPresent()) {
            BoardEntity boardEntity = optionalBoardEntity.get();
            CommentEntity commentEntity = CommentEntity.toSaveEntity(commentDTO, boardEntity);
            return commentRepository.save(commentEntity).getId();
        } else {
            return null;
        }
        // builder 패턴 사용 가능
    }

    public List<CommentDTO> findAll(Long boardId) {
        // boardId에 해당하는 게시글의 모든 BoardEntity를 불러와, BoardDTO로 변환 후, List로 반환
        
        // select * from comment_table where board_id=? order by id desc;
        BoardEntity boardEntity = boardRepository.findById(boardId).get();
        List<CommentEntity> commentEntityList = commentRepository.findAllByBoardEntityOrderByIdDesc(boardEntity);
        /* EntityList -> DTOList */
        List<CommentDTO> commentDTOList = new ArrayList<>();
        for (CommentEntity commentEntity: commentEntityList) {
            CommentDTO commentDTO = CommentDTO.toCommentDTO(commentEntity, boardId);
            commentDTOList.add(commentDTO);
        }
        return commentDTOList;
    }
}
