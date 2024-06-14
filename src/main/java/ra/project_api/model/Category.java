package ra.project_api.model;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "category")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Category
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long categoryId;
    @Column(name = "category_name", length = 100, nullable = false)
    private String categoryName;
    @Column(name = "description")
    private String description;
    @Column(name = "status")
    private Boolean status;
}