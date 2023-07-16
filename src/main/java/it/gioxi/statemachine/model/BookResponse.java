package it.gioxi.statemachine.model;

import it.gioxi.statemachine.model.enums.BookStates;
import lombok.AllArgsConstructor;
import lombok.Data;


@AllArgsConstructor
@Data
public class BookResponse {

    private Long id;
    private String title;
    private BookStates state;
//    private int availableOnes;
//    private int issuedOnes;
//    private int borrowedOnes;
}
