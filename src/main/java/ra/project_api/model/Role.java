package ra.project_api.model;

import jakarta.persistence.*;
import lombok.*;
import ra.project_api.constrants.RoleName;

@Entity
@Table(name = "role")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Role
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long roleId;
    @Column(name = "role_name")
    @Enumerated(EnumType.STRING)
    private RoleName roleName;
}