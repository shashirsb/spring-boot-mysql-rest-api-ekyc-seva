package com.oracle.ekyc.repository;

import com.oracle.ekyc.model.Person;
import com.oracle.ekyc.model.Scheme;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by shashiramachandra on 20/04/23.
 */

@Repository
public interface SchemeRepository extends JpaRepository<Scheme, Long> {



}
