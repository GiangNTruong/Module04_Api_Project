package ra.project_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SalesReportDTO {
    private Long orderId;
    private Date orderDate;
    private Double totalRevenue;
}