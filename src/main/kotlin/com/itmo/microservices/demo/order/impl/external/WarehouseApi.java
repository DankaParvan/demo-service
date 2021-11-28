package com.itmo.microservices.demo.order.impl.external;

import com.itmo.microservices.demo.order.api.dto.ItemQuantityRequestDTOJava;
import com.itmo.microservices.demo.order.api.dto.OrderDto;
import com.itmo.microservices.demo.order.api.external.IWarehouseApi;
import com.itmo.microservices.demo.order.impl.entity.BookingAttemptStatus;
import com.itmo.microservices.demo.order.impl.entity.BookingResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.*;
import java.util.stream.Collectors;

public class WarehouseApi implements IWarehouseApi {
    private static final String API_URL_PREFIX = "http://localhost:8080/items";

    @Override
    public BookingResponse bookOrder(OrderDto orderDto) {
        BookingResponse result = new BookingResponse();
        RestTemplate restTemplate = new RestTemplate();
        try {
            var response = restTemplate.postForEntity(API_URL_PREFIX + "/book", prepareRequest(orderDto), BookingHttpResponse.class);
            if (response.getBody() != null && response.getBody().status == 200) {
                result.setStatus(BookingAttemptStatus.SUCCESS);
            } else {
                result.setStatus(BookingAttemptStatus.FAIL);
            }
        } catch (RestClientException e) {
            e.printStackTrace();
            result.setStatus(BookingAttemptStatus.NO_RESPONSE);
        }
        return result;
    }

    @Override
    public void unbookOrder(OrderDto orderDto) {
        var currentRequestAttributes = RequestContextHolder.currentRequestAttributes();
        System.out.println(Arrays.toString(currentRequestAttributes.getAttributeNames(RequestAttributes.SCOPE_SESSION)));
        var bearerToken = (String) currentRequestAttributes.getAttribute("bearerAuth", RequestAttributes.SCOPE_SESSION);
        System.out.println(bearerToken);

        BookingResponse result = new BookingResponse();
        RestTemplate restTemplate = new RestTemplate();
        try {
            var response = restTemplate.postForEntity(API_URL_PREFIX + "/unbook", prepareRequest(orderDto), BookingHttpResponse.class);
            if (response.getBody() != null && response.getBody().status == 200) {
                result.setStatus(BookingAttemptStatus.SUCCESS);
            } else {
                result.setStatus(BookingAttemptStatus.FAIL);
            }
        } catch (RestClientException e) {
            result.setStatus(BookingAttemptStatus.NO_RESPONSE);
        }
    }

    private HttpEntity<List<ItemQuantityRequestDTOJava>> prepareRequest(OrderDto orderDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(SecurityContextHolder.getContext().getAuthentication().getCredentials().toString());
        List<ItemQuantityRequestDTOJava> itemList = orderDto.getOrderItems()
                .stream()
                .map(orderItem ->
                        new ItemQuantityRequestDTOJava(orderItem.getCatalogItemId(),
                                orderItem.getAmount()))
                .collect(Collectors.toList());

        return new HttpEntity<>(itemList, headers);
    }
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class BookingHttpResponse {
    Integer status;
    String message;
}
