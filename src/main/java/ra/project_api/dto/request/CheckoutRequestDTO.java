package ra.project_api.dto.request;
import lombok.Data;

import java.util.List;

@Data
public class CheckoutRequestDTO {
    private Double totalPrice;
    private String note;
    private String receiveName;
    private String receiveAddress;
    private String receivePhone;
    private List<OrderDetailRequestDTO> orderDetails;
}