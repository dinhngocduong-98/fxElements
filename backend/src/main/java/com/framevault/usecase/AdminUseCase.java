package com.framevault.usecase;

import static com.framevault.shared.Failures.*;

import com.framevault.dto.request.EnabledRequest;
import com.framevault.dto.request.EntitlementRequest;
import com.framevault.dto.response.AdminUserResponse;
import com.framevault.dto.response.AuditResponse;
import com.framevault.dto.response.DashboardResponse;
import com.framevault.dto.response.EmailAttemptResponse;
import com.framevault.dto.response.EmailJobResponse;
import com.framevault.dto.response.EnabledResponse;
import com.framevault.dto.response.MessageResponse;
import com.framevault.dto.response.OrderResponse;
import com.framevault.dto.response.StatusResponse;
import java.util.List;

public interface AdminUseCase {

  DashboardResponse dashboard();

  List<AdminUserResponse> users(int page);

  EnabledResponse enabled(Long id, EnabledRequest f, String actor);

  List<OrderResponse> orders(int page);

  OrderResponse order(String id);

  MessageResponse resend(String id, String actor);

  StatusResponse entitlement(String id, EntitlementRequest f, String actor);

  List<EmailJobResponse> emails(int page);

  List<EmailAttemptResponse> attempts(String id);

  MessageResponse retry(String id, String actor);

  List<AuditResponse> logs(int page);
}
