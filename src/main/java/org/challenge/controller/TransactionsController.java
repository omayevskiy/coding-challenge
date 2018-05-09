package org.challenge.controller;

import org.challenge.logic.AcceptSalesUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class TransactionsController {
    private final AcceptSalesUseCase acceptSalesUseCase;

    @Autowired
    TransactionsController(AcceptSalesUseCase acceptSalesUseCase) {
        this.acceptSalesUseCase = acceptSalesUseCase;
    }

    @RequestMapping(value = "/sales", method = RequestMethod.POST, consumes = "application/x-www-form-urlencoded")
    @ResponseStatus(HttpStatus.ACCEPTED)
    void acceptSale(@RequestParam("sales_amount") String salesAmount) {
        acceptSalesUseCase.accept(salesAmount);
    }
}
