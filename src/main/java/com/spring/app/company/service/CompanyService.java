package com.spring.app.company.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import com.spring.app.common.domain.EducationDTO;
import com.spring.app.common.domain.JobCategoryDTO;
import com.spring.app.common.domain.RegionDTO;
import com.spring.app.common.domain.SkillCategoryDTO;
import com.spring.app.common.domain.SkillDTO;
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
import com.spring.app.company.domain.OfferUpdateRequestDTO;
import com.spring.app.company.domain.PaymentCompleteRequest;
import com.spring.app.company.domain.PaymentCompleteResponse;
import com.spring.app.company.domain.PaymentReadyRequest;
import com.spring.app.company.domain.PaymentReadyResponse;
import com.spring.app.company.domain.TalentResumeDTO;
import com.spring.app.company.domain.TalentResumeDetailDTO;
import com.spring.app.company.domain.TalentSearchConditionDTO;

public interface CompanyService {

    // 기업 상단바 정보 조회
    CompanyTopbarDTO getCompanyTopbarInfo(String memberId);

    // 기업 대시보드 전체 정보 조회
    CompanyDashboardDTO getCompanyDashboard(String memberId);

    // 기업 프로필 조회
    CompanyProfileDTO getCompanyProfile(String memberId);

    // 기업 기본 정보 수정
    CompanyProfileUpdateResponseDTO updateBasicProfile(CompanyProfileUpdateDTO dto);

    // 기업 주소 정보 수정
    CompanyProfileUpdateResponseDTO updateAddressProfile(CompanyProfileUpdateDTO dto);

    // 기업 소개 정보 수정
    CompanyProfileUpdateResponseDTO updateIntroProfile(CompanyProfileUpdateDTO dto);

    // 기업 로고 업로드
    CompanyProfileUpdateResponseDTO uploadCompanyLogo(String memberId, MultipartFile logoFile) throws IOException;

    // 채용공고 목록 조회
    List<JobPostingDTO> getJobPostingList(String memberId);

    // 채용공고 목록 페이징 조회
    List<JobPostingDTO> getJobPostingListPaing(Map<String, Object> paraMap);

    // 채용공고 전체 개수 조회
    int getJobPostingCount(String memberId);

    // 채용공고 삭제
    int deleteJobPosting(Long jobId, String memberId);

    // 채용공고 상세 조회
    JobPostingDTO getJobPostingOne(Long jobId);

    // 수정용 채용공고 정보 조회
    JobPostingEditResponseDTO getJobPostingForEdit(Long jobId);

    // 채용공고 수정
    int updateJobPosting(JobPostingDTO dto, List<Long> skillIds);

    // 채용공고 등록
    int insertJobPosting(JobPostingDTO dto, List<Long> skillIds);

    // 채용공고 상태 동기화
    void refreshJobPostingStatuses();

    // 학력 목록 조회
    List<EducationDTO> selectEduList();

    // 직무 대분류 목록 조회
    List<JobCategoryDTO> getRoots();

    // 직무 하위 분류 목록 조회
    List<JobCategoryDTO> getChildren(Long parentId);

    // 스킬 카테고리와 하위 스킬 목록 조회
    List<SkillCategoryDTO> getSkillCategoryWithSkills();

    // 지역 대분류 목록 조회
    List<RegionDTO> getRegionLevel1();

    // 지역 하위 분류 목록 조회
    List<RegionDTO> getRegionChildren(String parentCode);

    // 지원자 전체 개수 조회
    int selectApplicantCount(Map<String, Object> paraMap);

    // 지원자 목록 페이징 조회
    List<ApplicantListDTO> selectApplicantListPaging(Map<String, Object> paraMap);

    // 지원자 상세 진입 시 읽음 처리
    boolean readApplicantDetail(Map<String, Object> paraMap);

    // 지원자 상태 변경 및 이력 저장
    boolean updateApplicantStatus(Map<String, Object> paraMap);

    // 지원자 상세 정보 조회
    ApplicantDetailDTO getApplicantDetailForCompany(Map<String, Object> paraMap);

