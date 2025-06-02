package com.toeic.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "courses")
@Data
@NoArgsConstructor
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String objective;

    private String thumbnail_url;

    private String preview_video_url; // url of the preview video on youtube

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    private ECourseStatus status;

    @Enumerated(EnumType.STRING)
    private ECourseLevel level;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private List<CourseSection> sections;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private List<CourseReview> reviews;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private List<CourseEnrollment> enrollments;

    @CreationTimestamp
    private LocalDateTime createdAt;   

    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
}
