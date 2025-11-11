package ru.companycart.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.companycart.client.DadataClient;
import ru.companycart.dto.dadata.DadataResponse;
import ru.companycart.entity.CompanyCartEntity;

@Service
@RequiredArgsConstructor
public class CompanyCartService {

    private final DadataClient dadataClient;

    public CompanyCartEntity getCompanyByInn(String inn) {

        DadataResponse response = dadataClient.findCompanyByInn(inn);

        DadataResponse.CompanyData companyData = response.getSuggestions().get(0).getData();

        CompanyCartEntity companyCart = new CompanyCartEntity();
        companyCart.setInn(companyData.getInn());
        companyCart.setFullName(companyData.getName().getFull_with_opf());
        companyCart.setStatus(companyData.getState().getStatus());

        return companyCart;
    }
}
