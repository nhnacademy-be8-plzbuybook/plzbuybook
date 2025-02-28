package com.nhnacademy.book.member.domain.service.Impl;

import com.nhnacademy.book.coupon.dto.WelComeCouponRequestDto;
import com.nhnacademy.book.coupon.service.CouponService;
import com.nhnacademy.book.member.domain.*;
import com.nhnacademy.book.member.domain.dto.*;
import com.nhnacademy.book.member.domain.exception.*;
import com.nhnacademy.book.member.domain.repository.MemberCertificationRepository;
import com.nhnacademy.book.member.domain.repository.MemberGradeRepository;
import com.nhnacademy.book.member.domain.repository.MemberRepository;
import com.nhnacademy.book.member.domain.repository.MemberStatusRepository;
import com.nhnacademy.book.member.domain.repository.auth.AuthRepository;
import com.nhnacademy.book.member.domain.repository.auth.MemberAuthRepository;
import com.nhnacademy.book.orderProduct.repository.OrderProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {


    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MemberGradeRepository memberGradeRepository;

    @Mock
    private MemberStatusRepository memberStatusRepository;

    @Mock
    private MemberAuthRepository memberAuthRepository;

    @Mock
    private MemberCertificationRepository memberCertificationRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthRepository authRepository;

    @Mock
    private CouponService couponService;

    @Mock
    private Clock clock;

    @Mock
    private OrderProductRepository orderProductRepository;


    @InjectMocks
    private MemberServiceImpl memberService;

    @BeforeEach
    void setUp() {
    }



    @Test
    @DisplayName("회원 가입 성공")
    void createMember_Success() {
        //given
        MemberCreateRequestDto memberCreateRequestDto = new MemberCreateRequestDto();
        memberCreateRequestDto.setName("윤지호");
        memberCreateRequestDto.setPhone("010-7237-3951");
        memberCreateRequestDto.setEmail("yonnwlgh12@naver.com");
        memberCreateRequestDto.setBirth(LocalDate.now());
        memberCreateRequestDto.setPassword("123456");

        // 기본 등급 및 기본 상태 설정
        MemberGrade memberGrade = new MemberGrade(1L, "NORMAL", new BigDecimal("100.0"), LocalDateTime.now());
        MemberStatus memberStatus = new MemberStatus(1L, "ACTIVE");
        Member member = new Member(1L, memberGrade, memberStatus, "윤지호", "010-7237-3951", "yoonwlgh12@naver.com", LocalDate.now(), "encodedPassword");
        Auth auth = new Auth(2L, "USER");
        MemberAuth memberAuth = new MemberAuth(1L, auth, member);


        //mocking
        when(memberRepository.existsByEmail(memberCreateRequestDto.getEmail())).thenReturn(false);
        when(memberGradeRepository.findById(1L)).thenReturn(Optional.of(memberGrade)); // 기본 등급 mock 설정
        when(memberStatusRepository.findById(1L)).thenReturn(Optional.of(memberStatus)); // 기본 상태 mock 설정
        when(authRepository.findById(2L)).thenReturn(Optional.of(auth));
        when(passwordEncoder.encode(memberCreateRequestDto.getPassword())).thenReturn("encodedPassword");
        when(memberRepository.save(any(Member.class))).thenReturn(member);
        when(memberAuthRepository.save(any(MemberAuth.class))).thenReturn(memberAuth);

        //when
        var response = memberService.createMember(memberCreateRequestDto);

        //then
        assertNotNull(response); // 응답이 null이 아닌지 확인
        assertEquals("윤지호", response.getName()); // 이름 확인
        assertEquals("010-7237-3951", response.getPhone()); // 전화번호 확인
        assertEquals("yoonwlgh12@naver.com", response.getEmail()); // 이메일 확인
        assertEquals("NORMAL", response.getMemberGradeName()); // 기본 등급 이름 확인
        assertEquals("ACTIVE", response.getMemberStateName()); // 기본 상태 이름 확인

        verify(memberAuthRepository, times(1)).save(any(MemberAuth.class));
    }

    @Test
    @DisplayName("회원 가입시 중복 이메일")
    void createMember_DuplicateEmailException() {
        //given
        MemberCreateRequestDto memberCreateRequestDto = new MemberCreateRequestDto();
        memberCreateRequestDto.setName("윤지호");
        memberCreateRequestDto.setPhone("010-7237-3951");
        memberCreateRequestDto.setEmail("yonnwlgh12@naver.com");
        memberCreateRequestDto.setBirth(LocalDate.now());
        memberCreateRequestDto.setPassword("123456");

        //mocking
        when(memberRepository.existsByEmail(memberCreateRequestDto.getEmail())).thenReturn(true);

        //when, then
        assertThrows(DuplicateEmailException.class, () -> memberService.createMember(memberCreateRequestDto));
    }


    @Test
    @DisplayName("회원 등급 없을 때")
    void createMember_MemberGradeNotFoundException() {
        //given
        MemberCreateRequestDto memberCreateRequestDto = new MemberCreateRequestDto();
        memberCreateRequestDto.setName("윤지호");
        memberCreateRequestDto.setPhone("010-7237-3951");
        memberCreateRequestDto.setEmail("yonnwlgh12@naver.com");
        memberCreateRequestDto.setBirth(LocalDate.now());
        memberCreateRequestDto.setPassword("123456");

        //mocking - 기본 등급 조회 실패 Mock
         when(memberGradeRepository.findById(1L)).thenReturn(Optional.empty());

        //when, then
        assertThrows(DefaultMemberGradeNotFoundException.class, () -> memberService.createMember(memberCreateRequestDto));
    }


    @Test
    @DisplayName("회원 상태 없을 때")
    void createMember_MemberStatusNotFoundException() {
        //given
        MemberCreateRequestDto memberCreateRequestDto = new MemberCreateRequestDto();
        memberCreateRequestDto.setName("윤지호");
        memberCreateRequestDto.setPhone("010-7237-3951");
        memberCreateRequestDto.setEmail("yonnwlgh12@naver.com");
        memberCreateRequestDto.setBirth(LocalDate.now());
        memberCreateRequestDto.setPassword("123456");

        MemberGrade memberGrade = new MemberGrade(1L, "NORMAL", new BigDecimal("100.0"), LocalDateTime.now());

        //mocking - 기본 상태 조회 실패 Mock
        when(memberGradeRepository.findById(1L)).thenReturn(Optional.of(memberGrade));
        when(memberStatusRepository.findById(1L)).thenReturn(Optional.empty());

        //when, then
        assertThrows(DefaultStatusGradeNotfoundException.class, () -> memberService.createMember(memberCreateRequestDto));
    }


    @Test
    @DisplayName("비밀번호 암호화 되어있나")
    void createMember_PasswordIsEncoded() {
        // given
        MemberCreateRequestDto memberCreateRequestDto = new MemberCreateRequestDto();
        memberCreateRequestDto.setName("윤지호");
        memberCreateRequestDto.setPhone("010-7237-3951");
        memberCreateRequestDto.setEmail("yonnwlgh12@naver.com");
        memberCreateRequestDto.setBirth(LocalDate.now());
        memberCreateRequestDto.setPassword("123456");

        MemberGrade memberGrade = new MemberGrade(1L, "NORMAL", new BigDecimal("100.0"), LocalDateTime.now());
        MemberStatus memberStatus = new MemberStatus(1L, "ACTIVE");
        Auth defaultAuth = new Auth(2L, "USER");

        //mocking
        when(memberGradeRepository.findById(1L)).thenReturn(Optional.of(memberGrade)); // 기본 등급
        when(memberStatusRepository.findById(1L)).thenReturn(Optional.of(memberStatus)); // 기본 상태
        when(passwordEncoder.encode(memberCreateRequestDto.getPassword())).thenReturn("encodedPassword");
        when(authRepository.findById(2L)).thenReturn(Optional.of(defaultAuth)); // 기본 권한

        Member savedMember = new Member();
        savedMember.setPassword("encodedPassword");

        when(memberRepository.save(any(Member.class))).thenReturn(savedMember);

        ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);

        // when
        memberService.createMember(memberCreateRequestDto);

        // then
        verify(memberRepository).save(captor.capture());
        Member capturedMember = captor.getValue();
        assertNotNull(capturedMember);  // capturedMember가 null이 아닌지 확인
        assertEquals("encodedPassword", capturedMember.getPassword()); // 비밀번호 암호화 확인
    }



    @Test
    @DisplayName("default가 normal, active인가")
    void testMemberCreationWithDefaultValues() {
        // given
        MemberCreateRequestDto memberCreateRequestDto = new MemberCreateRequestDto();
        memberCreateRequestDto.setName("test");
        memberCreateRequestDto.setPhone("010-1234-5678");
        memberCreateRequestDto.setEmail("test@naver.com");
        memberCreateRequestDto.setBirth(LocalDate.now());
        memberCreateRequestDto.setPassword("1234");


        // MemberGrade와 MemberStatus 설정
        MemberGrade memberGrade = new MemberGrade(1L, "NORMAL", new BigDecimal("100.0"), LocalDateTime.now());
        MemberStatus memberStatus = new MemberStatus(1L, "ACTIVE");
        Auth defaultAuth = new Auth(2L, "USER");

        when(memberGradeRepository.findById(1L)).thenReturn(Optional.of(memberGrade));
        when(memberStatusRepository.findById(1L)).thenReturn(Optional.of(memberStatus));
        when(authRepository.findById(2L)).thenReturn(Optional.of(defaultAuth));

        // 실제 회원 정보
        Member member = new Member(1L, memberGrade, memberStatus, "윤지호", "010-7237-3951", "yoonwlgh12@naver.com", LocalDate.now(), "encodedPassword");

        // save 메서드 mock
         when(memberRepository.save(any(Member.class))).thenReturn(member);

        // when
        var response = memberService.createMember(memberCreateRequestDto);

        // then
        // response가 null이 아니고, 정상적으로 "NORMAL"과 "ACTIVE" 값이 저장되었는지 확인
        assertNotNull(response);
        assertNotNull(response.getMemberGradeName());
        assertNotNull(response.getMemberStateName());

        // 기본값으로 'NORMAL'과 'ACTIVE'가 설정된 값이 맞는지 확인
        assertEquals("NORMAL", response.getMemberGradeName());  // MemberGrade가 NORMAL로 설정되었는지 확인
        assertEquals("ACTIVE", response.getMemberStateName());  // MemberStatus가 ACTIVE로 설정되었는지 확인

        verify(memberAuthRepository, times(1)).save(any(MemberAuth.class));
    }


    @Test
    @DisplayName("default grade 조회를 실패 했을 시")
    void creteMember_memberGradeNotFoundException() {
        //given
        MemberCreateRequestDto memberCreateRequestDto = new MemberCreateRequestDto();
        memberCreateRequestDto.setName("윤지호");
        memberCreateRequestDto.setPhone("010-7237-3951");
        memberCreateRequestDto.setEmail("yonnwlgh12@naver.com");
        memberCreateRequestDto.setBirth(LocalDate.now());
        memberCreateRequestDto.setPassword("123456");

        //mocking
        when(memberGradeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(DefaultMemberGradeNotFoundException.class, () -> memberService.createMember(memberCreateRequestDto));
    }

    @Test
    @DisplayName("default 상태가 없을 때 오류 처리")
    void createMember_memberStatusNotFoundException() {
        //given
        MemberCreateRequestDto memberCreateRequestDto = new MemberCreateRequestDto();
        memberCreateRequestDto.setName("윤지호");
        memberCreateRequestDto.setPhone("010-7237-3951");
        memberCreateRequestDto.setEmail("yonnwlgh12@naver.com");
        memberCreateRequestDto.setBirth(LocalDate.now());
        memberCreateRequestDto.setPassword("123456");

        MemberGrade memberGrade = new MemberGrade(1L, "NORMAL", new BigDecimal("100.0"), LocalDateTime.now());

        //mocking
        when(memberGradeRepository.findById(1L)).thenReturn(Optional.of(memberGrade));
        when(memberStatusRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(DefaultStatusGradeNotfoundException.class, () -> memberService.createMember(memberCreateRequestDto));
    }

    @Test
    @DisplayName("회원가입시 Welcome 쿠폰 발급 요청이 호출되는지")
    void createMember_Welcome() {
        // 회원가입 요청
        MemberCreateRequestDto memberCreateRequestDto = new MemberCreateRequestDto();
        memberCreateRequestDto.setName("윤지호");
        memberCreateRequestDto.setPhone("010-7237-3951");
        memberCreateRequestDto.setEmail("yonnwlgh12@naver.com");
        memberCreateRequestDto.setBirth(LocalDate.now());
        memberCreateRequestDto.setPassword("123456");

        // 회원 생성
        MemberGrade memberGrade = new MemberGrade(1L, "NORMAL", new BigDecimal("100.0"), LocalDateTime.now());
        MemberStatus memberStatus = new MemberStatus(1L, "ACTIVE");
        Member member = new Member(1L, memberGrade, memberStatus, "윤지호", "010-7237-3951", "yonnwlgh12@naver.com", LocalDate.now(), "encodedPassword");
        Auth auth = new Auth(2L, "USER");
        MemberAuth memberAuth = new MemberAuth(1L, auth, member);

        when(memberRepository.existsByEmail(memberCreateRequestDto.getEmail())).thenReturn(false);
        when(memberGradeRepository.findById(1L)).thenReturn(Optional.of(memberGrade));
        when(memberStatusRepository.findById(1L)).thenReturn(Optional.of(memberStatus));
        when(authRepository.findById(2L)).thenReturn(Optional.of(auth));
        when(memberRepository.save(any(Member.class))).thenReturn(member);
        when(memberAuthRepository.save(any(MemberAuth.class))).thenReturn(memberAuth);

        memberService.createMember(memberCreateRequestDto);

        // 쿠폰 요청 메서드 호출 확인
        verify(couponService, times(1)).issueWelcomeCoupon(any(WelComeCouponRequestDto.class));
    }

    @Test
    @DisplayName("이메일로 회원을 조회할 때 값이 잘 나오는 지")
    void getMemberByEmail() {
        MemberGrade memberGrade = new MemberGrade(1L, "NORMAL", new BigDecimal("100.0"), LocalDateTime.now());
        MemberStatus memberStatus = new MemberStatus(1L, "ACTIVE");
        Auth auth = new Auth(1L, "ADMIN");
        Member member = new Member(1L, memberGrade, memberStatus, "윤지호", "010-7237-3951", "yoonwlgh12@naver.com", LocalDate.now(), "Password");
        MemberAuth memberAuth = new MemberAuth(1L, auth, member);
        List<MemberAuth> memberAuthList = List.of(memberAuth);

        when(memberRepository.findByEmail("yoonwlgh12@naver.com")).thenReturn(Optional.of(member));
        when(memberAuthRepository.findByMember(member)).thenReturn(memberAuthList);

        var response = memberService.getMemberByEmail("yoonwlgh12@naver.com");

        assertNotNull(response);
        assertEquals("yoonwlgh12@naver.com", response.getEmail());
        assertEquals("ADMIN", response.getAuthName());

        verify(memberRepository).findByEmail("yoonwlgh12@naver.com");
        verify(memberAuthRepository).findByMember(member);
    }

    @Test
    @DisplayName("비어 있는 권한 리스트 예외 처리 테스트")
    void getMemberByEmail_exception() {
        MemberGrade memberGrade = new MemberGrade(1L, "NORMAL", new BigDecimal("100.0"), LocalDateTime.now());
        MemberStatus memberStatus = new MemberStatus(1L, "ACTIVE");
        Member member = new Member(1L, memberGrade, memberStatus, "윤지호", "010-7237-3951", "yoonwlgh12@naver.com", LocalDate.now(), "Password");

        when(memberRepository.findByEmail("yoonwlgh12@naver.com")).thenReturn(Optional.of(member));
        when(memberAuthRepository.findByMember(member)).thenReturn(List.of());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> memberService.getMemberByEmail(member.getEmail()));

        assertEquals("해당 멤버에 대한 권한 정보를 찾을 수 없습니다", exception.getMessage());

        verify(memberRepository).findByEmail(member.getEmail());
        verify(memberAuthRepository).findByMember(member);
        verifyNoInteractions(passwordEncoder);


    }

    @Test
    @DisplayName("이메일로 회원을 조회할 때 예외 처리")
    void getMemberByEmail_MemberEmailNotFoundException() {
        String email = "yoonwlgh12@naver.com";
        when(memberRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(MemberEmailNotFoundException.class, () -> memberService.getMemberByEmail(email));

        verify(memberRepository).findByEmail(email);
    }

    @Test
    @DisplayName("이메일로 회원을 조회할 때 값이 잘 나오는지 (myPage)")
    void getMemberMyByEmail_success() {
        MemberGrade memberGrade = new MemberGrade(1L, "NORMAL", new BigDecimal("100.0"), LocalDateTime.now());
        MemberStatus memberStatus = new MemberStatus(1L, "ACTIVE");
        Member member = new Member(1L, memberGrade, memberStatus, "윤지호", "010-7237-3951", "yoonwlgh12@naver.com", LocalDate.now(), "Password");

        when(memberRepository.findByEmailWithGradeAndStatus(member.getEmail())).thenReturn(Optional.of(member));

        var response = memberService.getMemberMyByEmail(member.getEmail());

        assertNotNull(response);
        assertEquals(member.getName(), response.getName());
        assertEquals(member.getPhone(), response.getPhone());
        assertEquals(member.getPassword(), response.getPassword());
        assertEquals(member.getEmail(), response.getEmail());
        assertEquals(member.getBirth(), response.getBirth());
        assertEquals(member.getMemberGrade().getMemberGradeName(), response.getMemberGradeName());
        assertEquals(member.getMemberStatus().getMemberStateName(), response.getMemberStateName());
    }

    @Test
    @DisplayName("탈퇴한 회원은 로그인시 예외처리 하는지")
    void testGetMemberMyByEmail_WithdrawnMember() {
        // given
        String email = "test@naver.com";
        Member withdrawnMember = new Member();
        withdrawnMember.setEmail(email);
        withdrawnMember.setName("Test");
        withdrawnMember.setPhone("010-2456-7890");
        withdrawnMember.setPassword("password");
        withdrawnMember.setBirth(LocalDate.of(2002, 7, 23));
        withdrawnMember.setMemberGrade(new MemberGrade(1L, "NORMAL", new BigDecimal("100.0"), LocalDateTime.now()));
        withdrawnMember.setMemberStatus(new MemberStatus(3L, "WITHDRAWAL"));

        when(memberRepository.findByEmailWithGradeAndStatus(email))
                .thenReturn(Optional.of(withdrawnMember));

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            memberService.getMemberMyByEmail(email);
        });

        assertEquals("탈퇴한 회원입니다.", exception.getMessage());
    }


    @Test
    void getMemberMyByEmail_memberEmailNotFoundException() {
        String email = "yoonwlgh12@naver.com";
        when(memberRepository.findByEmailWithGradeAndStatus(email)).thenReturn(Optional.empty());

        MemberEmailNotFoundException exception = assertThrows(MemberEmailNotFoundException.class, () -> memberService.getMemberMyByEmail(email));

        assertEquals("해당 이메일의 회원이 존재하지 않다!", exception.getMessage());

    }

    @Test
    @DisplayName("id 로 특정 회원 조회 잘 되는지")
    void getMemberById() {
        MemberGrade memberGrade = new MemberGrade(1L, "NORMAL", new BigDecimal("100.0"), LocalDateTime.now());
        MemberStatus memberStatus = new MemberStatus(1L, "ACTIVE");
        Member member = new Member(1L, memberGrade, memberStatus, "윤지호", "010-7237-3951", "yoonwlgh12@naver.com", LocalDate.of(2000, 3, 9), "Password");

        //id로 멤버 조회
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        var response = memberService.getMemberById(1L);

        assertNotNull(response);
        assertEquals("윤지호", response.getName());
        assertEquals("010-7237-3951", response.getPhone());
        assertEquals("yoonwlgh12@naver.com", response.getEmail());
        assertEquals(LocalDate.of(2000, 3, 9), response.getBirth());

        assertEquals("NORMAL", response.getMemberGradeName());
        assertEquals("ACTIVE", response.getMemberStateName());

        verify(memberRepository).findById(1L);
    }


    @Test
    @DisplayName("id로 회원 조회할 때 해당하는 아이디 없으면 예외처리 잘하는지")
    void getMemberById_MemberIdNotFoundException() {
        when(memberRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(MemberIdNotFoundException.class, () -> memberService.getMemberById(1L));

        verify(memberRepository).findById(1L);
    }

    @Test
    @DisplayName("회원 수정을 잘하는지")
    void memberModify_success() {
        Long memberId = 1L;
        Member member = new Member();
        member.setName("윤지호");
        member.setPhone("010-7237-3951");
        member.setEmail("yoonwlgh12@naver.com");
        member.setBirth(LocalDate.of(2000, 3, 9));
        member.setPassword("encodedPassword");

        MemberModifyRequestDto memberModifyRequestDto = new MemberModifyRequestDto();
        memberModifyRequestDto.setName("융징홍");
        memberModifyRequestDto.setPhone("010-1111-1111");
        memberModifyRequestDto.setEmail("yoonwlgh123@naver.com");
        memberModifyRequestDto.setBirth(LocalDate.of(2000, 3, 10));
        memberModifyRequestDto.setPassword("newPassword");

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
        when(memberRepository.existsByEmail(memberModifyRequestDto.getEmail())).thenReturn(false);
        when(memberRepository.save(any(Member.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = memberService.modify(memberId, memberModifyRequestDto);

        assertEquals("융징홍", response.getName());
        assertEquals("010-1111-1111", response.getPhone());
        assertEquals("yoonwlgh123@naver.com", response.getEmail());
        assertEquals(LocalDate.of(2000, 3, 10), response.getBirth());
        verify(passwordEncoder).encode("newPassword");
        verify(memberRepository).findById(memberId);
        verify(memberRepository, times(1)).save(any(Member.class));
    }


    @Test
    @DisplayName("회원 수정 값이 없을 때 예외를 잘 처리 하는지")
    void memberModify_DuplicateMemberModificationException() {
        Member member = new Member();
        member.setMemberId(1L);
        member.setName("윤지호");
        member.setPhone("010-7237-3951");
        member.setEmail("yoonwlgh12@naver.com");
        member.setBirth(LocalDate.of(2000, 3, 9));
        member.setPassword("EncodedPassword");

        MemberModifyRequestDto memberModifyRequestDto = new MemberModifyRequestDto();
        memberModifyRequestDto.setName(member.getName());
        memberModifyRequestDto.setPhone(member.getPhone());
        memberModifyRequestDto.setEmail(member.getEmail());
        memberModifyRequestDto.setBirth(member.getBirth());
        memberModifyRequestDto.setPassword("password");

        when(memberRepository.findById(member.getMemberId())).thenReturn(Optional.of(member));
        when(passwordEncoder.matches("password", "EncodedPassword")).thenReturn(true);

        assertThrows(DuplicateMemberModificationException.class, () -> memberService.modify(member.getMemberId(), memberModifyRequestDto));

        verify(memberRepository).findById(member.getMemberId());
        verify(passwordEncoder).matches("password", "EncodedPassword");

    }


    @Test
    @DisplayName("회원 수정하는데 중복된 이메일로 수정하려 할 떄")
    void memberModify_DuplicateEmailException() {
        Member member = new Member();
        member.setMemberId(1L);
        member.setName("윤지호");
        member.setPhone("010-7237-3951");
        member.setEmail("yoonwlgh12@naver.com");
        member.setBirth(LocalDate.of(2000, 3, 9));
        member.setPassword("EncodedPassword");

        MemberModifyRequestDto memberModifyRequestDto = new MemberModifyRequestDto();
        memberModifyRequestDto.setName("융징홍");
        memberModifyRequestDto.setPhone("010-1111-1111");
        memberModifyRequestDto.setEmail("duplicate@naver.com");
        memberModifyRequestDto.setBirth(LocalDate.of(2000, 3, 10));
        memberModifyRequestDto.setPassword("password");

        when(memberRepository.findById(member.getMemberId())).thenReturn(Optional.of(member));
        when(memberRepository.existsByEmail("duplicate@naver.com")).thenReturn(true);

        assertThrows(DuplicateEmailException.class, () -> memberService.modify(member.getMemberId(), memberModifyRequestDto));

    }

    @Test
    @DisplayName("회원 수정을 잘하는지(header email)")
    void updateMember_success() {
        String originalEmail = "yoonwlgh12@naver.com";
        String newEmail = "yoonwlgh123@naver.com";
        Member member = new Member();
        member.setName("윤지호");
        member.setPhone("010-7237-3951");
        member.setEmail(originalEmail);
        member.setBirth(LocalDate.of(2000, 3, 9));
        member.setPassword("encodedPassword");

        MemberModifyRequestDto memberModifyRequestDto = new MemberModifyRequestDto();
        memberModifyRequestDto.setName("융징홍");
        memberModifyRequestDto.setPhone("010-1111-1111");
        memberModifyRequestDto.setEmail(newEmail);
        memberModifyRequestDto.setBirth(LocalDate.of(2000, 3, 10));
        memberModifyRequestDto.setPassword("newPassword");

        when(memberRepository.findByEmail(member.getEmail())).thenReturn(Optional.of(member));
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
        when(memberRepository.existsByEmail(memberModifyRequestDto.getEmail())).thenReturn(false);

        memberService.updateMember(originalEmail, memberModifyRequestDto);

        assertEquals("융징홍", member.getName());
        assertEquals("010-1111-1111", member.getPhone());
        assertEquals("yoonwlgh123@naver.com", member.getEmail());
        assertEquals("encodedNewPassword", member.getPassword());
        assertEquals(LocalDate.of(2000, 3, 10), member.getBirth());

        verify(memberRepository, times(1)).findByEmail(originalEmail);
        verify(memberRepository, times(1)).existsByEmail(newEmail);


    }


    @Test
    @DisplayName("회원 수정 값이 없을 때 예외를 잘 처리 하는지(header email)")
    void updateMember_DuplicateMemberModificationException() {
        Member member = new Member();
        member.setMemberId(1L);
        member.setName("윤지호");
        member.setPhone("010-7237-3951");
        member.setEmail("yoonwlgh12@naver.com");
        member.setBirth(LocalDate.of(2000, 3, 9));
        member.setPassword("EncodedPassword");

        MemberModifyRequestDto memberModifyRequestDto = new MemberModifyRequestDto();
        memberModifyRequestDto.setName(member.getName());
        memberModifyRequestDto.setPhone(member.getPhone());
        memberModifyRequestDto.setEmail(member.getEmail());
        memberModifyRequestDto.setBirth(member.getBirth());
        memberModifyRequestDto.setPassword("password");

        when(memberRepository.findByEmail(member.getEmail())).thenReturn(Optional.of(member));
        when(passwordEncoder.matches("password", "EncodedPassword")).thenReturn(true);

        assertThrows(DuplicateMemberModificationException.class, () -> memberService.updateMember(member.getEmail(), memberModifyRequestDto));

        verify(memberRepository).findByEmail(member.getEmail());
        verify(passwordEncoder).matches("password", "EncodedPassword");

    }


    @Test
    @DisplayName("회원 수정하는데 중복된 이메일로 수정하려 할 떄(header email)")
    void updateMember_DuplicateEmailException() {
        Member member = new Member();
        member.setMemberId(1L);
        member.setName("윤지호");
        member.setPhone("010-7237-3951");
        member.setEmail("yoonwlgh12@naver.com");
        member.setBirth(LocalDate.of(2000, 3, 9));
        member.setPassword("EncodedPassword");

        MemberModifyRequestDto memberModifyRequestDto = new MemberModifyRequestDto();
        memberModifyRequestDto.setName("융징홍");
        memberModifyRequestDto.setPhone("010-1111-1111");
        memberModifyRequestDto.setEmail("duplicate@naver.com");
        memberModifyRequestDto.setBirth(LocalDate.of(2000, 3, 10));
        memberModifyRequestDto.setPassword("password");

        when(memberRepository.findByEmail(member.getEmail())).thenReturn(Optional.of(member));
        when(memberRepository.existsByEmail("duplicate@naver.com")).thenReturn(true);

        assertThrows(DuplicateEmailException.class, () -> memberService.updateMember(member.getEmail(), memberModifyRequestDto));

    }

    @Test
    @DisplayName("삭제시 회원 상태가 withdraw로 변경 되는지")
    void withdrawMember_Success() {
        MemberStatus withdrawalStatus = new MemberStatus();
        withdrawalStatus.setMemberStateName("WITHDRAWAL");

        Member member = new Member();
        member.setMemberId(1L);
        member.setName("윤지호");
        member.setPhone("010-7237-3951");
        member.setEmail("yoonwlgh12@naver.com");
        member.setMemberStatus(new MemberStatus());

        when(memberRepository.findById(member.getMemberId())).thenReturn(Optional.of(member));
        when(memberStatusRepository.findByMemberStateName("WITHDRAWAL")).thenReturn(Optional.of(withdrawalStatus));

        memberService.withdrawMember(member.getMemberId());

        verify(memberRepository).save(any(Member.class));
        assertEquals("WITHDRAWAL", member.getMemberStatus().getMemberStateName());
    }


    @Test
    @DisplayName("withdraw 상태가 없으면 오류 발생 잘 시키는지")
    void withdrawMember_MemberGradeNotFoundException() {
        when(memberStatusRepository.findByMemberStateName("WITHDRAWAL")).thenReturn(Optional.empty());

        assertThrows(MemberGradeNotFoundException.class, () -> memberService.withdrawMember(1L));
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    @DisplayName("탈퇴하려는 id에 해당하는 회원을 불러올 때 없으면 오류 발생 잘 시키는 지")
    void withdrawMember_MemberNotFoundException() {
        MemberStatus withdrawalStatus = new MemberStatus();
        withdrawalStatus.setMemberStateName("WITHDRAWAL");

        when(memberStatusRepository.findByMemberStateName("WITHDRAWAL")).thenReturn(Optional.of(withdrawalStatus));
        when(memberRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(MemberIdNotFoundException.class, () -> memberService.withdrawMember(1L));
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    @DisplayName("이메일로 조회 후 회원 탈퇴")
    void withdrawState_Success() {
        String email = "test@naver.com";
        MemberGrade memberGrade = new MemberGrade(1L, "NORMAL", new BigDecimal("10000.0"), LocalDateTime.now());
        MemberStatus withdrawStatus = new MemberStatus(1L, "WITHDRAWAL");
        MemberStatus activeStatus = new MemberStatus(2L, "ACTIVE");
        Member member = new Member(1L, memberGrade, activeStatus, "Test", "010-1234-5678", email, LocalDate.of(2002, 7, 23), "Password");

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(memberStatusRepository.findByMemberStateName("WITHDRAWAL")).thenReturn(Optional.of(withdrawStatus));

        memberService.withdrawState(email);

        assertEquals("WITHDRAWAL", member.getMemberStatus().getMemberStateName());
        verify(memberRepository, times(1)).save(member);
    }


    @Test
    @DisplayName("회원 조회를 성공적으로 하는가")
    void getMembers_success() {
        MemberSearchRequestDto memberSearchRequestDto = new MemberSearchRequestDto();
        memberSearchRequestDto.setPage(0);
        memberSearchRequestDto.setSize(10);

        MemberGrade memberGrade = new MemberGrade(1L, "NORMAL", new BigDecimal("100.0"), LocalDateTime.now());
        MemberStatus memberStatus = new MemberStatus(1L, "ACTIVE");
        Member member = new Member(1L, memberGrade, memberStatus, "윤지호", "010-7237-3951", "yoonwlgh12@naver.com", LocalDate.of(2000, 3, 9), "Password");
        Member member2 = new Member(2L, memberGrade, memberStatus, "윤지호2", "010-7237-3952", "yoonwlgh123@naver.com", LocalDate.of(2001, 3, 9), "Password");

        Page<Member> page = new PageImpl<>(List.of(member, member2));

        when(memberRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<MemberSearchResponseDto> response = memberService.getMembers(memberSearchRequestDto);

        assertNotNull(response);
        assertEquals(2, response.getContent().size());
        assertEquals("윤지호", response.getContent().getFirst().getName());
        assertEquals("010-7237-3951", response.getContent().getFirst().getPhone());
        assertEquals("윤지호2", response.getContent().get(1).getName());
        assertEquals("010-7237-3952", response.getContent().get(1).getPhone());

    }

    @Test
    @DisplayName("회원 조회할 값이 없을 시 예외 처리를 잘하는가")
    void getMembers_MemberNotFoundException() {
        MemberSearchRequestDto memberSearchRequestDto = new MemberSearchRequestDto();
        memberSearchRequestDto.setPage(0);
        memberSearchRequestDto.setSize(10);

        Page<Member> page = Page.empty();


        when(memberRepository.findAll(any(Pageable.class))).thenReturn(page);

        MemberNotFoundException exception = assertThrows(MemberNotFoundException.class, () -> {
            memberService.getMembers(memberSearchRequestDto);
        });

        assertEquals("등록된 회원이 없다!", exception.getMessage());

    }

    @Test
    @DisplayName("회원 상태를 ACTIVE로 변경시키는지")
    void updateActiveStatus_success() {
        String email = "test@naver.com";
        MemberGrade memberGrade = new MemberGrade(1L, "NORMAL", new BigDecimal("10000.0"), LocalDateTime.now());
        MemberStatus dormantStatus = new MemberStatus(2L,"DORMANT");
        MemberStatus activeStatus = new MemberStatus(1L, "ACTIVE");
        Member member = new Member(1L, memberGrade, dormantStatus, "test", "010-1234-5678", "test@naver.com", LocalDate.of(2002, 7, 23), "Password");


        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(memberStatusRepository.findByMemberStateName("ACTIVE")).thenReturn(Optional.of(activeStatus));

        memberService.updateActiveStatus(email);

        assertEquals("ACTIVE", member.getMemberStatus().getMemberStateName());
        verify(memberRepository).save(member);

    }

    @Test
    @DisplayName("회원 상태가 이미 ACTIVE 일때 변경하지 않는지")
    void updateActiveStatus_alreadyActive() {
        String email = "test@naver.com";
        MemberGrade memberGrade = new MemberGrade(1L, "NORMAL", new BigDecimal("10000.0"), LocalDateTime.now());
        MemberStatus activeStatus = new MemberStatus(1L, "ACTIVE");
        Member member = new Member(1L, memberGrade, activeStatus, "test", "010-1234-5678", "test@naver.com", LocalDate.of(2002, 7, 23), "Password");

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(memberStatusRepository.findByMemberStateName("ACTIVE")).thenReturn(Optional.of(activeStatus));

        memberService.updateActiveStatus(email);

        verify(memberRepository, never()).save(any());
    }

    @Test
    @DisplayName("관리자가 회원의 정보를 수정")
    void updateMemberByAdmin_success() {
        MemberGrade memberGrade = new MemberGrade(1L, "NORMAL", new BigDecimal("100.0"), LocalDateTime.now());
        MemberStatus memberStatus = new MemberStatus(1L, "ACTIVE");

        Member existingMember = new Member();
        existingMember.setName("윤지호");
        existingMember.setPhone("010-7237-3951");
        existingMember.setEmail("yoonwlgh12@naver.com");
        existingMember.setBirth(LocalDate.of(2000, 3, 9));
        existingMember.setPassword("encodedPassword");
        existingMember.setMemberGrade(memberGrade);
        existingMember.setMemberStatus(memberStatus);

        when(memberRepository.findByEmail("yoonwlgh12@naver.com")).thenReturn(Optional.of(existingMember));
        when(memberRepository.findByPhone("010-7237-3952")).thenReturn(Optional.empty());
        when(memberRepository.findByEmail("newemail@naver.com")).thenReturn(Optional.empty());


        MemberGrade newGrade = new MemberGrade();
        newGrade.setMemberGradeId(2L);
        when(memberGradeRepository.findById(2L))
                .thenReturn(Optional.of(newGrade));

        MemberStatus newStatus = new MemberStatus();
        newStatus.setMemberStateId(2L);
        when(memberStatusRepository.findById(2L))
                .thenReturn(Optional.of(newStatus));

        MemberModifyByAdminRequestDto requestDto = new MemberModifyByAdminRequestDto();
        requestDto.setOriginalEmail("yoonwlgh12@naver.com");
        requestDto.setName("test");
        requestDto.setPhone("010-7237-3952");
        requestDto.setEmail("newemail@naver.com");
        requestDto.setBirth(LocalDate.of(1995, 5, 15));
        requestDto.setMemberGradeId(2L);
        requestDto.setMemberStateId(2L);

        memberService.updateMemberByAdmin("yoonwlgh12@naver.com", requestDto);

        assertEquals("test", existingMember.getName());
        assertEquals("010-7237-3952", existingMember.getPhone());
        assertEquals("newemail@naver.com", existingMember.getEmail());
        assertEquals(LocalDate.of(1995, 5, 15), existingMember.getBirth());
        assertEquals(2L, existingMember.getMemberGrade().getMemberGradeId());
        assertEquals(2L, existingMember.getMemberStatus().getMemberStateId());

        verify(memberRepository).findByEmail("yoonwlgh12@naver.com");
        verify(memberRepository).findByEmail("newemail@naver.com");
        verify(memberRepository).findByPhone("010-7237-3952");
        verify(memberGradeRepository).findById(2L);
        verify(memberStatusRepository).findById(2L);

    }

    @Test
    @DisplayName("같은 이메일로 수정할 때 예외")
    void updateMemberByAdmin_DuplicateEmailException()  {
        // 기존 회원 데이터
        MemberGrade memberGrade = new MemberGrade(1L, "NORMAL", new BigDecimal("100.0"), LocalDateTime.now());
        MemberStatus memberStatus = new MemberStatus(1L, "ACTIVE");

        Member existingMember = new Member();
        existingMember.setName("윤지호");
        existingMember.setPhone("010-7237-3952");
        existingMember.setEmail("yoonwlgh123@naver.com");
        existingMember.setBirth(LocalDate.of(2000, 3, 9));
        existingMember.setPassword("encodedPassword");
        existingMember.setMemberGrade(memberGrade);
        existingMember.setMemberStatus(memberStatus);

        when(memberRepository.findByEmail("yoonwlgh123@naver.com")).thenReturn(Optional.of(existingMember));

        Member otherMember = new Member();
        otherMember.setMemberId(2L);
        otherMember.setEmail("yoonwlgh12@naver.com");

        when(memberRepository.findByEmail("yoonwlgh12@naver.com")).thenReturn(Optional.of(otherMember));

        MemberModifyByAdminRequestDto requestDto = new MemberModifyByAdminRequestDto();
        requestDto.setOriginalEmail("yoonwlgh123@naver.com");
        requestDto.setName("test");
        requestDto.setEmail("yoonwlgh12@naver.com");

        assertThrows(DuplicateEmailException.class, () ->
                memberService.updateMemberByAdmin("yoonwlgh123@naver.com", requestDto)
        );


        verify(memberRepository).findByEmail("yoonwlgh12@naver.com");


    }


    @Test
    @DisplayName("같은 전화번호 수정할 때 예외")
    void updateMemberByAdmin_DuplicatePhoneException() {
        // 기존 회원 데이터
        MemberGrade memberGrade = new MemberGrade(1L, "NORMAL", new BigDecimal("100.0"), LocalDateTime.now());
        MemberStatus memberStatus = new MemberStatus(1L, "ACTIVE");

        Member existingMember = new Member();
        existingMember.setName("윤지호");
        existingMember.setPhone("010-7237-3952");
        existingMember.setEmail("yoonwlgh12@naver.com");
        existingMember.setBirth(LocalDate.of(2000, 3, 9));
        existingMember.setPassword("encodedPassword");
        existingMember.setMemberGrade(memberGrade);
        existingMember.setMemberStatus(memberStatus);

        // 기존 회원 조회 Mock 설정
        when(memberRepository.findByEmail("yoonwlgh12@naver.com")).thenReturn(Optional.of(existingMember));

        // 중복 전화번호를 가진 다른 회원 Mock 설정
        Member otherMember = new Member();
        otherMember.setMemberId(2L);
        otherMember.setPhone("010-7237-3951");

        when(memberRepository.findByPhone("010-7237-3951")).thenReturn(Optional.of(otherMember));

        // 요청 DTO 설정
        MemberModifyByAdminRequestDto requestDto = new MemberModifyByAdminRequestDto();
        requestDto.setOriginalEmail("yoonwlgh12@naver.com");
        requestDto.setName("test");
        requestDto.setPhone("010-7237-3951"); // 기존 다른 회원의 전화번호와 동일하게 설정

        // 전화번호 중복으로 예외 발생 확인
        assertThrows(DuplicatePhoneException.class, () ->
                memberService.updateMemberByAdmin("yoonwlgh12@naver.com", requestDto)
        );

        // Mock 호출 검증
        verify(memberRepository).findByEmail("yoonwlgh12@naver.com");
        verify(memberRepository).findByPhone("010-7237-3951");
    }


    @Test
    @DisplayName("회원 상태를 DORMANT로 변경시키는지")
    void updateDormantStatus_success() {
        Clock fixedClock = Clock.fixed(
                LocalDateTime.of(2024, 10, 6, 9, 28, 52).toInstant(ZoneOffset.UTC),
                ZoneId.of("UTC")
        );
        when(clock.instant()).thenReturn(fixedClock.instant());
        when(clock.getZone()).thenReturn(fixedClock.getZone());

        LocalDateTime fixedNow = LocalDateTime.now(fixedClock);
        LocalDateTime threeMonthsAgo = fixedNow.minusMonths(3);

        MemberStatus activeStatus = new MemberStatus(1L, "ACTIVE");
        MemberStatus dormantStatus = new MemberStatus(2L, "DORMANT");

        MemberGrade memberGrade = new MemberGrade(1L, "NORMAL", new BigDecimal("10000.0"), LocalDateTime.now(clock));
        Member member1 = new Member(1L, memberGrade,activeStatus, "test", "010-1234-5678", "test@naver.com", LocalDate.of(2002, 7, 23), "Password");
        MemberCertification certification1 = new MemberCertification(1L, member1, threeMonthsAgo.minusDays(1), "일반");

        Member member2 = new Member(2L, memberGrade,activeStatus, "test2", "010-1234-5679", "test2@naver.com", LocalDate.of(2002, 7, 25), "Password2");
        MemberCertification certification2 = new MemberCertification(2L, member2, threeMonthsAgo.minusDays(5), "일반");

        doReturn(List.of(certification1, certification2)).when(memberCertificationRepository)
                .findInactiveMember(threeMonthsAgo);

        doReturn(Optional.of(dormantStatus)).when(memberStatusRepository).findByMemberStateName("DORMANT");
        memberService.updateDormantStatus();

        assertEquals("DORMANT", member1.getMemberStatus().getMemberStateName());
        assertEquals("DORMANT", member2.getMemberStatus().getMemberStateName());

        verify(memberRepository).saveAll(eq(List.of(member1, member2)));

    }
  
    @Test
    @DisplayName("변경 사항이 없는 경우 예외처리")
    void updateMemberByAdmin_DuplicateMemberModificationException() {
        // 기존 회원 데이터
        MemberGrade memberGrade = new MemberGrade(1L, "NORMAL", new BigDecimal("100.0"), LocalDateTime.now());
        MemberStatus memberStatus = new MemberStatus(1L, "ACTIVE");

        Member existingMember = new Member();
        existingMember.setName("윤지호");
        existingMember.setPhone("010-7237-3951");
        existingMember.setEmail("yoonwlgh12@naver.com");
        existingMember.setBirth(LocalDate.of(2000, 3, 9));
        existingMember.setPassword("encodedPassword");
        existingMember.setMemberGrade(memberGrade);
        existingMember.setMemberStatus(memberStatus);

        // 기존 회원 조회 Mock 설정
        when(memberRepository.findByEmail("yoonwlgh12@naver.com")).thenReturn(Optional.of(existingMember));

        // 요청 DTO 설정 (기존 데이터와 동일한 값으로 설정)
        MemberModifyByAdminRequestDto requestDto = new MemberModifyByAdminRequestDto();
        requestDto.setOriginalEmail("yoonwlgh12@naver.com");
        requestDto.setName("윤지호"); // 기존 이름과 동일
        requestDto.setPhone("010-7237-3951"); // 기존 전화번호와 동일
        requestDto.setEmail("yoonwlgh12@naver.com"); // 기존 이메일과 동일
        requestDto.setBirth(LocalDate.of(2000, 3, 9)); // 기존 생년월일과 동일
        requestDto.setMemberGradeId(1L); // 기존 회원 등급과 동일
        requestDto.setMemberStateId(1L); // 기존 회원 상태와 동일

        // 변경 사항이 없으므로 예외 발생 확인
        assertThrows(DuplicateMemberModificationException.class, () ->
                memberService.updateMemberByAdmin("yoonwlgh12@naver.com", requestDto)
        );

        // Mock 호출 검증
        verify(memberRepository).findByEmail("yoonwlgh12@naver.com");
    }

    @Test
    @DisplayName("이메일로 회원식별키를 가져오는지")
    void getMemberIdByEmail() {
        // 테스트 용 이메일
        String email = "test@naver.com";

        // 기존의 회원 데이터
        Member existingMember = new Member();
        existingMember.setMemberId(1L);
        existingMember.setEmail(email);

        // 기존 회원 조회 Mock 설정
        when(memberRepository.getMemberIdByEmail(email)).thenReturn(existingMember.getMemberId());

        // 메서드 호출
        Long memberId = memberService.getMemberIdByEmail(email);

        // 일치하는지 검증
        assertEquals(1L, memberId);

        // Mock 호출 검증
        verify(memberRepository).getMemberIdByEmail(email);
    }

    @Test
    @DisplayName("이메일로 회원식별키를 못찾는 경우")
    void getMemberIdByEmail_MemberNotFoundException() {
        // 테스트 용 이메일
        String email = "test@naver.com";

        // 기존의 회원 데이터
        Member existingMember = new Member();
        existingMember.setMemberId(1L);
        existingMember.setEmail(email);

        // 기존 회원 조회 Mock 설정
        when(memberRepository.getMemberIdByEmail(email)).thenReturn(null);

        // 이메일로 회원 식별키를 못찾을 경우 예외 발생 확인
        assertThrows(MemberEmailNotFoundException.class, () ->
                memberService.getMemberIdByEmail(email)
        );

        // Mock 호출 검증
        verify(memberRepository).getMemberIdByEmail(email);
    }

    @Test
    @DisplayName("회원 등급 갱신 성공")
    void updateMemberGrades_Success() {
        LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(3);

        Long memberId = 1L;
        BigDecimal totalAmount = BigDecimal.valueOf(250_000);
        List<Object[]> totalAmounts = new ArrayList<>();
        totalAmounts.add(new Object[]{memberId, totalAmount});

        // 현재 등급(NORMAL)
        MemberGrade currentGrade = new MemberGrade(1L, "NORMAL", BigDecimal.ZERO, LocalDateTime.now().minusMonths(3));

        // 갱신할 등급(GOLD)
        MemberGrade newGrade = new MemberGrade(2L, "GOLD", BigDecimal.valueOf(200_000), LocalDateTime.now());

        Member member = new Member();
        member.setMemberId(memberId);
        member.setMemberGrade(currentGrade);

        when(orderProductRepository.findTotalAmountByMemberAndRecentOrders(any(LocalDateTime.class)))
                .thenReturn(totalAmounts);

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(memberGradeRepository.findByMemberGradeName("GOLD")).thenReturn(Optional.of(newGrade));

        // 메서드 호출
        memberService.updateMemberGrades();

        // 검증
        assertEquals("GOLD", member.getMemberGrade().getMemberGradeName()); // 등급이 GOLD로 갱신되었는지 확인
        verify(memberRepository).save(member); // 저장 메서드가 호출되었는지 확인
    }

    @Test
    @DisplayName("회원 등급 갱신 실패 - 회원 등급이 없음")
    void updateMemberGrades_Fail_NoGrade() {
        LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(3);

        Long memberId = 1L;
        BigDecimal totalAmount = BigDecimal.valueOf(250_000);
        List<Object[]> totalAmounts = new ArrayList<>();
        totalAmounts.add(new Object[]{memberId, totalAmount});

        // 현재 등급(NORMAL)
        MemberGrade currentGrade = new MemberGrade(1L, "NORMAL", BigDecimal.ZERO, LocalDateTime.now().minusMonths(3));

        Member member = new Member();
        member.setMemberId(memberId);
        member.setMemberGrade(currentGrade);

        when(orderProductRepository.findTotalAmountByMemberAndRecentOrders(any(LocalDateTime.class)))
                .thenReturn(totalAmounts);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(memberGradeRepository.findByMemberGradeName("GOLD")).thenReturn(Optional.empty());

        // 메서드 실행 및 예외 검증
        assertThrows(MemberGradeNotFoundException.class, () -> memberService.updateMemberGrades());
    }

    @Test
    @DisplayName("회원 등급 갱신 실패 - 회원이 없음")
    void updateMemberGrades_Fail_NoMember() {
        LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(3);

        Long memberId = 1L;
        BigDecimal totalAmount = BigDecimal.valueOf(250_000);
        List<Object[]> totalAmounts = new ArrayList<>();
        totalAmounts.add(new Object[]{memberId, totalAmount});

        when(orderProductRepository.findTotalAmountByMemberAndRecentOrders(any(LocalDateTime.class)))
                .thenReturn(totalAmounts);
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // 메서드 실행 및 예외 검증
        assertThrows(MemberNotFoundException.class, () -> memberService.updateMemberGrades());
    }

}