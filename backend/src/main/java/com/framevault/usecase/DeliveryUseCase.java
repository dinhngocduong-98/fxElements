package com.framevault.usecase;

import static com.framevault.shared.Failures.*;

import com.framevault.dto.response.PurchaseResponse;
import com.framevault.dto.response.UrlResponse;
import com.framevault.usecase.model.FilePayload;
import java.util.List;

public interface DeliveryUseCase {

  List<PurchaseResponse> purchases(String email);

  UrlResponse grant(String email, String id);

  FilePayload download(String email, String token);

  void resend(String id);
}
