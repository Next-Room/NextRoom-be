// package com.nextroom.nextRoomServer.controller;
//
// import static com.nextroom.nextRoomServer.exceptions.StatusCode.*;
// import static org.mockito.Mockito.*;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.springframework.http.MediaType;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.nextroom.nextRoomServer.dto.AuthDto;
// import com.nextroom.nextRoomServer.service.AuthService;
//
// @ExtendWith(MockitoExtension.class)
// public class AuthControllerTest {
//     private final ObjectMapper objectMapper = new ObjectMapper();
//
//     @Mock
//     private AuthService authService;
//
//     @InjectMocks
//     private AuthController authController;
//
//     private MockMvc mockMvc;
//
//     @Test
//     public void testSignUp() throws Exception {
//         AuthDto.SignUpRequestDto signUpRequest = AuthDto.SignUpRequestDto
//             .builder()
//             .adminCode("77777")
//             .build();
//
//         AuthDto.SignUpResponseDto signUpResponse = AuthDto.SignUpResponseDto.builder()
//             .adminCode("77777")
//             .build();
//
//         when(authService.signUp(signUpRequest)).thenReturn(signUpResponse);
//
//         mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
//
//         // mockMvc.perform(post("/api/v1/auth/signup")
//         //         .contentType(MediaType.APPLICATION_JSON)
//         //         .content(objectMapper.writeValueAsString(signUpRequest)))
//         //     .andExpect(status().isOk())
//         //     .andExpect(jsonPath("$.statusCode").value(OK.getCode()));
//             // .andExpect(jsonPath("$.data").value());
//     }
//
//     @Test
//     public void testLogIn() throws Exception {
//         AuthDto.LogInRequestDto logInRequest = new AuthDto.LogInRequestDto();
//
//         AuthDto.LogInResponseDto logInResponse = AuthDto.LogInResponseDto
//             .builder()
//             .accessToken()
//             .build();
//
//         when(authService.login(logInRequest)).thenReturn();
//
//         mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
//
//         mockMvc.perform(post("/api/v1/auth/login")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(objectMapper.writeValueAsString(logInRequest)))
//             .andExpect(status().isOk())
//             .andExpect(jsonPath("$.statusCode").value(OK.getCode()))
//             .andExpect(jsonPath("$.data").value("some_response_here"));
//     }
// }
