package ra.project_api.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ra.project_api.model.Order;
import ra.project_api.model.OrderDetail;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDetailsResponseDTO {
    private Order order;
    private List<OrderDetailDTO> orderDetails;
}