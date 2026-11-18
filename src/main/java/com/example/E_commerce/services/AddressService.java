package com.example.E_commerce.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.example.E_commerce.dto.requests.AddressRequest;
import com.example.E_commerce.dto.responses.AddressResponse;
import com.example.E_commerce.exception.ResourceNotFoundException;
import com.example.E_commerce.mappers.AddressMapper;
import com.example.E_commerce.models.Address;
import com.example.E_commerce.models.Customer;
import com.example.E_commerce.repository.AddressRepository;
import com.example.E_commerce.repository.CustomerRepository;
import com.example.E_commerce.security.CurrentUserService;

import jakarta.transaction.Transactional;

@Service
public class AddressService {

    private final AddressRepository addressRepository;
    private final CustomerRepository customerReposity;
    private final AddressMapper addressMapper;
    private final CurrentUserService currentUserService;

    public AddressService(AddressRepository addressRepository, CustomerRepository customerReposity, AddressMapper addressMapper, CurrentUserService currentUserService){
        this.addressRepository = addressRepository;
        this.customerReposity = customerReposity;
        this.addressMapper = addressMapper;
        this.currentUserService = currentUserService;
    }

    @Transactional
    public AddressResponse create(AddressRequest request){
        Customer current = currentUserService.getCurrentCustomer();
        requireAdminOrCustomer(current, request.getCustomerId());
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
        Customer current = currentUserService.getCurrentCustomer();
        Long filterCustomerId = currentUserService.isAdmin(current) ? customerId : current.getId();

        List<Address> addresses = filterCustomerId == null
            ? addressRepository.findAll()
            : addressRepository.findByCustomerId(filterCustomerId);

        return addresses.stream().map(addressMapper::toResponse).collect(Collectors.toList());
    }

    public AddressResponse getById(Long id){
        return addressMapper.toResponse(getAccessibleEntity(id));
    }

    @Transactional
    public AddressResponse update(Long id, AddressRequest request){
        Address address = getAccessibleEntity(id);
        Customer current = currentUserService.getCurrentCustomer();
        requireAdminOrCustomer(current, request.getCustomerId());
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
        Address address = getAccessibleEntity(id);
        Long customerId = address.getCustomerId();
        boolean wasDefault = Boolean.TRUE.equals(address.getIsDefault());

        addressRepository.delete(address);
        addressRepository.flush();

        if(wasDefault){
            addressRepository.findByCustomerIdOrderByIdAsc(customerId).stream()
                .findFirst()
                .ifPresent(next -> {
                    next.setIsDefault(true);
                    addressRepository.save(next);
                });
        }
    }

    private Address getAccessibleEntity(Long id){
        Address address = getEntity(id);
        Customer current = currentUserService.getCurrentCustomer();

        if(!currentUserService.isAdmin(current) && !current.getId().equals(address.getCustomerId())){
            throw new ResourceNotFoundException("Address not found with id: " + id);
        }

        return address;
    }

    private void requireAdminOrCustomer(Customer current, Long customerId){
        if(!currentUserService.isAdmin(current) && !current.getId().equals(customerId)){
            throw new AccessDeniedException("You can only manage your own addresses");
        }
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
