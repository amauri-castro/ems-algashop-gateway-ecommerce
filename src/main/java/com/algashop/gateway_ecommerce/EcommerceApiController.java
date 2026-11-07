package com.algashop.gateway_ecommerce;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.service.annotation.GetExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/ecommerce/home")
public class EcommerceApiController {

    private final WebClient webClient;

    public EcommerceApiController(WebClient webClient) {
        this.webClient = webClient;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_products:read') and hasAuthority('SCOPE_categories:read')")
    public Mono<Map<String, Object>> getHome() {
        Mono<Map> productList = webClient.get().uri("lb://product-catalog/api/v1/products?hasDiscount=true")
                .retrieve()
                .bodyToMono(Map.class);

        Mono<Map> categoriesList = webClient.get().uri("lb://product-catalog/api/v1/categories")
                .retrieve()
                .bodyToMono(Map.class);

        return Mono.zip(productList, categoriesList)
                .map(tuple -> Map.of(
                        "highlights", tuple.getT1().get("content"),
                        "categories", tuple.getT2().get("content")
                ));
    }

}
