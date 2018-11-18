package be.tim.codingtim;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class ApiCaller {

    private WebClient webClient;

    public ApiCaller(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<SimpleResponseDto> callApi() {
        return webClient.put()
                .uri("/api/resource")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "customAuth")
                .syncBody(new SimpleRequestDto())
                .retrieve()
                .bodyToMono(SimpleResponseDto.class);
    }
}
