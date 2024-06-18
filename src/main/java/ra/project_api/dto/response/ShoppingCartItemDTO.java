package ra.project_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ShoppingCartItemDTO {
    private Long productId;
    private String productName;
    private String description;
    private Double unitPrice;
    private Integer orderQuantity;
}
