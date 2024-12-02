package com.example.boardproject.repository;

import com.example.boardproject.entity.BoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardRepository extends JpaRepository<BoardEntity, Long> {
    // update board_table set board_hits=board_hits+1 where id=?
    // nativeQuery = true 를 설정하면 실제 DB에서 사용하는 Query도 사용 가능하다.
    // 여기서는 Entity를 기준으로 하므로, nativeQuery = false인 기본 상태에서 진행한다.
    @Modifying // update, delete 같은 데이터 변경 작업은 영속성 컨텍스트에 영향을 미치므로, Modifying을 명시해주어야함.
    @Query(value = "update BoardEntity b set b.boardHits=b.boardHits+1 where b.id=:id")
    void updateHits(@Param("id") Long id); // @Param의 id는 :id의 id에 해당한다. (이름 동기화 필수)
}
