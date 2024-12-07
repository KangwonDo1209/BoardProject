package com.example.boardproject.entity;

import com.example.boardproject.dto.BoardDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Length;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;

// DB의 테이블 역할을 하는 클래스
@Entity
@Getter
@Setter
@Table(name = "board_table")
public class BoardEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto_increment
    private Long id;

    @Column(length = 20, nullable = false) // 크기 20, not null
    private String boardWriter;

    @Column // 크기 255, null 가능
    private String boardPass;

    @Column
    private String boardTitle;

    @Column(length = 500)
    private String boardContents;

    @Column
    private int boardHits;

    @Column
    private int fileAttached; // 1 or 0

    @OneToMany(mappedBy = "boardEntity", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<BoardFileEntity> boardFileEntityList = new ArrayList<>();

    @OneToMany(mappedBy = "boardEntity", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CommentEntity> commentEntityList = new ArrayList<>();

    @Column
    private int youtubeAttached;

    @Column(length = 100)
    private String youtubeId;

    // DTO를 받아서 Entity로 반환하는 함수
    // Controller단에서 데이터를 Repository에 저장하기 위하여 Service단에 요청
    public static BoardEntity toSaveEntity(BoardDTO boardDTO, int fileAttached, int youtubeAttached) {
        BoardEntity boardEntity = new BoardEntity();
        boardEntity.setBoardWriter(boardDTO.getBoardWriter());
        boardEntity.setBoardPass(boardDTO.getBoardPass());
        boardEntity.setBoardTitle(boardDTO.getBoardTitle());
        boardEntity.setBoardContents(boardDTO.getBoardContents());
        boardEntity.setBoardHits(0);
        boardEntity.setFileAttached(fileAttached);
        boardEntity.setYoutubeAttached(youtubeAttached);

        String link = boardDTO.getYoutubeId();
        String id = extractYoutubeVideoId(link);
        boardEntity.setYoutubeId(id);

        return boardEntity;
    }
    // DTO를 받아서 Entity로 반환하는 함수
    // Controller단에서 데이터를 수정하여 Repository에 업데이트 하기 위하여 Service단에 요청
    public static BoardEntity toUpdateEntity(BoardDTO boardDTO) {
        BoardEntity boardEntity = new BoardEntity();
        boardEntity.setId(boardDTO.getId()); // toSaveEntity와 다르게 Id를 가져옴
        boardEntity.setBoardWriter(boardDTO.getBoardWriter());
        boardEntity.setBoardPass(boardDTO.getBoardPass());
        boardEntity.setBoardTitle(boardDTO.getBoardTitle());
        boardEntity.setBoardContents(boardDTO.getBoardContents());
        boardEntity.setBoardHits(boardDTO.getBoardHits());
        return boardEntity;
    }

//    public static BoardEntity toSaveFileEntity(BoardDTO boardDTO) {
//        BoardEntity boardEntity = new BoardEntity();
//        boardEntity.setBoardWriter(boardDTO.getBoardWriter());
//        boardEntity.setBoardPass(boardDTO.getBoardPass());
//        boardEntity.setBoardTitle(boardDTO.getBoardTitle());
//        boardEntity.setBoardContents(boardDTO.getBoardContents());
//        boardEntity.setBoardHits(0);
//        boardEntity.setFileAttached(1); // 파일 있음
//
//        return boardEntity;
//    }

    static private String extractYoutubeVideoId(String url) {
        if (url == null || url.isEmpty()) {
            return "dQw4w9WgXcQ";
        }

        String videoId = "dQw4w9WgXcQ";

        try {
            // URL이 https://youtu.be/ 형식인 경우
            if (url.contains("youtu.be/")) {
                videoId = url.substring(url.indexOf("youtu.be/") + 9, url.indexOf("youtu.be/") + 20);
            }
            // URL이 https://www.youtube.com/watch?v= 형식인 경우
            else if (url.contains("v=")) {
                int startIndex = url.indexOf("v=") + 2;
                int endIndex = url.indexOf("&", startIndex); // 추가적인 파라미터가 있는 경우
                if (endIndex == -1) { // '&'가 없다면 URL 끝까지 비디오 ID
                    endIndex = url.length();
                }
                videoId = url.substring(startIndex, endIndex);
            }
        } catch (Exception e) {
            e.printStackTrace(); // 추출 실패 시 오류 출력
        }

        return videoId;
    }
}

