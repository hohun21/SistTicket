package org.doit.sistticket.category.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ImageUploadDTO {
    private String fileName;
    private String uuid;
    private String folderPath;
}