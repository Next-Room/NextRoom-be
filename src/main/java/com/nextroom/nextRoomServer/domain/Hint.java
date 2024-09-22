package com.nextroom.nextRoomServer.domain;

import com.nextroom.nextRoomServer.dto.HintDto;
import com.nextroom.nextRoomServer.util.StringListConverter;
import com.nextroom.nextRoomServer.util.Timestamped;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

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

    @Convert(converter = StringListConverter.class)
    private List<String> hintImageList;

    @Convert(converter = StringListConverter.class)
    private List<String> answerImageList;

    public void update(HintDto.EditHintRequest request) {
        this.hintCode = request.getHintCode();
        this.contents = request.getContents();
        this.answer = request.getAnswer();
        this.progress = request.getProgress();
        this.hintImageList = request.getHintImageList();
        this.answerImageList = request.getAnswerImageList();
    }
}