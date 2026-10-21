package com.framevault.repository;

import com.framevault.domain.model.MediaAsset;
import com.framevault.repository.page.*;
import java.util.*;

public interface MediaAssetRepository extends CrudRepository<MediaAsset, String> {

  boolean existsByIdAndKind(String id, String kind);
}
