package com.spring.springproject.controller;

import com.spring.springproject.dto.BidHistoryDto;
import com.spring.springproject.entities.*;
import com.spring.springproject.exception.FileStorageException;
import com.spring.springproject.service.BidService;
import com.spring.springproject.service.DocumentService;
import com.spring.springproject.service.impl.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {
    private final GovServiceService govServiceService;
    private final CategoryService categoryService;
    private final EstablishmentService establishmentService;
    private final BidService bidService;
    private final PaymentService paymentService;
    private final UserService userService;
    private final CitizenService citizenService;
    private final BidStatusService bidStatusService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final DocumentService documentService;
    private final TypeServiceImpl typeService;

    @GetMapping("/services")
    public String services(Model model,
                           @RequestParam(required = false) Long categoryId,
                           @RequestParam(required = false) Long establishmentId,
                           @RequestParam(required = false) String search,
                           @RequestParam(defaultValue = "name,asc") String sort,
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "9") int size) {

        String[] sortParams = sort.split(",");
        Sort.Direction direction = sortParams[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParams[0]));

        // Передача всех параметров в метод findAll
        Page<GovService> services = govServiceService.findAll(pageable, categoryId, establishmentId, search);

        model.addAttribute("services", services);
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("establishments", establishmentService.findAll());

        return "customer/services";
    }


    // Страница подтверждения заказа услуги
    @GetMapping("/services/{id}/order")
    public String orderService(@PathVariable Long id, Model model) {
        GovService service = govServiceService.findById(id);
        if (service == null) {
            return "redirect:/customer/services";
        }

        // Создаем новую заявку
        Bid bid = new Bid();
        bid.setService(service);
        bid.setDate(LocalDate.now());
        bid.setCitizen(getCurrentCitizen());
        bid.setStatus(bidStatusService.findById(1L)); // Статус "Новая"
        bid = bidService.save(bid);
        // Создаем платеж
        Payment payment = new Payment();
        payment.setBid(bid);
        payment.setSum(service.getPrice()); // Здесь должна быть логика расчета стоимости
        payment.setDate(LocalDate.now());

        model.addAttribute("service", service);
        model.addAttribute("bid", bid);
        model.addAttribute("payment", payment);

        return "customer/payment-confirm";
    }

    // Обработка оплаты
    @PostMapping("/payments/process")
    public String processPayment(@RequestParam Long bidId, RedirectAttributes redirectAttributes) {
        Bid bid = bidService.findById(bidId);
        if (bid == null) {
            return "redirect:/customer/services";
        }
        bid.setStatus(bidStatusService.findById(2L)); // Статус "В обработке"
        bidService.save(bid);

        Payment payment = new Payment();
        payment.setBid(bid);
        payment.setSum(bid.getService().getPrice());
        payment.setDate(LocalDate.now());
        paymentService.save(payment);



        redirectAttributes.addFlashAttribute("success", "Оплата успешно выполнена");
        return "redirect:/customer/history";
    }

    // История услуг
    @GetMapping("/history")
    public String history(Model model,
                         @RequestParam(required = false) Long serviceId,
                         @RequestParam(required = false) Long statusId,
                         @RequestParam(required = false) LocalDate date,
                         @RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "date"));
        Citizen citizen = getCurrentCitizen();
        
        // Получаем заявки с учетом всех фильтров
        Page<Bid> bidsPage = bidService.findAll(pageable, citizen.getId(), serviceId, statusId);
        
        // Преобразуем в DTO с историей статусов
        var bids = bidsPage.map(bid -> {
            List<BidHistoryDto.StatusHistoryItem> timeline = new ArrayList<>();
            
            // Добавляем начальный статус
            timeline.add(BidHistoryDto.StatusHistoryItem.builder()
                .status("НОВАЯ")
                .date(bid.getDate().atStartOfDay())
                .comment("Заявка создана")
                .isActive(bid.getStatus().getStatus().equals("НОВАЯ"))
                .build());
            
            // Добавляем статус "В ОБРАБОТКЕ" если он есть
            if (bid.getStatus().getStatus().equals("В ОБРАБОТКЕ") || 
                bid.getStatus().getStatus().equals("ВЫПОЛНЕНА")) {
                timeline.add(BidHistoryDto.StatusHistoryItem.builder()
                    .status("В ОБРАБОТКЕ")
                    .date(bid.getDate().atStartOfDay().plusHours(1)) // Примерное время
                    .comment("Заявка принята в обработку")
                    .isActive(bid.getStatus().getStatus().equals("В ОБРАБОТКЕ"))
                    .build());
            }
            
            // Добавляем статус "ВЫПОЛНЕНА" если он есть
            if (bid.getStatus().getStatus().equals("ВЫПОЛНЕНА")) {
                timeline.add(BidHistoryDto.StatusHistoryItem.builder()
                    .status("ВЫПОЛНЕНА")
                    .date(bid.getDate().atStartOfDay().plusHours(2)) // Примерное время
                    .comment("Заявка выполнена")
                    .isActive(true)
                    .build());
            }
            
            return BidHistoryDto.builder()
                .id(bid.getId())
                .date(bid.getDate())
                .serviceName(bid.getService().getName())
                .establishmentName(bid.getService().getEstablishment().getName())
                .status(bid.getStatus().getStatus())
                .amount(bid.getService().getPrice())
                .statusTimeline(timeline)
                .build();
        });
        
        // Подсчет статистики
        long inProgressCount = bidsPage.stream()
                .filter(bid -> "В ОБРАБОТКЕ".equals(bid.getStatus().getStatus()))
                .count();
        
        long completedCount = bidsPage.stream()
                .filter(bid -> "ВЫПОЛНЕНА".equals(bid.getStatus().getStatus()))
                .count();
        
        model.addAttribute("bids", bids);
        model.addAttribute("services", govServiceService.findAll());
        model.addAttribute("statuses", bidStatusService.findAll());
        model.addAttribute("inProgressCount", inProgressCount);
        model.addAttribute("completedCount", completedCount);
        
        return "customer/history";
    }

    // Профиль пользователя
    @GetMapping("/profile")
    public String profile(Model model) {
        model.addAttribute("user", getCurrentUser());
        model.addAttribute("citizen", getCurrentCitizen());
        return "customer/profile";
    }

    // Обновление профиля
    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute Citizen citizenUpdate, RedirectAttributes redirectAttributes) {
        Citizen citizen = getCurrentCitizen();
        
        citizen.setFirstName(citizenUpdate.getFirstName());
        citizen.setLastName(citizenUpdate.getLastName());
        citizen.setMiddleName(citizenUpdate.getMiddleName());
        citizen.setPhone(citizenUpdate.getPhone());
        citizen.setEmail(citizenUpdate.getEmail());
        citizen.setAddress(citizenUpdate.getAddress());
        
        citizenService.update(citizen);
        
        redirectAttributes.addFlashAttribute("success", "Профиль успешно обновлен");
        return "redirect:/customer/profile";
    }

    // Обновление аватара
    @PostMapping("/profile/avatar")
    public String updateAvatar(@RequestParam("image") MultipartFile image, RedirectAttributes redirectAttributes) {
        User user = getCurrentUser();
        userService.updateImage(user.getId(), image);
        
        redirectAttributes.addFlashAttribute("success", "Аватар успешно обновлен");
        return "redirect:/customer/profile";
    }

    // Смена пароля
    @PostMapping("/profile/password")
    public String updatePassword(@RequestParam String currentPassword,
                               @RequestParam String newPassword,
                               RedirectAttributes redirectAttributes) {
        User user = getCurrentUser();
        
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            redirectAttributes.addFlashAttribute("error", "Неверный текущий пароль");
            return "redirect:/customer/profile";
        }
        
        userService.resetPassword(user.getId(), newPassword);
        
        redirectAttributes.addFlashAttribute("success", "Пароль успешно изменен");
        return "redirect:/customer/profile";
    }

    // Вспомогательные методы
    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String identifyNumber = auth.getName(); // Получаем ИИН пользователя
        return userService.findByIdentifyNumber(identifyNumber); // Находим нашего пользователя по ИИН
    }

    private Citizen getCurrentCitizen() {
        User user = getCurrentUser();
        return citizenService.findByIdentifyNumber(user.getIdentifyNumber());
    }

    @GetMapping("/documents")
    public ModelAndView listDocuments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long typeId,
            @RequestParam(required = false) Long bidId
    ) {
        ModelAndView mav = new ModelAndView("customer/documents");
        
        // Получаем текущего пользователя
        Citizen citizen = getCurrentCitizen();
        
        // Получаем документы только для заявок текущего пользователя
        Page<Document> documents = documentService.findAllByCitizen(
            citizen.getId(),
            PageRequest.of(page, size),
            typeId,
            bidId
        );
        
        mav.addObject("documents", documents);
        mav.addObject("types", typeService.findAll());
        mav.addObject("bids", bidService.findAllByCitizen(citizen.getId()));
        
        return mav;
    }

    @GetMapping("/documents/{id}/download")
    public ResponseEntity<byte[]> downloadDocument(@PathVariable Long id) {
        // Проверяем, что документ принадлежит текущему пользователю
        Document document = documentService.findById(id);
        if (!Objects.equals(document.getBid().getCitizen().getId(), getCurrentCitizen().getId())) {
            throw new AccessDeniedException("У вас нет доступа к этому документу");
        }
        
        try {
            byte[] content = documentService.getFileContent(id);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(document.getMimeType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + document.getOriginalFilename() + "\"")
                    .body(content);
        } catch (IOException e) {
            throw new FileStorageException("Не удалось загрузить файл", e);
        }
    }

    @GetMapping("/bids")
    public String listBids(Model model,
                          @RequestParam(defaultValue = "0") int page,
                          @RequestParam(defaultValue = "10") int size,
                          @RequestParam(required = false) Long status) {
        Pageable pageable = PageRequest.of(page, size);
        Citizen citizen = getCurrentCitizen();
        
        Page<Bid> bids = bidService.findAll(pageable, citizen.getId(), null, status);
        
        model.addAttribute("bids", bids);
        model.addAttribute("statuses", bidStatusService.findAll());
        return "customer/bids";
    }
}
