package com.isanorte.constructora_api.service;

import java.util.List;
import java.util.UUID;

import com.isanorte.constructora_api.dto.request.ActivoRequest;
import com.isanorte.constructora_api.dto.request.TestimonioRequest;
import com.isanorte.constructora_api.dto.response.TestimonioResponse;

public interface ITestimonioService {

    List<TestimonioResponse> findAll();

    TestimonioResponse findById(UUID id);

    TestimonioResponse create(TestimonioRequest request);

    TestimonioResponse update(UUID id, TestimonioRequest request);

    TestimonioResponse updateActivo(UUID id, ActivoRequest request);

    void delete(UUID id);
}
