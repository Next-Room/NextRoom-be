//package com.nextroom.nextRoomServer.domain;
//
//import java.time.LocalDateTime;
//
//import com.nextroom.nextRoomServer.util.Timestamped;
//
//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.FetchType;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import jakarta.persistence.Id;
//import jakarta.persistence.JoinColumn;
//import jakarta.persistence.ManyToOne;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//
//@Entity
//@Getter
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//public class HintHistory extends Timestamped {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "hint_history_id", nullable = false)
//    private Long id;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "play_history_id", nullable = false)
//    private PlayHistory playHistory;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "hint_id", nullable = false)
//    private Hint hint;
//
//    @Column(nullable = false)
//    private LocalDateTime entryTime;
//
//    private LocalDateTime answerOpenTime;
//}
