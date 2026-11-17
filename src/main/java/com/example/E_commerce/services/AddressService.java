package com.example.E_commerce.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.E_commerce.dto.requests.AddressRequest;
import com.example.E_commerce.dto.responses.AddressResponse;
import com.example.E_commerce.exception.ResourceNotFoundException;
import com.example.E_commerce.mappers.AddressMapper;
import com.example.E_commerce.models.Address;
import com.example.E_commerce.repository.AddressRepository;
import com.example.E_commerce.repository.CustomerReposity;

import jakarta.transaction.Transactional;

@Service
public class AddressService {

    private final AddressRepository addressRepository;
    private final CustomerReposity customerReposity;
    private final AddressMapper addressMapper;

    public AddressService(AddressRepository addressRepository, CustomerReposity customerReposity, AddressMapper addressMapper){
        this.addressRepository = addressRepository;
        this.customerReposity = customerReposity;
        this.addressMapper = addressMapper;
    }

    @Transactional
    public AddressResponse create(AddressRequest request){
        validateCustomer(request.getCustomerId());

        Boolean isDefault = request.getIsDefault() == null ? false : request.getIsDefault();
        if(isDefault){
            addressRepository.clearDefaultByCustomerId(request.getCustomerId());
        }

        Address address = new Address();
        copyRequest(address, request);
        address.setIsDefault(isDefault);

        Address saved = addressRepository.save(address);

        return addressMapper.toResponse(saved);
    }

    public List<AddressResponse> getAll(Long customerId){
        List<Address> addresses = customerId == null
            ? addressRepository.findAll()
            : addressRepository.findByCustomerId(customerId);

        return addresses.stream().map(addressMapper::toResponse).collect(Collectors.toList());
    }

    public AddressResponse getById(Long id){
        return addressMapper.toResponse(getEntity(id));
    }

    @Transactional
    public AddressResponse update(Long id, AddressRequest request){
        Address address = getEntity(id);
        validateCustomer(request.getCustomerId());

        copyRequest(address, request);

        Boolean isDefault = request.getIsDefault() == null ? address.getIsDefault() : request.getIsDefault();
        if(isDefault){
            addressRepository.clearDefaultByCustomerIdExcluding(request.getCustomerId(), id);
        }
        address.setIsDefault(isDefault);

        Address saved = addressRepository.save(address);

        return addressMapper.toResponse(saved);
    }

    @Transactional
    public void delete(Long id){
        addressRepository.delete(getEntity(id));
    }

    private void copyRequest(Address address, AddressRequest request){
        address.setCustomerId(request.getCustomerId());
        address.setRecipientName(request.getRecipientName());
        address.setPhone(request.getPhone());
        address.setAddressLine(request.getAddressLine());
        address.setCity(request.getCity());
        address.setProvince(request.getProvince());
    }

    private void validateCustomer(Long customerId){
        if(!customerReposity.existsById(customerId)){
            throw new ResourceNotFoundException("Customer not found");
        }
    }

    private Address getEntity(Long id){
        return addressRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + id));
    }
}
