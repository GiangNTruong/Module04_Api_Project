package ra.project_api.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "address")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Address
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_Id")
    private Long addressId;
    @Column(name = "full_address", length = 255)
    private String fullAddress;
    @Column(name = "phone", length = 15)
    private String phone;
    @Column(name = "receive_name", length = 50)
    private String receiveName;
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;
}