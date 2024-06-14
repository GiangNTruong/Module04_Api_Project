package ra.project_api;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class ProjectApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProjectApiApplication.class, args);
	}
	@Bean
	public ModelMapper modelMapper(){
		return new ModelMapper();
	}
	@Bean
	public PasswordEncoder passwordEncoder(){
		return new BCryptPasswordEncoder();
	}

//    @Bean
//    CommandLineRunner commandLineRunner(IUserRepository userRepository){
//        return args -> {
//            Role admin = new Role(null, RoleName.ROLE_ADMIN);
//            Role pm = new Role(null, RoleName.ROLE_PM);
//            Role user = new Role(null, RoleName.ROLE_USER);
//
//            User u1= new User(null,"giangnt", passwordEncoder().encode("123456"), "Giang","giang@gmail.com",true,new Date(),new HashSet<>());
//            u1.getRoles().add(admin);
//            u1.getRoles().add(pm);
//            u1.getRoles().add(user);
//
//            userRepository.save(u1);
//        };
//    }
}
