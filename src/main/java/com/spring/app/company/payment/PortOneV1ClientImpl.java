package com.spring.app.company.payment;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class PortOneV1ClientImpl implements PortOneV1Client {

    // 표준 출력 대신 전용 logger 사용
    private static final Logger logger = LoggerFactory.getLogger(PortOneV1ClientImpl.class);

    // 중복 문자열을 상수로 분리하여 유지보수성을 높임
    private static final String AUTH_HEADER = "Authorization";

    private static final String URL_GET_TOKEN = "https://api.iamport.kr/users/getToken";
    private static final String URL_PAYMENT_BY_IMP_UID = "https://api.iamport.kr/payments/";
    private static final String URL_PAYMENT_BY_MERCHANT_UID = "https://api.iamport.kr/payments/find/";

    private static final String KEY_RESPONSE = "response";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_STATUS = "status";
    private static final String KEY_MERCHANT_UID = "merchant_uid";
    private static final String KEY_AMOUNT = "amount";
    private static final String KEY_IMP_UID = "imp_uid";
    private static final String KEY_PAY_METHOD = "pay_method";
    private static final String KEY_PG_PROVIDER = "pg_provider";
    private static final String KEY_EMB_PG_PROVIDER = "emb_pg_provider";
    private static final String KEY_IMP_KEY = "imp_key";
    private static final String KEY_IMP_SECRET = "imp_secret";

    @Value("${portone.apiKey}")
    private String apiKey;

    @Value("${portone.apiSecret}")
    private String apiSecret;

    // RestTemplate 은 재사용 가능하므로 필드로 선언
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public String getAccessToken() {
        try {
            // JSON 요청용 헤더 생성
            HttpHeaders headers = createJsonHeaders();

            // 포트원 토큰 발급 요청 바디 생성
            Map<String, String> requestBody = Map.of(
                    KEY_IMP_KEY, apiKey,
                    KEY_IMP_SECRET, apiSecret
            );

            HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

            // raw type 대신 제네릭 타입을 명시하여 타입 안정성 확보
            ResponseEntity<Map<String, Object>> responseEntity = restTemplate.exchange(
                    URL_GET_TOKEN,
                    HttpMethod.POST,
                    entity,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            Map<String, Object> responseBody = responseEntity.getBody();
            if (responseBody == null) {
                logger.warn("PortOne access token response body is null.");
                return null;
            }

            // 공통 response 영역만 추출
            Map<String, Object> responseMap = extractResponseMap(responseBody);
            if (responseMap == null) {
                logger.warn("PortOne access token response field is missing.");
                return null;
            }

            // access_token 값 반환
            return toStr(responseMap.get(KEY_ACCESS_TOKEN));

        } catch (Exception e) {
            logger.error("Failed to get PortOne access token.", e);
            return null;
        }
    }

    @Override
    public PortOnePaymentInfo getPaymentInfo(String accessToken, String impUid) {
        try {
            // 인증 헤더 생성
            HttpHeaders headers = createAuthHeaders(accessToken);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            // imp_uid 기준 결제 정보 조회
            ResponseEntity<Map<String, Object>> responseEntity = restTemplate.exchange(
                    URL_PAYMENT_BY_IMP_UID + impUid,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            Map<String, Object> responseBody = responseEntity.getBody();
            if (responseBody == null) {
                logger.warn("PortOne payment response body is null. impUid={}", impUid);
                return null;
            }

            Map<String, Object> responseMap = extractResponseMap(responseBody);
            if (responseMap == null) {
                logger.warn("PortOne payment response field is missing. impUid={}", impUid);
                return null;
            }

            // 조회 결과를 DTO 로 변환하여 반환
            return toPaymentInfo(responseMap);

        } catch (Exception e) {
            logger.error("Failed to get PortOne payment info. impUid={}", impUid, e);
            return null;
        }
    }

    @Override
    public PortOnePaymentInfo getPaymentInfoByMerchantUid(String token, String merchantUid) {
        try {
            // 인증 헤더 생성
            HttpHeaders headers = createAuthHeaders(token);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            // merchant_uid 기준 결제 정보 조회
            ResponseEntity<Map<String, Object>> responseEntity = restTemplate.exchange(
                    URL_PAYMENT_BY_MERCHANT_UID + merchantUid,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            Map<String, Object> responseBody = responseEntity.getBody();
            if (responseBody == null) {
                logger.warn("PortOne payment(find by merchantUid) response body is null. merchantUid={}", merchantUid);
                return null;
            }

            Map<String, Object> responseMap = extractResponseMap(responseBody);
            if (responseMap == null) {
                logger.warn("PortOne payment(find by merchantUid) response field is missing. merchantUid={}", merchantUid);
                return null;
            }

            // 조회 결과를 DTO 로 변환하여 반환
            return toPaymentInfo(responseMap);

        } catch (Exception e) {
            logger.error("Failed to get PortOne payment info by merchantUid. merchantUid={}", merchantUid, e);
            return null;
        }
    }

    // JSON 타입 요청 헤더 생성
    private HttpHeaders createJsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    // Authorization 헤더 생성
    private HttpHeaders createAuthHeaders(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTH_HEADER, accessToken);
        return headers;
    }

    // 응답 본문에서 response 영역만 안전하게 꺼내는 메서드
    @SuppressWarnings("unchecked")
    private Map<String, Object> extractResponseMap(Map<String, Object> body) {
        Object response = body.get(KEY_RESPONSE);

        // Java 16+ instanceof 패턴 매칭 적용
        if (response instanceof Map<?, ?> rawMap) {
            return (Map<String, Object>) rawMap;
        }

        return null;
    }

    // Map 응답 데이터를 DTO 로 변환하는 공통 메서드
    private PortOnePaymentInfo toPaymentInfo(Map<String, Object> responseMap) {
        PortOnePaymentInfo info = new PortOnePaymentInfo();
        info.setStatus(toStr(responseMap.get(KEY_STATUS)));
        info.setMerchantUid(toStr(responseMap.get(KEY_MERCHANT_UID)));
        info.setAmount(toLong(responseMap.get(KEY_AMOUNT)));
        info.setImpUid(safeStr(responseMap.get(KEY_IMP_UID)));
        info.setPayMethod(safeStr(responseMap.get(KEY_PAY_METHOD)));
        info.setPgProvider(safeStr(responseMap.get(KEY_PG_PROVIDER)));
        info.setEmbPgProvider(safeStr(responseMap.get(KEY_EMB_PG_PROVIDER)));
        return info;
    }

    // null 또는 문자열 "null" 값을 실제 null 로 정리
    private String safeStr(Object value) {
        if (value == null) {
            return null;
        }

        String strValue = String.valueOf(value);
        return "null".equalsIgnoreCase(strValue) ? null : strValue;
    }

    // Object 값을 문자열로 변환
    private String toStr(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    // Object 값을 Long 으로 변환
    private Long toLong(Object value) {
        if (value == null) {
            return null;
        }

        // Java 16+ instanceof 패턴 매칭 적용
        if (value instanceof Number number) {
            return number.longValue();
        }

        return Long.valueOf(String.valueOf(value));
    }
}