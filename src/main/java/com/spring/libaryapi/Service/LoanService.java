package com.spring.libaryapi.Service;

import com.spring.libaryapi.Dto.LoanFilterDTO;
import com.spring.libaryapi.ModelEntity.Book;
import com.spring.libaryapi.ModelEntity.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface LoanService {

    Loan save(Loan loan);

    Optional<Loan> getById(Long id);

    Loan update(Loan loan);

    Page<Loan> find(LoanFilterDTO filterDTO, Pageable pageable);

    Page<Loan> getLoanByBook(Book book, Pageable pageable);

    List<Loan> getAllLatLoans();
}
