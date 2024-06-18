package ra.project_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WishListResponse  {
    private Long wishListId;
    private Long productId;
    private String productName;
    private String description;
    private Double unitPrice;
}

