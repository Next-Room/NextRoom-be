package com.nextroom.oescape.domain;

import com.nextroom.oescape.dto.HintDto;

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
public class Hint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theme_id")
    private Theme theme;
    private String hintTitle;
    private String hintCode;
    private String contents;
    private String answer;
    private Integer progress;

    public HintDto.HintListResponse toHintListResponse() {
        return HintDto.HintListResponse
            .builder()
            .id(this.id)
            .hintTitle(this.hintTitle)
            .hintCode(this.hintCode)
            .contents(this.contents)
            .answer(this.answer)
            .progress(this.progress)
            .build();
    }
}