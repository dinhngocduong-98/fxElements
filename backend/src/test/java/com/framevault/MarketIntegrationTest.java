package com.framevault;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.*;
import com.framevault.shared.Tokens;
import java.nio.file.*;
import java.util.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.*;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@org.springframework.test.annotation.DirtiesContext(
    classMode = org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS)
class MarketIntegrationTest {
  @Autowired MockMvc mvc;
  @Autowired JdbcTemplate db;
  @Autowired PasswordEncoder encoder;
  @Autowired ObjectMapper json;
  long buyer, other, pid;
  String asset, version;

  @BeforeEach
  void setup() throws Exception {
    db.update(
        "INSERT INTO users(email,name,password_hash,role,enabled,verified)"
            + " VALUES(?,?,?,'CUSTOMER',TRUE,TRUE)",
        "buyer@test.local",
        "Buyer",
        encoder.encode("testing-password"));
    db.update(
        "INSERT INTO users(email,name,password_hash,role,enabled,verified)"
            + " VALUES(?,?,?,'CUSTOMER',TRUE,TRUE)",
        "other@test.local",
        "Other",
        encoder.encode("testing-password"));
    db.update(
        "INSERT INTO users(email,name,password_hash,role,enabled,verified)"
            + " VALUES('admin@test.local','Admin',?,'ADMIN',TRUE,TRUE)",
        encoder.encode("testing-password"));
    buyer = db.queryForObject("SELECT id FROM users WHERE email='buyer@test.local'", Long.class);
    other = db.queryForObject("SELECT id FROM users WHERE email='other@test.local'", Long.class);
    asset = UUID.randomUUID().toString();
    String thumb = UUID.randomUUID().toString(), preview = UUID.randomUUID().toString();
    for (String[] f :
        new String[][] {
          {asset, "ORIGINAL", "private/" + asset},
          {thumb, "THUMBNAIL", "public/" + thumb},
          {preview, "PREVIEW", "public/" + preview}
        })
      db.update(
          "INSERT INTO"
              + " media_assets(id,kind,storage_key,original_name,mime_type,size_bytes,checksum)"
              + " VALUES(?,?,?,'fixture.zip','application/zip',7,?)",
          f[0],
          f[1],
          f[2],
          Tokens.hash("fixture"));
    Path path =
        Paths.get(
            System.getProperty("java.io.tmpdir"), "framevault-test-storage", "private", asset);
    Files.createDirectories(path.getParent());
    Files.writeString(path, "fixture");
    long cat = db.queryForObject("SELECT MIN(id) FROM categories", Long.class);
    db.update(
        "INSERT INTO"
            + " products(name,slug,description,price,status,category_id,width,height,fps,format,alpha_channel,duration_seconds,featured,thumbnail_id,preview_id)"
            + " VALUES('Fixture','fixture','Test"
            + " asset',249000,'PUBLISHED',?,3840,2160,30,'ZIP',FALSE,10,FALSE,?,?)",
        cat,
        thumb,
        preview);
    pid = db.queryForObject("SELECT id FROM products WHERE slug='fixture'", Long.class);
    db.update(
        "INSERT INTO product_asset_versions(product_id,asset_id,version_number) VALUES(?,?,1)",
        pid,
        asset);
    version =
        db.queryForObject(
                "SELECT id FROM product_asset_versions WHERE product_id=?", Long.class, pid)
            .toString();
  }

  @Autowired com.framevault.usecase.MailUseCase mailService;
  @Autowired com.framevault.repository.EmailJobRepository emailJobs;

