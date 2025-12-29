package com.example.demo;

import com.example.demo.exception.BadRequestException;
import com.example.demo.model.*;
import com.example.demo.repository.*;
import com.example.demo.security.CustomUserDetailsService;
import com.example.demo.security.JwtTokenProvider;
import com.example.demo.servlet.HelloServlet;
import com.example.demo.service.*;
import com.example.demo.service.impl.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.mockito.Mockito;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDate;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Listeners(TestResultListener.class)
public class DynamicEventTicketPricingEngineApplicationTests {

    private EventRecordRepository eventRecordRepository;
    private SeatInventoryRecordRepository seatInventoryRecordRepository;
    private PricingRuleRepository pricingRuleRepository;
    private DynamicPriceRecordRepository dynamicPriceRecordRepository;
    private PriceAdjustmentLogRepository priceAdjustmentLogRepository;

    private EventRecordService eventRecordService;
    private SeatInventoryService seatInventoryService;
    private PricingRuleService pricingRuleService;
    private DynamicPricingEngineService dynamicPricingEngineService;
    private PriceAdjustmentLogService priceAdjustmentLogService;

    private CustomUserDetailsService customUserDetailsService;
    private JwtTokenProvider jwtTokenProvider;

    @BeforeClass
    public void setup() {
        eventRecordRepository = Mockito.mock(EventRecordRepository.class);
        seatInventoryRecordRepository = Mockito.mock(SeatInventoryRecordRepository.class);
        pricingRuleRepository = Mockito.mock(PricingRuleRepository.class);
        dynamicPriceRecordRepository = Mockito.mock(DynamicPriceRecordRepository.class);
        priceAdjustmentLogRepository = Mockito.mock(PriceAdjustmentLogRepository.class);

        eventRecordService = new EventRecordServiceImpl(eventRecordRepository);
        seatInventoryService = new SeatInventoryServiceImpl(seatInventoryRecordRepository, eventRecordRepository);
        pricingRuleService = new PricingRuleServiceImpl(pricingRuleRepository);
        dynamicPricingEngineService = new DynamicPricingEngineServiceImpl(
                eventRecordRepository,
                seatInventoryRecordRepository,
                pricingRuleRepository,
                dynamicPriceRecordRepository,
                priceAdjustmentLogRepository
        );
        priceAdjustmentLogService = new PriceAdjustmentLogServiceImpl(priceAdjustmentLogRepository);

        customUserDetailsService = new CustomUserDetailsService();
        jwtTokenProvider = new JwtTokenProvider("VerySecretKeyForJwtDemoApplication123456", 3600000L, true);
    }

    // 1. Servlet tests
    @Test(priority = 1, groups = "servlet")
    public void testServletRespondsWithHelloMessage() throws Exception {
        HelloServlet servlet = new HelloServlet();
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);

        StringWriter writer = new StringWriter();
        when(resp.getWriter()).thenReturn(new PrintWriter(writer));

        servlet.doGet(req, resp);

