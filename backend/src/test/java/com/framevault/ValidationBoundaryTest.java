package com.framevault;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.framevault.dto.request.*;
import com.framevault.interactor.*;
import com.framevault.validation.*;
import java.util.*;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import org.junit.jupiter.api.*;

class ValidationBoundaryTest {
  private ValidatorFactory factory;
  private ValidatorWrapper validator;
  private List<Object> dependencies;

  @BeforeEach
  void setup() {
    factory = Validation.buildDefaultValidatorFactory();
    validator = new ValidatorWrapper(factory.getValidator());
    dependencies = new ArrayList<>();
  }

  @AfterEach
  void close() {
    factory.close();
  }

  private <T> T create(Class<T> type) throws Exception {
    var ctor = type.getConstructors()[0];
    Object[] args = new Object[ctor.getParameterCount()];
    for (int i = 0; i < args.length; i++) {
      Class<?> parameter = ctor.getParameterTypes()[i];
      if (parameter == ValidatorWrapper.class) args[i] = validator;
      else if (parameter == String.class) args[i] = "https://example.test";
      else {
        args[i] = mock(parameter);
        dependencies.add(args[i]);
      }
    }
    return type.cast(ctor.newInstance(args));
  }

  private void untouched() {
    for (Object value : dependencies) verifyNoInteractions(value);
  }

  @Test
  void registrationIsValidatedWithoutController() throws Exception {
    var interactor = create(AuthInteractor.class);
    RegisterRequest f = new RegisterRequest();
    f.setEmail("invalid");
    f.setName("");
    f.setPassword("short");
    assertThrows(InputValidationException.class, () -> interactor.register(f, "127.0.0.1"));
    untouched();
  }

  @Test
  void checkoutIsValidatedBeforeAnyRepositoryCall() throws Exception {
    var interactor = create(CommerceInteractor.class);
    assertThrows(
        InputValidationException.class,
        () -> interactor.create("buyer@example.test", "checkout-key-12345", new CheckoutRequest()));
    untouched();
  }

  @Test
  void catalogPaginationIsValidatedAtUseCaseBoundary() throws Exception {
    var interactor = create(CatalogInteractor.class);
    CatalogFilterRequest f = new CatalogFilterRequest();
    f.setSize(0);
    assertThrows(InputValidationException.class, () -> interactor.list(f, false));
    untouched();
  }

  @Test
  void statusAndRequiredBooleanAreValidated() throws Exception {
    var interactor = create(AdminInteractor.class);
    EntitlementRequest status = new EntitlementRequest();
    status.setStatus("PAID");
    assertThrows(
        InputValidationException.class,
        () -> interactor.entitlement("id", status, "admin@example.test"));
    assertThrows(
        InputValidationException.class,
        () -> interactor.enabled(1L, new EnabledRequest(), "admin@example.test"));
    untouched();
  }
}
