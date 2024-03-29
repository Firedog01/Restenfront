package pl.lodz.p.edu.rest.authentication;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import pl.lodz.p.edu.data.model.DTO.CredentialsNewPasswordDTO;
import pl.lodz.p.edu.data.model.DTO.TokenDTO;
import pl.lodz.p.edu.data.model.users.User;
import pl.lodz.p.edu.data.model.users.UserType;
import pl.lodz.p.edu.rest.exception.AuthenticationFailureException;
import pl.lodz.p.edu.rest.repository.impl.UserRepository;

@Transactional
@RequestScoped
public class AuthenticationManager {

    @Inject
    private UserRepository userRepository;

    @Inject
    JwtUtilities utilities;

    public TokenDTO login(String login, String password) throws AuthenticationFailureException {
        User user = getUser(login, password);
        // if password ok then:
        return new TokenDTO(utilities.generateToken(user.getLogin(), user.getUserType()));
    }

    public String changePassword(CredentialsNewPasswordDTO credentials) throws AuthenticationFailureException {
        User user = getUser(credentials.getLogin(), credentials.getPassword());
        // if old password ok then:
        User updated = userRepository.get(user.getEntityId());
        updated.setPassword(credentials.getNewPassword());
        userRepository.update(updated);
        return null;
    }

    private User getUser(String login, String password) throws AuthenticationFailureException {
        try {
            User user = userRepository.getByLoginPassword(login, password);
            if (!user.isActive()) {
                throw new AuthenticationFailureException("User is inactive");
            }
            return user;
        } catch (NoResultException e) {
            throw new AuthenticationFailureException("User not found");
        }
    }
}
