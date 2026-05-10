package com.spring.app.company.payment;

// 포트원 결제 관련 기능을 정의한 인터페이스
public interface PortOneV1Client {

    // 포트원 access token 발급
    String getAccessToken();

    // imp_uid 기준 결제 정보 조회
    PortOnePaymentInfo getPaymentInfo(String accessToken, String impUid);

    // merchant_uid 기준 결제 정보 조회
    PortOnePaymentInfo getPaymentInfoByMerchantUid(String token, String merchantUid);

    // 포트원 결제 조회 결과를 담는 DTO
    class PortOnePaymentInfo {

        // 외부에서 직접 접근하지 못하도록 private 으로 캡슐화
        private String status;         // 결제 상태
        private String merchantUid;    // 주문번호
        private Long amount;           // 결제 금액
        private String impUid;         // 포트원 결제 고유번호
        private String payMethod;      // 결제 수단
        private String pgProvider;     // PG사
        private String embPgProvider;  // 간편결제사

        // 결제 상태 getter
        public String getStatus() {
            return status;
        }

        // 결제 상태 setter
        public void setStatus(String status) {
            this.status = status;
        }

        // 주문번호 getter
        public String getMerchantUid() {
            return merchantUid;
        }

        // 주문번호 setter
        public void setMerchantUid(String merchantUid) {
            this.merchantUid = merchantUid;
        }

        // 결제 금액 getter
        public Long getAmount() {
            return amount;
        }

        // 결제 금액 setter
        public void setAmount(Long amount) {
            this.amount = amount;
        }

        // imp_uid getter
        public String getImpUid() {
            return impUid;
        }

        // imp_uid setter
        public void setImpUid(String impUid) {
            this.impUid = impUid;
        }

        // 결제 수단 getter
        public String getPayMethod() {
            return payMethod;
        }

        // 결제 수단 setter
        public void setPayMethod(String payMethod) {
            this.payMethod = payMethod;
        }

        // PG사 getter
        public String getPgProvider() {
            return pgProvider;
        }

        // PG사 setter
        public void setPgProvider(String pgProvider) {
            this.pgProvider = pgProvider;
        }

        // 간편결제사 getter
        public String getEmbPgProvider() {
            return embPgProvider;
        }

        // 간편결제사 setter
        public void setEmbPgProvider(String embPgProvider) {
            this.embPgProvider = embPgProvider;
        }
    }
}