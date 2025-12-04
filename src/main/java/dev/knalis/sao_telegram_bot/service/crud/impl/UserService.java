package dev.knalis.sao_telegram_bot.service.crud.impl;

import dev.knalis.sao_telegram_bot.dto.UserUpdateRequest;
import dev.knalis.sao_telegram_bot.exception.EntityException;
import dev.knalis.sao_telegram_bot.model.user.User;
import dev.knalis.sao_telegram_bot.repo.jpa.UserRepo;
import dev.knalis.sao_telegram_bot.service.crud.intrf.CrudService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UserService implements CrudService<User, Long> {

    private final UserRepo userRepo;
    private final GangService gangService;

    private static final Pattern NICK_PATTERN = Pattern.compile("^[A-Za-z0-9_]{3,20}$");

    @Override
    public JpaRepository getRepo() {
        return userRepo;
    }

    public User getByNickname(String nickname) {
        return userRepo.findByNickname(nickname)
                .orElseThrow(() -> new EntityException("User not found: " + nickname));
    }

    @Transactional
    public User create(User user) {
        if (userRepo.existsById(user.getId())) {
            throw new EntityException("User with id " + user.getId() + " already exists");
        }
        if (userRepo.findByNickname(user.getNickname()).isPresent()) {
            throw new EntityException("User with nickname " + user.getNickname() + " already exists");
        }

        User saved = userRepo.save(user);
        saved.setConfigId(saved.getSettingsConfigs().get(0).getId());
        return saved;
    }

    @Transactional
    public User update(Long id, UserUpdateRequest dto) {
        User existing = findById(id);

        if (dto.getNickname() != null &&
                !dto.getNickname().equals(existing.getNickname()) &&
                userRepo.findByNickname(dto.getNickname()).isPresent()) {
            throw new EntityException("User with nickname " + dto.getNickname() + " already exists");
        }

        if (dto.getNickname() != null) {
            existing.setNickname(dto.getNickname());
        }

        if (dto.getLocation() != null) {
            existing.setLocation(dto.getLocation());
        }

        return userRepo.save(existing);
    }

    @Override
    @Transactional
    public void delete(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityException.EntityNotFoundException(String.format("User with id %s not found", userId)));
        gangService.leaveGang(user.getId());

        userRepo.delete(user);
    }

    public boolean existsByNickname(String nickname) {
        return userRepo.findByNickname(nickname).isPresent();
    }

    public boolean isNickNameValid(String nickname) {
        if (nickname == null) return false;
        String clean = nickname.startsWith("@") ? nickname.substring(1) : nickname;
        return NICK_PATTERN.matcher(clean).matches();
    }

    public boolean isAccountNameAvailable(String nickname) {
        String clean = nickname.startsWith("@") ? nickname.substring(1) : nickname;
        return userRepo.findByNickname(clean).isEmpty();
    }

    @Transactional
    public void setUserNickName(Long userId, String nickname) {
        User user = findById(userId);
        String clean = nickname.startsWith("@") ? nickname.substring(1) : nickname;
        if (!isAccountNameAvailable(clean)) throw new EntityException("Nickname already taken");
        user.setNickname(clean);
        userRepo.save(user);
    }

    @Transactional
    public void setUserLocation(Long userId, short location) {
        User user = findById(userId);
        user.setLocation(location);
        userRepo.save(user);
    }

    @Transactional
    public void linkAdditionalAccount(Long userId, String accountName) {
        User user = findById(userId);
        String clean = accountName.startsWith("@") ? accountName.substring(1) : accountName;
        if (user.getAdditionalAccounts() != null && user.getAdditionalAccounts().contains(clean)) {
            throw new EntityException("Account already linked");
        }
        user.getAdditionalAccounts().add(clean);
        userRepo.save(user);
    }

    @Transactional
    public void unlinkAdditionalAccount(Long userId, String accountName) {
        User user = findById(userId);
        String clean = accountName.startsWith("@") ? accountName.substring(1) : accountName;
        if (user.getAdditionalAccounts() != null) {
            user.getAdditionalAccounts().removeIf(a -> a.equalsIgnoreCase(clean));
            userRepo.save(user);
        }
    }

}