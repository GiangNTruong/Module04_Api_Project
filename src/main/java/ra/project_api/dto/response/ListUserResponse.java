package ra.project_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Sort;
import ra.project_api.model.Product;

import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ListUserResponse {
    private List<UserResponseDTO> content;
    private long totalElements;
    private int totalPages;
    private int size;
    private int number;
    private Sort sort;
}
