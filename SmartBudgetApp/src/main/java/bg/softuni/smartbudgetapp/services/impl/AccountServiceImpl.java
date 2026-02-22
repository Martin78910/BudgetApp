package bg.softuni.smartbudgetapp.services.impl;

import bg.softuni.smartbudgetapp.models.AccountEntity;
import bg.softuni.smartbudgetapp.models.UserEntity;
import bg.softuni.smartbudgetapp.models.dto.AccountDTO;
import bg.softuni.smartbudgetapp.repositories.AccountRepository;
import bg.softuni.smartbudgetapp.repositories.UserRepository;
import bg.softuni.smartbudgetapp.services.AccountService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {


    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public AccountServiceImpl(AccountRepository accountRepository, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<AccountDTO> getAllAccountsForUser(Long userId) {
        // Промяна: използваме findAllByOwner_Id вместо findAllByOwnerId
        List<AccountEntity> userAccounts = accountRepository.findAllByOwner_Id(userId);

        // Мапваме към DTO
        return userAccounts.stream()
                .map(this::mapEntityToDTO)
                .toList();
    }

    @Override
    public AccountDTO getAccountById(Long id) {
        return accountRepository.findById(id)
                .map(this::mapEntityToDTO)
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }

    @Override
    public AccountDTO createAccount(AccountDTO accountDTO, Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setAccountName(accountDTO.getAccountName());
        accountEntity.setAccountType(accountDTO.getAccountType());
        accountEntity.setBalance(accountDTO.getBalance());
        accountEntity.setOwner(user);

        accountRepository.save(accountEntity);

        return mapEntityToDTO(accountEntity);
    }

    private AccountDTO mapEntityToDTO(AccountEntity entity) {
        AccountDTO dto = new AccountDTO();
        dto.setId(entity.getId());
        dto.setAccountName(entity.getAccountName());
        dto.setAccountType(entity.getAccountType());
        dto.setBalance(entity.getBalance());
        return dto;
    }
}
