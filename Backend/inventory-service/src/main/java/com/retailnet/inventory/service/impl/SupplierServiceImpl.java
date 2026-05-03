package com.retailnet.inventory.service.impl;

import com.retailnet.inventory.utils.LogConstant;
import com.retailnet.inventory.dto.SupplierDTO;
import com.retailnet.inventory.entity.Supplier;
import com.retailnet.inventory.exception.BusinessException;
import com.retailnet.inventory.mapper.SupplierMapper;
import com.retailnet.inventory.repository.SupplierRepository;
import com.retailnet.inventory.service.SupplierService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service implementation for managing Vendor/Supplier information.
 * Handles the mapping between API data and the Database records.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;

    private static final String CLASS_NAME = "SupplierServiceImpl";

    @Override
    public SupplierDTO addSupplier(SupplierDTO supplierDTO) {
        final String METHOD_NAME = "addSupplier";

        log.info(LogConstant.INSIDE_CLASS_METHOD, CLASS_NAME, METHOD_NAME);

        try {
            if (supplierDTO == null) {
                throw new BusinessException("Supplier data provided is empty or null");
            }

            Supplier supplierEntity = supplierMapper.toEntity(supplierDTO);
            Supplier savedEntity = supplierRepository.save(supplierEntity);

            log.info(LogConstant.DEBUG_INSIDE_CLASS_METHOD_SINGLE, CLASS_NAME, METHOD_NAME,
                    "Supplier saved successfully");
            return supplierMapper.toDTO(savedEntity);

        } catch (BusinessException e) {
            log.error(LogConstant.EXCEPTION, CLASS_NAME, METHOD_NAME, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error(LogConstant.EXCEPTION, CLASS_NAME, METHOD_NAME, e.getMessage());
            String supplierName = supplierDTO != null ? supplierDTO.getSupplierName() : "unknown";
            throw new BusinessException(
                    "An error occurred while adding the supplier: " + supplierName);
        }
    }

    @Override
    public List<SupplierDTO> findAllSuppliers() {
        final String METHOD_NAME = "findAllSuppliers";
        log.info(LogConstant.INSIDE_CLASS_METHOD, CLASS_NAME, METHOD_NAME);

        try {
            List<Supplier> suppliers = supplierRepository.findAll();
            return supplierMapper.toDTOList(suppliers);

        } catch (Exception e) {
            log.error(LogConstant.EXCEPTION, CLASS_NAME, METHOD_NAME, e.getMessage());
            throw new BusinessException("Unable to fetch suppliers from the database at this time.");
        }
    }

    @Override
    public SupplierDTO updateSupplier(Long id, SupplierDTO supplierDTO) {
        final String METHOD_NAME = "updateSupplier";
        log.info(LogConstant.INSIDE_CLASS_METHOD, CLASS_NAME, METHOD_NAME);

        try {
            Supplier existingSupplier = supplierRepository.findById(id)
                    .orElseThrow(() -> new BusinessException("Supplier with ID " + id + " not found."));

            existingSupplier.setSupplierName(supplierDTO.getSupplierName());
            existingSupplier.setContactEmail(supplierDTO.getContactEmail());
            existingSupplier.setCategory(supplierDTO.getCategory());
            existingSupplier.setRating(supplierDTO.getRating());
            existingSupplier.setLeadTimeDays(supplierDTO.getLeadTimeDays());

            Supplier updatedEntity = supplierRepository.save(existingSupplier);
            return supplierMapper.toDTO(updatedEntity);

        } catch (BusinessException e) {
            log.error(LogConstant.EXCEPTION, CLASS_NAME, METHOD_NAME, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error(LogConstant.EXCEPTION, CLASS_NAME, METHOD_NAME, e.getMessage());
            throw new BusinessException("An error occurred while updating supplier: " + id);
        }
    }

    @Override
    public void deleteSupplier(Long id) {
        final String METHOD_NAME = "deleteSupplier";
        log.info(LogConstant.INSIDE_CLASS_METHOD, CLASS_NAME, METHOD_NAME);

        try {
            if (!supplierRepository.existsById(id)) {
                throw new BusinessException("Supplier with ID " + id + " does not exist.");
            }
            supplierRepository.deleteById(id);
        } catch (BusinessException e) {
            log.error(LogConstant.EXCEPTION, CLASS_NAME, METHOD_NAME, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error(LogConstant.EXCEPTION, CLASS_NAME, METHOD_NAME, e.getMessage());
            throw new BusinessException("An error occurred while deleting supplier: " + id);
        }
    }
}