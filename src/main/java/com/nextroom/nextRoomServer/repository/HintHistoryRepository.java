//package com.nextroom.nextRoomServer.repository;
//
//import java.util.List;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//
//import com.nextroom.nextRoomServer.domain.HintHistory;
//
//public interface HintHistoryRepository extends JpaRepository<HintHistory, Long> {
//    @Query(value = "SELECT A.hint_id AS id, COUNT(A.hint_id) AS hintOpenCount, SUM(A.answer_count) AS answerOpenCount "
//        +
//        "FROM (SELECT hint_id, IF(answer_open_time IS NULL, 0, 1) AS answer_count " +
//        "FROM play_history PH " +
//        "INNER JOIN hint_history HH ON PH.play_history_id = HH.play_history_id " +
//        "WHERE PH.theme_id = :themeId) A " +
//        "GROUP BY A.hint_id", nativeQuery = true)
//    List<Object[]> findAnalyticsByThemeId(@Param("themeId") Long themeId);
//}
