package com.unicksbyte.inkspire.projection;

import java.time.LocalDateTime;
import java.util.List;

public interface PostPreviewProjection {
    String getPublicId();
    String getTitle();
    String getPreview();
    String getAuthorId();
    String getAuthorName();
    String getCategory();
    LocalDateTime getUpdatedAt();
    List<String> getTags();
}
