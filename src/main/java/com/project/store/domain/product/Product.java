package com.project.store.domain.product;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity class for Product
 */
@Data
@Entity
@Table(name = "product")
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    // default value = false for primitive boolean
    private boolean availability;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private String createdBy;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private LocalDateTime modifiedAt;

    private String modifiedBy;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private Integer productQuantity;

    // Automatically set timestamps
    @PrePersist
    public void onCreate() {

        this.createdAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {

        this.modifiedAt = LocalDateTime.now();
    }

}