package uk.gov.dwp.uc.pairtest;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.paymentgateway.TicketPaymentServiceImpl;
import thirdparty.seatbooking.SeatReservationService;
import thirdparty.seatbooking.SeatReservationServiceImpl;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

public class TicketServiceImpl implements TicketService {
    /**
     * Should only have private methods other than the one below.
     */

    private final SeatReservationService seatReservationService;
    private final TicketPaymentService ticketPaymentService;

    public TicketServiceImpl (SeatReservationServiceImpl seatReservationServiceImpl, TicketPaymentServiceImpl ticketPaymentServiceImpl){
        this.seatReservationService = seatReservationServiceImpl;
        this.ticketPaymentService = ticketPaymentServiceImpl;
    }

    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {
        if(accountId <= 0)
            throw new InvalidPurchaseException("Insufficient Account balance");

        if(!CheckIfThereExistAdult(ticketTypeRequests))
            throw new InvalidPurchaseException("an adult must exist to be able to purchase ticket for infant and child");

        if(CalculateTotalTickets(ticketTypeRequests) > 20)
            throw new InvalidPurchaseException("No of purchased tickets greater than 20");

        int totalSeats = CalculateTotalSeatToAllocate(ticketTypeRequests);
        int totalPayableAmount = CalculateTotalAmountToPay(ticketTypeRequests);

        if (totalSeats != 0 && totalPayableAmount != 0){
            ticketPaymentService.makePayment(accountId, totalPayableAmount);
            seatReservationService.reserveSeat(accountId, totalSeats);
        }

    }

    private int CalculateTotalSeatToAllocate(TicketTypeRequest... ticketTypeRequests){
        int totalSeatToAllocate = 0;
        for (TicketTypeRequest ticketTypeRequest: ticketTypeRequests) {
            if(!ticketTypeRequest.getTicketType().equals(TicketTypeRequest.Type.INFANT)){
                totalSeatToAllocate += ticketTypeRequest.getNoOfTickets();
            }
        }

        return totalSeatToAllocate;
    }

    private int CalculateTotalAmountToPay(TicketTypeRequest... ticketTypeRequests){
        int totalAmountToPay = 0;
        for (TicketTypeRequest ticketTypeRequest: ticketTypeRequests) {
            if(!ticketTypeRequest.getTicketType().equals(TicketTypeRequest.Type.INFANT)){
                totalAmountToPay += ticketTypeRequest.getTicketType().equals(TicketTypeRequest.Type.ADULT)
                        ? 20 * ticketTypeRequest.getNoOfTickets()
                        : 10 * ticketTypeRequest.getNoOfTickets();
            }
        }

        return totalAmountToPay;
    }

    private boolean CheckIfThereExistAdult(TicketTypeRequest... ticketTypeRequests){
        for (TicketTypeRequest ticketTypeRequest: ticketTypeRequests) {
            if(ticketTypeRequest.getTicketType().equals(TicketTypeRequest.Type.ADULT)){
                return true;
            }
        }
        return false;
    }

    private int CalculateTotalTickets(TicketTypeRequest... ticketTypeRequests){
        int totalTickets = 0;
        for (TicketTypeRequest ticketTypeRequest: ticketTypeRequests) {
            if(!ticketTypeRequest.getTicketType().equals(TicketTypeRequest.Type.INFANT)){
                totalTickets += ticketTypeRequest.getNoOfTickets();
            }
        }

        return totalTickets;
    }
}
