package com.spring.springproject.service.interfaces;

import com.spring.springproject.dto.TechniqueDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;


public interface TechniqueService extends Service<TechniqueDto> {
    void update(Integer producerId, Integer modelId, Integer categoryId,
                Double price, Integer[] storeIdes, Integer id);

    TechniqueDto save(Integer producerId, Integer modelId, Integer categoryId,
                      Double price, Integer[] storeIdes);

    Page<TechniqueDto> findAll(Pageable pageable, Double startPrice, Double endPrice, String categoryName,
                               String producerName, String modelName);

    public void saveDataToJsonFile();

    void importDataFromJson(MultipartFile file) throws IOException;
    Optional<TechniqueDto> updateImage(Integer id, MultipartFile image);
    Optional<byte[]> findAvatar(Integer id);
}
