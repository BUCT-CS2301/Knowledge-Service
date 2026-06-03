package com.util;

/**
 * 根据馆藏编号与博物馆名称推断公开图片 CDN 地址（MySQL / Neo4j 均无 image_url 时使用）。
 */
public final class ArtifactImageUrlResolver {

    private ArtifactImageUrlResolver() {
    }

    public static String fromAccession(String accession, String museum) {
        if (accession == null || accession.trim().isEmpty()) {
            return null;
        }
        String acc = accession.trim();
        String muse = museum == null ? "" : museum.trim();
        if (isCleveland(muse) || (muse.isEmpty() && looksLikeClevelandAccession(acc))) {
            return "https://openaccess-cdn.clevelandart.org/" + acc + "/" + acc + "_web.jpg";
        }
        return null;
    }

    private static boolean isCleveland(String museum) {
        return museum.contains("克利夫兰") || museum.contains("Cleveland");
    }

    /** 克利夫兰馆藏编号常见形如 1962.162、1970.129.b、1948.228.b */
    private static boolean looksLikeClevelandAccession(String accession) {
        return accession.matches("\\d{4}\\..+") || accession.matches("\\d{4}-.+");
    }
}