        Assert.assertTrue(writer.toString().contains("Hello from Dynamic Event Ticket Pricing Servlet"));
    }

    @Test(priority = 2, groups = "servlet")
    public void testServletContentTypePlainText() throws Exception {
        HelloServlet servlet = new HelloServlet();
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        when(resp.getWriter()).thenReturn(new PrintWriter(new StringWriter()));

        servlet.doGet(req, resp);

        verify(resp).setContentType("text/plain");
    }

    @Test(priority = 3, groups = "servlet")
    public void testServletMultipleCalls() throws Exception {
        HelloServlet servlet = new HelloServlet();
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        when(resp.getWriter()).thenReturn(new PrintWriter(new StringWriter()));

        servlet.doGet(req, resp);
        servlet.doGet(req, resp);

        verify(resp, times(2)).getWriter();
    }

    @Test(priority = 4, groups = "servlet")
    public void testServletNullRequest() throws Exception {
        HelloServlet servlet = new HelloServlet();
        HttpServletResponse resp = mock(HttpServletResponse.class);
        when(resp.getWriter()).thenReturn(new PrintWriter(new StringWriter()));

        servlet.doGet(null, resp);

        verify(resp).getWriter();
    }

    @Test(priority = 5, groups = "servlet")
    public void testServletWriterException() throws Exception {
        HelloServlet servlet = new HelloServlet();
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        when(resp.getWriter()).thenThrow(new RuntimeException("Writer error"));

        try {
            servlet.doGet(req, resp);
            Assert.fail("Expected RuntimeException");
        } catch (RuntimeException ex) {
            Assert.assertEquals(ex.getMessage(), "Writer error");
        }
    }

    @Test(priority = 6, groups = "servlet")
    public void testServletOutputLengthPositive() throws Exception {
        HelloServlet servlet = new HelloServlet();
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        StringWriter writer = new StringWriter();
        when(resp.getWriter()).thenReturn(new PrintWriter(writer));

        servlet.doGet(req, resp);

        Assert.assertTrue(writer.toString().length() > 0);
    }

    @Test(priority = 7, groups = "servlet")
    public void testServletDeterministicOutput() throws Exception {
        HelloServlet servlet = new HelloServlet();
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);

        StringWriter w1 = new StringWriter();
        StringWriter w2 = new StringWriter();

        when(resp.getWriter()).thenReturn(new PrintWriter(w1))
                              .thenReturn(new PrintWriter(w2));

        servlet.doGet(req, resp);
        servlet.doGet(req, resp);

        Assert.assertEquals(w1.toString(), w2.toString());
    }

    @Test(priority = 8, groups = "servlet")
    public void testServletIsHttpServlet() {
        HelloServlet servlet = new HelloServlet();
        Assert.assertTrue(servlet instanceof jakarta.servlet.http.HttpServlet);
    }

    // 2. CRUD operations
    @Test(priority = 9, groups = "crud")
    public void testCreateEventSuccess() {
        EventRecord event = new EventRecord();
        event.setEventCode("EVT001");
        event.setEventName("Music Show");
        event.setVenue("Hall A");
        event.setEventDate(LocalDate.now().plusDays(10));
        event.setBasePrice(100.0);

        when(eventRecordRepository.existsByEventCode("EVT001")).thenReturn(false);
        when(eventRecordRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        EventRecord saved = eventRecordService.createEvent(event);
        Assert.assertEquals(saved.getEventCode(), "EVT001");
    }

    @Test(priority = 10, groups = "crud")
    public void testCreateEventDuplicateCodeThrows() {
        EventRecord event = new EventRecord();
        event.setEventCode("EVT001");
        event.setBasePrice(100.0);

        when(eventRecordRepository.existsByEventCode("EVT001")).thenReturn(true);

        try {
            eventRecordService.createEvent(event);
            Assert.fail("Expected BadRequestException");
        } catch (BadRequestException ex) {
            Assert.assertTrue(ex.getMessage().contains("Event code already exists"));
        }
    }

    @Test(priority = 11, groups = "crud")
    public void testCreateEventInvalidBasePrice() {
        EventRecord event = new EventRecord();
        event.setEventCode("EVT002");
        event.setBasePrice(0.0);

        try {
            eventRecordService.createEvent(event);
            Assert.fail("Expected BadRequestException");
        } catch (BadRequestException ex) {
            Assert.assertTrue(ex.getMessage().contains("Base price must be > 0"));
        }
    }

    @Test(priority = 12, groups = "crud")
    public void testGetEventById() {
        EventRecord event = new EventRecord();
        event.setId(1L);
        event.setEventCode("EVT003");

        when(eventRecordRepository.findById(1L))
                .thenReturn(java.util.Optional.of(event));

        EventRecord found = eventRecordService.getEventById(1L);
        Assert.assertEquals(found.getEventCode(), "EVT003");
    }

    @Test(priority = 13, groups = "crud")
    public void testGetAllEventsEmpty() {
        when(eventRecordRepository.findAll()).thenReturn(Collections.emptyList());
        Assert.assertTrue(eventRecordService.getAllEvents().isEmpty());
    }

    @Test(priority = 14, groups = "crud")
    public void testUpdateEventStatus() {
        EventRecord event = new EventRecord();
        event.setId(2L);
        event.setActive(true);

        when(eventRecordRepository.findById(2L))
                .thenReturn(java.util.Optional.of(event));
        when(eventRecordRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        EventRecord updated = eventRecordService.updateEventStatus(2L, false);
        Assert.assertFalse(updated.getActive());
    }

    @Test(priority = 15, groups = "crud")
    public void testCreateSeatInventorySuccess() {
        SeatInventoryRecord inv = new SeatInventoryRecord();
        inv.setEventId(5L);
        inv.setTotalSeats(100);
        inv.setRemainingSeats(80);

        when(eventRecordRepository.findById(5L))
                .thenReturn(java.util.Optional.of(new EventRecord()));
        when(seatInventoryRecordRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        SeatInventoryRecord saved = seatInventoryService.createInventory(inv);
        Assert.assertEquals(saved.getRemainingSeats(), Integer.valueOf(80));
    }

    @Test(priority = 16, groups = "crud")
    public void testCreateSeatInventoryInvalidRemainingSeats() {
        SeatInventoryRecord inv = new SeatInventoryRecord();
        inv.setEventId(6L);
        inv.setTotalSeats(50);
        inv.setRemainingSeats(60);

        when(eventRecordRepository.findById(6L))
                .thenReturn(java.util.Optional.of(new EventRecord()));

        try {
            seatInventoryService.createInventory(inv);
            Assert.fail("Expected BadRequestException");
        } catch (BadRequestException ex) {
            Assert.assertTrue(ex.getMessage().contains("Remaining seats cannot exceed total seats"));
        }
    }

    @Test(priority = 17, groups = "crud")
    public void testCreatePricingRuleSuccess() {
        PricingRule rule = new PricingRule();
        rule.setRuleCode("R001");
        rule.setMinRemainingSeats(0);
        rule.setMaxRemainingSeats(50);
        rule.setDaysBeforeEvent(10);
        rule.setPriceMultiplier(1.2);
        rule.setActive(true);

        when(pricingRuleRepository.existsByRuleCode("R001")).thenReturn(false);
        when(pricingRuleRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        PricingRule saved = pricingRuleService.createRule(rule);
        Assert.assertEquals(saved.getRuleCode(), "R001");
    }

    @Test(priority = 18, groups = "crud")
    public void testCreatePricingRuleInvalidMultiplier() {
        PricingRule rule = new PricingRule();
        rule.setRuleCode("R002");
        rule.setPriceMultiplier(0.0);

        try {
            pricingRuleService.createRule(rule);
            Assert.fail("Expected BadRequestException");
        } catch (BadRequestException ex) {
            Assert.assertTrue(ex.getMessage().contains("Price multiplier must be > 0"));
        }
    }

    // 3. Dependency Injection and IoC
    @Test(priority = 19, groups = "di")
    public void testEventRecordServiceInjected() {
        Assert.assertNotNull(eventRecordService);
    }

    @Test(priority = 20, groups = "di")
    public void testDynamicPricingEngineUsesRepositoriesNoInventory() {
        EventRecord event = new EventRecord();
        event.setId(10L);
        event.setActive(true);
        event.setBasePrice(100.0);
        event.setEventDate(LocalDate.now().plusDays(5));

        when(eventRecordRepository.findById(10L))
                .thenReturn(java.util.Optional.of(event));
        when(seatInventoryRecordRepository.findByEventId(10L))
                .thenReturn(java.util.Optional.empty());

        try {
            dynamicPricingEngineService.computeDynamicPrice(10L);
            Assert.fail("Expected NotFoundException");
        } catch (RuntimeException ex) {
            Assert.assertTrue(ex.getMessage().contains("Seat inventory not found"));
        }
    }

    @Test(priority = 21, groups = "di")
    public void testDynamicPricingEngineInactiveEvent() {
        EventRecord event = new EventRecord();
        event.setId(11L);
        event.setActive(false);
        event.setBasePrice(100.0);
        event.setEventDate(LocalDate.now().plusDays(5));

        when(eventRecordRepository.findById(11L))
                .thenReturn(java.util.Optional.of(event));

        try {
            dynamicPricingEngineService.computeDynamicPrice(11L);
            Assert.fail("Expected BadRequestException");
        } catch (BadRequestException ex) {
            Assert.assertTrue(ex.getMessage().contains("Event is not active"));
        }
    }

    @Test(priority = 22, groups = "di")
    public void testCustomUserDetailsServiceRegistersUser() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encoded = encoder.encode("pass123");

        Map<String, Object> user = customUserDetailsService.registerUser(
                "Admin User",
                "admin@example.com",
                encoded,
                "ADMIN"
        );
        Assert.assertNotNull(user.get("userId"));
        Assert.assertEquals(user.get("role"), "ADMIN");
    }

    @Test(priority = 23, groups = "di")
    public void testCustomUserDetailsServiceLoadsUser() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        customUserDetailsService.registerUser(
                "Manager",
                "manager@example.com",
                encoder.encode("secret"),
                "EVENT_MANAGER"
        );

        Assert.assertNotNull(customUserDetailsService.loadUserByUsername("manager@example.com"));
    }

    @Test(priority = 24, groups = "di")
    public void testLoadUnknownUserThrows() {
        try {
            customUserDetailsService.loadUserByUsername("unknown@example.com");
            Assert.fail("Expected UsernameNotFoundException");
        } catch (org.springframework.security.core.userdetails.UsernameNotFoundException ex) {
            Assert.assertTrue(ex.getMessage().contains("User not found"));
        }
    }

    // 4. Hibernate-like behavior
    @Test(priority = 25, groups = "hibernate")
    public void testEventRecordActiveDefaultTrue() {
        EventRecord event = new EventRecord();
        event.setEventCode("EVT-H1");
        event.setBasePrice(150.0);
        event.setEventDate(LocalDate.now().plusDays(3));
        event.prePersist();
        Assert.assertTrue(event.getActive());
    }

    @Test(priority = 26, groups = "hibernate")
    public void testSeatInventoryUpdatedAtSetOnPersist() {
        SeatInventoryRecord inv = new SeatInventoryRecord();
        inv.setEventId(1L);
        inv.setTotalSeats(100);
        inv.setRemainingSeats(80);
        inv.preUpdate();
        Assert.assertNotNull(inv.getUpdatedAt());
    }

    @Test(priority = 27, groups = "hibernate")
    public void testDynamicPriceRecordComputedAtSetOnPersist() {
        DynamicPriceRecord rec = new DynamicPriceRecord();
        rec.setEventId(1L);
        rec.setComputedPrice(120.0);
        rec.prePersist();
        Assert.assertNotNull(rec.getComputedAt());
    }

    @Test(priority = 28, groups = "hibernate")
    public void testPriceAdjustmentLogChangedAtSetOnPersist() {
        PriceAdjustmentLog log = new PriceAdjustmentLog();
        log.setEventId(1L);
        log.setOldPrice(100.0);
        log.setNewPrice(120.0);
        log.prePersist();
        Assert.assertNotNull(log.getChangedAt());
    }

    @Test(priority = 29, groups = "hibernate")
    public void testDynamicPricingComputeWithNoRulesUsesBasePrice() {
        EventRecord event = new EventRecord();
        event.setId(20L);
        event.setActive(true);
        event.setBasePrice(100.0);
        event.setEventDate(LocalDate.now().plusDays(10));

        SeatInventoryRecord inv = new SeatInventoryRecord();
        inv.setEventId(20L);
        inv.setTotalSeats(100);
        inv.setRemainingSeats(100);

        when(eventRecordRepository.findById(20L))
                .thenReturn(java.util.Optional.of(event));
        when(seatInventoryRecordRepository.findByEventId(20L))
                .thenReturn(java.util.Optional.of(inv));
        when(pricingRuleRepository.findByActiveTrue())
                .thenReturn(Collections.emptyList());
        when(dynamicPriceRecordRepository.save(any()))
                .thenAnswer(i -> i.getArgument(0));
        when(dynamicPriceRecordRepository.findFirstByEventIdOrderByComputedAtDesc(20L))
                .thenReturn(java.util.Optional.empty());

        DynamicPriceRecord rec = dynamicPricingEngineService.computeDynamicPrice(20L);
        Assert.assertEquals(rec.getComputedPrice(), 100.0);
    }

    @Test(priority = 30, groups = "hibernate")
    public void testDynamicPricingAppliesMultiplier() {
        EventRecord event = new EventRecord();
        event.setId(21L);
        event.setActive(true);
        event.setBasePrice(100.0);
        event.setEventDate(LocalDate.now().plusDays(5));

        SeatInventoryRecord inv = new SeatInventoryRecord();
        inv.setEventId(21L);
        inv.setTotalSeats(100);
        inv.setRemainingSeats(40);

        PricingRule rule = new PricingRule();
        rule.setRuleCode("R-HIB1");
        rule.setMinRemainingSeats(0);
        rule.setMaxRemainingSeats(50);
        rule.setDaysBeforeEvent(10);
        rule.setPriceMultiplier(1.5);
        rule.setActive(true);

        when(eventRecordRepository.findById(21L))
                .thenReturn(java.util.Optional.of(event));
        when(seatInventoryRecordRepository.findByEventId(21L))
                .thenReturn(java.util.Optional.of(inv));
        when(pricingRuleRepository.findByActiveTrue())
                .thenReturn(Collections.singletonList(rule));
        when(dynamicPriceRecordRepository.save(any()))
                .thenAnswer(i -> i.getArgument(0));
        when(dynamicPriceRecordRepository.findFirstByEventIdOrderByComputedAtDesc(21L))
                .thenReturn(java.util.Optional.empty());

        DynamicPriceRecord rec = dynamicPricingEngineService.computeDynamicPrice(21L);
        Assert.assertEquals(rec.getComputedPrice(), 150.0);
    }

    // 5. JPA normalization style checks
    @Test(priority = 31, groups = "jpa")
    public void testSeatInventoryUsesEventIdForeignKeyStyle() {
        SeatInventoryRecord inv = new SeatInventoryRecord();
        inv.setEventId(99L);
        Assert.assertEquals(inv.getEventId(), Long.valueOf(99L));
    }

    @Test(priority = 32, groups = "jpa")
    public void testDynamicPriceRecordUsesEventIdReference() {
        DynamicPriceRecord rec = new DynamicPriceRecord();
        rec.setEventId(88L);
        Assert.assertEquals(rec.getEventId(), Long.valueOf(88L));
    }

    @Test(priority = 33, groups = "jpa")
    public void testPriceAdjustmentLogUsesEventIdReference() {
        PriceAdjustmentLog log = new PriceAdjustmentLog();
        log.setEventId(77L);
        Assert.assertEquals(log.getEventId(), Long.valueOf(77L));
    }

    @Test(priority = 34, groups = "jpa")
    public void testEventRecordAttributesAreAtomic1NF() {
        EventRecord event = new EventRecord();
        event.setEventName("My Event");
        Assert.assertFalse(event.getEventName().contains(","));
    }

    @Test(priority = 35, groups = "jpa")
    public void testSeparateInventoryPreventsPartialDependency2NF() {
        SeatInventoryRecord inv = new SeatInventoryRecord();
        inv.setEventId(1L);
        inv.setTotalSeats(200);
        Assert.assertEquals(inv.getTotalSeats(), Integer.valueOf(200));
    }

    @Test(priority = 36, groups = "jpa")
    public void testSeparateDynamicPriceRemovesTransitiveDependency3NF() {
        DynamicPriceRecord rec = new DynamicPriceRecord();
        rec.setComputedPrice(200.0);
        rec.setAppliedRuleCodes("R1,R2");
        Assert.assertTrue(rec.getAppliedRuleCodes().contains("R1"));
    }

    @Test(priority = 37, groups = "jpa")
    public void testGetAllEventsReturnsList() {
        when(eventRecordRepository.findAll()).thenReturn(Collections.emptyList());
        Assert.assertTrue(eventRecordService.getAllEvents().isEmpty());
    }

    @Test(priority = 38, groups = "jpa")
    public void testGetAllRulesReturnsList() {
        when(pricingRuleRepository.findAll()).thenReturn(Collections.emptyList());
        Assert.assertTrue(pricingRuleService.getAllRules().isEmpty());
    }

    // 6. Many-to-many style via associations
    @Test(priority = 39, groups = "manyToMany")
    public void testEventHasMultipleDynamicPricesOverTime() {
        DynamicPriceRecord r1 = new DynamicPriceRecord();
        r1.setEventId(1L);
        r1.setComputedPrice(100.0);

        DynamicPriceRecord r2 = new DynamicPriceRecord();
        r2.setEventId(1L);
        r2.setComputedPrice(120.0);

        when(dynamicPriceRecordRepository.findByEventIdOrderByComputedAtDesc(1L))
                .thenReturn(Arrays.asList(r1, r2));

        List<DynamicPriceRecord> history = dynamicPricingEngineService.getPriceHistory(1L);
        Assert.assertEquals(history.size(), 2);
    }

    @Test(priority = 40, groups = "manyToMany")
    public void testEventHasMultiplePriceAdjustments() {
        PriceAdjustmentLog l1 = new PriceAdjustmentLog();
        l1.setEventId(2L);
        PriceAdjustmentLog l2 = new PriceAdjustmentLog();
        l2.setEventId(2L);

        when(priceAdjustmentLogRepository.findByEventId(2L))
                .thenReturn(Arrays.asList(l1, l2));

        List<PriceAdjustmentLog> list = priceAdjustmentLogService.getAdjustmentsByEvent(2L);
        Assert.assertEquals(list.size(), 2);
    }

    @Test(priority = 41, groups = "manyToMany")
    public void testMultipleRulesAffectSingleEvent() {
        PricingRule r1 = new PricingRule();
        r1.setRuleCode("M1");
        r1.setMinRemainingSeats(0);
        r1.setMaxRemainingSeats(50);
        r1.setDaysBeforeEvent(10);
        r1.setPriceMultiplier(1.2);
        r1.setActive(true);

        PricingRule r2 = new PricingRule();
        r2.setRuleCode("M2");
        r2.setMinRemainingSeats(51);
        r2.setMaxRemainingSeats(100);
        r2.setDaysBeforeEvent(10);
        r2.setPriceMultiplier(0.9);
        r2.setActive(true);

        when(pricingRuleRepository.findAll()).thenReturn(Arrays.asList(r1, r2));
        Assert.assertEquals(pricingRuleService.getAllRules().size(), 2);
    }

    @Test(priority = 42, groups = "manyToMany")
    public void testComputePriceLogsAdjustmentWhenChanged() {
        EventRecord event = new EventRecord();
        event.setId(30L);
        event.setActive(true);
        event.setBasePrice(100.0);
        event.setEventDate(LocalDate.now().plusDays(5));

        SeatInventoryRecord inv = new SeatInventoryRecord();
        inv.setEventId(30L);
        inv.setTotalSeats(100);
        inv.setRemainingSeats(40);

        PricingRule rule = new PricingRule();
        rule.setRuleCode("M-RULE");
        rule.setMinRemainingSeats(0);
        rule.setMaxRemainingSeats(50);
        rule.setDaysBeforeEvent(10);
        rule.setPriceMultiplier(1.5);
        rule.setActive(true);

        DynamicPriceRecord previous = new DynamicPriceRecord();
        previous.setEventId(30L);
        previous.setComputedPrice(100.0);

        when(eventRecordRepository.findById(30L))
                .thenReturn(java.util.Optional.of(event));
        when(seatInventoryRecordRepository.findByEventId(30L))
                .thenReturn(java.util.Optional.of(inv));
        when(pricingRuleRepository.findByActiveTrue())
                .thenReturn(Collections.singletonList(rule));
        when(dynamicPriceRecordRepository.findFirstByEventIdOrderByComputedAtDesc(30L))
                .thenReturn(java.util.Optional.of(previous));
        when(dynamicPriceRecordRepository.save(any()))
                .thenAnswer(i -> i.getArgument(0));
        when(priceAdjustmentLogRepository.save(any()))
                .thenAnswer(i -> i.getArgument(0));

        DynamicPriceRecord rec = dynamicPricingEngineService.computeDynamicPrice(30L);
        Assert.assertEquals(rec.getComputedPrice(), 150.0);
    }

    @Test(priority = 43, groups = "manyToMany")
    public void testAllComputedPricesList() {
        when(dynamicPriceRecordRepository.findAll()).thenReturn(Collections.emptyList());
        Assert.assertTrue(dynamicPricingEngineService.getAllComputedPrices().isEmpty());
    }

    // 7. Security and JWT
    @Test(priority = 44, groups = "security")
    public void testRegisterUserProducesValidToken() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        Map<String, Object> user =
                customUserDetailsService.registerUser(
                        "Security User",
                        "sec@example.com",
                        encoder.encode("secpass"),
                        "ADMIN"
                );

        Authentication auth =
                new UsernamePasswordAuthenticationToken(
                        "sec@example.com",
                        "secpass",
                        Collections.emptyList());

        String token = jwtTokenProvider.generateToken(auth,
                (Long) user.get("userId"),
                (String) user.get("role"));

        Assert.assertNotNull(token);
    }

    @Test(priority = 45, groups = "security")
    public void testJwtTokenContainsUsername() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        Map<String, Object> user =
                customUserDetailsService.registerUser(
                        "Jwt User",
                        "jwt@example.com",
                        encoder.encode("jwtpass"),
                        "EVENT_MANAGER"
                );

        Authentication auth =
                new UsernamePasswordAuthenticationToken(
                        "jwt@example.com",
                        "jwtpass",
                        Collections.emptyList());

        String token = jwtTokenProvider.generateToken(
                auth,
                (Long) user.get("userId"),
                (String) user.get("role"));

        String username = jwtTokenProvider.getUsernameFromToken(token);
        Assert.assertEquals(username, "jwt@example.com");
    }

    @Test(priority = 46, groups = "security")
    public void testJwtValidation() {
        Authentication auth =
                new UsernamePasswordAuthenticationToken(
                        "valid@example.com",
                        "password",
                        Collections.emptyList()
                );

        String token = jwtTokenProvider.generateToken(auth, 1L, "ADMIN");
        Assert.assertTrue(jwtTokenProvider.validateToken(token));
    }

    @Test(priority = 47, groups = "security")
    public void testJwtInvalidTokenFailsValidation() {
        Assert.assertFalse(jwtTokenProvider.validateToken("invalid.token.value"));
    }

    @Test(priority = 48, groups = "security")
    public void testPasswordEncoderMatches() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encoded = encoder.encode("mypassword");
        Assert.assertTrue(encoder.matches("mypassword", encoded));
    }

    @Test(priority = 49, groups = "security")
    public void testCustomUserDetailsServiceRoleStored() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        Map<String, Object> user =
                customUserDetailsService.registerUser(
                        "Role User",
                        "role@example.com",
                        encoder.encode("rolepass"),
                        "PRICING_ANALYST"
                );
        Assert.assertEquals(user.get("role"), "PRICING_ANALYST");
    }

    @Test(priority = 50, groups = "security")
    public void testJwtClaimsContainRoleAndUserId() {
        Authentication auth =
                new UsernamePasswordAuthenticationToken(
                        "claims@example.com",
                        "pass",
                        Collections.emptyList());

        String token =
                jwtTokenProvider.generateToken(auth, 42L, "ADMIN");

        Map<String, Object> claims = jwtTokenProvider.getAllClaims(token);
        Assert.assertEquals(((Number) claims.get("userId")).longValue(), 42L);
        Assert.assertEquals(claims.get("role"), "ADMIN");
        Assert.assertEquals(claims.get("email"), "claims@example.com");
    }

    @Test(priority = 51, groups = "security")
    public void testJwtTokenIsDifferentForDifferentUsers() {
        Authentication a1 =
                new UsernamePasswordAuthenticationToken("a@example.com", "pass");
        Authentication a2 =
                new UsernamePasswordAuthenticationToken("b@example.com", "pass");

        String t1 = jwtTokenProvider.generateToken(a1, 1L, "ADMIN");
        String t2 = jwtTokenProvider.generateToken(a2, 2L, "PRICING_ANALYST");

        Assert.assertNotEquals(t1, t2);
    }

    // 8. HQL / query-style tests
    @Test(priority = 52, groups = "hql")
    public void testFindEventByCodeActsLikeHqlQuery() {
        EventRecord event = new EventRecord();
        event.setEventCode("HQL1");

        when(eventRecordRepository.findByEventCode("HQL1"))
                .thenReturn(java.util.Optional.of(event));

        java.util.Optional<EventRecord> opt = eventRecordService.getEventByCode("HQL1");
        Assert.assertTrue(opt.isPresent());
    }

    @Test(priority = 53, groups = "hql")
    public void testGetActiveRulesActsAsQuery() {
        PricingRule r = new PricingRule();
        r.setActive(true);

        when(pricingRuleRepository.findByActiveTrue())
                .thenReturn(Collections.singletonList(r));

        List<PricingRule> list = pricingRuleService.getActiveRules();
        Assert.assertEquals(list.size(), 1);
    }

    @Test(priority = 54, groups = "hql")
    public void testGetPriceHistoryActsAsQuery() {
        DynamicPriceRecord r = new DynamicPriceRecord();
        r.setEventId(99L);

        when(dynamicPriceRecordRepository.findByEventIdOrderByComputedAtDesc(99L))
                .thenReturn(Collections.singletonList(r));

        List<DynamicPriceRecord> list =
                dynamicPricingEngineService.getPriceHistory(99L);

        Assert.assertEquals(list.size(), 1);
    }

    @Test(priority = 55, groups = "hql")
    public void testFindInventoryByEventIdActsAsCriteriaQuery() {
        SeatInventoryRecord inv = new SeatInventoryRecord();
        inv.setEventId(100L);

        when(seatInventoryRecordRepository.findByEventId(100L))
                .thenReturn(java.util.Optional.of(inv));

        SeatInventoryRecord found = seatInventoryService.getInventoryByEvent(100L);
        Assert.assertEquals(found.getEventId(), Long.valueOf(100L));
    }

    @Test(priority = 56, groups = "hql")
    public void testFindAdjustmentsByEventIdActsAsQuery() {
        PriceAdjustmentLog log = new PriceAdjustmentLog();
        log.setEventId(101L);

        when(priceAdjustmentLogRepository.findByEventId(101L))
                .thenReturn(Collections.singletonList(log));

        List<PriceAdjustmentLog> list =
                priceAdjustmentLogService.getAdjustmentsByEvent(101L);

        Assert.assertEquals(list.size(), 1);
    }

    @Test(priority = 57, groups = "hql")
    public void testNoActiveRulesReturnsEmpty() {
        when(pricingRuleRepository.findByActiveTrue())
                .thenReturn(Collections.emptyList());

        List<PricingRule> list = pricingRuleService.getActiveRules();
        Assert.assertTrue(list.isEmpty());
    }

    @Test(priority = 58, groups = "hql")
    public void testNoPriceHistoryReturnsEmpty() {
        when(dynamicPriceRecordRepository.findByEventIdOrderByComputedAtDesc(202L))
                .thenReturn(Collections.emptyList());
        List<DynamicPriceRecord> list =
                dynamicPricingEngineService.getPriceHistory(202L);
        Assert.assertTrue(list.isEmpty());
    }

    @Test(priority = 59, groups = "hql")
    public void testOptionalEventCodeNotFound() {
        when(eventRecordRepository.findByEventCode("NOEVT"))
                .thenReturn(java.util.Optional.empty());
        java.util.Optional<EventRecord> opt =
                eventRecordService.getEventByCode("NOEVT");
        Assert.assertFalse(opt.isPresent());
    }

    @Test(priority = 60, groups = "hql")
    public void testAdvancedFilteringByMultiplierInMemory() {
        PricingRule r1 = new PricingRule();
        r1.setPriceMultiplier(1.5);
        PricingRule r2 = new PricingRule();
        r2.setPriceMultiplier(0.8);

        List<PricingRule> rules = Arrays.asList(r1, r2);
        long countHigh = rules.stream()
                .filter(r -> r.getPriceMultiplier() > 1.0)
                .count();

        Assert.assertEquals(countHigh, 1);
    }
}
