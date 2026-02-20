package com.project.store.domain.item;


import com.project.store.domain.product.Product;
import jakarta.persistence.*;
import lombok.*;

/**
 * @author rahul
 */
@Data
@Entity
@Table(name = "item")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

}
