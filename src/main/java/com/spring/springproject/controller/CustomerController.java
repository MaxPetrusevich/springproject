package com.spring.springproject.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.spring.springproject.dto.OrderDto;
import com.spring.springproject.dto.OrderTechniqueDto;
import com.spring.springproject.dto.TechniqueDto;
import com.spring.springproject.dto.UserDto;
import com.spring.springproject.email.EmailSender;
import com.spring.springproject.entities.Status;
import com.spring.springproject.entities.User;
import com.spring.springproject.repositories.OrderRepository;
import com.spring.springproject.service.impl.OrderServiceImpl;
import com.spring.springproject.service.impl.OrderTechniqueServiceImpl;
import com.spring.springproject.service.impl.UserDetailsImpl;
import com.spring.springproject.service.interfaces.TechniqueService;
import com.spring.springproject.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.spring.springproject.controller.Constants.*;

@Controller
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {
    private final ModelMapper mapper;
    private final TechniqueService techniqueService;
    private final OrderServiceImpl orderService;
    private final OrderTechniqueServiceImpl orderTechniqueService;
    private final UserService userService;
    private final OrderRepository orderRepository;

    @GetMapping("/home")
    public String home(Model model, @RequestParam(defaultValue = "1", required = false) int page,
                       @RequestParam(defaultValue = "9", required = false) int size,
                       @RequestParam( required = false) Double startPrice,
                       @RequestParam( required = false) Double endPrice,
                       @RequestParam( required = false) String categoryName,
                       @RequestParam( required = false)String producerName,
                       @RequestParam( required = false)String modelName) throws JsonProcessingException {
        Pageable pageable = Pageable.ofSize(size);
        pageable = pageable.withPage(page - 1);
        Page<TechniqueDto> techniqueDtoPage = techniqueService.findAll(pageable, startPrice, endPrice, categoryName,producerName,modelName);
        model.addAttribute(PAGE, page);
        model.addAttribute(SIZE, size);
        model.addAttribute(START_PRICE, startPrice);
        model.addAttribute(END_PRICE, endPrice);
        model.addAttribute("categoryName", categoryName);
        model.addAttribute("modelName", modelName);
        model.addAttribute("producerName", producerName);
        model.addAttribute(TOTAL_PAGE, techniqueDtoPage.getTotalPages());
        model.addAttribute("items", techniqueDtoPage.getContent());
        return "tech/techListTest";
    }

    @GetMapping("/cart")
    public String toCart(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userDetails.getUser();
        Integer id = user.getId();
        model.addAttribute("order", orderService.getBucket(id));
        return "customer/cart";
    }

    @PostMapping("/cart")
    public String addToCart(@RequestParam("techId") Integer techId, @RequestParam("prev") String prev) {
        TechniqueDto techniqueDto = techniqueService.findById(techId);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userDetails.getUser();
        Integer id = user.getId();
        OrderDto orderDto = orderService.getBucket(id);
        OrderTechniqueDto orderTechniqueDto = orderTechniqueService.findByTechniqueIdAndOrderId(techId, orderDto.getId());
        if (orderTechniqueDto == null) {
            orderTechniqueDto = OrderTechniqueDto.builder()
                    .technique(techniqueDto)
                    .order(orderDto)
                    .quantity(1)
                    .build();
            orderTechniqueService.saveOrderTechnique(orderTechniqueDto);

        } else {
            orderTechniqueDto.setQuantity(orderTechniqueDto.getQuantity() + 1);
            orderTechniqueService.updateOrderTechnique(orderTechniqueDto);
        }
        orderDto.setAmount(orderDto.getAmount() + orderTechniqueDto.getTechnique().getPrice());
        orderService.updateOrder(orderDto);
        return "redirect:" + prev;
    }

    @PostMapping("/cart/quantity/minus")
    public String minusQuantity(@RequestParam("techId") Integer techId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userDetails.getUser();
        Integer id = user.getId();
        OrderDto bucket = orderService.getBucket(id);
        OrderTechniqueDto orderTechniqueDto = orderTechniqueService.findByTechniqueIdAndOrderId(techId, bucket.getId());
        orderTechniqueDto.setQuantity(orderTechniqueDto.getQuantity() - 1);
        bucket.setAmount(bucket.getAmount() - orderTechniqueDto.getTechnique().getPrice());
        if (orderTechniqueDto.getQuantity() <= 0) {
            bucket.getOrderTechniques().remove(orderTechniqueDto);
            orderTechniqueService.deleteOrderTechniqueById(orderTechniqueDto.getId());
        } else {
            orderTechniqueService.updateOrderTechnique(orderTechniqueDto);
        }
        orderService.updateOrder(bucket);
        return "redirect:/customer/cart";

    }


    @PostMapping("/cart/quantity/plus")
    public String plusQuantity(@RequestParam("techId") Integer techId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userDetails.getUser();
        Integer id = user.getId();
        OrderDto bucket = orderService.getBucket(id);
        OrderTechniqueDto orderTechniqueDto = orderTechniqueService.findByTechniqueIdAndOrderId(techId, bucket.getId());
        orderTechniqueDto.setQuantity(orderTechniqueDto.getQuantity() + 1);
        bucket.setAmount(bucket.getAmount() + orderTechniqueDto.getTechnique().getPrice());
        orderTechniqueService.updateOrderTechnique(orderTechniqueDto);
        orderService.updateOrder(bucket);
        return "redirect:/customer/cart";
    }

    @PostMapping("/cart/checkout")
    public String checkout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userDetails.getUser();
        Integer id = user.getId();
        OrderDto bucket = orderService.getBucket(id);
        bucket.setStatus(Status.ACTIVE);
        bucket.setOrderDate(new Date());
        orderService.updateOrder(bucket);
        EmailSender.sendPdf(user.getEmail(), orderRepository.findById(bucket.getId()).orElse(null));
        return "redirect:/customer/home";
    }

    @GetMapping("/profile")
    public String toProfile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userDetails.getUser();
        model.addAttribute("user", user);
        return "customer/profile";
    }

    @GetMapping("/profile/avatar")
    public String toAvatar(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userDetails.getUser();
        model.addAttribute("user", user);
        return "customer/edit";
    }

    @GetMapping("/profile/password")
    public String toPassword(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userDetails.getUser();
        model.addAttribute("userPassword", user.getPassword());
        model.addAttribute("user", user);
        return "customer/password";
    }

    @PostMapping("/profile/avatar")
    public String updateAvatar(Model model, @RequestParam("image") MultipartFile multipartFile) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userDetails.getUser();
        user = userService.updateImage(user.getId(), multipartFile).orElse(null);
        model.addAttribute("user", user);
        return "redirect:/customer/profile";
    }

    @PostMapping("/profile/edit")
    public String edit(@RequestParam("name") String name,
                       @RequestParam("surname") String surname,
                       @RequestParam("email") String email) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userDetails.getUser();
        user.setName(name);
        user.setSurname(surname);
        user.setEmail(email);
        userService.update(mapper.map(user, UserDto.class));
        return "redirect:/customer/profile";
    }

    @PostMapping("/profile/password")
    public String password(
            @RequestParam("newPassword") String password,
            @RequestParam("confirmPassword") String confirmPassword,
            @RequestParam("currentPassword") String currentPassword) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userDetails.getUser();
        if (currentPassword.equals(user.getPassword()) && confirmPassword.equals(password)) {
            user.setPassword(new BCryptPasswordEncoder().encode(password));
            userService.update(mapper.map(user, UserDto.class));
            return "redirect:/customer/profile";
        } else {
            return "redirect:/customer/profile/password";
        }
    }

    @GetMapping("/profile/orders")
    public String orders(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userDetails.getUser();
        List<OrderDto> orders = orderService.getOrdersByUser(user);
        model.addAttribute("orders", orders
                .stream()
                .filter(order -> !order.getStatus().equals(Status.BUCKET))
                .collect(Collectors.toList()));
        return "customer/orders";
    }

}
