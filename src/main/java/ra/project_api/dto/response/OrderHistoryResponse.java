package ra.project_api.dto.response;

import lombok.*;

import java.util.Date;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class OrderHistoryResponse {
    private String username;
    private String productName;
    private Double productPrice;
    private Integer quantity;
    private Date purchaseDate;
}