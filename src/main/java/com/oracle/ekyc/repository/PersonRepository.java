package com.oracle.ekyc.repository;

import com.oracle.ekyc.model.Person;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by shashiramachandra on 20/04/23.
 */

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {

    Optional<Person> findByPhone(String personPhone);

    Optional<Person> findByAadhaarID(String personAadhaarID);

    Optional<Person> findBySecID(String secID);

}
