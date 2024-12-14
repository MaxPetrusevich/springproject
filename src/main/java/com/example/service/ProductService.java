package com.example.service;

import com.example.dto.ProductDto;
import com.example.entity.Organisation;
import com.example.entity.Product;
import com.example.entity.SubscriptionPlan;
import com.example.mapper.EntityMapper;
import com.example.repository.OrganisationRepository;
import com.example.repository.ProductRepository;
import com.example.repository.SubscriptionPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProductService {
    
    private final ProductRepository productRepository;
    private final OrganisationRepository organisationRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final EntityMapper entityMapper;

    @Transactional(readOnly = true)
    public List<Product> getFeaturedProducts() {
        return productRepository.findTop6ByOrderByCreatedAtDesc();
    }
    
    @Transactional(readOnly = true)
    public Page<Product> findProducts(String category, String search, Pageable pageable) {
        if (category != null && search != null) {
            return productRepository.findByCategoryAndNameContaining(category, search, pageable);
        } else if (category != null) {
            return productRepository.findByCategory(category, pageable);
        } else if (search != null) {
            return productRepository.findByNameContaining(search, pageable);
        }
        return productRepository.findAll(pageable);
    }
    
    @Transactional(readOnly = true)
    public Set<String> getAllCategories() {
        return productRepository.findAllCategories();
    }
    

    @Transactional(readOnly = true)
    public Product getProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }
    
    @Transactional
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }
    
    @Transactional
    public Product updateProduct(Long id, Product product) {
        Product existingProduct = getProduct(id);
        existingProduct.setName(product.getName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setCategory(product.getCategory());
        return productRepository.save(existingProduct);
    }
    
    @Transactional
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
    
    @Transactional
    public Product createProduct(ProductDto productDto) {
        Product product = new Product();
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setCategory(productDto.getCategory());
        
        Organisation organisation = organisationRepository.findById(productDto.getOrganisationId())
                .orElseThrow(() -> new RuntimeException("Organisation not found"));
        product.setOrganisation(organisation);
        
        return productRepository.save(product);
    }
    
    @Transactional
    public Product updateProduct(Long id, ProductDto productDto) {
        Product existingProduct = getProduct(id);
        existingProduct.setName(productDto.getName());
        existingProduct.setDescription(productDto.getDescription());
        existingProduct.setCategory(productDto.getCategory());
        return productRepository.save(existingProduct);
    }
    
    @Transactional(readOnly = true)
    public List<Product> findByOrganisationId(Long organisationId) {
        return productRepository.findByOrganisationId(organisationId);
    }
    
    @Transactional(readOnly = true)
    public Page<Product> findAll(String search, String category, Boolean active, Long organisationId, Pageable pageable) {
        if (search != null && !search.isEmpty()) {
            if (category != null && active != null && organisationId != null) {
                return productRepository.findByNameContainingAndCategoryAndActiveAndOrganisationId(
                    search, category, active, organisationId, pageable);
            } else if (category != null && active != null) {
                return productRepository.findByNameContainingAndCategoryAndActive(
                    search, category, active, pageable);
            } else if (category != null && organisationId != null) {
                return productRepository.findByNameContainingAndCategoryAndOrganisationId(
                    search, category, organisationId, pageable);
            } else if (active != null && organisationId != null) {
                return productRepository.findByNameContainingAndActiveAndOrganisationId(
                    search, active, organisationId, pageable);
            } else if (category != null) {
                return productRepository.findByNameContainingAndCategory(search, category, pageable);
            } else if (active != null) {
                return productRepository.findByNameContainingAndActive(search, active, pageable);
            } else if (organisationId != null) {
                return productRepository.findByNameContainingAndOrganisationId(search, organisationId, pageable);
            } else {
                return productRepository.findByNameContaining(search, pageable);
            }
        } else {
            if (category != null && active != null && organisationId != null) {
                return productRepository.findByCategoryAndActiveAndOrganisationId(
                    category, active, organisationId, pageable);
            } else if (category != null && active != null) {
                return productRepository.findByCategoryAndActive(category, active, pageable);
            } else if (category != null && organisationId != null) {
                return productRepository.findByCategoryAndOrganisationId(category, organisationId, pageable);
            } else if (active != null && organisationId != null) {
                return productRepository.findByActiveAndOrganisationId(active, organisationId, pageable);
            } else if (category != null) {
                return productRepository.findByCategory(category, pageable);
            } else if (active != null) {
                return productRepository.findByActive(active, pageable);
            } else if (organisationId != null) {
                return productRepository.findByOrganisationId(organisationId, pageable);
            } else {
                return productRepository.findAll(pageable);
            }
        }
    }
    
    @Transactional
    public void toggleStatus(Long id) {
        Product product = findById(id);
        product.setActive(!product.isActive());
        productRepository.save(product);
    }
    
    @Transactional(readOnly = true)
    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }
    
    @Transactional(readOnly = true)
    public List<SubscriptionPlan> getSubscriptionPlans(Long productId) {
        return subscriptionPlanRepository.findByProductId(productId);
    }
    
    @Transactional
    public Product create(ProductDto productDto) {
        Product product = new Product();
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setCategory(productDto.getCategory());
        product.setActive(true);
        
        Organisation organisation = organisationRepository.findById(productDto.getOrganisationId())
                .orElseThrow(() -> new RuntimeException("Organisation not found"));
        product.setOrganisation(organisation);
        
        return productRepository.save(product);
    }
    
    @Transactional
    public Product update(Long id, ProductDto productDto) {
        Product existingProduct = findById(id);
        existingProduct.setName(productDto.getName());
        existingProduct.setDescription(productDto.getDescription());
        existingProduct.setCategory(productDto.getCategory());
        return productRepository.save(existingProduct);
    }
    
    @Transactional(readOnly = true)
    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Page<Product> findByOwnerId(Long userId, String search, Long organisationId, Pageable pageable) {
        if (search != null && !search.isEmpty() && organisationId != null) {
            return productRepository.findByNameContainingAndOrganisationIdAndOrganisationOwnerId(
                search, organisationId, userId, pageable);
        } else if (search != null && !search.isEmpty()) {
            return productRepository.findByNameContainingAndOrganisationOwnerId(search, userId, pageable);
        } else if (organisationId != null) {
            return productRepository.findByOrganisationIdAndOrganisationOwnerId(organisationId, userId, pageable);
        }
        return productRepository.findByOrganisationOwnerId(userId, pageable);
    }
    
    @Transactional(readOnly = true)
    public List<Product> findByOrganiserId(Long userId) {
        return productRepository.findByOrganisationOwnerId(userId);
    }
    
    @Transactional(readOnly = true)
    public long countByOrganisationId(Long organisationId) {
        return productRepository.countByOrganisationId(organisationId);
    }
    
    @Transactional(readOnly = true)
    public long countActiveByOrganisationId(Long organisationId) {
        return productRepository.countByOrganisationIdAndActive(organisationId, true);
    }
    @Transactional(readOnly = true)
    public Page<Product> findAllAvailableProducts(String search, String sort, Pageable pageable) {
        Page<Product> products;
        
        if (search != null && !search.isEmpty()) {
            products = productRepository.findByNameContainingIgnoreCaseAndActiveTrue(search, pageable);
        } else {
            if (sort != null) {
                Sort sorting = switch (sort) {
                    case "name" -> Sort.by("name").ascending();
                    case "price" -> Sort.by("subscriptionPlans.price").ascending();
                    default -> Sort.by("id").ascending();
                };
                pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sorting);
            }
            products = productRepository.findByActiveTrue(pageable);
        }
        
        return products;
    }
        @Transactional(readOnly = true)
    public Page<ProductDto> findAllAvailable(String search, String sort, Pageable pageable) {
        Page<Product> products;
        
        if (search != null && !search.isEmpty()) {
            products = productRepository.findByNameContainingIgnoreCaseAndActive(search, true, pageable);
        } else {
            products = productRepository.findByActive(true, pageable);
        }
        
        if (sort != null) {
            switch (sort) {
                case "name":
                    pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), 
                        Sort.by("name").ascending());
                    break;
                case "price":
                    pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), 
                        Sort.by("plans.price").ascending());
                    break;
            }
        }
        
        return products.map(product -> {
            ProductDto dto = entityMapper.toDto(product);
            dto.setActiveSubscriptionsCount(subscriptionPlanRepository.countByProduct_IdAndActive(product.getId(), true));
            dto.setOrganisationName(product.getOrganisation().getName());
            return dto;
        });
    }
} 