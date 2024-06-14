package ra.project_api.model;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "shopping_cart")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ShoppingCart
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shopping_cart_id")
    private Long shoppingCartId;
    @Column(name = "order_quantity")
    private Integer orderQuantity; // check >0

    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;
}