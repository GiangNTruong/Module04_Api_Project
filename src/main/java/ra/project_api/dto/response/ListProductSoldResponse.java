package ra.project_api.dto.response;

import lombok.*;
import org.springframework.data.domain.Sort;
import ra.project_api.model.Product;

import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ListProductSoldResponse {
    private List<Product> content;
    private long totalElements;
    private int totalPages;
    private int size;
    private int number;
    private Sort sort;
}
