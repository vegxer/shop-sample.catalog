package ru.vegxer.shopsample.catalog.entity;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "product")
public class Product {
    @Id
    @SequenceGenerator(name = "product_category_fk", allocationSize = 1, sequenceName = "product_category_fk")
    @GeneratedValue(generator = "product_category_fk", strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "amount")
    private Long amount;

    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    private ProductState state;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = @ForeignKey(name = "product_category_fk"))
    private Category category;

    @OneToMany(fetch = FetchType.LAZY)
    private List<Attachment> attachments;

    public enum ProductState {
        AVAILABLE,
        UNAVAILABLE
    }
}
