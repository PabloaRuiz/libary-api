package com.spring.libaryapi.Service.impl;

import com.spring.libaryapi.ModelEntity.Loan;
import com.spring.libaryapi.ModelRepository.LoanRepository;
import com.spring.libaryapi.Service.LoanService;

public class LoanServiceImpl implements LoanService {

    private LoanRepository repository;

    public LoanServiceImpl (LoanRepository repository) {
        this.repository = repository;
    }

    @Override
    public Loan save(Loan loan) {
        return repository.save(loan);
    }
}
