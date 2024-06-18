package ra.project_api.service;

import ra.project_api.dto.response.AddressDTO;
import ra.project_api.model.Address;

import java.util.List;
import java.util.Optional;

public interface AddressService {
    List<Address> getAddressesByUsername(String username);
     Address getAddressById(Long addressId);
    Address addAddress(String username, AddressDTO addressDTO);
    void deleteAddress(Long id);
}
