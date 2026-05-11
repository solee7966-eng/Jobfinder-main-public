<img width="165" height="150" alt="jobfinder_cicd_architecture" src="https://github.com/user-attachments/assets/0ee8ce2c-cd9e-4952-bba2-1ee26bb17526" />#  JobFinder — 채용공고 플랫폼

> 기업과 구직자를 연결하는 통합 채용 플랫폼  
> 기업 담당자의 실제 업무 흐름을 중심으로 설계된 B2B 중심 채용 서비스

[![Java](https://img.shields.io/badge/Java-17-007396?style=flat-square&logo=java)](https://www.java.com)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?style=flat-square&logo=springboot)](https://spring.io/projects/spring-boot)
[![Oracle](https://img.shields.io/badge/Oracle-DB-F80000?style=flat-square&logo=oracle)](https://www.oracle.com)
[![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=flat-square&logo=docker)](https://www.docker.com)
[![Jenkins](https://img.shields.io/badge/Jenkins-CI/CD-D24939?style=flat-square&logo=jenkins)](https://www.jenkins.io)
[![AWS](https://img.shields.io/badge/AWS-EC2-FF9900?style=flat-square&logo=amazonaws)](https://aws.amazon.com)

**개발 기간** | 2026.02.24 ~ 2026.03.31

**팀 구성** | 4인 팀 프로젝트

**담당 역할** | 기업 기능 전체, 포인트 결제 시스템, 공고 자동 동기화

🔗 **[배포 사이트 바로가기](http://52.78.3.125/user-service/index)** &nbsp;|&nbsp; 📄 **[API 명세 (Swagger)](http://52.78.3.125/user-service/swagger-ui/index.html#/)** &nbsp;|&nbsp; 🎨 **[Figma 화면설계](https://www.figma.com/make/tgAE5O7dx4tSwYLV9V70V2/JobFinder_%EA%B8%B0%EC%97%85%EB%8C%80%EC%8B%9C%EB%B3%B4%EB%93%9C_%EC%B5%9C%EC%A2%85%EB%B3%B8?t=wGpxEN9KR83A2Pvn-1)**

---

##  프로젝트 개요

JobFinder는 기업 담당자가 **공고 등록 → 지원자 수신 → 제안서 발송 → 합격/불합격 처리**라는 채용 흐름을 하나의 인터페이스에서 끊김 없이 처리할 수 있도록 설계된 채용 플랫폼입니다.

초기 모놀리식 구조로 시작해 **MSA(Microservice Architecture)** 로 전환하였으며, Jenkins + Docker 기반 CI/CD 파이프라인을 구축하여 운영/테스트 환경을 분리했습니다.

---

##  서비스 구조 (MSA)

이 레포지토리는 **메인 서비스(User Service, 포트 8001)** 입니다.  
구직자·기업 회원 기능과 기업 전용 핵심 기능(공고·지원자·제안서·결제)을 담당합니다.

![Uploading jobf<svg width="100%" viewBox="0 0 680 620" role="img" style="" xmlns="http://www.w3.org/2000/svg">
  <title style="fill:rgb(0, 0, 0);stroke:none;color:rgb(255, 255, 255);stroke-width:1px;stroke-linecap:butt;stroke-linejoin:miter;opacity:1;font-family:&quot;Anthropic Sans&quot;, -apple-system, &quot;system-ui&quot;, &quot;Segoe UI&quot;, sans-serif;font-size:16px;font-weight:400;text-anchor:start;dominant-baseline:auto">JobFinder CI/CD 및 운영 아키텍처</title>
  <desc style="fill:rgb(0, 0, 0);stroke:none;color:rgb(255, 255, 255);stroke-width:1px;stroke-linecap:butt;stroke-linejoin:miter;opacity:1;font-family:&quot;Anthropic Sans&quot;, -apple-system, &quot;system-ui&quot;, &quot;Segoe UI&quot;, sans-serif;font-size:16px;font-weight:400;text-anchor:start;dominant-baseline:auto">Developer macOS에서 GitHub, Jenkins, DockerHub를 거쳐 AWS EC2로 배포되는 전체 흐름</desc>
  <defs>
    <marker id="arrow" viewBox="0 0 10 10" refX="8" refY="5" markerWidth="6" markerHeight="6" orient="auto-start-reverse">
      <path d="M2 1L8 5L2 9" fill="none" stroke="context-stroke" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
    </marker>
  <mask id="imagine-text-gaps-758ycj" maskUnits="userSpaceOnUse"><rect x="0" y="0" width="680" height="620" fill="white"/><rect x="36" y="16" width="96.046875" height="19" fill="black" rx="2"/><rect x="57.4453125" y="61.5" width="75.109375" height="21" fill="black" rx="2"/><rect x="70.65625" y="80.5" width="48.6875" height="19" fill="black" rx="2"/><rect x="222.84375" y="61.5" width="54.3125" height="21" fill="black" rx="2"/><rect x="211.5" y="80.5" width="77" height="19" fill="black" rx="2"/><rect x="370.671875" y="61.5" width="58.65625" height="21" fill="black" rx="2"/><rect x="355.5" y="80.5" width="89" height="19" fill="black" rx="2"/><rect x="513.8828125" y="61.5" width="82.234375" height="21" fill="black" rx="2"/><rect x="510.5859375" y="80.5" width="88.828125" height="19" fill="black" rx="2"/><rect x="408" y="124" width="73.53125" height="19" fill="black" rx="2"/><rect x="52" y="171" width="133.578125" height="19" fill="black" rx="2"/><rect x="536" y="136" width="70.609375" height="19" fill="black" rx="2"/><rect x="318.0234375" y="204.5" width="43.953125" height="21" fill="black" rx="2"/><rect x="286.640625" y="223.5" width="106.71875" height="19" fill="black" rx="2"/><rect x="281.2421875" y="299.5" width="117.515625" height="21" fill="black" rx="2"/><rect x="280.921875" y="318.5" width="118.15625" height="19" fill="black" rx="2"/><rect x="152.9453125" y="394.5" width="94.109375" height="21" fill="black" rx="2"/><rect x="140.1953125" y="413.5" width="119.609375" height="19" fill="black" rx="2"/><rect x="419.59375" y="394.5" width="100.8125" height="21" fill="black" rx="2"/><rect x="431.9453125" y="413.5" width="76.109375" height="19" fill="black" rx="2"/><rect x="97.0078125" y="204.5" width="125.984375" height="21" fill="black" rx="2"/><rect x="119.5859375" y="223.5" width="80.828125" height="19" fill="black" rx="2"/><rect x="332.8671875" y="499.5" width="74.265625" height="21" fill="black" rx="2"/><rect x="323.34375" y="518.5" width="93.3125" height="19" fill="black" rx="2"/><rect x="65.40625" y="460" width="109.1875" height="19" fill="black" rx="2"/><rect x="558.0859375" y="204.5" width="43.828125" height="21" fill="black" rx="2"/><rect x="553.546875" y="223.5" width="52.90625" height="19" fill="black" rx="2"/></mask></defs>

  <!-- CI/CD 영역 라벨 -->
  <text x="40" y="30" style="fill:var(--color-text-secondary);fill:rgb(194, 192, 182);stroke:none;color:rgb(255, 255, 255);stroke-width:1px;stroke-linecap:butt;stroke-linejoin:miter;opacity:1;font-family:&quot;Anthropic Sans&quot;, -apple-system, &quot;system-ui&quot;, &quot;Segoe UI&quot;, sans-serif;font-size:12px;font-weight:400;text-anchor:start;dominant-baseline:auto">CI/CD 파이프라인</text>
  <line x1="40" y1="36" x2="640" y2="36" stroke="var(--color-border-tertiary)" stroke-width="0.5" stroke-dasharray="4 3" style="fill:rgb(0, 0, 0);stroke:rgba(222, 220, 209, 0.15);color:rgb(255, 255, 255);stroke-width:0.5px;stroke-dasharray:4px, 3px;stroke-linecap:butt;stroke-linejoin:miter;opacity:1;font-family:&quot;Anthropic Sans&quot;, -apple-system, &quot;system-ui&quot;, &quot;Segoe UI&quot;, sans-serif;font-size:16px;font-weight:400;text-anchor:start;dominant-baseline:auto"/>

  <!-- Developer macOS -->
  <g style="fill:rgb(0, 0, 0);stroke:none;color:rgb(255, 255, 255);stroke-width:1px;stroke-linecap:butt;stroke-linejoin:miter;opacity:1;font-family:&quot;Anthropic Sans&quot;, -apple-system, &quot;system-ui&quot;, &quot;Segoe UI&quot;, sans-serif;font-size:16px;font-weight:400;text-anchor:start;dominant-baseline:auto">
    <rect x="40" y="50" width="110" height="56" rx="8" stroke-width="0.5" style="fill:rgb(60, 52, 137);stroke:rgb(175, 169, 236);color:rgb(255, 255, 255);stroke-width:0.5px;stroke-linecap:butt;stroke-linejoin:miter;opacity:1;font-family:&quot;Anthropic Sans&quot;, -apple-system, &quot;system-ui&quot;, &quot;Segoe UI&quot;, sans-serif;font-size:16px;font-weight:400;text-anchor:start;dominant-baseline:auto"/>
    <text x="95" y="72" text-anchor="middle" dominant-baseline="central" style="fill:rgb(206, 203, 246);stroke:none;color:rgb(255, 255, 255);stroke-width:1px;stroke-linecap:butt;stroke-linejoin:miter;opacity:1;font-family:&quot;Anthropic Sans&quot;, -apple-system, &quot;system-ui&quot;, &quot;Segoe UI&quot;, sans-serif;font-size:14px;font-weight:500;text-anchor:middle;dominant-baseline:central">Developer</text>
    <text x="95" y="90" text-anchor="middle" dominant-baseline="central" style="fill:rgb(175, 169, 236);stroke:none;color:rgb(255, 255, 255);stroke-width:1px;stroke-linecap:butt;stroke-linejoin:miter;opacity:1;font-family:&quot;Anthropic Sans&quot;, -apple-system, &quot;system-ui&quot;, &quot;Segoe UI&quot;, sans-serif;font-size:12px;font-weight:400;text-anchor:middle;dominant-baseline:central">macOS</text>
  </g>

  <!-- GitHub -->
  <g style="fill:rgb(0, 0, 0);stroke:none;color:rgb(255, 255, 255);stroke-width:1px;stroke-linecap:butt;stroke-linejoin:miter;opacity:1;font-family:&quot;Anthropic Sans&quot;, -apple-system, &quot;system-ui&quot;, &quot;Segoe UI&quot;, sans-serif;font-size:16px;font-weight:400;text-anchor:start;dominant-baseline:auto">
    <rect x="200" y="50" width="100" height="56" rx="8" stroke-width="0.5" style="fill:rgb(68, 68, 65);stroke:rgb(180, 178, 169);color:rgb(255, 255, 255);stroke-width:0.5px;stroke-linecap:butt;stroke-linejoin:miter;opacity:1;font-family:&quot;Anthropic Sans&quot;, -apple-system, &quot;system-ui&quot;, &quot;Segoe UI&quot;, sans-serif;font-size:16px;font-weight:400;text-anchor:start;dominant-baseline:auto"/>
    <text x="250" y="72" text-anchor="middle" dominant-baseline="central" style="fill:rgb(211, 209, 199);stroke:none;color:rgb(255, 255, 255);stroke-width:1px;stroke-linecap:butt;stroke-linejoin:miter;opacity:1;font-family:&quot;Anthropic Sans&quot;, -apple-system, &quot;system-ui&quot;, &quot;Segoe UI&quot;, sans-serif;font-size:14px;font-weight:500;text-anchor:middle;dominant-baseline:central">GitHub</text>
    <text x="250" y="90" text-anchor="middle" dominant-baseline="central" style="fill:rgb(180, 178, 169);stroke:none;color:rgb(255, 255, 255);stroke-width:1px;stroke-linecap:butt;stroke-linejoin:miter;opacity:1;font-family:&quot;Anthropic Sans&quot;, -apple-system, &quot;system-ui&quot;, &quot;Segoe UI&quot;, sans-serif;font-size:12px;font-weight:400;text-anchor:middle;dominant-baseline:central">push trigger</text>
  </g>

  <!-- Jenkins -->
  <g style="fill:rgb(0, 0, 0);stroke:none;color:rgb(255, 255, 255);stroke-width:1px;stroke-linecap:butt;stroke-linejoin:miter;opacity:1;font-family:&quot;Anthropic Sans&quot;, -apple-system, &quot;system-ui&quot;, &quot;Segoe UI&quot;, sans-serif;font-size:16px;font-weight:400;text-anchor:start;dominant-baseline:auto">
    <rect x="350" y="50" width="100" height="56" rx="8" stroke-width="0.5" style="fill:rgb(113, 43, 19);stroke:rgb(240, 153, 123);color:rgb(255, 255, 255);stroke-width:0.5px;stroke-linecap:butt;stroke-linejoin:miter;opacity:1;font-family:&quot;Anthropic Sans&quot;, -apple-system, &quot;system-ui&quot;, &quot;Segoe UI&quot;, sans-serif;font-size:16px;font-weight:400;text-anchor:start;dominant-baseline:auto"/>
    <text x="400" y="72" text-anchor="middle" dominant-baseline="central" style="fill:rgb(245, 196, 179);stroke:none;color:rgb(255, 255, 255);stroke-width:1px;stroke-linecap:butt;stroke-linejoin:miter;opacity:1;font-family:&quot;Anthropic Sans&quot;, -apple-system, &quot;system-ui&quot;, &quot;Segoe UI&quot;, sans-serif;font-size:14px;font-weight:500;text-anchor:middle;dominant-baseline:central">Jenkins</text>
    <text x="400" y="90" text-anchor="middle" dominant-baseline="central" style="fill:rgb(240, 153, 123);stroke:none;color:rgb(255, 255, 255);stroke-width:1px;stroke-linecap:butt;stroke-linejoin:miter;opacity:1;font-family:&quot;Anthropic Sans&quot;, -apple-system, &quot;system-ui&quot;, &quot;Segoe UI&quot;, sans-serif;font-size:12px;font-weight:400;text-anchor:middle;dominant-baseline:central">build &amp; deploy</text>
  </g>

  <!-- DockerHub -->
  <g style="fill:rgb(0, 0, 0);stroke:none;color:rgb(255, 255, 255);stroke-width:1px;stroke-linecap:butt;stroke-linejoin:miter;opacity:1;font-family:&quot;Anthropic Sans&quot;, -apple-system, &quot;system-ui&quot;, &quot;Segoe UI&quot;, sans-serif;font-size:16px;font-weight:400;text-anchor:start;dominant-baseline:auto">
    <rect x="500" y="50" width="110" height="56" rx="8" stroke-width="0.5" style="fill:rgb(12, 68, 124);stroke:rgb(133, 183, 235);color:rgb(255, 255, 255);stroke-width:0.5px;stroke-linecap:butt;stroke-linejoin:miter;opacity:1;font-family:&quot;Anthropic Sans&quot;, -apple-system, &quot;system-ui&quot;, &quot;Segoe UI&quot;, sans-serif;font-size:16px;font-weight:400;text-anchor:start;dominant-baseline:auto"/>
    <text x="555" y="72" text-anchor="middle" dominant-baseline="central" style="fill:rgb(181, 212, 244);stroke:none;color:rgb(255, 255, 255);stroke-width:1px;stroke-linecap:butt;stroke-linejoin:miter;opacity:1;font-family:&quot;Anthropic Sans&quot;, -apple-system, &quot;system-ui&quot;, &quot;Segoe UI&quot;, sans-serif;font-size:14px;font-weight:500;text-anchor:middle;dominant-baseline:central">DockerHub</text>
    <text x="555" y="90" text-anchor="middle" dominant-baseline="central" style="fill:rgb(133, 183, 235);stroke:none;color:rgb(255, 255, 255);stroke-width:1px;stroke-linecap:butt;stroke-linejoin:miter;opacity:1;font-family:&quot;Anthropic Sans&quot;, -apple-system, &quot;system-ui&quot;, &quot;Segoe UI&quot;, sans-serif;font-size:12px;font-weight:400;text-anchor:middle;dominant-baseline:central">image registry</text>
  </g>

  <!-- 화살표: Dev → GitHub -->
  <line x1="150" y1="78" x2="198" y2="78" marker-end="url(#arrow)" stroke="#7F77DD" style="fill:none;stroke:rgb(156, 154, 146);color:rgb(255, 255, 255);stroke-width:1.5px;stroke-linecap:butt;stroke-linejoin:miter;opacity:1;font-family:&quot;Anthropic Sans&quot;, -apple-system, &quot;system-ui&quot;, &quot;Segoe UI&quot;, sans-serif;font-size:16px;font-weight:400;text-anchor:start;dominant-baseline:auto"/>
  <!-- 화살표: GitHub → Jenkins -->
  <line x1="300" y1="78" x2="348" y2="78" marker-end="url(#arrow)" stroke="#888780" style="fill:none;stroke:rgb(156, 154, 146);color:rgb(255, 255, 255);stroke-width:1.5px;stroke-linecap:butt;stroke-linejoin:miter;opacity:1;font-family:&quot;Anthropic Sans&quot;, -apple-system, &quot;system-ui&quot;, &quot;Segoe UI&quot;, sans-serif;font-size:16px;font-weight:400;text-anchor:start;dominant-baseline:auto"/>
  <!-- 화살표: Jenkins → DockerHub -->
  <line x1="450" y1="78" x2="498" y2="78" marker-end="url(#arrow)" stroke="#D85A30" style="fill:none;stroke:rgb(156, 154, 146);color:rgb(255, 255, 255);stroke-width:1.5px;stroke-linecap:butt;stroke-linejoin:miter;opacity:1;font-family:&quot;Anthropic Sans&quot;, -apple-system, &quot;system-ui&quot;, &quot;Segoe UI&quot;, sans-serif;font-size:16px;font-weight:400;text-anchor:start;dominant-baseline:auto"/>

  <!-- Jenkins → EC2 SSH 화살표 (아래로) -->
  <line x1="400" y1="106" x2="400" y2="160" marker-end="url(#arrow)" stroke="#D85A30" style="fill:none;stroke:rgb(156, 154, 146);color:rgb(255, 255, 255);stroke-width:1.5px;stroke-linecap:butt;stroke-linejoin:miter;opacity:1;font-family:&quot;Anthropic Sans&quot;, -apple-system, &quot;system-ui&quot;, &quot;Segoe UI&quot;, sans-serif;font-size:16px;font-weight:400;text-anchor:start;dominant-baseline:auto"/>
  <text x="412" y="138" style="fill:var(--color-text-secondary);fill:rgb(194, 192, 182);stroke:none;color:rgb(255, 255, 255);stroke-width:1px;stroke-linecap:butt;stroke-linejoin:miter;opacity:1;font-family:&quot;Anthropic Sans&quot;, -apple-system, &quot;system-ui&quot;, &quot;Segoe UI&quot;, sans-serif;font-size:12px;font-weight:400;text-anchor:start;dominant-baseline:auto">SSH deploy</text>

  <!-- AWS EC2 서비스 서버 컨테이너 -->
  <rect x="40" y="165" width="600" height="310" rx="14" fill="none" stroke="var(--color-border-secondary)" stroke-width="0.5" stroke-dasharray="6 3" style="fill:none;stroke:rgba(222, 220, 209, 0.3);color:rgb(255, 255, 255);stroke-width:0.5px;stroke-dasharray:6px, 3px;stroke-linecap:butt;stroke-linejoin:miter;opacity:1;font-family:&quot;Anthropic Sans&quot;, -apple-system, &quot;system-ui&quot;, &quot;Segoe UI&quot;, sans-serif;font-size:16px;font-weight:400;text-anchor:start;dominant-baseline:auto"/>
  <text x="56" y="185" style="fill:var(--color-text-secondary);fill:rgb(194, 192, 182);stroke:none;color:rgb(255, 255, 255);stroke-width:1px;stroke-linecap:butt;stroke-linejoin:miter;opacity:1;font-family:&quot;Anthropic Sans&quot;, -apple-system, &quot;system-ui&quot;, &quot;Segoe UI&quot;, sans-serif;font-size:12px;font-weight:400;text-anchor:start;dominant-baseline:auto">AWS EC2 — 서비스 서버</text>

  <!-- DockerHub → EC2 pull 화살표 -->
  <path d="M555 106 Q555 145 480 200" fill="none" marker-end="url(#arrow)" stroke="#378ADD" mask="url(#imagine-text-gaps-758ycj)" style="fill:none;stroke:rgb(156, 154, 146);color:rgb(255, 255, 255);stroke-width:1.5px;stroke-linecap:butt;stroke-linejoin:miter;opacity:1;font-family:&quot;Anthropic Sans&quot;, -apple-system, &quot;system-ui&quot;, &quot;Segoe UI&quot;, sans-serif;font-size:16px;font-weight:400;text-anchor:start;dominant-baseline:auto"/>
  <text x="540" y="150" style="fill:var(--color-text-secondary);fill:rgb(194, 192, 182);stroke:none;color:rgb(255, 255, 255);stroke-width:1px;stroke-linecap:butt;stroke-linejoin:miter;opacity:1;font-family:&quot;Anthropic Sans&quot;, -apple-system, &quot;system-ui&quot;, &quot;Segoe UI&quot;, sans-serif;font-size:12px;font-weight:400;text-anchor:start;dominant-baseline:auto">docker pull</text>

  <!-- nginx -->
  <g style="fill:rgb(0, 0, 0);stroke:none;color:rgb(255, 255, 255);stroke-width:1px;stroke-linecap:butt;stroke-linejoin:miter;opacity:1;font-family:&quot;Anthropic Sans&quot;, -apple-system, &quot;system-ui&quot;, &quot;Segoe UI&quot;, sans-serif;font-size:16px;font-weight:400;text-anchor:start;dominant-baseline:auto">
    <rect x="270" y="195" width="140" height="50" rx="8" stroke-width="0.5" style="fill:rgb(8, 80, 65);stroke:rgb(93, 202, 165);color:rgb(255, 255, 255);stroke-width:0.5px;stroke-linecap:butt;stroke-linejoin:miter;opacity:1;font-family:&quot;Anthropic Sans&quot;, -apple-system, &quot;system-ui&quot;, &quot;Segoe UI&quot;, sans-serif;font-size:16px;font-weight:400;text-anchor:start;dominant-baseline:auto"/>
    <text x="340" y="215" text-anchor="middle" dominant-baseline="central" style="fill:rgb(159, 225, 203);stroke:none;color:rgb(255, 255, 255);stroke-width:1px;stroke-linecap:butt;stroke-linejoin:miter;opacity:1;font-family:&quot;Anthropic Sans&quot;, -apple-system, &quot;system-ui&quot;, &quot;Segoe UI&quot;, sans-serif;font-size:14px;font-weight:500;text-anchor:middle;dominant-baseline:central">nginx</text>
    <text x="340" y="233" text-anchor="middle" dominant-baseline="central" style="fill:rgb(93, 202, 165);stroke:none;color:rgb(255, 255, 255);stroke-width:1px;stroke-linecap:butt;stroke-linejoin:miter;opacity:1;font-family:&quot;Anthropic Sans&quot;, -apple-system, &quot;system-ui&quot;, &quot;Segoe UI&quot;, sans-serif;font-size:12px;font-weight:400;text-anchor:middle;dominant-baseline:central">:80 reverse proxy</text>
  </g>

  <!-- nginx → gateway -->
  <line x1="340" y1="245" x2="340" y2="285" marker-end="url(#arrow)" stroke="#1D9E75" style="fill:none;stroke:rgb(156, 154, 146);color:rgb(255, 255, 255);stroke-width:1.5px;stroke-linecap:butt;stroke-linejoin:miter;opacity:1;font-family:&quot;Anthropic Sans&quot;, -apple-system, &quot;system-ui&quot;, &quot;Segoe UI&quot;, sans-serif;font-size:16px;font-weight:400;text-anchor:start;dominant-baseline:auto"/>

  <!-- gateway -->
  <g style="fill:rgb(0, 0, 0);stroke:none;color:rgb(255, 255, 255);stroke-width:1px;stroke-linecap:butt;stroke-linejoin:miter;opacity:1;font-family:&quot;Anthropic Sans&quot;, -apple-system, &quot;system-ui&quot;, &quot;Segoe UI&quot;, sans-serif;font-size:16px;font-weight:400;text-anchor:start;dominant-baseline:auto">
    <rect x="240" y="290" width="200" height="50" rx="8" stroke-width="0.5" style="fill:rgb(8, 80, 65);stroke:rgb(93, 202, 165);color:rgb(255, 255, 255);stroke-width:0.5px;stroke-linecap:butt;stroke-linejoin:miter;opacity:1;font-family:&quot;Anthropic Sans&quot;, -apple-system, &quot;system-ui&quot;, &quot;Segoe UI&quot;, sans-serif;font-size:16px;font-weight:400;text-anchor:start;dominant-baseline:auto"/>
    <text x="340" y="310" text-anchor="middle" dominant-baseline="central" style="fill:rgb(159, 225, 203);stroke:none;color:rgb(255, 255, 255);stroke-width:1px;stroke-linecap:butt;stroke-linejoin:miter;opacity:1;font-family:&quot;Anthropic Sans&quot;, -apple-system, &quot;system-ui&quot;, &quot;Segoe UI&quot;, sans-serif;font-size:14px;font-weight:500;text-anchor:middle;dominant-baseline:central">gateway-service</text>
    <text x="340" y="328" text-anchor="middle" dominant-baseline="central" style="fill:rgb(93, 202, 165);stroke:none;color:rgb(255, 255, 255);stroke-width:1px;stroke-linecap:butt;stroke-linejoin:miter;opacity:1;font-family:&quot;Anthropic Sans&quot;, -apple-system, &quot;system-ui&quot;, &quot;Segoe UI&quot;, sans-serif;font-size:12px;font-weight:400;text-anchor:middle;dominant-baseline:central">:8000  Eureka 라우팅</text>
  </g>

  <!-- gateway → main, board -->
  <path d="M300 340 L220 380" fill="none" marker-end="url(#arrow)" stroke="#1D9E75" style="fill:none;stroke:rgb(156, 154, 146);color:rgb(255, 255, 255);stroke-width:1.5px;stroke-linecap:butt;stroke-linejoin:miter;opacity:1;font-family:&quot;Anthropic Sans&quot;, -apple-system, &quot;system-ui&quot;, &quot;Segoe UI&quot;, sans-serif;font-size:16px;font-weight:400;text-anchor:start;dominant-baseline:auto"/>
  <path d="M380 340 L460 380" fill="none" marker-end="url(#arrow)" stroke="#1D9E75" style="fill:none;stroke:rgb(156, 154, 146);color:rgb(255, 255, 255);stroke-width:1.5px;stroke-linecap:butt;stroke-linejoin:miter;opacity:1;font-family:&quot;Anthropic Sans&quot;, -apple-system, &quot;system-ui&quot;, &quot;Segoe UI&quot;, sans-serif;font-size:16px;font-weight:400;text-anchor:start;dominant-baseline:auto"/>

  <!-- main-service -->
  <g style="fill:rgb(0, 0, 0);stroke:none;color:rgb(255, 255, 255);stroke-width:1px;stroke-linecap:butt;stroke-linejoin:miter;opacity:1;font-family:&quot;Anthropic Sans&quot;, -apple-system, &quot;system-ui&quot;, &quot;Segoe UI&quot;, sans-serif;font-size:16px;font-weight:400;text-anchor:start;dominant-baseline:auto">
    <rect x="100" y="385" width="200" height="56" rx="8" stroke-width="0.5" style="fill:rgb(60, 52, 137);stroke:rgb(175, 169, 236);color:rgb(255, 255, 255);stroke-width:0.5px;stroke-linecap:butt;stroke-linejoin:miter;opacity:1;font-family:&quot;Anthropic Sans&quot;, -apple-system, &quot;system-ui&quot;, &quot;Segoe UI&quot;, sans-serif;font-size:16px;font-weight:400;text-anchor:start;dominant-baseline:auto"/>
    <text x="200" y="405" text-anchor="middle" dominant-baseline="central" style="fill:rgb(206, 203, 246);stroke:none;color:rgb(255, 255, 255);stroke-width:1px;stroke-linecap:butt;stroke-linejoin:miter;opacity:1;font-family:&quot;Anthropic Sans&quot;, -apple-system, &quot;system-ui&quot;, &quot;Segoe UI&quot;, sans-serif;font-size:14px;font-weight:500;text-anchor:middle;dominant-baseline:central">main-service</text>
    <text x="200" y="423" text-anchor="middle" dominant-baseline="central" style="fill:rgb(175, 169, 236);stroke:none;color:rgb(255, 255, 255);stroke-width:1px;stroke-linecap:butt;stroke-linejoin:miter;opacity:1;font-family:&quot;Anthropic Sans&quot;, -apple-system, &quot;system-ui&quot;, &quot;Segoe UI&quot;, sans-serif;font-size:12px;font-weight:400;text-anchor:middle;dominant-baseline:central">:8001  회원/파일업로드</text>
  </g>

  <!-- board-service -->
  <g style="fill:rgb(0, 0, 0);stroke:none;color:rgb(255, 255, 255);stroke-width:1px;stroke-linecap:butt;stroke-linejoin:miter;opacity:1;font-family:&quot;Anthropic Sans&quot;, -apple-system, &quot;system-ui&quot;, &quot;Segoe UI&quot;, sans-serif;font-size:16px;font-weight:400;text-anchor:start;dominant-baseline:auto">
    <rect x="370" y="385" width="200" height="56" rx="8" stroke-width="0.5" style="fill:rgb(60, 52, 137);stroke:rgb(175, 169, 236);color:rgb(255, 255, 255);stroke-width:0.5px;stroke-linecap:butt;stroke-linejoin:miter;opacity:1;font-family:&quot;Anthropic Sans&quot;, -apple-system, &quot;system-ui&quot;, &quot;Segoe UI&quot;, sans-serif;font-size:16px;font-weight:400;text-anchor:start;dominant-baseline:auto"/>
    <text x="470" y="405" text-anchor="middle" dominant-baseline="central" style="fill:rgb(206, 203, 246);stroke:none;color:rgb(255, 255, 255);stroke-width:1px;stroke-linecap:butt;stroke-linejoin:miter;opacity:1;font-family:&quot;Anthropic Sans&quot;, -apple-system, &quot;system-ui&quot;, &quot;Segoe UI&quot;, sans-serif;font-size:14px;font-weight:500;text-anchor:middle;dominant-baseline:central">board-service</text>
    <text x="470" y="423" text-anchor="middle" dominant-baseline="central" style="fill:rgb(175, 169, 236);stroke:none;color:rgb(255, 255, 255);stroke-width:1px;stroke-linecap:butt;stroke-linejoin:miter;opacity:1;font-family:&quot;Anthropic Sans&quot;, -apple-system, &quot;system-ui&quot;, &quot;Segoe UI&quot;, sans-serif;font-size:12px;font-weight:400;text-anchor:middle;dominant-baseline:central">:8002  게시판</text>
  </g>

  <!-- discovery (오른쪽 상단 내부) -->
  <g style="fill:rgb(0, 0, 0);stroke:none;color:rgb(255, 255, 255);stroke-width:1px;stroke-linecap:butt;stroke-linejoin:miter;opacity:1;font-family:&quot;Anthropic Sans&quot;, -apple-system, &quot;system-ui&quot;, &quot;Segoe UI&quot;, sans-serif;font-size:16px;font-weight:400;text-anchor:start;dominant-baseline:auto">
    <rect x="80" y="195" width="160" height="50" rx="8" stroke-width="0.5" style="fill:rgb(68, 68, 65);stroke:rgb(180, 178, 169);color:rgb(255, 255, 255);stroke-width:0.5px;stroke-linecap:butt;stroke-linejoin:miter;opacity:1;font-family:&quot;Anthropic Sans&quot;, -apple-system, &quot;system-ui&quot;, &quot;Segoe UI&quot;, sans-serif;font-size:16px;font-weight:400;text-anchor:start;dominant-baseline:auto"/>
    <text x="160" y="215" text-anchor="middle" dominant-baseline="central" style="fill:rgb(211, 209, 199);stroke:none;color:rgb(255, 255, 255);stroke-width:1px;stroke-linecap:butt;stroke-linejoin:miter;opacity:1;font-family:&quot;Anthropic Sans&quot;, -apple-system, &quot;system-ui&quot;, &quot;Segoe UI&quot;, sans-serif;font-size:14px;font-weight:500;text-anchor:middle;dominant-baseline:central">discovery-service</text>
    <text x="160" y="233" text-anchor="middle" dominant-baseline="central" style="fill:rgb(180, 178, 169);stroke:none;color:rgb(255, 255, 255);stroke-width:1px;stroke-linecap:butt;stroke-linejoin:miter;opacity:1;font-family:&quot;Anthropic Sans&quot;, -apple-system, &quot;system-ui&quot;, &quot;Segoe UI&quot;, sans-serif;font-size:12px;font-weight:400;text-anchor:middle;dominant-baseline:central">:8761  Eureka</text>
  </g>

  <!-- main/board → Oracle DB -->
  <line x1="200" y1="441" x2="200" y2="485" marker-end="url(#arrow)" stroke="#7F77DD" style="fill:none;stroke:rgb(156, 154, 146);color:rgb(255, 255, 255);stroke-width:1.5px;stroke-linecap:butt;stroke-linejoin:miter;opacity:1;font-family:&quot;Anthropic Sans&quot;, -apple-system, &quot;system-ui&quot;, &quot;Segoe UI&quot;, sans-serif;font-size:16px;font-weight:400;text-anchor:start;dominant-baseline:auto"/>
  <line x1="470" y1="441" x2="470" y2="485" marker-end="url(#arrow)" stroke="#7F77DD" style="fill:none;stroke:rgb(156, 154, 146);color:rgb(255, 255, 255);stroke-width:1.5px;stroke-linecap:butt;stroke-linejoin:miter;opacity:1;font-family:&quot;Anthropic Sans&quot;, -apple-system, &quot;system-ui&quot;, &quot;Segoe UI&quot;, sans-serif;font-size:16px;font-weight:400;text-anchor:start;dominant-baseline:auto"/>

  <!-- Oracle DB EC2 -->
  <g style="fill:rgb(0, 0, 0);stroke:none;color:rgb(255, 255, 255);stroke-width:1px;stroke-linecap:butt;stroke-linejoin:miter;opacity:1;font-family:&quot;Anthropic Sans&quot;, -apple-system, &quot;system-ui&quot;, &quot;Segoe UI&quot;, sans-serif;font-size:16px;font-weight:400;text-anchor:start;dominant-baseline:auto">
    <rect x="270" y="490" width="200" height="50" rx="8" stroke-width="0.5" style="fill:rgb(99, 56, 6);stroke:rgb(239, 159, 39);color:rgb(255, 255, 255);stroke-width:0.5px;stroke-linecap:butt;stroke-linejoin:miter;opacity:1;font-family:&quot;Anthropic Sans&quot;, -apple-system, &quot;system-ui&quot;, &quot;Segoe UI&quot;, sans-serif;font-size:16px;font-weight:400;text-anchor:start;dominant-baseline:auto"/>
    <text x="370" y="510" text-anchor="middle" dominant-baseline="central" style="fill:rgb(250, 199, 117);stroke:none;color:rgb(255, 255, 255);stroke-width:1px;stroke-linecap:butt;stroke-linejoin:miter;opacity:1;font-family:&quot;Anthropic Sans&quot;, -apple-system, &quot;system-ui&quot;, &quot;Segoe UI&quot;, sans-serif;font-size:14px;font-weight:500;text-anchor:middle;dominant-baseline:central">Oracle DB</text>
    <text x="370" y="528" text-anchor="middle" dominant-baseline="central" style="fill:rgb(239, 159, 39);stroke:none;color:rgb(255, 255, 255);stroke-width:1px;stroke-linecap:butt;stroke-linejoin:miter;opacity:1;font-family:&quot;Anthropic Sans&quot;, -apple-system, &quot;system-ui&quot;, &quot;Segoe UI&quot;, sans-serif;font-size:12px;font-weight:400;text-anchor:middle;dominant-baseline:central">EC2 — XEPDB1</text>
  </g>
  <path d="M200 485 Q200 515 268 515" fill="none" marker-end="url(#arrow)" stroke="#BA7517" style="fill:none;stroke:rgb(156, 154, 146);color:rgb(255, 255, 255);stroke-width:1.5px;stroke-linecap:butt;stroke-linejoin:miter;opacity:1;font-family:&quot;Anthropic Sans&quot;, -apple-system, &quot;system-ui&quot;, &quot;Segoe UI&quot;, sans-serif;font-size:16px;font-weight:400;text-anchor:start;dominant-baseline:auto"/>
  <path d="M470 485 Q470 515 472 515" fill="none" marker-end="url(#arrow)" stroke="#BA7517" style="fill:none;stroke:rgb(156, 154, 146);color:rgb(255, 255, 255);stroke-width:1.5px;stroke-linecap:butt;stroke-linejoin:miter;opacity:1;font-family:&quot;Anthropic Sans&quot;, -apple-system, &quot;system-ui&quot;, &quot;Segoe UI&quot;, sans-serif;font-size:16px;font-weight:400;text-anchor:start;dominant-baseline:auto"/>

  <!-- volume mount 표시 -->
  <rect x="60" y="455" width="120" height="28" rx="6" fill="none" stroke="var(--color-border-tertiary)" stroke-width="0.5" stroke-dasharray="3 2" style="fill:none;stroke:rgba(222, 220, 209, 0.15);color:rgb(255, 255, 255);stroke-width:0.5px;stroke-dasharray:3px, 2px;stroke-linecap:butt;stroke-linejoin:miter;opacity:1;font-family:&quot;Anthropic Sans&quot;, -apple-system, &quot;system-ui&quot;, &quot;Segoe UI&quot;, sans-serif;font-size:16px;font-weight:400;text-anchor:start;dominant-baseline:auto"/>
  <text x="120" y="474" text-anchor="middle" style="fill:var(--color-text-tertiary);fill:rgb(156, 154, 146);stroke:none;color:rgb(255, 255, 255);stroke-width:1px;stroke-linecap:butt;stroke-linejoin:miter;opacity:1;font-family:&quot;Anthropic Sans&quot;, -apple-system, &quot;system-ui&quot;, &quot;Segoe UI&quot;, sans-serif;font-size:12px;font-weight:400;text-anchor:middle;dominant-baseline:auto">volume: /app/data</text>

  <!-- Slack 알림 -->
  <g style="fill:rgb(0, 0, 0);stroke:none;color:rgb(255, 255, 255);stroke-width:1px;stroke-linecap:butt;stroke-linejoin:miter;opacity:1;font-family:&quot;Anthropic Sans&quot;, -apple-system, &quot;system-ui&quot;, &quot;Segoe UI&quot;, sans-serif;font-size:16px;font-weight:400;text-anchor:start;dominant-baseline:auto">
    <rect x="530" y="195" width="100" height="50" rx="8" stroke-width="0.5" style="fill:rgb(39, 80, 10);stroke:rgb(151, 196, 89);color:rgb(255, 255, 255);stroke-width:0.5px;stroke-linecap:butt;stroke-linejoin:miter;opacity:1;font-family:&quot;Anthropic Sans&quot;, -apple-system, &quot;system-ui&quot;, &quot;Segoe UI&quot;, sans-serif;font-size:16px;font-weight:400;text-anchor:start;dominant-baseline:auto"/>
    <text x="580" y="215" text-anchor="middle" dominant-baseline="central" style="fill:rgb(192, 221, 151);stroke:none;color:rgb(255, 255, 255);stroke-width:1px;stroke-linecap:butt;stroke-linejoin:miter;opacity:1;font-family:&quot;Anthropic Sans&quot;, -apple-system, &quot;system-ui&quot;, &quot;Segoe UI&quot;, sans-serif;font-size:14px;font-weight:500;text-anchor:middle;dominant-baseline:central">Slack</text>
    <text x="580" y="233" text-anchor="middle" dominant-baseline="central" style="fill:rgb(151, 196, 89);stroke:none;color:rgb(255, 255, 255);stroke-width:1px;stroke-linecap:butt;stroke-linejoin:miter;opacity:1;font-family:&quot;Anthropic Sans&quot;, -apple-system, &quot;system-ui&quot;, &quot;Segoe UI&quot;, sans-serif;font-size:12px;font-weight:400;text-anchor:middle;dominant-baseline:central">배포 알림</text>
  </g>
  <path d="M450 106 Q530 140 530 193" fill="none" marker-end="url(#arrow)" stroke="#3B6D11" stroke-dasharray="4 3" style="fill:none;stroke:rgb(156, 154, 146);color:rgb(255, 255, 255);stroke-width:1.5px;stroke-dasharray:4px, 3px;stroke-linecap:butt;stroke-linejoin:miter;opacity:1;font-family:&quot;Anthropic Sans&quot;, -apple-system, &quot;system-ui&quot;, &quot;Segoe UI&quot;, sans-serif;font-size:16px;font-weight:400;text-anchor:start;dominant-baseline:auto"/>
</svg>inder_cicd_architecture.svg…]()

| 서비스 | 포트 | 레포지토리 | 설명 |
|--------|------|-----------|------|
| **Main Service** | 8001 | ⬅️ 현재 레포 | 회원, 공고, 지원자, 제안서, 결제 |
| **Board Service** | 8002 | [Jobfinder-board-public](https://github.com/solee7966-eng/Jobfinder-board-public) | 커뮤니티 게시판 |
| **API Gateway** | 8000 | [Jobfinder-gateway-public](https://github.com/solee7966-eng/Jobfinder-gateway-public) | 단일 진입점, 서비스 라우팅 |
| **Discovery** | 8761 | [Jobfinder-discovery-public](https://github.com/solee7966-eng/Jobfinder-discovery-public) | Eureka 서비스 레지스트리 |

```
Client (Browser)
    │
    ▼
API Gateway (8000)          ← 단일 진입점, Discovery 연동 라우팅
    │
    ├──▶ Main Service (8001) ← 회원/공고/지원/결제 (현재 레포)
    │         │
    ├──▶ Board Service (8002) ← 커뮤니티
    │
    └──▶ Oracle DB / External API (PortOne, SOLAPI)

Discovery Service (8761)    ← Eureka 서비스 레지스트리
```

---

##  주요 기능 (내 담당 파트)

### 1. 기업 기능 전체
- **공고 관리**: 채용공고 등록·수정·삭제, 배너 등록
- **지원자 관리**: 지원서 열람, 상태 변경(미열람 → 열람 → 서류합격/불합격 → 최종합격/불합격)
- **인재 검색**: 조건 기반 구직자 탐색 및 제안서 발송
- **제안서 관리**: 제안서 템플릿 작성, 수신자별 발송·열람·응답 현황 추적

### 2. 포인트 결제 시스템 (PortOne 연동)
- PortOne(아임포트) PG사 연동으로 포인트 충전
- **PENDING → VERIFYING → PAID** 상태 흐름으로 외부 결제와 내부 적립 간 데이터 정합성 보장
- 서버에서 PG사 API 직접 재검증 → 프론트 조작·중복 결제 원천 차단
- 결제 완료·포인트 적립·거래내역 저장을 단일 트랜잭션으로 처리

### 3. Spring Scheduler 기반 공고 상태 자동 동기화
- 1분 주기로 전체 공고 상태 자동 갱신 (`@Scheduled`)
- 삭제 → 마감 → 대기 → 진행중 순서로 우선순위 기반 상태 전이
- 마감된 공고가 구직자 화면에 노출되는 문제 원천 방지

---

##  기술 스택

| 분류 | 기술 |
|------|------|
| **Backend** | Java, Spring Boot, Spring Security, Spring Scheduler, MyBatis |
| **Frontend** | HTML5, CSS3, JavaScript |
| **Database** | Oracle |
| **결제** | PortOne (아임포트) V1 |
| **인프라** | AWS EC2, Docker, Docker Compose |
| **CI/CD** | Jenkins, Docker Hub |
| **API 문서** | Swagger |
| **코드 품질** | SonarQube |
| **화면 설계** | Figma |

---

##  설계 결정 & 기술적 고민

### 사용자 구조 분리
구직자와 기업은 보유 데이터와 역할이 근본적으로 달라, 단일 테이블보다 도메인 단위로 분리하는 방향을 선택했습니다. 기업 계정은 공고·지원자·제안서 중심의 독립 도메인으로 설계했습니다.

### 상태값 기반 지원자 관리
단순 상태 UPDATE가 아닌, 변경 시 **DB에서 현재 상태를 재조회**하여 Race Condition을 방지했습니다. 화면에서 전달된 이전 상태와 DB 실제 상태가 다르면 변경을 차단하고, 변경 이력은 별도 테이블로 저장해 감사 추적이 가능하도록 구성했습니다.

### 결제 멱등성 처리
PG사와 내부 DB는 별개 시스템이기 때문에 외부 결제 성공 후 내부 예외 발생 시 포인트 미적립 문제가 생길 수 있습니다. `VERIFYING` 단계 도입과 주문번호 기반 재검증 API로 이를 해결했습니다.

### MSA 전환
초기 모놀리식에서 MSA로 전환하며 서비스 독립 배포 구조를 확보했지만, 현재 규모에서는 다소 과한 선택일 수 있다는 트레이드오프도 인지했습니다. **아키텍처 선택은 현재 규모와 팀 역량을 함께 고려해야 한다**는 점을 배웠습니다.

---

##  트러블슈팅

### 1. 외부 결제 성공 후 포인트 미적립 문제
**상황**: PG사 결제는 완료됐지만 서버 예외로 포인트가 적립되지 않는 데이터 불일치 발생  
**원인**: 외부 결제 성공 신호 수신 후 서버 예외 발생 시 롤백이 PG사에 미적용되는 구조적 한계  
**해결**: `VERIFYING` 상태 단계 추가 + 주문번호 기반 PG사 재검증 API 도입. 조건부 업데이트로 중복 처리 차단  
**결과**: 결제-적립 데이터 불일치 해소, 서버 오류 상황에서도 복구 가능한 구조 확보

### 2. Docker 내부 네트워크 설정 문제
**상황**: EC2 환경에서 컨테이너는 정상 실행되었지만 서비스 간 통신 실패, Eureka 등록 불가  
**원인**: 각 컨테이너가 서로 다른 네트워크에 배치되어 DNS 기반 통신 불가  
**해결**: `docker-compose`로 공통 네트워크 구성 후 모든 서비스를 동일 네트워크에 재배치  
**결과**: Eureka 등록 및 서비스 간 API 통신 정상화

### 3. CI/CD 파이프라인 환경 분리
**상황**: 단일 Jenkins 파이프라인으로 개발 중인 코드가 운영 배포에 영향을 줄 수 있는 구조  
**해결**: `main` 브랜치(운영) / 개인 브랜치(테스트) 파이프라인 분리 + Jenkinsfile 별도 관리  
**결과**: 개발-운영 환경 독립적 운영, 안정적 배포 흐름 확보

---

##  코드 품질 개선 (SonarQube)

SonarQube 정적 분석을 통한 리팩토링 진행

| 항목 | 개선 전 | 개선 후 |
|------|--------|--------|
| Code Smell | 324 | 212 (**약 35% 감소**) |
| 예외 처리 | `System.out` 기반 | `Logger` + 구체적 예외 타입 |
| DTO 구조 | `public` 필드 직접 접근 | `private` + 접근자, 타입 안정성 확보 |
| 중복 코드 | 반복 문자열 리터럴 | 상수 분리 및 공통 메서드 추출 |

---

##  DB 설계

- 핵심 도메인(회원·기업·공고·이력서·지원 흐름) 중심 ERD 설계
- 기술스택·자격증 등 다대다 관계는 중간 테이블로 정규화
- 결제·포인트·배너 테이블을 도메인 단위로 분리해 확장성 확보
- 공고 → 지원 → 제안 → 결과로 이어지는 채용 프로세스를 데이터 구조에 자연스럽게 반영

---

##  로컬 실행 방법

```bash
# 1. 레포지토리 클론
git clone https://github.com/solee7966-eng/Jobfinder-main-public.git
cd Jobfinder-main-public

# 2. 환경변수 설정 (.env 또는 application.yml)
# DB 접속 정보, PortOne API 키, SOLAPI 키 등 설정 필요

# 3. Discovery Service 먼저 실행 (포트 8761)
# → https://github.com/solee7966-eng/Jobfinder-discovery-public

# 4. Gateway Service 실행 (포트 8000)
# → https://github.com/solee7966-eng/Jobfinder-gateway-public

# 5. Main Service 빌드 및 실행
./gradlew build
java -jar build/libs/*.jar

# Docker로 실행하는 경우
docker-compose up --build
```

>  전체 서비스 동작을 위해 Discovery → Gateway → Main(Board) 순서로 실행해야 합니다.

---

##  관련 레포지토리

| 레포 | 역할 |
|------|------|
| [Jobfinder-main-public](https://github.com/solee7966-eng/Jobfinder-main-public) | ⬅️ 현재 (메인 서비스) |
| [Jobfinder-board-public](https://github.com/solee7966-eng/Jobfinder-board-public) | 커뮤니티 게시판 서비스 |
| [Jobfinder-gateway-public](https://github.com/solee7966-eng/Jobfinder-gateway-public) | API Gateway |
| [Jobfinder-discovery-public](https://github.com/solee7966-eng/Jobfinder-discovery-public) | Eureka Discovery 서비스 |
