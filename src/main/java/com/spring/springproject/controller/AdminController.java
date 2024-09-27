package com.spring.springproject.controller;

import com.spring.springproject.dto.OrderDto;
import com.spring.springproject.dto.UserDto;
import com.spring.springproject.entities.Order;
import com.spring.springproject.entities.Status;
import com.spring.springproject.service.impl.OrderServiceImpl;
import com.spring.springproject.service.impl.OrderTechniqueServiceImpl;
import com.spring.springproject.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.spring.springproject.controller.Constants.*;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;
    private final OrderServiceImpl orderService;
    private final ModelMapper mapper;
    private final OrderTechniqueServiceImpl orderTechniqueService;

    @GetMapping("/users")
    public String users(Model model, @RequestParam(defaultValue = "1", required = false) int page,
                        @RequestParam(defaultValue = "3", required = false) int size,
                        @RequestParam(name = "name", required = false) String name,
                        @RequestParam(name = "email", required = false) String email,
                        @RequestParam(name = "surname", required = false) String surname,
                        @RequestParam(name = "username", required = false) String username) {
        Pageable pageable = Pageable.ofSize(size);
        pageable = pageable.withPage(page - 1);
        Page<UserDto> userDtoPage = userService.findAll(pageable, username, name, surname, email);
        model.addAttribute(PAGE, page);
        model.addAttribute(SIZE, size);
        model.addAttribute("name", name);
        model.addAttribute("username", username);
        model.addAttribute("surname", surname);
        model.addAttribute("email", email);
        model.addAttribute(TOTAL_PAGE, userDtoPage.getTotalPages());
        model.addAttribute(LIST, userDtoPage.getContent());
        return "admin/users";
    }

    @GetMapping("/edit-user/{userId}")
    public String showEditUserForm(@PathVariable Integer userId, Model model) {
        UserDto userDto = userService.findById(userId);
        model.addAttribute("userDto", userDto);
        return "admin/editUser";
    }

    @PostMapping("/edit-user/{userId}")
    public String editUser(@PathVariable Integer userId, UserDto updatedUserDto) {
        updatedUserDto.setId(userId);
        userService.update(updatedUserDto);
        return "redirect:/admin/users"; // Redirect to user list or another appropriate page
    }

    @PostMapping("/delete-user/{userId}")
    public String deleteUser(@PathVariable Integer userId) {
        userService.delete(userId);
        return "redirect:/admin/users";
    }

    @GetMapping("/add-user")
    public String showAddUserForm() {
        // Можете добавить атрибуты модели, если это необходимо
        return "admin/addUser"; // Предполагается, что ваш шаблон Thymeleaf назван "add-user.html"
    }

    @PostMapping("/add-user")
    public String addUser(@RequestParam("username") String username,
                          @RequestParam("password") String password,
                          @RequestParam("email") String email,
                          @RequestParam("name") String name,
                          @RequestParam("surname") String surname) {
        UserDto userDto = UserDto.builder()
                .name(name)
                .surname(surname)
                .email(email)
                .username(username)
                .password(password)
                .build();
        // Обработка userDto и добавление нового пользователя в базу данных
        // Также можно добавить логику валидации
        userService.save(userDto);
        // Перенаправление на страницу со списком пользователей или любую другую подходящую страницу
        return "redirect:/admin/users";
    }

    @GetMapping("/users/orders")
    public String toUserOrders(@RequestParam(name = "userId", required = false) Integer userId,
                               @RequestParam(defaultValue = "1", required = false) int page,
                               @RequestParam(defaultValue = "3", required = false) int size,
                               @RequestParam(name = "minAmount", required = false)Double minAmount,
                               @RequestParam(name = "maxAmount", required = false)Double maxAmount,
                               @RequestParam(name = "status", required = false) String statusString,
                               @RequestParam(name = "startDate", required = false) Date startDate,
                               @RequestParam(name = "endDate", required = false) Date endDate,
                               Model model) {
        Status status = statusString!= null? Status.valueOf(statusString):null;
        UserDto user = userService.findById(userId);
        Pageable pageable = Pageable.ofSize(size);
        pageable = pageable.withPage(page - 1);
        Page<OrderDto> orderDtoPage = orderService.findAll(pageable, user, minAmount, maxAmount, startDate, endDate, status);
        model.addAttribute("userId" , userId);
        model.addAttribute(PAGE, page);
        model.addAttribute(SIZE, size);
        model.addAttribute(TOTAL_PAGE, orderDtoPage.getTotalPages());
        model.addAttribute("orders", orderDtoPage.getContent());
        return "admin/orders";
    }
    @GetMapping("/users/orders/change")
    public String toChangeOrderStatus(@RequestParam("orderId") Integer orderId, Model model){
        OrderDto orderDto = orderService.getOrderById(orderId);
        model.addAttribute("statusValues", Status.values());
        model.addAttribute("orderDto", orderDto);
        return "admin/changeOrderStatus";
    }

    @PostMapping("/users/orders/change")
    public String changeOrderStatus(@RequestParam Integer orderId, @RequestParam Status newStatus, Model model){
        OrderDto order = orderService.getOrderById(orderId);

        // Update the order status
        order.setStatus(newStatus);

        // Save the updated order
        orderService.updateOrder(order);
        return "redirect:/admin/users/";
    }
}
