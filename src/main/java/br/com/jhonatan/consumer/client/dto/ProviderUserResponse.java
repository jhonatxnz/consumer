package br.com.jhonatan.consumer.client.dto;

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
public class ProviderUserResponse {

    private String username;

    private String document;

    private String name;

    private String email;

    private String phone;
}
