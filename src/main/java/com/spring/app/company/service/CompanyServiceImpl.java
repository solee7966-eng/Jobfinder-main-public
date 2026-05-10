package com.spring.app.company.service;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.spring.app.common.FileManager;
import com.spring.app.common.domain.EducationDTO;
import com.spring.app.common.domain.JobCategoryDTO;
import com.spring.app.common.domain.RegionDTO;
import com.spring.app.common.domain.SkillCategoryDTO;
import com.spring.app.common.domain.SkillDTO;
import com.spring.app.common.domain.SkillJoinRowDTO;
import com.spring.app.company.domain.ApplicantDetailDTO;
import com.spring.app.company.domain.ApplicantListDTO;
import com.spring.app.company.domain.BannerDTO;
import com.spring.app.company.domain.BannerListDTO;
import com.spring.app.company.domain.CompanyDashboardDTO;
import com.spring.app.company.domain.CompanyProfileDTO;
import com.spring.app.company.domain.CompanyProfileUpdateDTO;
import com.spring.app.company.domain.CompanyProfileUpdateResponseDTO;
import com.spring.app.company.domain.CompanyTopbarDTO;
import com.spring.app.company.domain.DeletedOfferHistoryDTO;
import com.spring.app.company.domain.ImageFileDTO;
import com.spring.app.company.domain.JobPostingDTO;
import com.spring.app.company.domain.JobPostingEditResponseDTO;
import com.spring.app.company.domain.MemberSimpleDTO;
import com.spring.app.company.domain.OfferCreateRequestDTO;
import com.spring.app.company.domain.OfferDetailDTO;
import com.spring.app.company.domain.OfferListDTO;
import com.spring.app.company.domain.OfferMetricsDTO;
import com.spring.app.company.domain.OfferMetricsSummaryDTO;
import com.spring.app.company.domain.OfferRecipientDetailDTO;
import com.spring.app.company.domain.OfferSendRequestDTO;
import com.spring.app.company.domain.OfferSendValidationDTO;
import com.spring.app.company.domain.OfferUpdateRequestDTO;
import com.spring.app.company.domain.PaymentCompleteRequest;
import com.spring.app.company.domain.PaymentCompleteResponse;
import com.spring.app.company.domain.PaymentReadyRequest;
import com.spring.app.company.domain.PaymentReadyResponse;
import com.spring.app.company.domain.RegionChainDTO;
import com.spring.app.company.domain.TalentResumeDTO;
import com.spring.app.company.domain.TalentResumeDetailDTO;
import com.spring.app.company.domain.TalentSearchConditionDTO;
import com.spring.app.company.model.CompanyApplicantMapper;
import com.spring.app.company.model.CompanyBannerMapper;
import com.spring.app.company.model.CompanyDashBoardMapper;
import com.spring.app.company.model.CompanyJobMapper;
import com.spring.app.company.model.CompanyOfferMapper;
import com.spring.app.company.model.CompanyProfileMapper;
import com.spring.app.company.model.CompanyTalentMapper;
import com.spring.app.company.model.CompanyWalletMapper;
import com.spring.app.company.payment.PortOneV1Client;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private static final Logger log = LoggerFactory.getLogger(CompanyServiceImpl.class);

    private static final long BANNER_AD_PRICE = 300000L;
    private static final long PROJECT_PAY_AMOUNT = 100L;

    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_PAID = "PAID";
    private static final String STATUS_FAILED = "FAILED";
    private static final String STATUS_VERIFYING = "VERIFYING";
    private static final String STATUS_CANCELED = "CANCELED";
    private static final String STATUS_DONE = "DONE";
    private static final String TX_TYPE_CHARGE = "CHARGE";
    private static final String TX_TYPE_USE = "USE";

    private final CompanyJobMapper jobMapper;
    private final CompanyOfferMapper offerMapper;
    private final CompanyBannerMapper bannerMapper;
    private final CompanyApplicantMapper applicantMapper;
    private final CompanyDashBoardMapper boardMapper;
    private final CompanyProfileMapper profileMapper;
    private final CompanyTalentMapper talentMapper;
    private final CompanyWalletMapper walletMapper;
    private final PortOneV1Client portOneV1Client;
    private final FileManager fileManager;

    @Value("${file.images-dir}")
    private String uploadPath;

    // 주문번호를 생성한다.
    private String generateOrderId() {
        String time = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = ThreadLocalRandom.current().nextInt(1000, 10000);
        return "ORD" + time + random;
    }

    // Date 타입을 LocalDate 타입으로 변환한다.
    private LocalDate toLocalDate(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    // 문자열이 null 또는 공백인지 확인한다.
    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    // Object 값을 long 값으로 안전하게 변환한다.
    private long toLong(Object value) {
        if (value == null) {
            return 0L;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        try {
            return Long.parseLong(String.valueOf(value));
        } catch (NumberFormatException e) {
            log.warn("long 변환 실패: {}", value);
            return 0L;
        }
    }

    // null 리스트를 빈 리스트로 바꿔준다.
    private <T> List<T> emptyIfNull(List<T> list) {
        return list == null ? Collections.emptyList() : list;
    }

    // 신고 처리 상태를 화면용 문구로 변환한다.
    private String convertReportProcessStatusText(String status) {
        if (isBlank(status)) {
            return "신고 접수";
        }

        return switch (status) {
            case "WAIT", "PENDING", "처리전" -> "처리대기";
            case "DONE", "COMPLETE", "처리완료" -> "처리완료";
            case "REJECT", "반려" -> "반려";
            default -> status;
        };
    }

    // 지원 처리 상태를 화면용 문구로 변환한다.
    private String convertProcessStatusText(Integer processStatus) {
        if (processStatus == null) {
            return "";
        }

        return switch (processStatus) {
            case 0 -> "미열람";
            case 1 -> "열람";
            case 2 -> "서류탈락";
            case 3 -> "면접요청";
            case 4 -> "합격";
            case 5 -> "불합격";
            default -> "";
        };
    }

    // 제안서 응답 상태를 화면용 문구로 변환한다.
    private String convertResponseStatusText(Integer responseStatus) {
        if (responseStatus == null) {
            return "미응답";
        }

        return switch (responseStatus) {
            case 1 -> "수락";
            case 2 -> "거절";
            default -> "미응답";
        };
    }

    // 채용공고 목록의 신고 상태 문구를 세팅한다.
    private void applyReportStatusText(List<JobPostingDTO> jobList) {
        if (jobList == null) {
            return;
        }

        for (JobPostingDTO dto : jobList) {
            dto.setReportStatusText(convertReportProcessStatusText(dto.getReportProcessStatus()));
        }
    }

    // 기업 소개 행이 없으면 생성하고, 있으면 기존 ID를 반환한다.
    private Long getOrCreateCompanyIntroId(String memberId) {
        Long companyIntroId = profileMapper.selectCompanyIntroIdByMemberId(memberId);
        if (companyIntroId != null) {
            return companyIntroId;
        }

        Long newIntroId = profileMapper.getCompanyIntroSeq();
        CompanyProfileUpdateDTO dto = new CompanyProfileUpdateDTO();
        dto.setCompanyIntroId(newIntroId);
        dto.setMemberId(memberId);

        int inserted = profileMapper.insertEmptyCompanyIntro(dto);
        if (inserted <= 0) {
            throw new IllegalStateException("기업 소개 기본 행 생성에 실패했습니다.");
        }

        return newIntroId;
    }

    // 업로드 기준 경로 아래의 하위 디렉토리를 OS 독립적인 Path 방식으로 조합한다.
    private String resolveImageUploadDir(String childDir) {
        return Paths.get(uploadPath)
                .resolve(childDir)
                .normalize()
                .toString();
    }

    // 파일을 업로드하고 저장된 파일명을 반환한다.
    private String uploadImageFile(MultipartFile file, String targetDir, String failMessage) throws IOException {
        byte[] bytes = file.getBytes();
        String originalFilename = file.getOriginalFilename();

        // targetDir은 resolveImageUploadDir()에서 만들어진 경로를 사용한다.
        // 실제 파일명 생성, 디렉토리 생성, 저장은 FileManager에서 Path 기반으로 처리한다.
        String savedFileName = fileManager.doFileUpload(bytes, originalFilename, targetDir);

        if (savedFileName == null) {
            throw new IllegalStateException(failMessage);
        }

        return savedFileName;
    }

    // 결제 준비 주문을 여러 번 재시도하며 저장한다.
    private String insertPendingPaymentWithRetry(String memberId, Long chargeAmount) {
        for (int i = 0; i < 3; i++) {
            String orderId = generateOrderId();
            try {
                int inserted = walletMapper.insertPayment(memberId, orderId, chargeAmount, STATUS_PENDING);
                if (inserted == 1) {
                    return orderId;
                }
            } catch (DataAccessException e) {
                log.warn("결제 준비 저장 재시도 {}/3 실패", i + 1, e);
            }
        }
        return null;
    }

    // 결제 확인 중 상태로 변경한다.
    private void markPaymentVerifying(String orderId) {
        walletMapper.updatePaymentStatusIfCurrent(orderId, STATUS_PENDING, STATUS_VERIFYING);
    }

    // 실패 응답 객체를 만들어 반환한다.
    private PaymentCompleteResponse failPaymentResponse(String orderId, String message) {
        PaymentCompleteResponse res = new PaymentCompleteResponse();
        res.ok = false;
        res.orderId = orderId;
        res.message = message;
        return res;
    }

    // 이미 처리된 결제 응답을 만들어 반환한다.
    private PaymentCompleteResponse buildAlreadyPaidResponse(String orderId, String memberId, Long chargeAmount) {
        PaymentCompleteResponse res = new PaymentCompleteResponse();
        Long balance = walletMapper.selectPointAvailableBalance(memberId);
        res.ok = true;
        res.orderId = orderId;
        res.paidAmount = chargeAmount;
        res.pointBalance = balance == null ? 0L : balance;
        res.message = "이미 처리된 결제입니다.";
        return res;
    }

    // 결제 승인 전 외부 결제 검증을 수행한다.
    private PaymentCompleteResponse validateExternalPayment(String orderId, String reqImpUid,
                                                           PortOneV1Client.PortOnePaymentInfo info) {
        if (!orderId.equals(info.getMerchantUid())) {
            walletMapper.updatePaymentStatusIfCurrent(orderId, STATUS_PENDING, STATUS_FAILED);
            return failPaymentResponse(orderId, "주문번호가 일치하지 않습니다.");
        }

        if (!isBlank(reqImpUid) && !reqImpUid.equals(info.getImpUid())) {
            walletMapper.updatePaymentStatusIfCurrent(orderId, STATUS_PENDING, STATUS_FAILED);
            return failPaymentResponse(orderId, "결제 고유번호가 일치하지 않습니다.");
        }

        if (!Long.valueOf(PROJECT_PAY_AMOUNT).equals(info.getAmount())) {
            walletMapper.updatePaymentStatusIfCurrent(orderId, STATUS_PENDING, STATUS_FAILED);
            return failPaymentResponse(orderId, "결제 금액이 일치하지 않습니다.");
        }

        if ("cancelled".equalsIgnoreCase(info.getStatus()) || "cancel".equalsIgnoreCase(info.getStatus())) {
            walletMapper.updatePaymentStatusIfCurrent(orderId, STATUS_PENDING, STATUS_CANCELED);
            return failPaymentResponse(orderId, "취소된 결제입니다.");
        }

        if (!"paid".equalsIgnoreCase(info.getStatus())) {
            walletMapper.updatePaymentStatusIfCurrent(orderId, STATUS_PENDING, STATUS_FAILED);
            return failPaymentResponse(orderId, "결제가 완료 상태가 아닙니다: " + info.getStatus());
        }

        return null;
    }

    // 기업 설립연도를 검증하고 openDate 값을 세팅한다.
    private void validateAndSetOpenDate(CompanyProfileUpdateDTO dto) {
        String openYear = dto.getOpenYear();
        if (openYear != null) {
            openYear = openYear.trim();
        }

        if (isBlank(openYear)) {
            dto.setOpenDate(null);
            return;
        }

        if (!openYear.matches("^\\d{4}$")) {
            throw new IllegalArgumentException("설립연도는 4자리 숫자로 입력하세요.");
        }

        int year = Integer.parseInt(openYear);
        int currentYear = LocalDate.now().getYear();
        if (year < 1800 || year > currentYear) {
            throw new IllegalArgumentException("설립연도는 1800년부터 현재 연도 사이여야 합니다.");
        }

        dto.setOpenDate(java.sql.Date.valueOf(year + "-01-01"));
    }

    // 기업 소개 기본정보를 update 또는 insert 한다.
    private int upsertCompanyIntroBasicInfo(CompanyProfileUpdateDTO dto) {
        int exists = profileMapper.existsCompanyIntro(dto.getMemberId());
        if (exists > 0) {
            return profileMapper.updateCompanyIntroBasicInfo(dto);
        }

        Long companyIntroId = profileMapper.getCompanyIntroSeq();
        dto.setCompanyIntroId(companyIntroId);
        return profileMapper.insertCompanyIntroBasicInfo(dto);
    }

    // 기업 소개 상세정보를 update 또는 insert 한다.
    private int upsertCompanyIntroDetail(CompanyProfileUpdateDTO dto) {
        int exists = profileMapper.existsCompanyIntro(dto.getMemberId());
        if (exists > 0) {
            return profileMapper.updateCompanyIntroDetail(dto);
        }

        Long companyIntroId = profileMapper.getCompanyIntroSeq();
        dto.setCompanyIntroId(companyIntroId);
        return profileMapper.insertCompanyIntroDetail(dto);
    }

    // 기업 로고 정보를 update 또는 insert 한다.
    private int saveOrUpdateCompanyLogo(Long companyIntroId, String fileUrl, String originalFilename) {
        ImageFileDTO oldLogo = profileMapper.selectCompanyLogo(companyIntroId);

        if (oldLogo != null) {
            oldLogo.setFileUrl(fileUrl);
            oldLogo.setOriginalFilename(originalFilename);
            return profileMapper.updateCompanyLogoFile(oldLogo);
        }

        Long fileId = profileMapper.getImageFileSeq();
        ImageFileDTO imageDto = new ImageFileDTO();
        imageDto.setFileId(fileId);
        imageDto.setTargetId(companyIntroId);
        imageDto.setTargetType("company");
        imageDto.setFileCategory("logo");
        imageDto.setFileUrl(fileUrl);
        imageDto.setOriginalFilename(originalFilename);
        return profileMapper.insertImageFile(imageDto);
    }

    // 제안서 공통 유효성 검사를 수행한다.
    private void validateOfferLetterCommon(Long jobId, String title, String message) {
        if (jobId == null) {
            throw new IllegalArgumentException("연결할 공고는 필수입니다.");
        }
        if (isBlank(title)) {
            throw new IllegalArgumentException("제안서 제목은 필수입니다.");
        }
        if (title.trim().length() > 200) {
            throw new IllegalArgumentException("제안서 제목은 200자 이하여야 합니다.");
        }
        if (isBlank(message)) {
            throw new IllegalArgumentException("제안 메시지는 필수입니다.");
        }
    }

    // 제안서 발송 요청값의 기본 유효성을 검사한다.
    private void validateOfferSendRequest(OfferSendRequestDTO req) {
        if (req == null || req.getOfferLetterId() == null) {
            throw new IllegalArgumentException("제안서 정보가 올바르지 않습니다.");
        }
        if (isBlank(req.getExpireAt())) {
            throw new IllegalArgumentException("만료일은 필수입니다.");
        }
        if (req.getReceiverMemberIds() == null || req.getReceiverMemberIds().isEmpty()) {
            throw new IllegalArgumentException("수신자를 1명 이상 선택하세요.");
        }
    }

    // 수신자 목록의 null/공백/중복을 제거한다.
    private List<String> sanitizeReceiverIds(List<String> receiverMemberIds) {
        return receiverMemberIds.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .toList();
    }

    // 제안서 소유권과 연결 공고 존재 여부를 검사한다.
    private void validateOfferOwnership(OfferSendValidationDTO info, String companyMemberId) {
        if (info == null) {
            throw new IllegalArgumentException("제안서를 찾을 수 없습니다.");
        }
        if (!companyMemberId.equals(info.getCompanyMemberId())) {
            throw new SecurityException("해당 제안서를 발송할 권한이 없습니다.");
        }
        if (info.getJobId() == null) {
            throw new IllegalStateException("연결된 공고가 없는 제안서는 발송할 수 없습니다.");
        }
    }

    // 제안서 발송 가능 공고 상태인지 검사한다.
    private void validateOfferJobStatus(LocalDate today, LocalDate openedAt,
                                        LocalDate deadlineAt, LocalDate closedAt) {
        if (openedAt != null && today.isBefore(openedAt)) {
            throw new IllegalStateException("아직 게시 시작 전인 공고에는 제안서를 발송할 수 없습니다.");
        }
        if (deadlineAt != null && today.isAfter(deadlineAt)) {
            throw new IllegalStateException("공고 마감일이 지난 공고에는 제안서를 발송할 수 없습니다.");
        }
        if (closedAt != null && today.isAfter(closedAt)) {
            throw new IllegalStateException("게시 종료된 공고에는 제안서를 발송할 수 없습니다.");
        }
    }

    // 제안서 만료일 범위를 검사한다.
    private void validateExpireDate(LocalDate today, LocalDate expireDate,
                                    LocalDate openedAt, LocalDate deadlineAt, LocalDate closedAt) {
        if (expireDate.isBefore(today)) {
            throw new IllegalArgumentException("만료일은 오늘 이전으로 설정할 수 없습니다.");
        }
        if (openedAt != null && expireDate.isBefore(openedAt)) {
            throw new IllegalArgumentException("만료일은 게시 시작일보다 빠를 수 없습니다.");
        }
        if (deadlineAt != null && expireDate.isAfter(deadlineAt)) {
            throw new IllegalArgumentException("만료일은 공고 마감일을 넘길 수 없습니다.");
        }
        if (closedAt != null && expireDate.isAfter(closedAt)) {
            throw new IllegalArgumentException("만료일은 게시 종료일을 넘길 수 없습니다.");
        }
    }

    // 제안서 수신자의 유효성과 중복 발송 여부를 검사한다.
    private void validateOfferReceivers(Long offerLetterId, List<String> receiverIds) {
        if (receiverIds.isEmpty()) {
            throw new IllegalArgumentException("유효한 수신자가 없습니다.");
        }

        int validCount = offerMapper.countValidOfferReceivers(receiverIds);
        if (validCount != receiverIds.size()) {
            throw new IllegalArgumentException("유효하지 않은 수신자가 포함되어 있습니다.");
        }

        List<String> alreadySent = offerMapper.selectAlreadySentReceiverIds(offerLetterId, receiverIds);
        if (alreadySent != null && !alreadySent.isEmpty()) {
            throw new IllegalStateException(
                    "이미 제안서를 발송한 회원이 포함되어 있습니다: " + String.join(", ", alreadySent)
            );
        }
    }

    // 배너 등록 입력값을 검증한다.
    private void validateBannerRequest(BannerDTO bannerDto, MultipartFile bannerImage) {
        if (bannerDto == null) {
            throw new IllegalArgumentException("배너 정보가 없습니다.");
        }
        if (isBlank(bannerDto.getFkMemberId())) {
            throw new IllegalArgumentException("회원 정보가 없습니다.");
        }
        if (bannerDto.getFkJobId() == null) {
            throw new IllegalArgumentException("연결할 공고가 없습니다.");
        }
        if (isBlank(bannerDto.getTitle())) {
            throw new IllegalArgumentException("배너 제목이 없습니다.");
        }
        if (isBlank(bannerDto.getStartAt())) {
            throw new IllegalArgumentException("시작일이 없습니다.");
        }
        if (bannerImage == null || bannerImage.isEmpty()) {
            throw new IllegalArgumentException("배너 이미지를 첨부해 주세요.");
        }

        LocalDate startDate = LocalDate.parse(bannerDto.getStartAt());
        LocalDate minStartDate = LocalDate.now().plusDays(2);
        if (startDate.isBefore(minStartDate)) {
            throw new IllegalArgumentException("배너 등록 시작일은 " + minStartDate + "부터 선택할 수 있습니다.");
        }
    }

    // 배너 포인트 차감을 수행한다.
    private void deductBannerPoint(String memberId) {
        int updated = walletMapper.deductPointAvailable(memberId, BANNER_AD_PRICE);
        if (updated == 0) {
            throw new IllegalStateException("포인트 차감에 실패했습니다. 잔액을 다시 확인해 주세요.");
        }
    }

    // 배너 이미지를 저장하고 image_file 및 배너 이미지 연결을 처리한다.
    private void saveBannerImage(Long bannerId, MultipartFile bannerImage) throws IOException {
        String originalFilename = bannerImage.getOriginalFilename();
        String savedFileName = uploadImageFile(bannerImage, resolveImageUploadDir("Banner"), "배너 이미지 업로드에 실패했습니다.");
        String fileUrl = "images/Banner/" + savedFileName;

        Long fileId = bannerMapper.getImageFileSeq();

        ImageFileDTO imageDto = new ImageFileDTO();
        imageDto.setFileId(fileId);
        imageDto.setTargetId(bannerId);
        imageDto.setTargetType("banner");
        imageDto.setFileCategory("-");
        imageDto.setFileUrl(fileUrl);
        imageDto.setOriginalFilename(originalFilename);

        bannerMapper.insertImageFile(imageDto);
        bannerMapper.updateBannerImageFileId(bannerId, fileId);
    }

    // 포인트 지갑이 없으면 생성하고, 있으면 기존 지갑 ID를 반환한다.
    private Long getOrCreatePointWallet(String memberId) {
        Long walletId = walletMapper.selectPointWalletId(memberId);
        if (walletId != null) {
            return walletId;
        }

        int inserted = walletMapper.insertPointWallet(memberId);
        if (inserted != 1) {
            throw new IllegalStateException("포인트 지갑 생성 실패");
        }

        Long createdWalletId = walletMapper.selectPointWalletId(memberId);
        if (createdWalletId == null) {
            throw new IllegalStateException("포인트 지갑 재조회 실패");
        }

        return createdWalletId;
    }

    // 결제 상태를 PAID로 변경한다.
    private PaymentCompleteResponse updatePaymentToPaid(String orderId, String memberId,
                                                        Long chargeAmount,
                                                        PortOneV1Client.PortOnePaymentInfo info) {
        int updated = walletMapper.updatePaymentPaidIfPending(
                orderId,
                info.getPayMethod(),
                info.getPgProvider(),
                info.getEmbPgProvider()
        );

        if (updated == 1) {
            return null;
        }

        Map<String, Object> payment = walletMapper.selectPaymentByOrderId(orderId);
        String latestStatus = payment == null ? null : String.valueOf(payment.get("STATUS"));

        if (STATUS_PAID.equalsIgnoreCase(latestStatus)) {
            return buildAlreadyPaidResponse(orderId, memberId, chargeAmount);
        }

        throw new IllegalStateException("결제 상태 변경 실패");
    }

    // 충전 포인트를 적립한다.
    private void addChargePoint(String memberId, Long chargeAmount) {
        int updated = walletMapper.addPointAvailable(memberId, chargeAmount);
        if (updated != 1) {
            throw new IllegalStateException("포인트 적립 실패");
        }
    }

    // 충전 거래내역을 저장한다.
    private void insertChargeTransaction(Long pointWalletId, String orderId, Long chargeAmount) {
        int inserted = walletMapper.insertPointTransactionCharge(
                pointWalletId,
                orderId,
                TX_TYPE_CHARGE,
                STATUS_DONE,
                chargeAmount
        );

        if (inserted != 1) {
            throw new IllegalStateException("포인트 거래내역 저장 실패");
        }
    }

    // 상단바의 기업 기본 정보를 조회한다.
    @Override
    public CompanyTopbarDTO getCompanyTopbarInfo(String memberId) {
        return profileMapper.selectCompanyTopbarInfo(memberId);
    }

    // 기업 대시보드에 필요한 KPI와 최근 목록을 조회한다.
    @Override
    public CompanyDashboardDTO getCompanyDashboard(String memberId) {
        CompanyDashboardDTO dto = new CompanyDashboardDTO();

        dto.setOngoingJobCount(boardMapper.selectOngoingJobCount(memberId));
        dto.setJobWaitingCount(boardMapper.selectJobWaitingCount(memberId));
        dto.setJobPostingCount(boardMapper.selectJobPostingCount(memberId));
        dto.setJobClosedCount(boardMapper.selectJobClosedCount(memberId));

        dto.setTotalApplicantCount(boardMapper.selectTotalApplicantCount(memberId));
        dto.setUnreadApplicantCount(boardMapper.selectUnreadApplicantCount(memberId));
        dto.setApplicantUnreadCount(boardMapper.selectApplicantUnreadCount(memberId));
        dto.setApplicantInterviewRequestCount(boardMapper.selectApplicantInterviewRequestCount(memberId));

        dto.setSentOfferCount(boardMapper.selectSentOfferCount(memberId));
        dto.setOfferPendingCount(boardMapper.selectOfferPendingCount(memberId));
        dto.setOfferAcceptedCount(boardMapper.selectOfferAcceptedCount(memberId));
        dto.setOfferRejectedCount(boardMapper.selectOfferRejectedCount(memberId));

        Long pointBalance = boardMapper.selectPointBalance(memberId);
        dto.setPointBalance(pointBalance != null ? pointBalance : 0L);

        dto.setBannerCount(boardMapper.selectBannerCount(memberId));
        dto.setBannerPendingCount(boardMapper.selectBannerPendingCount(memberId));
        dto.setBannerApprovedCount(boardMapper.selectBannerApprovedCount(memberId));
        dto.setBannerRejectedCount(boardMapper.selectBannerRejectedCount(memberId));

        dto.setRecentJobs(emptyIfNull(boardMapper.selectRecentJobs(memberId)));
        dto.setRecentApplicants(emptyIfNull(boardMapper.selectRecentApplicants(memberId)));
        dto.setRecentOffers(emptyIfNull(boardMapper.selectRecentOffers(memberId)));

        return dto;
    }

    // 기업 프로필을 조회한다.
    @Override
    public CompanyProfileDTO getCompanyProfile(String memberId) {
        return profileMapper.selectCompanyProfile(memberId);
    }

    // 기업 기본 정보를 저장한다.
    @Override
    @Transactional
    public CompanyProfileUpdateResponseDTO updateBasicProfile(CompanyProfileUpdateDTO dto) {
        CompanyProfileUpdateResponseDTO res = new CompanyProfileUpdateResponseDTO();

        try {
            validateAndSetOpenDate(dto);
            int n1 = profileMapper.updateCompanyBasicInfo(dto);
            int n2 = upsertCompanyIntroBasicInfo(dto);

            if (n1 > 0 && n2 > 0) {
                res.setSuccess(true);
                res.setMessage("기본 정보가 저장되었습니다.");
            } else {
                res.setSuccess(false);
                res.setMessage("기본 정보 저장에 실패했습니다.");
            }
        } catch (IllegalArgumentException e) {
            res.setSuccess(false);
            res.setMessage(e.getMessage());
        } catch (DataAccessException e) {
            log.error("기본 정보 저장 중 DB 오류", e);
            res.setSuccess(false);
            res.setMessage("기본 정보 저장 중 오류가 발생했습니다.");
        }

        return res;
    }

    // 기업 주소 정보를 저장한다.
    @Override
    @Transactional
    public CompanyProfileUpdateResponseDTO updateAddressProfile(CompanyProfileUpdateDTO dto) {
        CompanyProfileUpdateResponseDTO res = new CompanyProfileUpdateResponseDTO();

        try {
            int updated = profileMapper.updateCompanyAddressInfo(dto);
            res.setSuccess(updated > 0);
            res.setMessage(updated > 0 ? "주소 정보가 저장되었습니다." : "주소 정보 저장에 실패했습니다.");
        } catch (DataAccessException e) {
            log.error("주소 정보 저장 중 DB 오류", e);
            res.setSuccess(false);
            res.setMessage("주소 정보 저장 중 오류가 발생했습니다.");
        }

        return res;
    }

    // 기업 소개 정보를 저장한다.
    @Override
    @Transactional
    public CompanyProfileUpdateResponseDTO updateIntroProfile(CompanyProfileUpdateDTO dto) {
        CompanyProfileUpdateResponseDTO res = new CompanyProfileUpdateResponseDTO();

        try {
            int updated = upsertCompanyIntroDetail(dto);
            res.setSuccess(updated > 0);
            res.setMessage(updated > 0 ? "기업 소개가 저장되었습니다." : "기업 소개 저장에 실패했습니다.");
        } catch (DataAccessException e) {
            log.error("기업 소개 저장 중 DB 오류", e);
            res.setSuccess(false);
            res.setMessage("기업 소개 저장 중 오류가 발생했습니다.");
        }

        return res;
    }

    // 기업 로고를 업로드하고 DB에 반영한다.
    @Override
    @Transactional
    public CompanyProfileUpdateResponseDTO uploadCompanyLogo(String memberId, MultipartFile logoFile) throws IOException {
        CompanyProfileUpdateResponseDTO res = new CompanyProfileUpdateResponseDTO();

        try {
            if (logoFile == null || logoFile.isEmpty()) {
                res.setSuccess(false);
                res.setMessage("업로드할 로고 파일이 없습니다.");
                return res;
            }

            Long companyIntroId = getOrCreateCompanyIntroId(memberId);
            String originalFilename = logoFile.getOriginalFilename();
            String savedFileName = uploadImageFile(logoFile, resolveImageUploadDir("Logo"), "기업 로고 업로드에 실패했습니다.");
            String fileUrl = "images/Logo/" + savedFileName;

            int updated = saveOrUpdateCompanyLogo(companyIntroId, fileUrl, originalFilename);
            if (updated > 0) {
                res.setSuccess(true);
                res.setMessage("기업 로고가 등록되었습니다.");
                res.setLogoPath(fileUrl);
            } else {
                res.setSuccess(false);
                res.setMessage("기업 로고 등록에 실패했습니다.");
            }
        } catch (IllegalStateException e) {
            res.setSuccess(false);
            res.setMessage(e.getMessage());
        } catch (DataAccessException e) {
            log.error("기업 로고 등록 중 DB 오류", e);
            res.setSuccess(false);
            res.setMessage("기업 로고 등록 중 오류가 발생했습니다.");
        }

        return res;
    }

    // 기업의 채용공고 목록을 조회한다.
    @Override
    public List<JobPostingDTO> getJobPostingList(String memberId) {
        List<JobPostingDTO> jobList = jobMapper.getJobPostingList(memberId);
        applyReportStatusText(jobList);
        return jobList;
    }

    // 기업의 채용공고 목록을 페이징 조회한다.
    @Override
    public List<JobPostingDTO> getJobPostingListPaing(Map<String, Object> paraMap) {
        List<JobPostingDTO> jobList = jobMapper.getJobPostingListPaing(paraMap);
        applyReportStatusText(jobList);
        return jobList;
    }

    // 기업의 전체 채용공고 개수를 조회한다.
    @Override
    public int getJobPostingCount(String memberId) {
        return jobMapper.getJobPostingCount(memberId);
    }

    // 기업 소유의 채용공고를 삭제한다.
    @Override
    public int deleteJobPosting(Long jobId, String memberId) {
        return jobMapper.deleteJobPosting(jobId, memberId);
    }

    // 채용공고 상세 정보를 조회한다.
    @Override
    public JobPostingDTO getJobPostingOne(Long jobId) {
        JobPostingDTO dto = jobMapper.getJobPostingOne(jobId);
        if (dto == null) {
            return null;
        }

        dto.setReportStatusText(convertReportProcessStatusText(dto.getReportProcessStatus()));
        dto.setSkillList(jobMapper.getSkillNamesByJobId(jobId));

        if (dto.getEducationLevelName() == null) {
            dto.setEducationLevelName(dto.getEduLevelName());
        }

        return dto;
    }

    // 채용공고 수정 화면용 정보를 조회한다.
    @Override
    public JobPostingEditResponseDTO getJobPostingForEdit(Long jobId) {
        JobPostingEditResponseDTO dto = jobMapper.getJobPostingForEdit(jobId);
        if (dto == null) {
            return null;
        }

        if (!isBlank(dto.getRegionCode())) {
            RegionChainDTO chain = jobMapper.getRegionChain(dto.getRegionCode());
            if (chain != null) {
                dto.setRegionLv1(chain.getRegionLv1());
                dto.setRegionLv2(chain.getRegionLv2());
                dto.setRegionLv3(chain.getRegionLv3());
            }
        }

        dto.setSkillIds(jobMapper.getSkillIdsByJobId(jobId));
        return dto;
    }

    // 채용공고와 기술스택 매핑을 함께 수정한다.
    @Override
    @Transactional
    public int updateJobPosting(JobPostingDTO dto, List<Long> skillIds) {
        JobPostingDTO origin = jobMapper.getJobPostingOne(dto.getJobId());

        if (origin == null) {
            return 0;
        }
        if (!origin.getMemberId().equals(dto.getMemberId())) {
            return 0;
        }
        if (origin.getIsHidden() != null && origin.getIsHidden() == 1) {
            return 0;
        }

        int updated = jobMapper.updateJobPosting(dto);
        if (updated != 1) {
            return updated;
        }

        jobMapper.deleteJobPostingSkills(dto.getJobId());
        if (skillIds != null && !skillIds.isEmpty()) {
            jobMapper.insertNewJobPostingSkills(dto.getJobId(), skillIds);
        }

        return updated;
    }

    // 채용공고와 기술스택 매핑을 함께 등록한다.
    @Override
    @Transactional
    public int insertJobPosting(JobPostingDTO dto, List<Long> skillIds) {
        if (dto.getMemberId() == null) {
            throw new IllegalArgumentException("로그인 정보가 없습니다.");
        }

        int inserted = jobMapper.insertJobPosting(dto);
        if (inserted != 1) {
            return inserted;
        }

        Long jobId = dto.getJobId();
        if (jobId == null) {
            throw new IllegalStateException("insert 후 jobId가 DTO에 세팅되지 않았습니다.");
        }

        if (skillIds != null && !skillIds.isEmpty()) {
            jobMapper.insertNewJobPostingSkills(jobId, skillIds);
        }

        return 1;
    }

    // 공고 마감일과 게시 종료일에 따라 공고 상태를 동기화한다.
    @Override
    @Transactional
    public void refreshJobPostingStatuses() {
        jobMapper.updateJobStatusToDeleted();
        jobMapper.updateJobStatusToClosed();
        jobMapper.updateJobStatusToWaiting();
        jobMapper.updateJobStatusToOpen();
    }

    // 학력 목록을 조회한다.
    @Override
    public List<EducationDTO> selectEduList() {
        return jobMapper.selectEduList();
    }

    // 직무 대분류 목록을 조회한다.
    @Override
    public List<JobCategoryDTO> getRoots() {
        return jobMapper.selectRoots();
    }

    // 직무 하위 분류 목록을 조회한다.
    @Override
    public List<JobCategoryDTO> getChildren(Long parentId) {
        return jobMapper.selectChildren(parentId);
    }

    // 스킬 카테고리와 하위 스킬 목록을 그룹핑해서 조회한다.
    @Override
    public List<SkillCategoryDTO> getSkillCategoryWithSkills() {
        List<SkillJoinRowDTO> rows = jobMapper.selectSkillCategorySkillRows();
        Map<Long, SkillCategoryDTO> grouped = new LinkedHashMap<>();

        for (SkillJoinRowDTO row : rows) {
            SkillCategoryDTO category = grouped.get(row.getSkillCategoryId());
            if (category == null) {
                category = new SkillCategoryDTO();
                category.setSkillCategoryId(row.getSkillCategoryId());
                category.setCategoryName(row.getCategoryName());
                grouped.put(row.getSkillCategoryId(), category);
            }

            if (row.getSkillId() != null) {
                SkillDTO skill = new SkillDTO();
                skill.setSkillId(row.getSkillId());
                skill.setFkSkillCategoryId(row.getSkillCategoryId());
                skill.setSkillName(row.getSkillName());
                category.getSkills().add(skill);
            }
        }

        return new ArrayList<>(grouped.values());
    }

    // 지역 대분류 목록을 조회한다.
    @Override
    public List<RegionDTO> getRegionLevel1() {
        return jobMapper.selectRegionLevel1();
    }

    // 지역 하위 분류 목록을 조회한다.
    @Override
    public List<RegionDTO> getRegionChildren(String parentCode) {
        return jobMapper.selectRegionChildren(parentCode);
    }

    // 지원자 전체 개수를 조회한다.
    @Override
    public int selectApplicantCount(Map<String, Object> paraMap) {
        return applicantMapper.selectApplicantCount(paraMap);
    }

    // 지원자 목록을 페이징 조회한다.
    @Override
    public List<ApplicantListDTO> selectApplicantListPaging(Map<String, Object> paraMap) {
        return applicantMapper.selectApplicantListPaging(paraMap);
    }

    // 지원자 상세 진입 시 미열람 상태를 열람 상태로 변경하고 이력을 남긴다.
    @Override
    @Transactional
    public boolean readApplicantDetail(Map<String, Object> paraMap) {
        Integer currentStatus = applicantMapper.selectApplicantCurrentStatus(paraMap);
        if (currentStatus == null) {
            return false;
        }
        if (currentStatus != 0) {
            return true;
        }

        paraMap.put("prevStatus", 0);
        paraMap.put("newStatus", 1);

        int updateCount = applicantMapper.updateApplicantReadStatus(paraMap);
        if (updateCount != 1) {
            throw new IllegalStateException("열람 처리 실패");
        }

        int historyCount = applicantMapper.insertApplicationHistory(paraMap);
        if (historyCount != 1) {
            throw new IllegalStateException("열람 이력 저장 실패");
        }

        return true;
    }

    // 지원자 상태를 변경하고 이력을 저장한다.
    @Override
    @Transactional
    public boolean updateApplicantStatus(Map<String, Object> paraMap) {
        Integer currentStatus = applicantMapper.selectApplicantCurrentStatus(paraMap);
        Integer prevStatus = (Integer) paraMap.get("prevStatus");
        Integer newStatus = (Integer) paraMap.get("newStatus");

        if (currentStatus == null || prevStatus == null || newStatus == null) {
            return false;
        }
        if (!currentStatus.equals(prevStatus)) {
            return false;
        }
        if (prevStatus.equals(newStatus)) {
            return false;
        }
        if (currentStatus == 0) {
            return false;
        }

        int updateCount = applicantMapper.updateApplicantStatus(paraMap);
        if (updateCount != 1) {
            throw new IllegalStateException("지원 상태 변경 실패");
        }

        int historyCount = applicantMapper.insertApplicationHistory(paraMap);
        if (historyCount != 1) {
            throw new IllegalStateException("지원 상태 이력 저장 실패");
        }

        return true;
    }

    // 기업용 지원자 상세 정보를 조회한다.
    @Override
    public ApplicantDetailDTO getApplicantDetailForCompany(Map<String, Object> paraMap) {
        ApplicantDetailDTO dto = applicantMapper.selectApplicantDetailForCompany(paraMap);
        if (dto != null) {
            dto.setProcessStatusText(convertProcessStatusText(dto.getProcessStatus()));
        }
        return dto;
    }

    // 지원자의 첨부파일 목록을 조회한다.
    @Override
    public List<ImageFileDTO> getApplicationFiles(Long applicationId) {
        return applicantMapper.getApplicationFiles(applicationId);
    }

    // 지원자의 기술스택 목록을 조회한다.
    @Override
    public List<Map<String, Object>> getApplicationTechstackList(Long submittedResumeId) {
        return applicantMapper.getApplicationTechstackList(submittedResumeId);
    }

    // 지원자의 자격증 목록을 조회한다.
    @Override
    public List<Map<String, Object>> getApplicationCertificateList(Long submittedResumeId) {
        return applicantMapper.getApplicationCertificateList(submittedResumeId);
    }

    // 제안서 발송 대상 구직자 목록을 조회한다.
    @Override
    public List<MemberSimpleDTO> getReceiverList() {
        return offerMapper.selectJobSeekerList();
    }

    // 제안서 요약 통계를 조회한다.
    @Override
    public OfferMetricsSummaryDTO selectOfferMetricsSummary(String companyMemberId) {
        return offerMapper.selectOfferMetricsSummary(companyMemberId);
    }

    // 제안서 상태별 통계를 조회한다.
    @Override
    public List<OfferMetricsDTO> selectOfferMetricsByCompany(String companyMemberId) {
        return offerMapper.selectOfferMetricsByCompany(companyMemberId);
    }

    // 제안서 목록을 조회한다.
    @Override
    public List<OfferListDTO> selectOfferList(String companyMemberId) {
        return offerMapper.selectOfferList(companyMemberId);
    }

    // 제안서 상세 정보를 조회한다.
    @Override
    public OfferDetailDTO selectOfferDetail(Long id) {
        return offerMapper.selectOfferDetail(id);
    }

    // 제안서를 등록한다.
    @Override
    @Transactional
    public Long createOfferLetter(OfferCreateRequestDTO req) {
        validateOfferLetterCommon(req.getJobId(), req.getTitle(), req.getMessage());

        int inserted = offerMapper.insertOfferLetter(req);
        if (inserted != 1) {
            throw new IllegalStateException("제안서 등록에 실패했습니다.");
        }

        return req.getOfferLetterId();
    }

    // 제안서를 수정한다.
    @Override
    @Transactional
    public int updateOfferLetter(OfferUpdateRequestDTO req) {
        if (req.getOfferLetterId() == null) {
            throw new IllegalArgumentException("제안서 번호가 없습니다.");
        }

        validateOfferLetterCommon(req.getJobId(), req.getTitle(), req.getMessage());

        int updated = offerMapper.updateOfferLetter(req);
        if (updated == 0) {
            throw new IllegalStateException("삭제되었거나 존재하지 않는 제안서입니다.");
        }

        return updated;
    }

    // 제안서를 삭제한다.
    @Override
    @Transactional
    public int deleteOfferLetter(long offerLetterId, String companyMemberId) {
        int owns = offerMapper.existsOfferLetterOwnedByCompany(offerLetterId, companyMemberId);
        if (owns != 1) {
            return 0;
        }
        return offerMapper.deleteOfferLetter(offerLetterId);
    }

    // 제안서를 실제 수신자에게 발송한다.
    @Override
    @Transactional
    public Long sendOffer(OfferSendRequestDTO req, String companyMemberId) {
        validateOfferSendRequest(req);

        List<String> receiverIds = sanitizeReceiverIds(req.getReceiverMemberIds());
        OfferSendValidationDTO info = offerMapper.selectOfferSendValidationInfo(req.getOfferLetterId());
        validateOfferOwnership(info, companyMemberId);

        LocalDate today = LocalDate.now();
        LocalDate expireDate = LocalDate.parse(req.getExpireAt());
        LocalDate openedAt = toLocalDate(info.getOpenedAt());
        LocalDate deadlineAt = toLocalDate(info.getDeadlineAt());
        LocalDate closedAt = toLocalDate(info.getClosedAt());

        validateOfferJobStatus(today, openedAt, deadlineAt, closedAt);
        validateExpireDate(today, expireDate, openedAt, deadlineAt, closedAt);
        validateOfferReceivers(req.getOfferLetterId(), receiverIds);

        java.sql.Date expireSqlDate = java.sql.Date.valueOf(expireDate);

        Map<String, Object> param = new HashMap<>();
        param.put("offerLetterId", req.getOfferLetterId());
        param.put("expireAt", expireSqlDate);

        offerMapper.insertOfferSubmitSnapshot(param);

        Long offerSubmitId = ((Number) param.get("offerSubmitId")).longValue();
        offerMapper.insertOfferResponses(offerSubmitId, receiverIds);

        return offerSubmitId;
    }

    // 발송 이력이 있는 제안서의 수신 회원 ID 목록을 조회한다.
    @Override
    public List<String> selectSentMemberIdsByOfferLetterId(Long offerLetterId, String companyMemberId) {
        int ownsTemplate = offerMapper.existsOfferLetterOwnedByCompany(offerLetterId, companyMemberId);
        int ownsHistory = offerMapper.existsOfferHistoryOwnedByCompany(offerLetterId, companyMemberId);

        if (ownsTemplate != 1 && ownsHistory != 1) {
            throw new IllegalStateException("권한이 없거나 존재하지 않는 제안서입니다.");
        }

        return offerMapper.selectSentMemberIdsByOfferLetterId(offerLetterId);
    }

    // 제안서 수신자 상세 목록을 조회하고 화면용 상태 문구를 세팅한다.
    @Override
    public List<OfferRecipientDetailDTO> selectOfferRecipientDetailsByOfferLetterId(Long offerLetterId, String companyMemberId) {
        int ownsTemplate = offerMapper.existsOfferLetterOwnedByCompany(offerLetterId, companyMemberId);
        int ownsHistory = offerMapper.existsOfferHistoryOwnedByCompany(offerLetterId, companyMemberId);

        if (ownsTemplate != 1 && ownsHistory != 1) {
            throw new IllegalStateException("권한이 없거나 존재하지 않는 제안서입니다.");
        }

        List<OfferRecipientDetailDTO> list = offerMapper.selectOfferRecipientDetailsByOfferLetterId(offerLetterId);
        if (list == null) {
            return Collections.emptyList();
        }

        for (OfferRecipientDetailDTO dto : list) {
            dto.setResponseStatusText(convertResponseStatusText(dto.getResponseStatus()));
        }

        return list;
    }

    // 삭제된 원본 제안서 중 발송 이력이 있는 목록을 조회한다.
    @Override
    public List<DeletedOfferHistoryDTO> selectDeletedOfferHistoryList(String companyMemberId) {
        return offerMapper.selectDeletedOfferHistoryList(companyMemberId);
    }

    // 포인트 충전을 위한 결제 준비 데이터를 생성한다.
    @Override
    public PaymentReadyResponse preparePointCharge(PaymentReadyRequest req, Authentication authentication) {
        PaymentReadyResponse res = new PaymentReadyResponse();

        if (req == null || req.chargeAmount == null) {
            res.ok = false;
            res.message = "chargeAmount가 필요합니다.";
            return res;
        }

        long chargeAmount = req.chargeAmount;
        if (!(chargeAmount == 100000 || chargeAmount == 200000 || chargeAmount == 500000)) {
            res.ok = false;
            res.message = "허용되지 않은 충전 금액입니다.";
            return res;
        }

        if (authentication == null || isBlank(authentication.getName())) {
            res.ok = false;
            res.message = "로그인 정보가 없습니다.";
            return res;
        }

        String memberId = authentication.getName();
        String orderId = insertPendingPaymentWithRetry(memberId, chargeAmount);

        if (orderId == null) {
            res.ok = false;
            res.message = "결제 준비 중 오류가 발생했습니다.";
            return res;
        }

        res.ok = true;
        res.orderId = orderId;
        res.chargeAmount = chargeAmount;
        res.payAmount = PROJECT_PAY_AMOUNT;
        res.orderName = "포인트 충전(" + (chargeAmount / 10000) + "만원)";
        res.buyerName = "기업회원";
        res.buyerEmail = "";
        res.buyerTel = "";
        return res;
    }

    // 결제 완료 요청을 받아 내부 결제 반영 처리를 시작한다.
    @Override
    public PaymentCompleteResponse completePointCharge(PaymentCompleteRequest req) {
        if (req == null || isBlank(req.merchantUid)) {
            return failPaymentResponse(null, "merchantUid가 필요합니다.");
        }

        return reconcileByOrderId(req.merchantUid, req.impUid);
    }

    // 주문번호 기준으로 결제 재확인을 수행한다.
    @Override
    public PaymentCompleteResponse reconcilePointCharge(String orderId) {
        if (isBlank(orderId)) {
            return failPaymentResponse(null, "orderId가 필요합니다.");
        }

        return reconcileByOrderId(orderId, null);
    }

    // 주문번호 기준으로 외부 결제와 내부 결제 반영 상태를 동기화한다.
    private PaymentCompleteResponse reconcileByOrderId(String orderId, String reqImpUid) {
        Map<String, Object> payment = walletMapper.selectPaymentByOrderId(orderId);
        if (payment == null) {
            return failPaymentResponse(orderId, "주문번호가 존재하지 않습니다.");
        }

        String status = String.valueOf(payment.get("STATUS"));
        Long chargeAmount = toLong(payment.get("CHARGE_AMOUNT"));
        String memberId = String.valueOf(payment.get("FK_MEMBERID"));

        if (STATUS_PAID.equalsIgnoreCase(status)) {
            return buildAlreadyPaidResponse(orderId, memberId, chargeAmount);
        }

        String token = portOneV1Client.getAccessToken();
        if (isBlank(token)) {
            markPaymentVerifying(orderId);
            return failPaymentResponse(orderId, "결제 확인 중입니다. 잠시 후 다시 확인해주세요.");
        }

        PortOneV1Client.PortOnePaymentInfo info = portOneV1Client.getPaymentInfoByMerchantUid(token, orderId);
        if (info == null) {
            markPaymentVerifying(orderId);
            return failPaymentResponse(orderId, "결제 확인 중입니다. 잠시 후 다시 확인해주세요.");
        }

        PaymentCompleteResponse validationResult = validateExternalPayment(orderId, reqImpUid, info);
        if (validationResult != null) {
            return validationResult;
        }

        try {
            return applyChargePayment(orderId, memberId, chargeAmount, info);
        } catch (IllegalStateException e) {
            markPaymentVerifying(orderId);
            log.error("결제 반영 처리 중 오류", e);
            return failPaymentResponse(orderId, "결제는 완료되었을 수 있습니다. 잠시 후 다시 확인해주세요.");
        }
    }

    // 실제 포인트 충전 반영과 거래내역 저장을 처리한다.
    @Transactional
    public PaymentCompleteResponse applyChargePayment(String orderId, String memberId, Long chargeAmount,
                                                      PortOneV1Client.PortOnePaymentInfo info) {
        PaymentCompleteResponse alreadyPaid = updatePaymentToPaid(orderId, memberId, chargeAmount, info);
        if (alreadyPaid != null) {
            return alreadyPaid;
        }

        Long pointWalletId = getOrCreatePointWallet(memberId);
        addChargePoint(memberId, chargeAmount);
        insertChargeTransaction(pointWalletId, orderId, chargeAmount);

        PaymentCompleteResponse res = new PaymentCompleteResponse();
        Long balance = walletMapper.selectPointAvailableBalance(memberId);
        res.ok = true;
        res.orderId = orderId;
        res.paidAmount = chargeAmount;
        res.pointBalance = balance == null ? 0L : balance;
        res.message = "결제가 정상 반영되었습니다.";
        return res;
    }

    // 결제창 취소 시 남아 있는 대기 주문을 취소 상태로 정리한다.
    @Override
    public PaymentCompleteResponse cancelPendingPayment(String orderId) {
        if (isBlank(orderId)) {
            return failPaymentResponse(null, "orderId가 필요합니다.");
        }

        Map<String, Object> payment = walletMapper.selectPaymentByOrderId(orderId);
        if (payment == null) {
            return failPaymentResponse(null, "존재하지 않는 주문입니다.");
        }

        String status = String.valueOf(payment.get("STATUS"));

        if (STATUS_CANCELED.equalsIgnoreCase(status)) {
            PaymentCompleteResponse res = new PaymentCompleteResponse();
            res.ok = true;
            res.orderId = orderId;
            res.message = "이미 취소 처리된 주문입니다.";
            return res;
        }

        if (STATUS_PAID.equalsIgnoreCase(status)) {
            return failPaymentResponse(orderId, "이미 결제가 완료된 주문은 취소 정리할 수 없습니다.");
        }

        int updated = walletMapper.updatePaymentStatusIfCurrent(orderId, STATUS_PENDING, STATUS_CANCELED);
        if (updated == 1) {
            PaymentCompleteResponse res = new PaymentCompleteResponse();
            res.ok = true;
            res.orderId = orderId;
            res.message = "결제 취소가 정상 처리되었습니다.";
            return res;
        }

        Map<String, Object> latestPayment = walletMapper.selectPaymentByOrderId(orderId);
        String latestStatus = latestPayment == null ? null : String.valueOf(latestPayment.get("STATUS"));

        if (STATUS_CANCELED.equalsIgnoreCase(latestStatus)) {
            PaymentCompleteResponse res = new PaymentCompleteResponse();
            res.ok = true;
            res.orderId = orderId;
            res.message = "이미 취소 처리된 주문입니다.";
            return res;
        }

        if (STATUS_PAID.equalsIgnoreCase(latestStatus)) {
            return failPaymentResponse(orderId, "이미 결제가 완료된 주문입니다.");
        }

        return failPaymentResponse(orderId, "결제 취소 처리 중 상태 변경에 실패했습니다.");
    }

    // 지갑/결제 페이지에서 필요한 요약 정보와 목록을 조회한다.
    @Override
    public Map<String, Object> getWalletPageData(String memberId, String tab, int currentShowPageNo, int sizePerPage) {
        Map<String, Object> result = new HashMap<>();

        Long pointBalance = walletMapper.selectPointAvailableBalance(memberId);
        result.put("pointBalance", pointBalance == null ? 0L : pointBalance);

        Map<String, Object> summary = walletMapper.selectPaymentSummary(memberId);
        if (summary == null) {
            summary = Collections.emptyMap();
        }

        result.put("paidTotal", toLong(summary.get("PAID_TOTAL")));
        result.put("pendingTotal", toLong(summary.get("PENDING_TOTAL")));
        result.put("cancelTotal", toLong(summary.get("CANCEL_TOTAL")));

        int startRno = ((currentShowPageNo - 1) * sizePerPage) + 1;
        int endRno = startRno + sizePerPage - 1;

        Map<String, Object> paraMap = new HashMap<>();
        paraMap.put("memberId", memberId);
        paraMap.put("startRno", startRno);
        paraMap.put("endRno", endRno);

        if ("payment".equalsIgnoreCase(tab)) {
            result.put("totalCount", walletMapper.getPaymentCount(memberId));
            result.put("paymentList", walletMapper.selectPaymentListPaging(paraMap));
        } else {
            result.put("totalCount", walletMapper.getPointTxCount(memberId));
            result.put("pointTxList", walletMapper.selectPointTxListPaging(paraMap));
        }

        result.put("currentShowPageNo", currentShowPageNo);
        result.put("sizePerPage", sizePerPage);
        return result;
    }

    // 배너 등록과 이미지 저장, 포인트 차감, 거래내역 저장을 함께 처리한다.
    @Override
    @Transactional
    public void insertBannerWithImage(BannerDTO bannerDto, MultipartFile bannerImage) throws IOException {
        validateBannerRequest(bannerDto, bannerImage);

        Map<String, Object> wallet = walletMapper.selectPointWalletByMemberId(bannerDto.getFkMemberId());
        if (wallet == null) {
            throw new IllegalStateException("포인트 지갑이 없습니다. 먼저 포인트를 충전해 주세요.");
        }

        Long pointWalletId = toLong(wallet.get("POINT_WALLET_ID"));
        long availableBalance = toLong(wallet.get("AVAILABLE_BALANCE"));

        if (availableBalance < BANNER_AD_PRICE) {
            throw new IllegalStateException("포인트가 부족합니다. 현재 포인트: " + availableBalance + "P");
        }

        LocalDate startDate = LocalDate.parse(bannerDto.getStartAt());
        bannerDto.setEndAt(startDate.plusDays(7).toString());
        bannerDto.setStatus("처리중");

        deductBannerPoint(bannerDto.getFkMemberId());

        Long bannerId = bannerMapper.getBannerSeq();
        bannerDto.setBannerId(bannerId);
        bannerMapper.insertBanner(bannerDto);

        saveBannerImage(bannerId, bannerImage);

        walletMapper.insertPointTransactionBannerUse(
                pointWalletId,
                bannerId,
                TX_TYPE_USE,
                STATUS_DONE,
                -BANNER_AD_PRICE
        );
    }

    // 배너 등록 화면에서 보여줄 포인트 및 결제 가능 여부를 조회한다.
    @Override
    public Map<String, Object> getBannerPaymentInfo(String memberId) {
        Map<String, Object> result = new HashMap<>();

        Long pointBalance = walletMapper.selectPointAvailableBalance(memberId);
        long balance = pointBalance == null ? 0L : pointBalance;

        result.put("bannerPrice", BANNER_AD_PRICE);
        result.put("pointBalance", balance);
        result.put("canPay", balance >= BANNER_AD_PRICE);
        return result;
    }

    // 배너 등록 가능한 공고 목록을 조회한다.
    @Override
    public List<JobPostingDTO> getBannerPostingList(String memberId) {
        return bannerMapper.getBannerPostingList(memberId);
    }

    // 기업 배너 전체 개수를 조회한다.
    @Override
    public int getBannerCountByMemberId(String memberId) {
        return bannerMapper.getBannerCountByMemberId(memberId);
    }

    // 기업 배너 목록을 페이징 조회한다.
    @Override
    public List<BannerListDTO> getBannerListByMemberIdPaging(Map<String, Object> paraMap) {
        return bannerMapper.selectBannerListByMemberIdPaging(paraMap);
    }

    // 배너 종료일 기준으로 상태를 동기화한다.
    @Override
    @Transactional
    public void refreshBannerStatuses() {
        bannerMapper.updateBannerStatusToClosed();
    }

    // 마감된 배너를 삭제한다.
    @Override
    @Transactional
    public boolean deleteBanner(Long bannerId, String memberId) {
        int deleted = bannerMapper.deleteBannerByBannerId(bannerId, memberId);
        return deleted > 0;
    }

    // 인재검색용 직무 목록을 조회한다.
    @Override
    public List<JobCategoryDTO> getJobCategoryList() {
        return talentMapper.selectJobCategoryList();
    }

    // 인재검색용 스킬 카테고리 목록을 조회한다.
    @Override
    public List<SkillCategoryDTO> getSkillCategoryList() {
        return talentMapper.selectSkillCategoryList();
    }

    // 인재검색용 스킬 목록을 조회한다.
    @Override
    public List<SkillDTO> getSkillList() {
        return talentMapper.selectSkillList();
    }

    // 공개 대표이력서 목록을 조회한다.
    @Override
    public List<TalentResumeDTO> getPublicPrimaryResumeList(TalentSearchConditionDTO searchDto) {
        List<TalentResumeDTO> list = talentMapper.selectPublicPrimaryResumeList(searchDto);

        for (TalentResumeDTO dto : list) {
            if (!isBlank(dto.getTechStackNamesRaw())) {
                dto.setTechStackNames(Arrays.asList(dto.getTechStackNamesRaw().split("\\|\\|")));
            }
        }

        return list;
    }

    // 공개 대표이력서 개수를 조회한다.
    @Override
    public int getPublicPrimaryResumeCount(TalentSearchConditionDTO searchDto) {
        return talentMapper.selectPublicPrimaryResumeCount(searchDto);
    }

    // 공개 대표이력서 상세 정보를 조회한다.
    @Override
    public TalentResumeDetailDTO getPublicPrimaryResumeDetail(Long resumeId) {
        TalentResumeDetailDTO resume = talentMapper.selectPublicPrimaryResumeDetail(resumeId);
        if (resume == null) {
            return null;
        }

        resume.setEducationList(talentMapper.selectTalentEducationList(resumeId));
        resume.setCareerList(talentMapper.selectTalentCareerList(resumeId));
        resume.setLanguageList(talentMapper.selectTalentLanguageList(resumeId));
        resume.setCertificateList(talentMapper.selectTalentCertificateList(resumeId));
        resume.setPortfolioList(talentMapper.selectTalentPortfolioList(resumeId));
        resume.setAwardList(talentMapper.selectTalentAwardList(resumeId));
        resume.setTechstackList(talentMapper.selectTalentTechstackList(resumeId));
        return resume;
    }
}
