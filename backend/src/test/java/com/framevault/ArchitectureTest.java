package com.framevault;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RestController;

class ArchitectureTest {
  @Test
  void controllersDependOnInputPortsAndNeverReturnModels() throws Exception {
    var scan = new ClassPathScanningCandidateComponentProvider(false);
    scan.addIncludeFilter(new AnnotationTypeFilter(RestController.class));
    var beans = scan.findCandidateComponents("com.framevault.controller");
    assertEquals(7, beans.size());
    for (var bean : beans) {
      Class<?> type = Class.forName(bean.getBeanClassName());
      for (Field f : type.getDeclaredFields()) {
        assertEquals("com.framevault.usecase", f.getType().getPackageName());
        assertTrue(f.getType().isInterface());
      }
      for (Method m : type.getDeclaredMethods())
        if (Modifier.isPublic(m.getModifiers())) {
          assertNotEquals(Object.class, m.getReturnType());
          assertFalse(Map.class.isAssignableFrom(m.getReturnType()));
          noModel(m.getGenericReturnType());
        }
    }
  }

  private void noModel(Type t) {
    if (t instanceof Class<?>)
      assertFalse(((Class<?>) t).getPackageName().startsWith("com.framevault.domain.model"));
    if (t instanceof ParameterizedType)
      for (Type a : ((ParameterizedType) t).getActualTypeArguments()) noModel(a);
  }

  @Test
  void adaptersImplementOutputPortsAndOwnSql() throws Exception {
    var scan = new ClassPathScanningCandidateComponentProvider(false);
    scan.addIncludeFilter(new AnnotationTypeFilter(Repository.class));
    var adapters = scan.findCandidateComponents("com.framevault.adapter.sql");
    assertEquals(20, adapters.size());
    for (var bean : adapters) {
      Class<?> adapter = Class.forName(bean.getBeanClassName());
      assertTrue(
          Arrays.stream(adapter.getInterfaces())
              .anyMatch(
                  i -> i.getPackageName().equals("com.framevault.repository") && i.isInterface()),
          adapter.getName());
    }
    Path root = Paths.get("src/main/java/com/framevault");
    try (var files = Files.walk(root)) {
      for (Path p :
          files.filter(x -> x.toString().endsWith(".java")).collect(Collectors.toList())) {
        String source = Files.readString(p), path = p.toString();
        assertFalse(source.contains("javax.persistence"), path);
        assertFalse(source.contains("JpaRepository"), path);
        if (!path.contains("/adapter/")) {
          assertFalse(source.contains("JdbcTemplate"), path);
          for (String sql : List.of("\"SELECT ", "\"INSERT ", "\"UPDATE ", "\"DELETE "))
            assertFalse(source.contains(sql), path);
        }
        if (path.contains("/controller/")) {
          assertFalse(source.contains("com.framevault.repository"));
          assertFalse(source.contains("@Transactional"));
          assertFalse(source.contains("@Valid"));
        }
        if (path.contains("/repository/")) {
          assertFalse(source.contains("org.springframework"), path);
          assertFalse(source.contains("@Query"), path);
        }
      }
    }
  }

  @Test
  void interactorsImplementInputPortsAndUseValidatorWrapper() throws Exception {
    Path root = Paths.get("src/main/java/com/framevault/interactor");
    try (var files = Files.list(root)) {
      for (Path p :
          files.filter(x -> x.toString().endsWith(".java")).collect(Collectors.toList())) {
        Class<?> cls =
            Class.forName(
                "com.framevault.interactor." + p.getFileName().toString().replace(".java", ""));
        assertTrue(
            Arrays.stream(cls.getInterfaces())
                .anyMatch(i -> i.getPackageName().equals("com.framevault.usecase")));
        assertTrue(
            Arrays.stream(cls.getDeclaredFields())
                .anyMatch(f -> f.getType() == com.framevault.validation.ValidatorWrapper.class));
      }
    }
  }
}
