package com.framevault.usecase.model;

import org.springframework.core.io.Resource;

public class FilePayload {

  private final Resource resource;

  private final String name, mime;

  public FilePayload(Resource resource, String name, String mime) {
    this.resource = resource;
    this.name = name;
    this.mime = mime;
  }

  public Resource getResource() {
    return resource;
  }

  public String getName() {
    return name;
  }

  public String getMime() {
    return mime;
  }
}
