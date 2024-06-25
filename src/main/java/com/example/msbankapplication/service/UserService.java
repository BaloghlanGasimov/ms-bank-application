package com.example.msbankapplication.service;

import com.example.msbankapplication.dao.entity.AccountEntity;
import com.example.msbankapplication.dao.entity.TransactionEntity;
import com.example.msbankapplication.dao.entity.UserEntity;
import com.example.msbankapplication.dao.repository.AccountRepository;
import com.example.msbankapplication.dao.repository.TransactionRepository;
import com.example.msbankapplication.dao.repository.UserRepository;
import com.example.msbankapplication.exceptions.AccountNotUserException;
import com.example.msbankapplication.exceptions.NoBalance;
import com.example.msbankapplication.exceptions.NotFound;
import com.example.msbankapplication.exceptions.SameCurrencyException;
import com.example.msbankapplication.mapper.AccountMapper;
import com.example.msbankapplication.mapper.UserMapper;
import com.example.msbankapplication.model.PaymentDto;
import com.example.msbankapplication.model.UserRequestDto;
import com.example.msbankapplication.model.UserResponseDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
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

        accountFreeCheck(accountEntity);
        if (!checkCurrencyAccount(userEntity, accountEntity)) {
            accountEntity.setAccountId(createAccountId(userEntity, accountEntity));
            accountEntity.setUser(userEntity);

            userRepository.save(userEntity);
            accountRepository.save(accountEntity);
            log.info("ActionLog.assignAccountToUser.end user {} account {}", userId, accountId);
        } else {
            throw new SameCurrencyException("SAME_CURRENCY_ACCOUNTS", "Error ActionLog.assignAccountToUser user {" + userId + "}" + " account {" + accountId + "}");
        }

    }

    public void cardToCard(PaymentDto paymentDto) {
        log.info("ActionLog.cardToCard.start payment {}", paymentDto);
        String fromAccNumb = paymentDto.getFromAccount();
        String toAccNumb = paymentDto.getToAccount();
        Double amount = paymentDto.getAmount();

        AccountEntity fromAccount = findAccount(fromAccNumb);
        AccountEntity toAccount = findAccount(toAccNumb);

        if (!(checkRelatedUser(fromAccount) && checkRelatedUser(toAccount))) {
            throw new AccountNotUserException("ACCOUNT_DOES_NOT_HAVE_USER", "Error ActionLog.cardToCard payment {" + paymentDto + "}");
        }
        checkBalanceAndAmount(fromAccount, amount);

        if (!(fromAccount.getUser().equals(toAccount.getUser()))) {
            if (fromAccount.getCurrency().equals(toAccount.getCurrency())) {
                purchaseCardToCard(0.1, fromAccount, toAccount, amount);
            } else {
                purchaseCardToCard(1D, fromAccount, toAccount, amount);
            }
        } else {
            purchaseCardToCard(0D, fromAccount, toAccount, amount);
        }
        log.info("ActionLog.cardToCard.end payment {}", paymentDto);
    }

    private void checkBalanceAndAmount(AccountEntity fromCard, Double amount) {
        if (fromCard.getBalance() < amount) {
            throw new NoBalance("NOT_ENOUGH_BALANCE", "Error ActionLog.checkBalanceAndAmount");
        }
    }

    @Transactional
    public void purchaseCardToCard(Double commissionRate, AccountEntity fromCard, AccountEntity toCard, Double amount) {
        Double commission = amount * commissionRate / 100;
        Double amountWithCommission = amount + commission;

        fromCard.setBalance(fromCard.getBalance() - amountWithCommission);
        toCard.setBalance(toCard.getBalance() + amount);
        accountRepository.save(fromCard);
        accountRepository.save(toCard);

        saveTransaction(fromCard,toCard,amount,commission);
    }

    private void saveTransaction(AccountEntity fromCard,AccountEntity toCard,Double amount,Double commission){
        TransactionEntity transactionEntity=new TransactionEntity();
        transactionEntity.setFromAccount(fromCard.getAccNumb());
        transactionEntity.setToAccount(toCard.getAccNumb());
        transactionEntity.setAmount(amount);
        transactionEntity.setCommission(commission);
        transactionEntity.setPurchaseDate(LocalDate.now());
        transactionRepository.save(transactionEntity);
    }

    private boolean checkRelatedUser(AccountEntity account) {
        return account.getAccountId() != null;
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

    private void accountFreeCheck(AccountEntity accountEntity) {
        if (accountEntity.getAccountId() != null) {
            throw new AccountNotUserException("ACCOUNT_HAS_USER", "Error ActionLog.accountFreeCheck {" + accountEntity + "}");
        }
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
    private AccountEntity findAccount(String accountNumb){
        AccountEntity accountEntity = accountRepository.findByAccNumb(accountNumb).
                orElseThrow(() -> new NotFound("ACCOUNT_NOT_FOUND", "Error ActionLog.findAccount account {" + accountNumb + "}"));
        return accountEntity;
    }

}
