package it.gioxi.statemachine.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class BookRequestUpdate extends BookRequest {

    private Long id;
    private int issuedOnes;
    private int borrowedOnes;
}
