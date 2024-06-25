package com.example.msbankapplication.mapper;

import com.example.msbankapplication.dao.entity.AccountEntity;
import com.example.msbankapplication.dao.repository.AccountRepository;
import com.example.msbankapplication.model.AccountDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    AccountEntity mapToEntity(AccountDto accountDto);
    AccountDto mapToDto(AccountEntity accountEntity);
}
