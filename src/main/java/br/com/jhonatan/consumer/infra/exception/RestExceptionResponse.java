package br.com.jhonatan.consumer.infra.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RestExceptionResponse {
    @JsonProperty("status")
    @Valid
    private String status;

    @JsonProperty("message")
    private String message;

    @JsonProperty("statusCode")
    private HttpStatus statusCode;
}
