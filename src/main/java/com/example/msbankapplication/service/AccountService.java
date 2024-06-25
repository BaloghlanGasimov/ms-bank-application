package com.example.msbankapplication.service;

import com.example.msbankapplication.dao.entity.AccountEntity;
import com.example.msbankapplication.dao.entity.UserEntity;
import com.example.msbankapplication.dao.repository.AccountRepository;
import com.example.msbankapplication.exceptions.NotFound;
import com.example.msbankapplication.mapper.AccountMapper;
import com.example.msbankapplication.model.AccountDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.LongConsumer;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    public List<AccountDto> getAllAccounts(){
        log.info("ActionLog.getAllAccounts.start");

        List<AccountEntity> accountEntities = accountRepository.findAll();
        List<AccountDto> accountDtos = accountEntities.stream().map(accountMapper::mapToDto).toList();

        log.info("ActionLog.getAllAccounts.end");
        return accountDtos;
    }

    public AccountDto getAccountById(Long accountId){
        log.info("ActionLog.getAccountById.start account {}",accountId);

        AccountEntity accountEntity = findAccount(accountId);
        AccountDto accountDto = accountMapper.mapToDto(accountEntity);

        log.info("ActionLog.getAccountById.end account {}",accountId);
        return accountDto;
    }

    public void saveAccount(AccountDto accountDto){
        log.info("ActionLog.saveAccount.start account {}",accountDto);

        AccountEntity accountEntity = accountMapper.mapToEntity(accountDto);
        accountRepository.save(accountEntity);

        log.info("ActionLog.saveAccount.end account {}",accountDto);
    }

    public void editAccount(Long accountId,AccountDto accountDto){
        log.info("ActionLog.editAccount.start accountId {} account {}",accountId,accountDto);

        AccountEntity accountEntity = findAccount(accountId);
        if(accountDto.getAccNumb()!=null){
            accountEntity.setAccNumb(accountDto.getAccNumb());
        }
        if(accountDto.getAccountId()!=null){
            accountEntity.setAccountId(accountDto.getAccountId());
        }
        if(accountDto.getBalance()!=null){
            accountEntity.setBalance(accountDto.getBalance());
        }
        if(accountDto.getCurrency()!=null){
            accountEntity.setCurrency(accountDto.getCurrency());
        }
        accountRepository.save(accountEntity);
        log.info("ActionLog.editAccount.start accountId {} account {}",accountId,accountDto);
    }

    public AccountDto deleteAccount(Long accountId){
        log.info("ActionLog.deleteAccount.start account {}",accountId);

        AccountEntity accountEntity = findAccount(accountId);
        AccountDto accountDto = accountMapper.mapToDto(accountEntity);
        accountRepository.deleteById(accountId);

        log.info("ActionLog.deleteAccount.end account {}",accountId);
        return accountDto;
    }

    private AccountEntity findAccount(Long accountId){
        AccountEntity accountEntity =accountRepository.findById(accountId).
                orElseThrow(()->new NotFound("ACCOUNT_NOT_FOUND","Error ActionLog.findById account {"+accountId+"}"));
        return accountEntity;
    }

}
