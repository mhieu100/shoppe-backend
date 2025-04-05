package com.project.service;

import com.project.dto.UserDiscountDTO;
import com.project.exception.ExistException;
import com.project.model.Discount;
import com.project.model.User;
import com.project.model.UserDiscount;
import com.project.repository.DiscountRepository;
import com.project.repository.UserDiscountRepository;
import com.project.repository.UserRepository;
import com.project.util.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserDiscountService {
    private final UserDiscountRepository userDiscountRepository;
    private final UserRepository userRepository;
    private final DiscountRepository discountRepository;

    @Transactional
    public UserDiscountDTO createUserDiscount(String discountCode) throws ExistException {
        String email = JwtUtils.getCurrentUserLogin().isPresent() ? JwtUtils.getCurrentUserLogin().get() : null;
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ExistException("User not found"));
        Discount discount = discountRepository.findByCode(discountCode)
                .orElseThrow(() -> new ExistException("Discount not found"));

        if (discount.getUsedCount() >= discount.getUsageLimit()) {
            throw new ExistException("Discount usage limit exceeded");
        }

        UserDiscount userDiscount = new UserDiscount();
        userDiscount.setUser(user);
        userDiscount.setDiscount(discount);
        userDiscount.setDiscountDate(new Date());

        discount.setUsedCount(discount.getUsedCount() + 1);
        discountRepository.save(discount);

        return new UserDiscountDTO(userDiscountRepository.save(userDiscount));
    }

    public List<UserDiscountDTO> getUserDiscounts(int userId) throws ExistException {
        if (!userRepository.existsById(userId)) {
            throw new ExistException("User not found");
        }
        return userDiscountRepository.findByUserId(userId).stream()
                .map(UserDiscountDTO::new)
                .collect(Collectors.toList());
    }
}