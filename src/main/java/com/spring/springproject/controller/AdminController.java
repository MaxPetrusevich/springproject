package com.spring.springproject.controller;

import com.spring.springproject.dto.*;
import com.spring.springproject.entities.*;
import com.spring.springproject.mapper.EntityMapper;
import com.spring.springproject.service.BidService;
import com.spring.springproject.service.DocumentService;
import com.spring.springproject.service.impl.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final CitizenService citizenService;
    private final BidService bidService;
    private final CategoryService categoryService;
    private final DocumentService documentService;
    private final PaymentService paymentService;
    private final PaymentStatusService paymentStatusService;
    private final GovServiceService govServiceService;
    private final BidStatusService bidStatusService;
    private final RoleService roleService;
    private final EntityMapper mapper;
    private final EstablishmentService establishmentService;

    // Dashboard
    @GetMapping("")
    public String dashboard(Model model) {
        model.addAttribute("title", "Панель управления");
        model.addAttribute("usersCount", userService.count());
        model.addAttribute("bidsCount", bidService.countActive());
        model.addAttribute("servicesCount", govServiceService.count());
        model.addAttribute("documentsCount", documentService.count());
        model.addAttribute("recentBids", bidService.findRecent(5));
        return "admin/dashboard";
    }

    // Users management
    @GetMapping("/users")
    public ModelAndView listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String identifyNumber,
            @RequestParam(required = false) Long roleId
    ) {
        ModelAndView mav = new ModelAndView("admin/users");
        Page<User> users = userService.findAll(
                PageRequest.of(page, size),
                identifyNumber,
                roleId
        );
        mav.addObject("users", users);
        mav.addObject("roles", roleService.findAll());
        return mav;
    }

    @GetMapping("/users/create")
    public ModelAndView createUserForm() {
        ModelAndView mav = new ModelAndView("admin/user-edit");
        mav.addObject("user", new UserRequestDto());
        mav.addObject("roles", roleService.findAll());
        return mav;
    }

    @GetMapping("/users/{id}/edit")
    public ModelAndView editUserForm(@PathVariable Long id) {
        ModelAndView mav = new ModelAndView("admin/user-edit");
        User user = userService.findById(id);
        mav.addObject("user", mapper.toUserDto(user));
        mav.addObject("roles", roleService.findAll());
        return mav;
    }

    // Roles management
    @GetMapping("/roles")
    public ModelAndView listRoles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String roleName
    ) {
        ModelAndView mav = new ModelAndView("admin/roles");
        Page<Role> roles = roleService.findAll(
                PageRequest.of(page, size),
                roleName
        );
        mav.addObject("roles", roles);
        return mav;
    }


    // Citizens management
    @GetMapping("/citizens")
    public ModelAndView listCitizens(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String identifyNumber
    ) {
        ModelAndView mav = new ModelAndView("admin/citizen");
        Page<Citizen> citizens = citizenService.findAll(
                PageRequest.of(page, size),
                lastName,
                firstName,
                identifyNumber
        );
        mav.addObject("citizens", citizens);
        return mav;
    }

    @GetMapping("/citizens/create")
    public ModelAndView createCitizenForm() {
        ModelAndView mav = new ModelAndView("admin/citizen-edit");
        mav.addObject("citizen", new CitizenRequestDto());
        return mav;
    }

    @GetMapping("/citizens/{id}/edit")
    public ModelAndView editCitizenForm(@PathVariable Long id) {
        ModelAndView mav = new ModelAndView("admin/citizen-edit");
        Citizen citizen = citizenService.findById(id);
        mav.addObject("citizen", mapper.toCitizenDto(citizen));
        return mav;
    }

    // Bids management
    @GetMapping("/bids")
    public String listBids(Model model,
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "10") int size,
                           @RequestParam(required = false) Long citizenId,
                           @RequestParam(required = false) Long serviceId,
                           @RequestParam(required = false) Long statusId) {

        Page<Bid> bids = bidService.findAll(PageRequest.of(page, size), citizenId, serviceId, statusId);

        // Подсчет статистики
        long inProgressCount = bids.getContent().stream()
                .filter(bid -> bid.getStatus() != null && "В ОБРАБОТКЕ".equals(bid.getStatus().getStatus()))
                .count();

        long completedCount = bids.getContent().stream()
                .filter(bid -> bid.getStatus() != null && "ВЫПОЛНЕНА".equals(bid.getStatus().getStatus()))
                .count();

        model.addAttribute("title", "Заявки");
        model.addAttribute("bids", bids);
        model.addAttribute("inProgressCount", inProgressCount);
        model.addAttribute("completedCount", completedCount);
        model.addAttribute("statuses", bidStatusService.findAll());
        model.addAttribute("citizens", citizenService.findAll());
        model.addAttribute("services", govServiceService.findAll());

        return "admin/bids";
    }

    // Categories management
    @GetMapping("/categories")
    public ModelAndView listCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String category
    ) {
        ModelAndView mav = new ModelAndView("admin/categories");
        Page<Category> categories = categoryService.findAll(
                PageRequest.of(page, size),
                category
        );
        mav.addObject("categories", categories);
        return mav;
    }

    // Payments management
    @GetMapping("/payments")
    public ModelAndView listPayments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long bidId,
            @RequestParam(required = false) Long statusId
    ) {
        ModelAndView mav = new ModelAndView("admin/payments");
        Page<Payment> payments = paymentService.findAll(
                PageRequest.of(page, size),
                bidId,
                statusId
        );
        mav.addObject("payments", payments);
        mav.addObject("paymentStatuses", paymentStatusService.findAll());
        return mav;
    }

    @GetMapping("/payments/create")
    public ModelAndView createPaymentForm() {
        ModelAndView mav = new ModelAndView("admin/payment-edit");
        mav.addObject("payment", new PaymentRequestDto());
        mav.addObject("bids", bidService.findAll());
        mav.addObject("paymentStatuses", paymentStatusService.findAll());
        return mav;
    }

    @GetMapping("/payments/{id}/edit")
    public ModelAndView editPaymentForm(@PathVariable Long id) {
        ModelAndView mav = new ModelAndView("admin/payment-edit");
        Payment payment = paymentService.findById(id).orElseThrow();
        mav.addObject("payment", mapper.toPaymentDto(payment));
        mav.addObject("bids", bidService.findAll());
        mav.addObject("paymentStatuses", paymentStatusService.findAll());
        return mav;
    }

    // POST endpoints
    @PostMapping("/users")
    public String createUser(@Valid @ModelAttribute UserRequestDto userDto) {
        User user = mapper.toUser(userDto);
        user.setRole(roleService.findById(userDto.getRoleId()));
        user.setRole(roleService.findById(userDto.getRoleId()));
        userService.save(user);
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}")
    public String updateUser(@PathVariable Long id, @Valid @ModelAttribute UserRequestDto userDto) {
        User user = mapper.toUser(userDto);
        user.setId(id);
        user.setRole(roleService.findById(userDto.getRoleId()));
        userService.update(user);
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/reset-password")
    public String resetUserPassword(@PathVariable Long id, @RequestParam String newPassword) {
        userService.resetPassword(id, newPassword);
        return "redirect:/admin/users";
    }

    // Citizens
    @PostMapping("/citizens")
    public String createCitizen(@Valid @ModelAttribute CitizenRequestDto citizenDto) {
        Citizen citizen = mapper.toCitizen(citizenDto);
        citizenService.save(citizen);
        return "redirect:/admin/citizens";
    }

    @PostMapping("/citizens/{id}")
    public String updateCitizen(@PathVariable Long id, @Valid @ModelAttribute CitizenRequestDto citizenDto) {
        Citizen citizen = mapper.toCitizen(citizenDto);
        citizen.setId(id);
        citizenService.update(citizen);
        return "redirect:/admin/citizens";
    }

    @DeleteMapping("/citizens/{id}")
    public String deleteCitizen(@PathVariable Long id) {
        citizenService.delete(id);
        return "redirect:/admin/citizens";
    }

    // Bids
    @PostMapping("/bids")
    public String createBid(@Valid @ModelAttribute BidRequestDto bidDto) {
        Bid bid = mapper.toBid(bidDto);
        bidService.save(bid);
        return "redirect:/admin/bids";
    }

    @PostMapping("/bids/{id}")
    public String updateBid(@PathVariable Long id, @Valid @ModelAttribute BidRequestDto bidDto) {
        Bid bid = mapper.toBid(bidDto);
        bidService.update(id, bid);
        return "redirect:/admin/bids";
    }

    @PostMapping("/bids/{id}/delete")
    public String deleteBid(@PathVariable Long id) {
        bidService.delete(id);
        return "redirect:/admin/bids";
    }

    // Categories
    @PostMapping("/categories")
    public String createCategory(@Valid @ModelAttribute CategoryRequestDto categoryDto) {
        Category category = mapper.toCategory(categoryDto);
        categoryService.save(category.getCategory());
        return "redirect:/admin/categories";
    }

    @PostMapping("/categories/{id}")
    public String updateCategory(@PathVariable Long id, @Valid @ModelAttribute CategoryRequestDto categoryDto) {
        Category category = mapper.toCategory(categoryDto);
        categoryService.update(id, category.getCategory());
        return "redirect:/admin/categories";
    }

    @PostMapping("/categories/{id}/delete")
    public String deleteCategory(@PathVariable Long id) {
        categoryService.delete(id);
        return "redirect:/admin/categories";
    }

    // Payments
    @PostMapping("/payments")
    public String createPayment(@Valid @ModelAttribute PaymentRequestDto paymentDto) {
        Payment payment = mapper.toPayment(paymentDto);
        paymentService.save(payment);
        return "redirect:/admin/payments";
    }

    @PostMapping("/payments/{id}")
    public String updatePayment(@PathVariable Long id, @Valid @ModelAttribute PaymentRequestDto paymentDto) {
        Payment payment = mapper.toPayment(paymentDto);
        paymentService.update(id, payment);
        return "redirect:/admin/payments";
    }

    @DeleteMapping("/payments/{id}")
    public String deletePayment(@PathVariable Long id) {
        paymentService.delete(id);
        return "redirect:/admin/payments";
    }

    // Roles
    @PostMapping("/roles")
    public String createRole(@Valid @ModelAttribute Role role) {
        roleService.create(role);
        return "redirect:/admin/roles";
    }

    @PostMapping("/roles/{id}")
    public String updateRole(@PathVariable Long id, @Valid @ModelAttribute Role role) {
        roleService.update(id, role);
        return "redirect:/admin/roles";
    }

    @DeleteMapping("/roles/{id}")
    public String deleteRole(@PathVariable Long id) {
        roleService.delete(id);
        return "redirect:/admin/roles";
    }

    // Обработка ошибок


    @ExceptionHandler(Exception.class)
    public ModelAndView handleError(Exception ex, HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("message", ex.getMessage()); // Сообщение об ошибке
        mav.addObject("status", 500); // Статус ошибки
        mav.addObject("path", request.getRequestURI()); // URL, на котором произошла ошибка
        mav.addObject("timestamp", LocalDateTime.now()); // Текущее время
        return mav;
    }


    @GetMapping("/services")
    public String listServices(Model model,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size,
                               @RequestParam(required = false) Long categoryId,
                               @RequestParam(required = false) Long establishmentId) {

        Page<GovService> services = govServiceService.findAll(
                PageRequest.of(page, size),
                categoryId,
                establishmentId
        );
        model.addAttribute("services", services);
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("establishments", establishmentService.findAll());

        return "admin/services";
    }

    @GetMapping("/services/create")
    public String createServiceForm(Model model) {
        model.addAttribute("service", new GovService());
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("establishments", establishmentService.findAll());
        return "admin/services-edit";
    }

    @GetMapping("/services/{id}/edit")
    public String editServiceForm(@PathVariable Long id, Model model) {
        GovService service = govServiceService.findById(id);
        if (service == null) {
            return "redirect:/admin/services";
        }
        model.addAttribute("service", service);
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("establishments", establishmentService.findAll());
        return "admin/services-edit";
    }

    @PostMapping("/services")
    public String createService(@ModelAttribute GovService service) {
        govServiceService.save(service);
        return "redirect:/admin/services";
    }

    @PostMapping("/services/{id}")
    public String updateService(@PathVariable Long id, @ModelAttribute GovService service) {
        service.setId(id);
        govServiceService.update(service);
        return "redirect:/admin/services";
    }

    @PostMapping("/services/{id}/delete")
    public String deleteService(@PathVariable Long id) {
        govServiceService.delete(id);
        return "redirect:/admin/services";
    }

    // Establishments management
    @GetMapping("/establishments")
    public String listEstablishments(Model model,
                                     @RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size,
                                     @RequestParam(required = false) String name) {

        Page<Establishment> establishments = establishmentService.findAll(
                PageRequest.of(page, size),
                name
        );

        model.addAttribute("establishments", establishments);
        return "admin/establishment";
    }

    @GetMapping("/establishments/create")
    public String createEstablishmentForm(Model model) {
        model.addAttribute("establishment", new Establishment());
        return "admin/establishment-edit";
    }

    @GetMapping("/establishments/{id}/edit")
    public String editEstablishmentForm(@PathVariable Long id, Model model) {
        Establishment establishment = establishmentService.findById(id);
        model.addAttribute("establishment", establishment);
        return "admin/establishment-edit";
    }

    @PostMapping("/establishments")
    public String createEstablishment(@Valid @ModelAttribute Establishment establishment) {
        establishmentService.save(establishment);
        return "redirect:/admin/establishments";
    }

    @PostMapping("/establishments/{id}")
    public String updateEstablishment(@PathVariable Long id, @Valid @ModelAttribute Establishment establishment) {
        establishment.setId(id);
        establishmentService.update(establishment);
        return "redirect:/admin/establishments";
    }

    @PostMapping("/establishments/{id}/delete")
    public String deleteEstablishment(@PathVariable Long id) {
        establishmentService.delete(id);
        return "redirect:/admin/establishments";
    }


}