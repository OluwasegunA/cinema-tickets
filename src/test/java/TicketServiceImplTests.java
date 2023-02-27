import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.paymentgateway.TicketPaymentServiceImpl;
import thirdparty.seatbooking.SeatReservationService;
import thirdparty.seatbooking.SeatReservationServiceImpl;
import uk.gov.dwp.uc.pairtest.TicketService;
import uk.gov.dwp.uc.pairtest.TicketServiceImpl;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

@RunWith(MockitoJUnitRunner.class)
public class TicketServiceImplTests {
    @Mock
    private TicketPaymentServiceImpl ticketPaymentService;
    @Mock
    private SeatReservationServiceImpl seatReservationService;

    private TicketService ticketService;
    private TicketTypeRequest ticketTypeRequest;
    private Long accountId;
    @Before
    public void setup(){
        ticketPaymentService = Mockito.mock(TicketPaymentServiceImpl.class);
        seatReservationService = Mockito.mock(SeatReservationServiceImpl.class);
        ticketService = new TicketServiceImpl(seatReservationService, ticketPaymentService);
        ticketTypeRequest = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 3);

        accountId = 3L;
    }

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();
    @Test
    public void When_PurchaseTickets_Is_Called_Passing_The_TicketTypeRequests_Of_Only_Infant_Then_Should_Return_An_InvalidPurchaseException(){
        exceptionRule.expect(InvalidPurchaseException.class);
        exceptionRule.expectMessage("an adult must exist to be able to purchase ticket for infant and child");
        ticketService.purchaseTickets(accountId, ticketTypeRequest);
    }
    @Test
    public void When_PurchaseTickets_Is_Called_Passing_The_TicketTypeRequests_Of_Only_Infant_And_Child_Type_Then_Should_Return_An_InvalidPurchaseException(){
        TicketTypeRequest[] ticketTypeRequests = new TicketTypeRequest[2];
        TicketTypeRequest ticketTypeRequest1 = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 3);
        TicketTypeRequest ticketTypeRequest2 = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 3);
        ticketTypeRequests[0] = ticketTypeRequest1;
        ticketTypeRequests[1] = ticketTypeRequest2;
        exceptionRule.expect(InvalidPurchaseException.class);
        exceptionRule.expectMessage("an adult must exist to be able to purchase ticket for infant and child");
        ticketService.purchaseTickets(accountId, ticketTypeRequests);
    }

    @Test
    public void When_PurchaseTickets_Is_Called_Passing_The_TicketTypeRequests_Of_Only_Infant_And_Child_Then_Should_Return_An_InvalidPurchaseException(){
        TicketTypeRequest[] ticketTypeRequests = new TicketTypeRequest[2];
        TicketTypeRequest ticketTypeRequest1 = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 3);
        TicketTypeRequest ticketTypeRequest2 = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 3);
        ticketTypeRequests[0] = ticketTypeRequest1;
        ticketTypeRequests[1] = ticketTypeRequest2;
        exceptionRule.expect(InvalidPurchaseException.class);
        exceptionRule.expectMessage("an adult must exist to be able to purchase ticket for infant and child");
        ticketService.purchaseTickets(accountId, ticketTypeRequests);
    }

    @Test
    public void When_PurchaseTickets_Is_Called_Passing_No_Of_Tickets_Greater_Than_20_Then_Should_Return_An_InvalidPurchaseException(){
        TicketTypeRequest[] ticketTypeRequests = new TicketTypeRequest[2];
        TicketTypeRequest ticketTypeRequest1 = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 12);
        TicketTypeRequest ticketTypeRequest2 = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 10);
        ticketTypeRequests[0] = ticketTypeRequest1;
        ticketTypeRequests[1] = ticketTypeRequest2;
        exceptionRule.expect(InvalidPurchaseException.class);
        exceptionRule.expectMessage("No of purchased tickets greater than 20");
        ticketService.purchaseTickets(accountId, ticketTypeRequests);
    }

    @Test
    public void When_PurchaseTickets_Is_Called_Passing_Invalid_AccountId_Then_Should_Return_An_InvalidPurchaseException(){
        TicketTypeRequest[] ticketTypeRequests = new TicketTypeRequest[2];
        TicketTypeRequest ticketTypeRequest1 = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 3);
        TicketTypeRequest ticketTypeRequest2 = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 10);
        ticketTypeRequests[0] = ticketTypeRequest1;
        ticketTypeRequests[1] = ticketTypeRequest2;
        accountId = 0L;
        exceptionRule.expect(InvalidPurchaseException.class);
        exceptionRule.expectMessage("Insufficient Account balance");
        ticketService.purchaseTickets(accountId, ticketTypeRequests);
    }

    @Test
    public void When_PurchaseTickets_Is_Called_Passing_Valid_AccountId_And_TicketTypeRequests_Then_Should_Invoke_TicketPaymentService_And_SeatReservationService(){
        TicketTypeRequest[] ticketTypeRequests = new TicketTypeRequest[2];
        TicketTypeRequest ticketTypeRequest1 = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 3);
        TicketTypeRequest ticketTypeRequest2 = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 3);
        ticketTypeRequests[0] = ticketTypeRequest1;
        ticketTypeRequests[1] = ticketTypeRequest2;

        ticketService.purchaseTickets(accountId, ticketTypeRequests);

        Mockito.verify(ticketPaymentService, Mockito.times(1)).makePayment(accountId, 90);
        Mockito.verify(seatReservationService, Mockito.times(1)).reserveSeat(accountId, 6);
    }
}
