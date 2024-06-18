package ra.project_api.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ra.project_api.dto.response.AddressDTO;
import ra.project_api.model.Address;
import ra.project_api.model.User;
import ra.project_api.repository.AddressRepository;
import ra.project_api.repository.IUserRepository;
import ra.project_api.service.AddressService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class AddressServiceImpl implements AddressService {
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private IUserRepository userRepository;
    @Override
    public List<Address> getAddressesByUsername(String username) {
        return addressRepository.findByUser_Username(username);
    }

    @Override
    public Address getAddressById(Long addressId) {
        return addressRepository.findById(addressId).orElseThrow(()->new NoSuchElementException("Khong ton tai id"));
    }

    @Override
    public Address addAddress(String username, AddressDTO addressDTO) {
        // Tìm người dùng dựa trên username
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("User Not Found with username: " + username));

        // Tạo đối tượng Address từ AddressDTO
        Address newAddress = Address.builder()
                .fullAddress(addressDTO.getFullAddress())
                .phone(addressDTO.getPhone())
                .receiveName(addressDTO.getReceiveName())
                .user(user)
                .build();

        // Lưu đối tượng Address vào cơ sở dữ liệu
        return addressRepository.save(newAddress);
    }

    @Override
    public void deleteAddress(Long id) {
        addressRepository.deleteById(id);
    }
}