  @Test
  void catalogQueryDtoBindsFiltersAndRejectsInvalidValues() throws Exception {
    mvc.perform(get("/api/v1/products").param("minPrice", "250000"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.total").value(0));
    mvc.perform(
            get("/api/v1/products")
                .param("height", "2160")
                .param("fps", "30")
                .param("alpha_channel", "false")
                .param("sort", "priceDesc"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.total").value(1));
    mvc.perform(get("/api/v1/products").param("category_id", "abc"))
        .andExpect(status().isBadRequest());
    mvc.perform(get("/api/v1/products").param("size", "0")).andExpect(status().isBadRequest());
  }

  @Test
  void emailJobClaimIsExclusiveAndCompletionChecksLease() {
    String id = mailService.enqueue("buyer@test.local", "Test", "secret token body");
    com.framevault.usecase.model.MailPayload job = mailService.claim(id);
    assertNotNull(job);
    assertNull(mailService.claim(id));
    mailService.complete(id, job.getLease().minusSeconds(1), null);
    assertEquals("PROCESSING", emailJobs.findById(id).orElseThrow().getStatus());
    mailService.complete(id, job.getLease(), null);
    var completed = emailJobs.findById(id).orElseThrow();
    assertEquals("SENT", completed.getStatus());
    assertEquals("[redacted after delivery]", completed.getBody());
  }

  @Test
  void sessionLoginAndLogout() throws Exception {
    MvcResult login =
        mvc.perform(
                post("/api/v1/auth/login")
                    .with(csrf())
                    .contentType("application/json")
                    .content("{\"email\":\"buyer@test.local\",\"password\":\"testing-password\"}"))
            .andExpect(status().isOk())
            .andReturn();
    javax.servlet.http.Cookie cookie = login.getResponse().getCookie("SESSION");
    assertNotNull(cookie);
    mvc.perform(get("/api/v1/auth/me").cookie(cookie))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value("buyer@test.local"));
    mvc.perform(post("/api/v1/auth/logout").cookie(cookie).with(csrf())).andExpect(status().isOk());
    mvc.perform(get("/api/v1/auth/me").cookie(cookie)).andExpect(status().isUnauthorized());
  }

  @Test
  void adminPublishesOnlyWithCompleteAssets() throws Exception {
    Map<String, Object> payload = new HashMap<>();
    payload.put("name", "New asset");
    payload.put("slug", "new-asset");
    payload.put("description", "New asset description");
    payload.put("price", "123000");
    payload.put("status", "PUBLISHED");
    payload.put("categoryId", db.queryForObject("SELECT MIN(id) FROM categories", Long.class));
    // Missing media must not create a published product.
    mvc.perform(
            post("/api/v1/admin/products")
                .with(user("admin@test.local").roles("ADMIN"))
                .with(csrf())
                .contentType("application/json")
                .content(json.writeValueAsString(payload)))
        .andExpect(status().isBadRequest());
    payload.put(
        "thumbnailId",
        db.queryForObject("SELECT id FROM media_assets WHERE kind='THUMBNAIL'", String.class));
    payload.put(
        "previewId",
        db.queryForObject("SELECT id FROM media_assets WHERE kind='PREVIEW'", String.class));
    payload.put("originalAssetId", asset);
    mvc.perform(
            post("/api/v1/admin/products")
                .with(user("admin@test.local").roles("ADMIN"))
                .with(csrf())
                .contentType("application/json")
                .content(json.writeValueAsString(payload)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("PUBLISHED"));
    mvc.perform(get("/api/v1/products/new-asset"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.price").value("123000.00"));
  }

  @Test
  void publicCatalogExcludesDraftAndOriginal() throws Exception {
    mvc.perform(get("/api/v1/products"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.total").value(1))
        .andExpect(jsonPath("$.items[0].price").value("249000.00"))
        .andExpect(jsonPath("$.items[0].storage_key").doesNotExist());
    mvc.perform(get("/api/v1/media/public/" + asset)).andExpect(status().isNotFound());
    mvc.perform(get("/api/v1/admin/products")).andExpect(status().isUnauthorized());
    mvc.perform(get("/api/v1/admin/products").with(user("buyer@test.local").roles("CUSTOMER")))
        .andExpect(status().isForbidden());
  }

  @Test
  void checkoutValidatesPriceAndIsIdempotent() throws Exception {
    addCart();
    String payload = "{\"expectedPrices\":{\"" + pid + "\":\"1\"}}";
    mvc.perform(
            post("/api/v1/orders")
                .with(user("buyer@test.local"))
                .with(csrf())
                .header("Idempotency-Key", "checkout-key-12345")
                .contentType("application/json")
                .content(payload))
        .andExpect(status().isConflict());
    assertEquals(0, db.queryForObject("SELECT COUNT(*) FROM orders", Integer.class));
    payload = "{\"expectedPrices\":{\"" + pid + "\":\"249000\"}}";
    MvcResult first =
        mvc.perform(
                post("/api/v1/orders")
                    .with(user("buyer@test.local"))
                    .with(csrf())
                    .header("Idempotency-Key", "checkout-key-12345")
                    .contentType("application/json")
                    .content(payload))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("PENDING_PAYMENT"))
            .andReturn();
    String oid = json.readTree(first.getResponse().getContentAsString()).get("id").asText();
    mvc.perform(
            post("/api/v1/orders")
                .with(user("buyer@test.local"))
                .with(csrf())
                .header("Idempotency-Key", "checkout-key-12345")
                .contentType("application/json")
                .content(payload))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(oid));
    mvc.perform(get("/api/v1/orders/" + oid).with(user("other@test.local")))
        .andExpect(status().isNotFound());
    assertEquals(1, db.queryForObject("SELECT COUNT(*) FROM orders", Integer.class));
    assertEquals(0, db.queryForObject("SELECT COUNT(*) FROM download_entitlements", Integer.class));
    assertEquals(0, db.queryForObject("SELECT COUNT(*) FROM email_jobs", Integer.class));
  }

  @Test
  void downloadRequiresOwnershipAndActiveEntitlement() throws Exception {
    String entitlement = seedPaidPurchase();
    MvcResult r =
        mvc.perform(
                post("/api/v1/purchases/" + entitlement + "/download-grants")
                    .with(user("buyer@test.local"))
                    .with(csrf()))
            .andExpect(status().isOk())
            .andReturn();
    String link = json.readTree(r.getResponse().getContentAsString()).get("url").asText();
    String raw = link.substring(link.indexOf("#token=") + 7);
    assertFalse(
        db.queryForObject(
                "SELECT token_hash FROM download_grants WHERE entitlement_id=?",
                String.class,
                entitlement)
            .equals(raw));
    mvc.perform(get("/api/v1/downloads/" + raw).with(user("other@test.local")))
        .andExpect(status().isNotFound());
    mvc.perform(get("/api/v1/downloads/" + raw).with(user("buyer@test.local")))
        .andExpect(status().isOk())
        .andExpect(content().string("fixture"));
    mvc.perform(
            put("/api/v1/admin/entitlements/" + entitlement)
                .with(user("admin@test.local").roles("ADMIN"))
                .with(csrf())
                .contentType("application/json")
                .content("{\"status\":\"REVOKED\"}"))
        .andExpect(status().isOk());
    mvc.perform(get("/api/v1/downloads/" + raw).with(user("buyer@test.local")))
        .andExpect(status().isNotFound());
  }

  @Test
  void csrfAndUploadRulesAreEnforced() throws Exception {
    mvc.perform(
            post("/api/v1/cart/items")
                .with(user("buyer@test.local"))
                .contentType("application/json")
                .content("{\"productId\":" + pid + "}"))
        .andExpect(status().isForbidden());
    MockMultipartFile fake =
        new MockMultipartFile(
            "file", "fake.png", "image/png", "<script>alert(1)</script>".getBytes());
    mvc.perform(
            multipart("/api/v1/admin/media/uploads")
                .file(fake)
                .param("kind", "THUMBNAIL")
                .with(user("admin@test.local").roles("ADMIN"))
                .with(csrf()))
        .andExpect(status().isBadRequest());
  }

  @Test
  void emailVerificationTokenIsOneUse() throws Exception {
    mvc.perform(
            post("/api/v1/auth/register")
                .with(csrf())
                .contentType("application/json")
                .content(
                    "{\"email\":\"new@test.local\",\"name\":\"New\",\"password\":\"testing-password\"}"))
        .andExpect(status().isOk());
    String body =
        db.queryForObject(
            "SELECT body FROM email_jobs WHERE recipient='new@test.local'", String.class);
    String raw = body.substring(body.indexOf("#token=") + 7);
    assertEquals(
        Tokens.hash(raw),
        db.queryForObject(
            "SELECT token_hash FROM account_tokens WHERE purpose='VERIFY'", String.class));
    String payload = "{\"token\":\"" + raw + "\"}";
    mvc.perform(
            post("/api/v1/auth/verify-email")
                .with(csrf())
                .contentType("application/json")
                .content(payload))
        .andExpect(status().isOk());
    mvc.perform(
            post("/api/v1/auth/verify-email")
                .with(csrf())
                .contentType("application/json")
                .content(payload))
        .andExpect(status().isBadRequest());
  }

  private void addCart() {
    db.update("INSERT INTO cart_items(user_id,product_id) VALUES(?,?)", buyer, pid);
  }

  private String seedPaidPurchase() {
    String oid = UUID.randomUUID().toString();
    db.update(
        "INSERT INTO orders(id,user_id,email,currency,total,status,idempotency_key,expires_at)"
            + " VALUES(?,?,'buyer@test.local','VND',249000,'PAID','fixture-paid',CURRENT_TIMESTAMP)",
        oid,
        buyer);
    db.update(
        "INSERT INTO"
            + " order_items(order_id,product_id,asset_version_id,name,price,license_version,license_body)"
            + " VALUES(?,?,?,'Fixture',249000,'test','test license')",
        oid,
        pid,
        Long.parseLong(version));
    long item = db.queryForObject("SELECT id FROM order_items WHERE order_id=?", Long.class, oid);
    String e = UUID.randomUUID().toString();
    db.update(
        "INSERT INTO download_entitlements(id,user_id,order_item_id) VALUES(?,?,?)",
        e,
        buyer,
        item);
    return e;
  }
}
