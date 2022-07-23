package ru.vegxer.shopsample.catalog.entity;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "category")
public class Category {
    @Id
    @SequenceGenerator(name = "category_id_seq", allocationSize = 1, sequenceName = "category_id_seq")
    @GeneratedValue(generator = "category_id_seq", strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "name")
    private String name;

    @OneToOne(fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(foreignKey = @ForeignKey(name = "category_attachment_fk"))
    private Attachment attachment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = @ForeignKey(name = "category_parent_fk"))
    private Category parent;

    @OneToMany(mappedBy = "parent")
    private List<Category> children;
}
