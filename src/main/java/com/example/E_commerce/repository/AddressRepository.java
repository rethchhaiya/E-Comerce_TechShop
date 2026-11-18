package com.example.E_commerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.E_commerce.models.Address;

public interface AddressRepository extends JpaRepository<Address, Long>{
    List<Address> findByCustomerId(Long customerId);
    List<Address> findByCustomerIdOrderByIdAsc(Long customerId);

    // use it when customer want to delete default address entirely
    @Modifying // this tell JPA that this method change data in Data base
    @Query("update Address a set a.isDefault = false where a.customerId = :customerId and a.isDefault = true")
    void clearDefaultByCustomerId(@Param("customerId") Long customerId);

    // use it when customer want to change default address
    @Modifying // <> mean not equal to example !=      
    @Query("update Address a set a.isDefault = false where a.customerId = :customerId and a.isDefault = true and a.id <> :id")
    void clearDefaultByCustomerIdExcluding(@Param("customerId") Long customerId, @Param("id") Long id);
}
