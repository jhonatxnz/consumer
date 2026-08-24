package br.com.jhonatan.consumer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Body used by the activate, reactivate and update-contact endpoints.
 * username/subscription come from the path already, so the body only carries
 * the contact fields the original spec listed alongside them (email, phone).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactRequest {

    private String email;
    private String phone;
}
