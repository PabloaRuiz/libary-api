package com.spring.libaryapi.ModelEntity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Loan {

    private Long id;
    private String customer;
    private Book book;
    private LocalDate loanDate;
    private boolean returned;
}
