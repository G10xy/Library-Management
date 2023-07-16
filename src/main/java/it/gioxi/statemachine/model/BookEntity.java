package it.gioxi.statemachine.model;

import it.gioxi.statemachine.model.enums.BookStates;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "books")
public class BookEntity {

    @Id
    @GeneratedValue
    private Long id;
    @Column(unique=true)
    private String title;
//    @Column(name = "available_num")
//    private int availableOnes;
//    @Column(name = "issued_num")
//    private int issuedOnes;
//    @Column(name = "borrowed_num")
//    private int borrowedOnes;
    @Column
    @Enumerated(EnumType.STRING)
    private BookStates state;

    @ManyToMany
    @JoinTable(
            name = "borrowing",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<UserEntity> usersWhoBorrowed = new HashSet<>();
}
