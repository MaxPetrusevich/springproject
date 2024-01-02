package com.spring.springproject.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.springproject.dto.TechniqueDto;
import com.spring.springproject.dto.UserDto;
import com.spring.springproject.entities.*;
import com.spring.springproject.repositories.OrderTechniqueRepository;
import com.spring.springproject.repositories.TechniqueRepository;
import com.spring.springproject.repositories.specifications.TechniqueSpecifications;
import com.spring.springproject.service.interfaces.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TechniqueServiceImpl implements TechniqueService {

    private final ModelMapper modelMapper;
    private final TechniqueRepository repository;
    private final CategoryService categoryService;
    private final StoreService storeService;
    private final ModelService modelService;
    private final ProducerService producerService;
    private final ImageServiceImpl imageService;
    private final OrderTechniqueRepository orderTechniqueRepository;


    @Override
    public Set<TechniqueDto> findAll() {
        return repository.findAll()
                .stream()
                .map(technique -> modelMapper.map(technique, TechniqueDto.class))
                .collect(Collectors.toSet());
    }

    @Override
    public TechniqueDto findById(Integer id) {
        return repository.findById(id)
                .stream()
                .map(technique -> modelMapper.map(technique, TechniqueDto.class))
                .findFirst()
                .orElse(null);
    }

    @Override
    @Transactional
    public TechniqueDto save(TechniqueDto object) {
        Technique technique = modelMapper.map(object, Technique.class);
        repository.saveTechnique(technique);
        return object;
    }

    @Override
    @Transactional
    public void update(TechniqueDto object) {
        repository.update(modelMapper.map(object, Technique.class));
    }

    @Override
    public void delete(Integer id) {
        Technique technique = repository.findById(id).orElse(null);
        if (technique != null) {
            technique.getOrderTechniques().forEach(
                    orderTechnique -> {
                        orderTechniqueRepository.delete(orderTechnique);
                    }
            );
        }
        repository.deleteById(id);
    }


    @Override
    @Transactional
    public void update(Integer producerId, Integer modelId, Integer categoryId, Double price, Integer[] storeIdes, Integer id) {
        TechniqueDto techniqueDto = findById(id);
        if (techniqueDto != null) {
            setParams(producerId, modelId, categoryId, price, storeIdes, techniqueDto);
        }
        Technique technique = modelMapper.map(techniqueDto, Technique.class);
        repository.update(technique);
        technique.getStoreList().forEach(store -> {
                    repository.saveStoreTech(store.getId(), technique.getId());
                }
        );
    }

    @Override
    @Transactional
    public TechniqueDto save(Integer producerId, Integer modelId, Integer categoryId, Double price, Integer[] storeIdes) {
        TechniqueDto techniqueDto = new TechniqueDto();
        techniqueDto.setStoreList(new HashSet<>());
        setParams(producerId, modelId, categoryId, price, storeIdes, techniqueDto);
        Technique technique = modelMapper.map(techniqueDto, Technique.class);
        repository.saveTechnique(technique);
        technique.getStoreList().forEach(store -> {
                    repository.saveStoreTech(store.getId(), technique.getId());
                }
        );

        return techniqueDto;
    }

    @Override
    public Page<TechniqueDto> findAll(Pageable pageable, Double startPrice, Double endPrice, String categoryName,
                                      String producerName, String modelName) {
        startPrice = startPrice == null ? 0 : startPrice;
        endPrice = endPrice == null ? Double.MAX_VALUE : endPrice;
        Page<Technique> techniquesPage =
                repository.findAll(TechniqueSpecifications.filterByCriteria(startPrice, endPrice, producerName, modelName, categoryName), pageable);
        List<TechniqueDto> techniqueDtoList = techniquesPage
                .stream()
                .map(technique -> modelMapper.map(technique, TechniqueDto.class))
                .toList();
        return new PageImpl<>(techniqueDtoList, pageable, techniquesPage.getTotalElements());
    }

    @Override
    public void saveDataToJsonFile() {
        String filePath = "data.json";
        List<Technique> entities = repository.findAll();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.writeValue(new File(filePath), entities);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void importDataFromJson(MultipartFile file) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Technique[] entities = objectMapper.readValue(file.getBytes(), Technique[].class);
        List<Technique> entityList = Arrays.asList(entities);
        entityList.stream().forEach(technique -> repository.saveTechnique(technique));
    }


    private void setParams(Integer producerId, Integer modelId, Integer categoryId, Double price, Integer[] storeIdes, TechniqueDto techniqueDto) {
        techniqueDto.setProducer(producerService.findById(producerId));
        techniqueDto.setModel(modelService.findById(modelId));
        techniqueDto.setCategory(categoryService.findById(categoryId));
        techniqueDto.setPrice(price);
        techniqueDto.getStoreList().removeAll(techniqueDto.getStoreList());
        if (storeIdes != null) {
            Arrays.stream(storeIdes).forEach(storeId -> techniqueDto.getStoreList().add(storeService.findById(storeId)));
        }
    }

    @SneakyThrows
    private void uploadImage(MultipartFile image) {
        if (image.isEmpty()) {
            imageService.upload(image.getOriginalFilename(), image.getInputStream());
        }
    }

    @Transactional
    @Override
    public Optional<TechniqueDto> updateImage(Integer id, MultipartFile image) {
        return repository.findById(id)
                .map(entity -> {
                    uploadImage(image);
                    Optional.ofNullable(image)
                            .filter(Predicate.not(MultipartFile::isEmpty))
                            .ifPresent(avatar -> entity.setImage("\\tech\\" + avatar.getOriginalFilename()));
                    return entity;
                })
                .map(repository::saveAndFlush)
                .map(technique -> modelMapper.map(technique, TechniqueDto.class));
    }

    @Override
    public Optional<byte[]> findAvatar(Integer id) {
        return repository.findById(id)
                .map(Technique::getImage)
                .filter(StringUtils::hasText)
                .flatMap(imageService::get);

    }
}
