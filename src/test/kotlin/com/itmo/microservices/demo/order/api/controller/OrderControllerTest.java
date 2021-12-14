package com.itmo.microservices.demo.order.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itmo.microservices.demo.users.api.model.AuthenticationRequest;
import com.itmo.microservices.demo.users.api.model.UserRequestDto;
import com.itmo.microservices.demo.warehouse.api.model.CatalogItemModel;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("dev")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OrderControllerTest {
    @Autowired
    MockMvc mockMvc;
    ObjectMapper jsonMapper = new ObjectMapper();
    static String accessToken;

    @BeforeAll
    void beforeAll() throws Exception {
        String name = "user";
        String password = "password";
        registerUser(name, password);
        accessToken = "Bearer " + getAccessToken(name, password);
    }

    @Test
    void shouldCreateEmptyOrderAndReturnOkStatus() throws Exception {
        // region init+action
        String orderId = createOrderAndReturnId();
        // endregion

        // region assertion
        assert orderId != null;
        // endregion
    }

    @Test
    void shouldGetOrderByIdAndReturnOkStatus() throws Exception {
        // region init
        String orderId = createOrderAndReturnId();
        // endregion

        // region action
        JSONObject result = sendGetRequestWithoutContent("/orders/" + orderId);
        // endregion

        // region assertion
        assert orderId.equals(result.getString("id"));
        // endregion
    }

    @Test
    void shouldPutItemToOrder() throws Exception {
        // region init
        String orderId = createOrderAndReturnId();
        String itemId = createItemAndReturnId();
        String url = "/orders/" + orderId +
                "/items/" + itemId +
                "?amount=" + 666;
        // endregion

        // region action
        sendPutRequestWithoutContent(url);
        JSONObject result = sendGetRequestWithoutContent("/orders/" + orderId);
        // endregion

        // region assertion
        String savedItemId = result.getJSONObject("itemsMap").getString(itemId);
        assert "666".equals(savedItemId);
        // endregion
    }

    @Test
    void shouldBookOrder() throws Exception {
        // region init
        String orderId = createOrderAndReturnId();
        // endregion

        // region action
        JSONObject result = sendPostRequestWithoutContent("/orders/" + orderId + "/bookings");
        // endregion

        // region assertion
        assert result.getString("id").equals(orderId);
        // endregion
    }

    @Test
    void shouldSelectDeliveryTime() throws Exception {
        // region init
        String orderId = createOrderAndReturnId();
        String url = "/orders/" + orderId +
                "/delivery" +
                "?slot=" + 100;
        // endregion

        // region action
        JSONObject result = sendPostRequestWithoutContent(url);
        // endregion

        // region assertion
        assert result.getString("id").equals(orderId);
        // endregion
    }


    private void registerUser(String name, String password) throws Exception {
        UserRequestDto userRequest = new UserRequestDto(name, password);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(userRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    private String getAccessToken(String name, String password) throws Exception {
        AuthenticationRequest authRequest = new AuthenticationRequest(name, password);

        MvcResult result = mockMvc.perform(post("/authentication")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(authRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        return new JSONObject(result.getResponse().getContentAsString()).getString("accessToken");
    }

    private String createOrderAndReturnId() throws Exception {
        JSONObject result = sendPostRequestWithoutContent("/orders");
        return result.getString("id");
    }

    private String createItemAndReturnId() throws Exception {
        CatalogItemModel item = new CatalogItemModel("title", "description", 100);
        JSONObject result = sendPostRequestWithContent("/items/add", item);

        return result.getString("message");
    }

    private JSONObject sendPostRequestWithoutContent(String url) throws Exception {
        MvcResult result = mockMvc.perform(post(url)
                        .header(HttpHeaders.AUTHORIZATION, accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        return new JSONObject(result.getResponse().getContentAsString());
    }

    private <T> JSONObject sendPostRequestWithContent(String url, T content) throws Exception {
        MvcResult result = mockMvc.perform(post(url)
                        .header(HttpHeaders.AUTHORIZATION, accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(content))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        return new JSONObject(result.getResponse().getContentAsString());
    }

    private JSONObject sendGetRequestWithoutContent(String url) throws Exception {
        MvcResult result = mockMvc.perform(get(url)
                        .header(HttpHeaders.AUTHORIZATION, accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        return new JSONObject(result.getResponse().getContentAsString());
    }

    private void sendPutRequestWithoutContent(String url) throws Exception {
        mockMvc.perform(put(url)
                        .header(HttpHeaders.AUTHORIZATION, accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
    }
}