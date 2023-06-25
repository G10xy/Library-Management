package it.gioxi.statemachine;

import lombok.Builder;
import lombok.Data;


@Builder
@Data
public class BookResponse {

    private Long id;
    private String title;
    private BookStates state;
//    private int availableOnes;
//    private int issuedOnes;
//    private int borrowedOnes;
}
