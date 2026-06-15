package com.sourashis.quizapp.modules.reward.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "level_config")
public class LevelConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private Integer level;

    @Column(nullable = false)
    private Long xpRequired;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String rewardsJson;
}
