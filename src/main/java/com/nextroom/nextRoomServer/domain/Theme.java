package com.nextroom.nextRoomServer.domain;

import java.util.ArrayList;
import java.util.List;

import com.nextroom.nextRoomServer.dto.ThemeDto;
import com.nextroom.nextRoomServer.util.Timestamped;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Theme extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "theme_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private int timeLimit;

    @Column(nullable = false)
    private int hintLimit;

    @OneToMany(mappedBy = "theme", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Hint> hints = new ArrayList<>();

    //    @OneToMany(mappedBy = "theme", cascade = CascadeType.ALL)
    //    @Builder.Default
    //    private List<PlayHistory> playHistories = new ArrayList<>();

    public void update(ThemeDto.EditThemeRequest request) {
        this.title = request.getTitle();
        this.timeLimit = request.getTimeLimit();
        this.hintLimit = request.getHintLimit();
    }
}
