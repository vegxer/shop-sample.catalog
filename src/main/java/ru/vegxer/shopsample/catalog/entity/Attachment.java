package ru.vegxer.shopsample.catalog.entity;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "attachment")
public class Attachment {
    @Id
    @SequenceGenerator(name = "attachment_id_seq", allocationSize = 1, sequenceName = "attachment_id_seq")
    @GeneratedValue(generator = "attachment_id_seq", strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "path")
    private String path;

    @Column(name = "thumbnail_path")
    private String thumbnailPath;
}
