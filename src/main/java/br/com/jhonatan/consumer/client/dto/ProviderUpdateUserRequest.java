package br.com.jhonatan.consumer.client.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProviderUpdateUserRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String document;

    @NotBlank
    private String email;

    @NotBlank
    private String phone;
}