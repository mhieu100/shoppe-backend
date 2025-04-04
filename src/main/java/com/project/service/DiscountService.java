package com.project.service;

import com.project.dto.DiscountDTO;
import com.project.exception.ExistException;
import com.project.model.Discount;
import com.project.repository.DiscountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DiscountService {
    private final DiscountRepository discountRepository;

    public DiscountDTO createDiscount(Discount discount) throws ExistException {
        if (discountRepository.findByCode(discount.getCode()).isPresent()) {
            throw new ExistException("Discount code already exists");
        }
        Discount savedDiscount = discountRepository.save(discount);
        return new DiscountDTO(savedDiscount);
    }

    public DiscountDTO getDiscount(String code) throws ExistException {
        Discount discount = discountRepository.findByCode(code)
                .orElseThrow(() -> new ExistException("Discount not found"));
        return new DiscountDTO(discount);
    }

    public List<DiscountDTO> getAllDiscounts() {
        return discountRepository.findAll().stream()
                .map(DiscountDTO::new)
                .collect(Collectors.toList());
    }

    public DiscountDTO updateDiscount(int id, Discount discount) throws ExistException {
        Discount existingDiscount = discountRepository.findById(id)
                .orElseThrow(() -> new ExistException("Discount not found"));

        existingDiscount.setCode(discount.getCode());
        existingDiscount.setUsageLimit(discount.getUsageLimit());
        existingDiscount.setValue(discount.getValue());

        return new DiscountDTO(discountRepository.save(existingDiscount));
    }

    public void deleteDiscount(int id) throws ExistException {
        if (!discountRepository.existsById(id)) {
            throw new ExistException("Discount not found");
        }
        discountRepository.deleteById(id);
    }
}