    // 지원자 첨부파일 목록 조회
    List<ImageFileDTO> getApplicationFiles(Long applicationId);

    // 지원자 기술스택 목록 조회
    List<Map<String, Object>> getApplicationTechstackList(Long submittedResumeId);

    // 지원자 자격증 목록 조회
    List<Map<String, Object>> getApplicationCertificateList(Long submittedResumeId);

    // 제안서 발송 대상 구직자 목록 조회
    List<MemberSimpleDTO> getReceiverList();

    // 제안서 전체 통계 조회
    OfferMetricsSummaryDTO selectOfferMetricsSummary(String companyMemberId);

    // 제안서 상태별 통계 조회
    List<OfferMetricsDTO> selectOfferMetricsByCompany(String companyMemberId);

    // 제안서 목록 조회
    List<OfferListDTO> selectOfferList(String companyMemberId);

    // 제안서 상세 조회
    OfferDetailDTO selectOfferDetail(Long id);

    // 제안서 등록
    Long createOfferLetter(OfferCreateRequestDTO req);

    // 제안서 수정
    int updateOfferLetter(OfferUpdateRequestDTO req);

    // 제안서 삭제
    int deleteOfferLetter(long offerLetterId, String companyMemberId);

    // 제안서 발송
    Long sendOffer(OfferSendRequestDTO req, String companyMemberId);

    // 제안서를 발송한 회원 ID 목록 조회
    List<String> selectSentMemberIdsByOfferLetterId(Long offerLetterId, String companyMemberId);

    // 제안서 수신자 상세 목록 조회
    List<OfferRecipientDetailDTO> selectOfferRecipientDetailsByOfferLetterId(Long offerLetterId, String companyMemberId);

    // 삭제된 제안서 중 발송 이력이 있는 목록 조회
    List<DeletedOfferHistoryDTO> selectDeletedOfferHistoryList(String companyMemberId);

    // 결제 준비
    PaymentReadyResponse preparePointCharge(PaymentReadyRequest req, Authentication authentication);

    // 결제 완료 처리
    PaymentCompleteResponse completePointCharge(PaymentCompleteRequest req);

    // 결제 재확인
    PaymentCompleteResponse reconcilePointCharge(String orderId);

    // 결제창 취소 시 대기 주문 취소 처리
    PaymentCompleteResponse cancelPendingPayment(String orderId);

    // 지갑 페이지 데이터 조회
    Map<String, Object> getWalletPageData(String memberId, String tab, int currentShowPageNo, int sizePerPage);

    // 배너 등록 및 이미지 저장
    void insertBannerWithImage(BannerDTO bannerDto, MultipartFile bannerImage) throws IOException;

    // 배너 등록 대상 공고 목록 조회
    List<JobPostingDTO> getBannerPostingList(String memberId);

    // 배너 전체 개수 조회
    int getBannerCountByMemberId(String memberId);

    // 배너 목록 페이징 조회
    List<BannerListDTO> getBannerListByMemberIdPaging(Map<String, Object> paraMap);

    // 배너 결제 가능 여부 및 포인트 정보 조회
    Map<String, Object> getBannerPaymentInfo(String memberId);

    // 배너 상태 동기화
    void refreshBannerStatuses();

    // 마감된 배너 삭제
    boolean deleteBanner(Long bannerId, String memberId);

    // 인재검색 직무 목록 조회
    List<JobCategoryDTO> getJobCategoryList();

    // 인재검색 스킬 카테고리 목록 조회
    List<SkillCategoryDTO> getSkillCategoryList();

    // 인재검색 스킬 목록 조회
    List<SkillDTO> getSkillList();

    // 공개 대표이력서 목록 조회
    List<TalentResumeDTO> getPublicPrimaryResumeList(TalentSearchConditionDTO searchDto);

    // 공개 대표이력서 개수 조회
    int getPublicPrimaryResumeCount(TalentSearchConditionDTO searchDto);

    // 공개 대표이력서 상세 조회
    TalentResumeDetailDTO getPublicPrimaryResumeDetail(Long resumeId);
}
