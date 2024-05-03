//package com.arreo.payments.swagger;
//
//import io.swagger.v3.oas.models.Components;
//import io.swagger.v3.oas.models.OpenAPI;
//import io.swagger.v3.oas.models.info.Info;
//import io.swagger.v3.oas.models.security.SecurityRequirement;
//import io.swagger.v3.oas.models.security.SecurityScheme;
//import org.springdoc.core.models.GroupedOpenApi;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class SwaggerConfig {
//    @Bean
//    public GroupedOpenApi api() {
//        return GroupedOpenApi.builder()
//                .group("api")
//                .pathsToMatch("/**")
////                .addOperationCustomizer(addHeadersOperationCustomizer())
//                .build();
//
//    }
//
//    @Bean
//    public OpenAPI openAPI() {
//
//        Info info = new Info()
//                .version("v1.0.0")
//                .title("차세대 정산 및 커스터머 api")
//                .description("차세대 정산 시스템 및 고객사이트 api 입니다.");
//
//        // SecuritySecheme명
//        String jwtSchemeName = "jwtAuth";
//        // API 요청헤더에 인증정보 포함
//        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtSchemeName);
//        // SecuritySchemes 등록
//        Components components = new Components()
//                .addSecuritySchemes(jwtSchemeName, new SecurityScheme()
//                        .name(jwtSchemeName)
//                        .type(SecurityScheme.Type.HTTP) // HTTP 방식
//                        .scheme("bearer")
//                        .bearerFormat("JWT")); // 토큰 형식을 지정하는 임의의 문자(Optional)
//
//        return new OpenAPI()
//                .info(info)
//                .addSecurityItem(securityRequirement)
//                .components(components);
//    }
//
////    private OperationCustomizer addHeadersOperationCustomizer() {
////        return (operation, handlerMethod) -> {
////            operation.addParametersItem(new Parameter().in("header").name("Authorization").description("Your authorization header description"));
////            return operation;
////        };
////    }
//}
