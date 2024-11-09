package com.example.EShop.services;

import com.example.EShop.dtos.ProductOrderDto;
import com.example.EShop.models.CustomUserDetails;
import com.example.EShop.models.User;
import com.example.EShop.repositories.UserRepository;
import com.example.EShop.utils.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DefaultEmailService {
    @Value("${spring.mail.username}")
    private String from;
    private final JavaMailSender emailSender;
    private final UserRepository userRepository;
    private final JwtTokenUtils jwtTokenUtils;
    private final BasketService basketService;

    public void sendSimpleEmail(String toAddress, String subject, String order) {

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(toAddress);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(order);
        simpleMailMessage.setFrom(from);
        emailSender.send(simpleMailMessage);
    }

    public void sendEmail(CustomUserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername());
        List<ProductOrderDto> usersProducts = basketService.getUserProductDtos(userDetails);

        int totalPrice = 0;
        StringBuilder emailContent = new StringBuilder("Ваш заказ на E-shop:\n\n");

        for (ProductOrderDto productOrder : usersProducts) {
            emailContent.append(productOrder.getTitle())
                    .append(" - Количество: ").append(productOrder.getCount())
                    .append(" - Цена: ").append(productOrder.getPrice() * productOrder.getCount())
                    .append("\n");
            totalPrice += (int) (productOrder.getPrice() * productOrder.getCount());
        }
        emailContent.append("\nИтоговая стоимость: ").append(totalPrice).append(" руб.");
        String address = user.getEmail();
        sendSimpleEmail(
                address,
                "Ваш заказ на E-shop",
                emailContent.toString()
        );
        for (ProductOrderDto productOrder : usersProducts) {
            basketService.cleanBasketAfterEmail(user, productOrder.getId());
        }
    }

}
