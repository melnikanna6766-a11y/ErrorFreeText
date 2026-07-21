package com.github.melnikanna6766a11y.errorfreetext.entity;

import com.github.melnikanna6766a11y.errorfreetext.dto.CorrectedTextResponse;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@NoArgsConstructor
@NamedEntityGraph(
        name = "task-entity-graph",
        attributeNodes = {
                @NamedAttributeNode("status"),
                @NamedAttributeNode("language")
        }
)
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "input_text")
    private String inputText;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private List<CorrectedTextResponse> response;

    @Column(name = "number_of_characters")
    private Integer numberOfCharacters;

    @Column(name = "completion_date")
    private LocalDate completionDate;

    @Column(name = "number_of_executions")
    private Integer numberOfExecutions;

    @Column(name = "number_of_saved_elements")
    private Integer numberOfSavedElements;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id")
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "language_id")
    private Language language;
}
