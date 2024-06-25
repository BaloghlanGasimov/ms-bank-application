package com.example.msbankapplication.dao.repository;

import com.example.msbankapplication.dao.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<AccountEntity,Long> {
}
