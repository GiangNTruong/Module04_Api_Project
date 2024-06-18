package ra.project_api.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ra.project_api.constrants.OrderStatus;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateOrderStatusDTO {
    private OrderStatus orderStatus;
}
