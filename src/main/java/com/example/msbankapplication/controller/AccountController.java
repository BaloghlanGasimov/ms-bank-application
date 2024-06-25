package com.example.msbankapplication.controller;

import com.example.msbankapplication.dao.entity.AccountEntity;
import com.example.msbankapplication.model.AccountDto;
import com.example.msbankapplication.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/accounts")
public class AccountController {
    private final AccountService accountService;
    @GetMapping
    public List<AccountDto> getAllAccounts(){
        return accountService.getAllAccounts();
    }

    @GetMapping("/{accountId}")
    public AccountDto getAccountById(@PathVariable Long accountId){
        return accountService.getAccountById(accountId);
    }

    @PostMapping
    public void saveAccount(@RequestBody @Valid AccountDto accountDto){
        accountService.saveAccount(accountDto);
    }

    @PutMapping("/{accountId}")
    public void editAccount(@PathVariable Long accountId,@RequestBody AccountDto accountDto){
        accountService.editAccount(accountId,accountDto);
    }

    @DeleteMapping("/{accountId}")
    public AccountDto deleteAccount(@PathVariable Long accountId){
        return accountService.deleteAccount(accountId);
    }
}
