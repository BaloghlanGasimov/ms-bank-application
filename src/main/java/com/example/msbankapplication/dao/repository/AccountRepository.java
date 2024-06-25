package com.example.msbankapplication.dao.repository;

import com.example.msbankapplication.dao.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<AccountEntity,Long> {
    Optional<AccountEntity> findByAccNumb(String accNumb);
}
