package com.isanorte.constructora_api.service;

import com.isanorte.constructora_api.dto.response.IsadecorHomeResponse;

public interface IIsadecorHomeService {

    IsadecorHomeResponse findHome(String siteKey, String unitSlug);
}
