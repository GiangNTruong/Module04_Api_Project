package ra.project_api.dto.request;

import lombok.Data;

@Data
public class OrderDetailRequestDTO {
    private Long productId;
    private Integer quantity;
}