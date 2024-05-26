package com.nextroom.nextRoomServer.domain;

import com.nextroom.nextRoomServer.util.Timestamped;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Shop extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shop_id", nullable = false)
    private Long id;

    @Column
    private String email;

    @Column(nullable = false, length = 5)
    private String adminCode;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Comment(value = "1: 웹(홈페이지)에서 PC로 들어온 유저, 2: 웹(홈페이지)에서 모바일로 들어온 유저, 3: 앱에서 들어온 유저")
    @Column
    private Integer type;

    @Column
    private String comment;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Authority authority;

    @Column
    private LocalDateTime lastLoginAt;

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Theme> themes = new ArrayList<>();

    public void updateLastLoginAt() {
        this.lastLoginAt = LocalDateTime.now();
    }

    //    @OneToOne(mappedBy = "shop", cascade = CascadeType.DETACH)
    //    private Subscription subscription;
}
