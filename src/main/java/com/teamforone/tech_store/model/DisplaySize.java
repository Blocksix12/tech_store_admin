package com.teamforone.tech_store.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "displaysize")
public class DisplaySize {
    @Id
    @UuidGenerator
    @Column(name = "sizeID", columnDefinition = "CHAR(36)")
    private String sizeID;

    @Column(name = "size_inch", precision = 4, scale = 2, nullable = false)
    private BigDecimal sizeInch;

    @Column(name = "resolution", length = 30)
    private String resolution;

    @Column(name = "technology", length = 100)
    private String technology;

    @Column(name = "refresh_rate", length = 20)
    private String refreshRate;

    @Column(name = "brightness", length = 50)
    private String brightness;

    @Column(name = "color_depth", length = 50)
    private String colorDepth;

    @Column(name = "screen_type", length = 50)
    private String screenType;

    @CreationTimestamp
    @Column(name = "created_at")
    private Date createdAt;
}