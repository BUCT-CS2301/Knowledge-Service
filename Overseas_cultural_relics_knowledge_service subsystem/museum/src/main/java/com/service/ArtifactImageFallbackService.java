package com.service;

import com.entity.Cart;
import com.mapper.ArtifactImageMapper;
import com.util.ArtifactImageUrlResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Neo4j 未返回图片时，从 MySQL 查图；仍无图则按馆藏编号推断 CDN 地址。
 */
@Service
public class ArtifactImageFallbackService {

    @Autowired
    private ArtifactImageMapper artifactImageMapper;

    public void fillMissingImages(List<Cart> carts) {
        if (carts == null || carts.isEmpty()) {
            return;
        }
        List<Cart> missing = carts.stream()
                .filter(c -> c != null && isBlank(c.getImg_url()))
                .toList();
        if (missing.isEmpty()) {
            return;
        }

        List<String> objectIds = missing.stream()
                .map(Cart::getObjectId)
                .filter(id -> !isBlank(id))
                .distinct()
                .collect(Collectors.toList());
        Map<String, MetaRow> metaByObjectId = objectIds.isEmpty()
                ? Map.of()
                : loadMetaByObjectId(objectIds);

        List<String> accessionNumbers = missing.stream()
                .map(c -> accessionOf(c, metaByObjectId.get(c.getObjectId())))
                .filter(acc -> !isBlank(acc))
                .distinct()
                .collect(Collectors.toList());
        Map<String, String> byAccession = accessionNumbers.isEmpty()
                ? Map.of()
                : toMap(artifactImageMapper.findImageRowsByAccessionNumbers(accessionNumbers),
                "accessionNumber", "imageUrl");

        for (Cart cart : missing) {
            MetaRow meta = metaByObjectId.get(cart.getObjectId());
            String accession = accessionOf(cart, meta);
            String museum = museumOf(cart, meta);
            String url = firstNonBlank(
                    meta == null ? null : meta.imageUrl,
                    byAccession.get(accession),
                    isBlank(accession) ? null : artifactImageMapper.findImageUrlByAccessionNumber(accession),
                    ArtifactImageUrlResolver.fromAccession(accession, museum)
            );
            if (!isBlank(url)) {
                cart.setImg_url(pickFirstUrl(url));
            }
        }
    }

    public void fillMissingImage(Map<String, Object> detail) {
        if (detail == null || detail.isEmpty()) {
            return;
        }
        if (!isBlank(stringVal(detail.get("img_url")))) {
            return;
        }
        String objectId = stringVal(detail.get("objectId"));
        String accession = stringVal(detail.get("accession_number"));
        String museum = stringVal(detail.get("museum"));

        String url = resolveUrl(objectId, accession, museum);
        if (!isBlank(url)) {
            detail.put("img_url", pickFirstUrl(url));
        }
    }

    private String resolveUrl(String objectId, String accession, String museum) {
        String url = null;
        if (!isBlank(objectId)) {
            url = artifactImageMapper.findImageUrlByObjectId(objectId);
            if (isBlank(url)) {
                List<Map<String, String>> metaRows = artifactImageMapper.findMetaRowsByObjectIds(List.of(objectId));
                if (!metaRows.isEmpty()) {
                    Map<String, String> row = metaRows.get(0);
                    accession = firstNonBlank(accession, stringVal(row.get("accessionNumber")));
                    museum = firstNonBlank(museum, stringVal(row.get("museumNameCn")), stringVal(row.get("museumName")));
                    url = stringVal(row.get("imageUrl"));
                }
            }
        }
        if (isBlank(url) && !isBlank(accession)) {
            url = artifactImageMapper.findImageUrlByAccessionNumber(accession);
        }
        if (isBlank(url)) {
            url = ArtifactImageUrlResolver.fromAccession(accession, museum);
        }
        return isBlank(url) ? null : pickFirstUrl(url);
    }

    private Map<String, MetaRow> loadMetaByObjectId(List<String> objectIds) {
        Map<String, MetaRow> map = new HashMap<>();
        for (Map<String, String> row : artifactImageMapper.findMetaRowsByObjectIds(objectIds)) {
            if (row == null) {
                continue;
            }
            String objectId = stringVal(row.get("objectId"));
            if (isBlank(objectId)) {
                continue;
            }
            map.putIfAbsent(objectId, new MetaRow(
                    stringVal(row.get("imageUrl")),
                    stringVal(row.get("accessionNumber")),
                    stringVal(row.get("museumName")),
                    stringVal(row.get("museumNameCn"))
            ));
        }
        return map;
    }

    private String accessionOf(Cart cart, MetaRow meta) {
        return firstNonBlank(cart.getAccessionNumber(), meta == null ? null : meta.accessionNumber);
    }

    private String museumOf(Cart cart, MetaRow meta) {
        return firstNonBlank(cart.getMakers_name(), meta == null ? null : meta.museumLabel());
    }

    private Map<String, String> toMap(List<Map<String, String>> rows, String keyField, String valueField) {
        Map<String, String> map = new HashMap<>();
        if (rows == null) {
            return map;
        }
        for (Map<String, String> row : rows) {
            if (row == null) {
                continue;
            }
            String key = stringVal(row.get(keyField));
            String value = stringVal(row.get(valueField));
            if (!isBlank(key) && !isBlank(value) && !map.containsKey(key)) {
                map.put(key, value);
            }
        }
        return map;
    }

    private String firstNonBlank(String... values) {
        if (values == null) {
            return "";
        }
        for (String value : values) {
            if (!isBlank(value)) {
                return value.trim();
            }
        }
        return "";
    }

    private String pickFirstUrl(String url) {
        if (url == null) {
            return null;
        }
        String trimmed = url.trim();
        if (trimmed.contains(",")) {
            return trimmed.split(",")[0].trim();
        }
        return trimmed;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String stringVal(Object value) {
        return value == null ? "" : Objects.toString(value, "").trim();
    }

    private static final class MetaRow {
        private final String imageUrl;
        private final String accessionNumber;
        private final String museumName;
        private final String museumNameCn;

        private MetaRow(String imageUrl, String accessionNumber, String museumName, String museumNameCn) {
            this.imageUrl = imageUrl;
            this.accessionNumber = accessionNumber;
            this.museumName = museumName;
            this.museumNameCn = museumNameCn;
        }

        private String museumLabel() {
            if (museumNameCn != null && !museumNameCn.isBlank()) {
                return museumNameCn.trim();
            }
            return museumName == null ? "" : museumName.trim();
        }
    }
}
