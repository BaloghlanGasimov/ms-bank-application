package com.example.msbankapplication.service;

import com.example.msbankapplication.dao.entity.AccountEntity;
import com.example.msbankapplication.dao.entity.UserEntity;
import com.example.msbankapplication.dao.repository.AccountRepository;
import com.example.msbankapplication.dao.repository.UserRepository;
import com.example.msbankapplication.exceptions.NotFound;
import com.example.msbankapplication.exceptions.SameCurrencyException;
import com.example.msbankapplication.mapper.AccountMapper;
import com.example.msbankapplication.mapper.UserMapper;
import com.example.msbankapplication.model.UserRequestDto;
import com.example.msbankapplication.model.UserResponseDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final UserMapper userMapper;

    public List<UserResponseDto> getAllUsers() {
        log.info("ActionLog.getAllUsers.start");

        List<UserEntity> userEntities = userRepository.findAll();
        List<UserResponseDto> userResponseDtos = userEntities.stream().map(userMapper::mapToRespDto).toList();

        log.info("ActionLog.getAllUsers.end");
        return userResponseDtos;
    }

    public UserResponseDto getUserById(Long userId) {
        log.info("ActionLog.getUserById.start userId {}", userId);

        UserEntity userEntity = findUser(userId);
        UserResponseDto userResponseDto = userMapper.mapToRespDto(userEntity);

        log.info("ActionLog.getUserById.end userId {}", userId);
        return userResponseDto;
    }

    public void saveUser(UserRequestDto userRequestDto) {
        log.info("ActionLog.saveUser.start user {}", userRequestDto);

        UserEntity userEntity = userMapper.mapToEntity(userRequestDto);
        userRepository.save(userEntity);

        log.info("ActionLog.saveUser.end user {}", userRequestDto);
    }

    public void editUser(Long userId, UserRequestDto userRequestDto) {
        log.info("ActionLog.editUser.start userId {} user {}", userId, userRequestDto);

        UserEntity userEntity = findUser(userId);
        if (userRequestDto.getName() != null) {
            userEntity.setName(userRequestDto.getName());
        }
        if (userRequestDto.getSurname() != null) {
            userEntity.setSurname(userRequestDto.getSurname());
        }
        if (userRequestDto.getFinNo() != null) {
            userEntity.setFinNo(userRequestDto.getFinNo());
        }
        if (userRequestDto.getBirthDate() != null) {
            userEntity.setBirthDate(userRequestDto.getBirthDate());
        }
        userRepository.save(userEntity);
        log.info("ActionLog.editUser.end userId {} user {}", userId, userRequestDto);
    }

    public UserResponseDto deleteUser(Long userId) {
        log.info("ActionLog.deleteUser.start user {}", userId);

        UserEntity userEntity = findUser(userId);
        UserResponseDto userResponseDto = userMapper.mapToRespDto(userEntity);
        userRepository.deleteById(userId);

        log.info("ActionLog.deleteUser.end user {}", userId);
        return userResponseDto;
    }

    @Transactional
    public void assignAccountToUser(Long userId, Long accountId) {
        log.info("ActionLog.assignAccountToUser.start user {} account {}", userId, accountId);

        UserEntity userEntity = findUser(userId);
        AccountEntity accountEntity = findAccount(accountId);
        if (!checkCurrencyAccount(userEntity, accountEntity)) {
            accountEntity.setAccountId(createAccountId(userEntity, accountEntity));

            List<AccountEntity> accountEntities = userEntity.getAccounts();
            accountEntities.add(accountEntity);
            userEntity.setAccounts(accountEntities);

            userRepository.save(userEntity);
            log.info("ActionLog.assignAccountToUser.end user {} account {}", userId, accountId);
        } else {
            throw new SameCurrencyException("SAME_CURRENCY_ACCOUNTS", "Error ActionLog.assignAccountToUser user {" + userId + "}" + " account {" + accountId + "}");
        }

    }

    private String createAccountId(UserEntity userEntity, AccountEntity accountEntity) {
        return accountEntity.getCurrency().toString() + userEntity.getId() + userEntity.getName().toUpperCase().charAt(0);
    }

    private boolean checkCurrencyAccount(UserEntity userEntity, AccountEntity accountEntity) {
        List<AccountEntity> accountEntities = userEntity.getAccounts();
        boolean has = false;
        for (AccountEntity account : accountEntities) {
            if (account.getCurrency().equals(accountEntity.getCurrency())) {
                has = true;
            }
        }
        return has;
    }

    private UserEntity findUser(Long userId) {
        UserEntity userEntity = userRepository.findById(userId).
                orElseThrow(() -> new NotFound("USER_NOT_FOUND", "Error ActionLog.findUser userId {" + userId + "}"));
        return userEntity;
    }

    private AccountEntity findAccount(Long accountId) {
        AccountEntity accountEntity = accountRepository.findById(accountId).
                orElseThrow(() -> new NotFound("ACCOUNT_NOT_FOUND", "Error ActionLog.findAccount account {" + accountId + "}"));
        return accountEntity;
    }

}
