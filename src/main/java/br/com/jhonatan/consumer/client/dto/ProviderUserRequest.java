package br.com.jhonatan.consumer.client.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProviderUserRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String document;

    @NotBlank
    private String email;

    @NotBlank
    private String phone;
}
