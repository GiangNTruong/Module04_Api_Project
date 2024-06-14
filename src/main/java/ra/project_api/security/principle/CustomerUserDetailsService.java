package ra.project_api.security.principle;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ra.project_api.model.User;
import ra.project_api.repository.IUserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerUserDetailsService implements UserDetailsService {
    @Autowired
    private  IUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameOrEmail(username, username).orElseThrow(() -> new UsernameNotFoundException("Username not found"));
        List<GrantedAuthority> authorities = user.getRoles()
                .stream()
                .map(r->new SimpleGrantedAuthority(r.getRoleName().name()))
                .collect(Collectors.toList());
        return CustomerUserDetail.builder()
                .id(user.getUserId())
                .username(user.getUsername())
                .password(user.getPassword())
                .fullName(user.getFullname())
                .authorities(authorities).build();

    }
}
