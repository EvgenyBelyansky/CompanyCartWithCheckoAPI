package ru.companycart.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.companycart.client.CheckoClient;
import ru.companycart.dto.checko.AddressComponents;
import ru.companycart.dto.checko.CheckoResponse;
import ru.companycart.dto.checko.NameComponents;
import ru.companycart.entity.CompanyCartEntity;
import ru.companycart.repository.CompanyCartRepository;

@Service
@RequiredArgsConstructor
public class CompanyCartService {

    private final CheckoClient checkoClient;
    private final AddressParserService addressParserService;
    private final NameParserService nameParserService;
    private final CompanyCartRepository companyCartRepository;

    public CompanyCartEntity getCompanyByInn(String inn) {
        CheckoResponse response = checkoClient.findCompanyByInn(inn);

        if (response == null || response.getData() == null) {
            throw new RuntimeException("Компания с ИНН " + inn + " не найдена в Checko");
        }

        CompanyCartEntity companyCart = new CompanyCartEntity();

        companyCart.setInn(response.getInn());
        companyCart.setKpp(response.getKpp());
        companyCart.setOgrn(response.getOgrn());
        companyCart.setFullName(response.getFullName());
        companyCart.setShortName(response.getShortName());
        companyCart.setStatus(response.getStatus());
        companyCart.setCompanyRegisterDateTimestamp(response.getRegistrationDate());

        String fullAddress = response.getAddress();
        if (fullAddress != null) {
            AddressComponents addressComponents = addressParserService.parseAddress(fullAddress);
            companyCart.setAddress(addressComponents.getFullAddress());
            companyCart.setCompanyZip(addressComponents.getPostalCode());
            companyCart.setCompanyCountry(addressComponents.getCountry());
            companyCart.setCompanyRegion(addressComponents.getRegion());
            companyCart.setCompanyCity(addressComponents.getCity());
            companyCart.setCompanyStreet(addressComponents.getStreet());
            companyCart.setCompanyBuilding(addressComponents.getFullHouseNumber());
        }

        companyCart.setMainOkved(response.getMainOkved());
        companyCart.setAdditionalOkveds(response.getAdditionalOkveds());

        companyCart.setCompanyMsisdn(response.getPhone());
        companyCart.setCompanyEmail(response.getEmail());
        companyCart.setCompanyUrl(response.getWebsite());

        String managerName = response.getManagerName();
        if (managerName != null) {
            NameComponents parsedName = nameParserService.parseFullName(managerName);
            companyCart.setCompanyRepresentativeName(parsedName.getName());
            companyCart.setCompanyRepresentativeLastName(parsedName.getLastName());
            companyCart.setCompanyRepresentativeMiddleName(parsedName.getMidlName());
        }
        companyCart.setCompanyRepresentativePosition(response.getManagerPosition());
        companyCart.setCompanyRepresentativeInn(response.getManagerInn());

        return companyCart;
    }

    @Transactional
    public CompanyCartEntity saveCompany(String inn) {
        return companyCartRepository.save(getCompanyByInn(inn));
    }
}
