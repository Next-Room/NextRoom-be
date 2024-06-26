package com.nextroom.nextRoomServer.domain;

import com.nextroom.nextRoomServer.dto.HintDto;
import com.nextroom.nextRoomServer.util.Timestamped;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Hint extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hint_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theme_id", nullable = false)
    private Theme theme;

    @Column(nullable = false, length = 4)
    private String hintCode;

    @Column(nullable = false, length = 4000)
    private String contents;

    @Column(nullable = false, length = 1000)
    private String answer;

    @Column(nullable = false)
    private Integer progress;

    public void update(HintDto.EditHintRequest request) {
        this.hintCode = request.getHintCode();
        this.contents = request.getContents();
        this.answer = request.getAnswer();
        this.progress = request.getProgress();
    }
}