package com.example.boardproject.service;

import com.example.boardproject.dto.BoardDTO;
import com.example.boardproject.entity.BoardEntity;
import com.example.boardproject.entity.BoardFileEntity;
import com.example.boardproject.repository.BoardFileRepository;
import com.example.boardproject.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
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
    private final BoardFileRepository boardFileRepository;
    @Value("${file.spring_img}")
    String savePath;

    public void save(BoardDTO boardDTO) throws IOException {
        // Controller단에서 DTO를 받아서 Entity로 변경해준 뒤, Repository에 저장
        // 파일 첨부 여부에 따라 로직 분리
        if(boardDTO.getBoardFile().isEmpty()){
            // 첨부 파일 없음
            BoardEntity boardEntity = BoardEntity.toSaveEntity(boardDTO);
            boardRepository.save(boardEntity);
        } else{
            // 첨부 파일 있음
            /*
            *   1. DTO에 담긴  파일을 꺼냄
            *   2. 파일의 이름 가져옴
            *   3. 서버 저장용 이름을 만듦
            *   // 내사진.jpg => 839798375892_내사진.jpg
            *   4. 저장 경로 설정
            *   5. 해당 경로에 파일 저장
            *   6. board_table에 해당 데이터 save 처리
            *   7. board_file_table에 해당 데이터 save 처리
            */
            BoardEntity boardEntity = BoardEntity.toSaveFileEntity(boardDTO); // (id 미존재)
            Long saveId = boardRepository.save(boardEntity).getId();
            BoardEntity board = boardRepository.findById(saveId).get(); // 부모 Entity (id 존재)
            for(MultipartFile boardFile: boardDTO.getBoardFile()){
                // MultipartFile boardFile = boardDTO.getBoardFile(); // 1.
                String originalFileName = boardFile.getOriginalFilename(); // 2.
                String storedFileName = System.currentTimeMillis() + "_" + originalFileName; // 3.
                String path = savePath + storedFileName;
//                String savePath = System.getProperty("user.home") + "/Desktop/springboot_img/" + storedFileName; // 4.
                boardFile.transferTo(new File(path)); // 5. 이 부분에 의해 throws IOException 처리를 해주어야함.
                BoardFileEntity boardFileEntity = BoardFileEntity.toBoardFileEntity(board, originalFileName, storedFileName);
                boardFileRepository.save(boardFileEntity);
            }
        }

    }

    @Transactional
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

    @Transactional
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

    public Page<BoardDTO> paging(Pageable pageable, Integer pageLimit) {
        if(pageLimit == null){
            pageLimit = 3;
        }
        // pageable을 입력받아, 해당되는 Page<BoardDTO> 객체를 반환
        int page = pageable.getPageNumber() - 1; // page 위치에 있는 값은 0부터 시작(요청은 1부터 시작)
        // int pageLimit = 3; // 한 페이지에 보여줄 글 갯수
        System.out.println("pageLimit: " + pageLimit);
        Page<BoardEntity> boardEntities = // BoardEntity의 id를 기준으로 내림차순하여 한 페이지에 3개의 글을 보여줌.
                boardRepository.findAll(PageRequest.of(page,pageLimit, Sort.by(Sort.Direction.DESC,"id")));
//        System.out.println("boardEntities.getContent() = " + boardEntities.getContent()); // 요청 페이지에 해당하는 글
//        System.out.println("boardEntities.getTotalElements() = " + boardEntities.getTotalElements()); // 전체 글갯수
//        System.out.println("boardEntities.getNumber() = " + boardEntities.getNumber()); // DB로 요청한 페이지 번호
//        System.out.println("boardEntities.getTotalPages() = " + boardEntities.getTotalPages()); // 전체 페이지 갯수
//        System.out.println("boardEntities.getSize() = " + boardEntities.getSize()); // 한 페이지에 보여지는 글 갯수
//        System.out.println("boardEntities.hasPrevious() = " + boardEntities.hasPrevious()); // 이전 페이지 존재 여부
//        System.out.println("boardEntities.isFirst() = " + boardEntities.isFirst()); // 첫 페이지 여부
//        System.out.println("boardEntities.isLast() = " + boardEntities.isLast()); // 마지막 페이지 여부

        // 목록: id, writer, title, hits, createdTime
        Page<BoardDTO> boardDTOS = boardEntities.map(board -> new BoardDTO(
                board.getId(),board.getBoardWriter(),board.getBoardTitle(), board.getBoardHits(), board.getCreatedTime()));
        return boardDTOS;
    }
}
