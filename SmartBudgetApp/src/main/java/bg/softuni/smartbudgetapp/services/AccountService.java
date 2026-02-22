package bg.softuni.smartbudgetapp.services;

import bg.softuni.smartbudgetapp.models.dto.AccountDTO;

import java.util.List;

public interface AccountService {

    List<AccountDTO> getAllAccountsForUser(Long userId);

    AccountDTO createAccount(AccountDTO accountDTO, Long userId);

    AccountDTO getAccountById(Long id);
}
