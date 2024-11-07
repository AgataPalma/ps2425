package com.example.fix4you_api.Service.Ticket;

import com.example.fix4you_api.Data.MongoRepositories.TicketServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketServiceRepository ticketServiceRepository;

    @Override
    @Transactional
    public void deleteTickets(String userId) {
        ticketServiceRepository.deleteByUserId(userId);
    }
}
