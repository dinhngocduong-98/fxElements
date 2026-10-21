package com.framevault.repository;

import com.framevault.domain.model.DownloadGrant;
import com.framevault.repository.page.*;
import java.util.*;

public interface DownloadGrantRepository extends CrudRepository<DownloadGrant, String> {

  Optional<DownloadGrant> findByTokenHash(String hash);

  int revoke(String id);
}
